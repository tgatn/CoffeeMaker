var app = angular.module('myApp', ['ngCookies']);

app.controller('myCtrl', function($scope, $window, $http, $cookies) {
  $scope.customerName = $cookies.get('username');
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
