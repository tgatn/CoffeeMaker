var app = angular.module('myApp', []);

app.controller('myCtrl', function($scope, $http, $sce) {
    $scope.passwordInputType = 'password';
    $scope.togglePassword = function() {
    // Toggle password input type between text and password
    if ($scope.passwordInputType === 'password') {
      $scope.passwordInputType = 'text';
      $scope.togglePasswordIcon = 'fa-eye-slash';
    } else {
      $scope.passwordInputType = 'password';
      $scope.togglePasswordIcon = 'fa-eye';
    }
  };
  $scope.hasError = false;
  $scope.submitForm = function() {
    // Prevent default form submit action
    event.preventDefault(); 
    //display the message div   
    $scope.showSuccessMessage = true;
    $scope.successMessageStyles = {
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center'
    };
    
    $scope.successMessage = $sce.trustAsHtml(`
    <div class="spinner"><i class="fas fa-spinner fa-spin"></i></div>
    <div>Logging in as customer</div>
  `);
    
    // Make API request to register endpoint
    $http.post('/api/v1/users', {
        username: $scope.username,
        password: $scope.password,
        firstName: $scope.firstname,
        lastName: $scope.lastname,
        role: 0
      })
      .then(function(response) {
        // On success, show success message and redirect
        $scope.successMessage = $sce.trustAsHtml(`
          <div><i class="fas fa-check" style="color: #4ad219;"></i></div>
          <div style="margin-left: 5px;">Welcome, ${$scope.username}!</div>
        `);
          window.location.href = 'login.html';
      })
      .catch(function(error) {
        // On error, show error message
        $scope.successMessage = $sce.trustAsHtml(`
          <div><i class="fas fa-exclamation-triangle" style="color: #ea2f1a;"></i></div>
          <div style="margin-left: 5px;">Registration failed</div>
        `);
        $scope.hasError = true;
      });
  };
});
