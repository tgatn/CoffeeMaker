var app = angular.module('myApp', ['ngCookies']);

app.controller('myCtrl', function($scope, $window, $http, $cookies) {
  $http.get('/api/v1/session/staff').then(function(response) {
		$scope.customerName = response.data.username;
		$scope.role = response.data.role;
	}, function(error) {
		console.log(error);
		window.location.href="login";
	});  
  $scope.viewFulfillOrders = () => {    
    $window.location.href = 'viewOrders.html';
  }

   $scope.logout = function() {
        $http.post('api/v1/logout').then(function(response) {
            window.location.href = '/login.html';
        });
    };
  
  
});
