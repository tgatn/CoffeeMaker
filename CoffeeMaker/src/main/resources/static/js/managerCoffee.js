var app = angular.module('myApp', ['ngCookies']);

app.controller('myCtrl', function($scope, $window, $http, $cookies) {
  $scope.customerName = $cookies.get('username');   
  $scope.viewFulfillOrders = () => {    
    $window.location.href = 'viewOrders.html';
  }
   $scope.addIngredient = () => {    
    $window.location.href = 'addingredient.html';
  }
 $scope.updateInventory = () => {    
    $window.location.href = 'inventory.html';
  }
 $scope.editRecipe = () => {    
    $window.location.href = 'editrecipe.html';
  }
 $scope.deleteRecipe = () => {    
    $window.location.href = 'deleterecipe.html';
  }
 $scope.addRecipe = () => {    
    $window.location.href = 'recipe.html';
  }


   $scope.logout = function() {
        $http.post('api/v1/logout').then(function(response) {
            window.location.href = '/login.html';
        });
    };
  
  
});
