var app = angular.module('myApp', ['ngCookies']);

app.controller('myCtrl', function($scope, $http, $cookies) {
	$scope.customerName = $cookies.get('username');
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