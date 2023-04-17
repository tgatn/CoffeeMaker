var app = angular.module('myApp', ['ngCookies']);

app.controller('myCtrl', function($scope, $window, $http, $cookies) {
	$scope.customerName = $cookies.get('username');
	$scope.selectedDrink = null;

	$http.get('/api/v1/recipes').then(function(response) {
		$scope.drinks = response.data;
	}, function(error) {
		console.error(error);
	});

	$scope.updatePrice = function() {
		if ($scope.selectedDrink) {
			console.log($scope.quantity);
			$scope.totalPrice = $scope.quantity * angular.fromJson($scope.selectedDrink).price;
		}
	};

	$scope.placeOrder = function() {
		$http.post('/api/v1/orders', {
			customerid: $scope.customerName,
			is_complete: false,
			total_cost: $scope.totalPrice
		}).then(function(response) {
			console.log(response.data);
		}, function(error) {
			console.error(error);
		})
	};
	$scope.logout = function() {
		$http.post('api/v1/logout').then(function(response) {
			window.location.href = '/login.html';
		});
	};


});
