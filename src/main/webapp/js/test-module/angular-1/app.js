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
      templateUrl : "/kame-house/view/test-module/angular-1/home.html"
    })
    .when("/dragonball/users", {
      templateUrl : "/kame-house/view/test-module/angular-1/dragonball-users.html"
    })
    .when("/400", {
      templateUrl : "/kame-house/view/test-module/angular-1/400.html"
    })
    .when("/403", {
      templateUrl : "/kame-house/view/test-module/angular-1/403.html"
    })
    .when("/405", {
      templateUrl : "/kame-house/view/test-module/angular-1/405.html"
    })
    .when("/409", {
      templateUrl : "/kame-house/view/test-module/angular-1/409.html"
    })
    .when("/500", {
      templateUrl : "/kame-house/view/test-module/angular-1/500.html"
    })
    .otherwise({
      templateUrl : "/kame-house/view/test-module/angular-1/404.html"
    });

  $locationProvider.hashPrefix('');
});