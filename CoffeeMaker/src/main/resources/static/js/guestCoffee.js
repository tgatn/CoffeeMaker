var app = angular.module('myApp', ['ngCookies']);

app.controller('myCtrl', function($scope, $window, $http, $cookies) {
	$scope.customerName = 'Guest';
	$scope.viewFulfillOrders = () => {
		$http.post('api/v1/users/guest').then(() => {
			$window.location.href = 'guestOrder.html';
		});
	}

	$scope.logout = function() {
		$http.post('api/v1/logout').then(function(response) {
			window.location.href = '/login.html';
		});
	};


});
