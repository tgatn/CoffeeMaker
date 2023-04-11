var app = angular.module('myApp', []);

app.controller('myCtrl', function($scope, $window, $http) {
  
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
