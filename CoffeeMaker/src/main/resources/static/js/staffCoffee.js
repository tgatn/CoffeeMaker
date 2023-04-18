var app = angular.module('myApp', ['ngCookies']);

app.controller('myCtrl', function($scope, $window, $http, $cookies) {
  $scope.customerName = $cookies.get('username');   
  $scope.viewFulfillOrders = () => {    
    $window.location.href = 'viewOrders.html';
  }

   $scope.logout = function() {
        $http.post('api/v1/logout').then(function(response) {
            window.location.href = '/login.html';
        });
    };
  
  
});
