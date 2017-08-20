'use strict';

var App = angular.module('myApp', [ "ngRoute" ]);

App.config(function($routeProvider, $locationProvider) {
  $routeProvider
    .when("/", {
      templateUrl : "view/home.html"
    })
    .when("/dragonball/users", {
      templateUrl : "view/dragonball-users.html"
    })
    .otherwise({
      templateUrl : "view/404.html"
    });

  $locationProvider.hashPrefix('');
});