var app = angular.module('myApp', []);

app.controller('myCtrl', function($scope, $http) {
    $scope.pastOrders = [];

    $http.get('/pastOrders').then(function(response) {
        $scope.pastOrders = response.data;
    });

    $scope.logout = function() {
        $http.post('api/v1/logout').then(function(response) {
            window.location.href = '/login.html';
        });
    };
});