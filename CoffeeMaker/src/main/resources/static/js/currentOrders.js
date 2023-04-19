var app = angular.module('myApp', ['ngCookies']);

app.controller('myCtrl', function($scope, $http, $cookies) {
	$http.get('/api/v1/session').then(function(response) {
		$scope.customerName = response.data.username;
		$scope.role = response.data.role;
		$http.get(`api/v1/orders/${$scope.customerName}/pending`).then(function(response) {
			$scope.currentOrders = response.data;
		});
	}, function(error) {
		console.log(error);
		window.location.href = "login";
	});
	$scope.currentOrders = [];



	$scope.logout = function() {
		$http.post('api/v1/logout').then(function(response) {
			window.location.href = '/login.html';
		});
	};
});