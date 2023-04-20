var app = angular.module('myApp', []);

app.controller('myCtrl', function($scope, $window) {
  
  $scope.customerLogin = function() {    
    $window.location.href = 'customerLogin.html';
  }
  
  $scope.guestLogin = function() {    
    $window.location.href = 'guestIndex.html';
  }
  
  
  $scope.staffLogin = function() {
    $window.location.href = 'staffLogin.html';
  }
  
});
