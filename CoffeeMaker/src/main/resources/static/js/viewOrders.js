var app = angular.module('myApp', ['ngCookies']);

app.controller('myCtrl', function($scope, $http, $cookies, $timeout) {
	$scope.staffName = $cookies.get('username');
	$scope.currentOrders = [];

	$http.get(`api/v1/orders/pending`).then(function(response) {
		$scope.currentOrders = response.data;
	});

	$scope.fulfillSelectedOrders = function() {
		const selectedOrders = $scope.currentOrders.filter(function(order) {
			return order.selected;
		});

		if (selectedOrders.length === 0) {
			alert('Please select at least one order to fulfill.');
			return;
		}

		selectedOrders.forEach(function(order) {
			$http.post(`api/v1/orders/${order.id}/complete`).then(function(response) {
				console.log(`Order ${order.id} fulfilled.`);
			}, function(error) {
				console.error(`Failed to fulfill order ${order.id}. Error: ${error}`);
			});
		});
		alert('Selected orders have been fulfilled.');
		$scope.refreshOrders();
	};

	$scope.refreshOrders = function() {
		$timeout(function() {
			$http.get(`api/v1/orders/pending`).then(function(response) {
				$scope.currentOrders = response.data;
			});
		});
	};

	$scope.logout = function() {
		$http.post('api/v1/logout').then(function(response) {
			window.location.href = '/login.html';
		});
	};
});	