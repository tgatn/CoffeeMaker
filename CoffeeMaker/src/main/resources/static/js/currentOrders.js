var app = angular.module('myApp', ['ngCookies']);

app.controller('myCtrl', function($scope, $http, $cookies) {
	const orders = [
  {
    "orderId": "1",
    "date": "2023-04-11T09:15:30Z",
    "items": "Cappuccino, Latte"
  },
  {
    "orderId": "2",
    "date": "2023-04-10T13:45:00Z",
    "items": "Cappuccino, Latte"
  },
  {
    "orderId": "3",
    "date": "2023-04-09T19:30:15Z",
    "items": "Cappuccino, Latte"
  }
]

	$scope.customerName = $cookies.get('username');
    $scope.currentOrders = [];
    
    $http.get('api/v1/currentOrders').then(function(response) {
        $scope.currentOrders = response.data;
    });
    
    $scope.currentOrders = orders;
    

    $scope.logout = function() {
        $http.post('api/v1/logout').then(function(response) {
            window.location.href = '/login.html';
        });
    };
});