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
      templateUrl: "/kame-house/test-module/angular-1/view/home.html"
    })
    .when("/dragonball/users", {
      templateUrl: "/kame-house/test-module/angular-1/view/dragonball-users.html"
    })
    .when("/400", {
      templateUrl: "/kame-house/test-module/angular-1/view/400.html"
    })
    .when("/403", {
      templateUrl: "/kame-house/test-module/angular-1/view/403.html"
    })
    .when("/405", {
      templateUrl: "/kame-house/test-module/angular-1/view/405.html"
    })
    .when("/409", {
      templateUrl: "/kame-house/test-module/angular-1/view/409.html"
    })
    .when("/500", {
      templateUrl: "/kame-house/test-module/angular-1/view/500.html"
    })
    .otherwise({
      templateUrl: "/kame-house/test-module/angular-1/view/404.html"
    });

  $locationProvider.hashPrefix('');
});