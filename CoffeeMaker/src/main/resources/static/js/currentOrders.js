var app = angular.module('myApp', ['ngCookies']);

app.controller('myCtrl', function($scope, $http, $cookies) {
	const orders = [
  {
    date: new Date('2023-04-11T10:25:00Z'),
    orderId: '1001',
    items: [
      {
        name: 'Latte',
        quantity: 2
      }
    ],
    total: 6.50,
    orderStatus: 'In Progress'
  },
  {
    date: new Date('2023-04-10T13:45:00Z'),
    orderId: '1002',
    items: [
      {
        name: 'Espresso',
        quantity: 1
      },
      {
        name: 'Cappuccino',
        quantity: 1
      }
    ],
    total: 5.00,
    orderStatus: 'Completed'
  },
  {
    date: new Date('2023-04-09T08:15:00Z'),
    orderId: '1003',
    items: [
      {
        name: 'Americano',
        quantity: 1
      }
    ],
    total: 3.00,
    orderStatus: 'Canceled'
  },
   {
    date: new Date('2023-04-09T08:15:00Z'),
    orderId: '1003',
    items: [
      {
        name: 'Americano',
        quantity: 1
      }
    ],
    total: 3.00,
    orderStatus: 'Canceled'
  }
];
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