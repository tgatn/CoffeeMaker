var app = angular.module('myApp', ['ngCookies']);

app.controller('myCtrl', function($scope, $window, $http, $cookies, $sce) {
	$http.get('/api/v1/session/').then(function(response) {
		$scope.customerName = response.data.username;
		$scope.role = response.data.role;
	}, function(error) {
		console.log(error);
		window.location.href="login";
	});

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

	$scope.showMessage = false;
	$scope.message = '';
	$scope.messageType = '';
	$scope.placeOrder = function() {
		console.log('Place order button clicked');
		$http.get(`/api/v1/recipes/${angular.fromJson($scope.selectedDrink).name}`).then(function(response) {
			const recipe = response.data;
			const cart = [{
				recipe: recipe,
				amount: $scope.quantity
			}];
			$http.post('/api/v1/orders', {
				totalCost: $scope.totalPrice,
				customer: $scope.customerName,
				cart: cart,
				isComplete: false,
			}).then(function(response) {
				$scope.message = 'Order placed successfully!';
				$scope.messageType = 'success';
				$scope.showMessage = true;
			}, function(error) {
				$scope.message = 'Order could not be placed.';
				$scope.messageType = 'error';
				$scope.showMessage = true;
			});
		}, function(error) {
			$scope.message = 'Order could not be placed.';
			$scope.messageType = 'error';
			$scope.showMessage = true;
		});
	};
	$scope.logout = function() {
		$http.post('api/v1/logout').then(function(response) {
			window.location.href = '/login.html';
		});
	};
});
