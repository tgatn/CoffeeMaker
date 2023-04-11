var app = angular.module('myApp', []);

app.controller('myCtrl', function($scope, $http) {
    $scope.currentOrders = [];

    $http.get('api/v1/currentOrders').then(function(response) {
        $scope.currentOrders = response.data;
    });

    $scope.logout = function() {
        $http.post('api/v1/logout').then(function(response) {
            window.location.href = '/login.html';
        });
    };
});