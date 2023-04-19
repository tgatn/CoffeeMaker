var app = angular.module('myApp', ['ngCookies']);

app.controller('myCtrl', function($scope, $window, $http, $cookies) {
  $http.get('/api/v1/session').then(function(response) {
		$scope.customerName = response.data.username;
		$scope.role = response.data.role;
	}, function(error) {
		console.log(error);
		window.location.href="login";
	});  
  $scope.viewPastOrders = () => {
    // api endpoint here
    
    $window.location.href = 'pastOrders.html';
  }
  
  $scope.viewCurrentOrders = () => {
    // api endpoint here
    
    $window.location.href = 'currentOrders.html';
  }

    
  $scope.viewPlaceOrder = () => {
    // api endpoint here
    
    $window.location.href = 'placeOrders.html';
  }

   $scope.logout = function() {
        $http.post('api/v1/logout').then(function(response) {
            window.location.href = '/login.html';
        });
    };
  
  
});
