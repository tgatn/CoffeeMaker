var app = angular.module('myApp', []);

app.controller('myCtrl', function($scope, $window) {
  
  $scope.customerLogin = function() {
    // api endpoint here
    
    $window.location.href = 'customerLogin.html';
  }
  
  $scope.staffLogin = function() {
    // api endpoint here
    
    $window.location.href = 'staffLogin.html';
  }
  
});
