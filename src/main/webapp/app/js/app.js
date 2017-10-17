'use strict';

/**
 * Angular App.
 * 
 * @author nbrest
 */
var App = angular.module('myApp', [ "ngRoute" ]);

/**
 * Configure routes.
 */
App.config(function($routeProvider, $locationProvider) {
  $routeProvider
    .when("/", {
      templateUrl : "view/home.html"
    })
    .when("/dragonball/users", {
      templateUrl : "view/dragonball-users.html"
    })
    .when("/400", {
      templateUrl : "view/400.html"
    })
    .when("/403", {
      templateUrl : "view/403.html"
    })
    .when("/405", {
      templateUrl : "view/405.html"
    })
    .when("/409", {
      templateUrl : "view/409.html"
    })
    .when("/500", {
      templateUrl : "view/500.html"
    })
    .otherwise({
      templateUrl : "view/404.html"
    });

  $locationProvider.hashPrefix('');
});