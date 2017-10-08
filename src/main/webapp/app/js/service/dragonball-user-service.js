'use strict';

angular.module('myApp').service('dragonBallUserService', [ '$http', '$q', function($http, $q) {

  var REST_SERVICE_URI = '/kame-house/api/v1/dragonball/users/';

  var dragonBallUserService = {
    fetchAllDragonBallUsers : fetchAllDragonBallUsers,
    createDragonBallUser : createDragonBallUser,
    updateDragonBallUser : updateDragonBallUser,
    deleteDragonBallUser : deleteDragonBallUser
  };

  return dragonBallUserService;

  function fetchAllDragonBallUsers() {
    var deferred = $q.defer();
    $http.get(REST_SERVICE_URI)
      .then(
        function(response) {
          deferred.resolve(response.data);
        },
        function(errResponse) {
          console.error('Error while fetching all DragonBallUsers');
          deferred.reject(errResponse);
        }
    );
    return deferred.promise;
  }

  function createDragonBallUser(dragonBallUser, csrf) {
    var deferred = $q.defer();
    var config = generateConfig(csrf);
    $http.post(REST_SERVICE_URI, dragonBallUser, config)
      .then(
        function(response) {
          deferred.resolve(response.data);
        },
        function(errResponse) {
          console.error('Error while creating DragonBallUser');
          deferred.reject(errResponse);
        }
    );
    return deferred.promise;
  }

  function updateDragonBallUser(dragonBallUser, id, csrf) {
    var deferred = $q.defer();
    var config = generateConfig(csrf);
    $http.put(REST_SERVICE_URI + id, dragonBallUser, config)
      .then(
        function(response) {
          deferred.resolve(response.data);
        },
        function(errResponse) {
          console.error('Error while updating DragonBallUser');
          deferred.reject(errResponse);
        }
    );
    return deferred.promise;
  }

  function deleteDragonBallUser(id, csrf) {
    var deferred = $q.defer();
    var config = generateConfig(csrf);
    $http.delete(REST_SERVICE_URI + id, config)
      .then(
        function(response) {
          deferred.resolve(response.data);
        },
        function(errResponse) {
          console.error('Error while deleting DragonBallUser');
          deferred.reject(errResponse);
        }
    );
    return deferred.promise;
  }

  function generateConfig(csrf) {
    var config = {};
    config.headers = {};
    config.headers[csrf.header] = csrf.token;
    //console.log("config" + JSON.stringify(config));
    return config;
  }
} ]);