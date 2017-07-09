'use strict';

angular.module('myApp').factory('dragonBallUserService', [ '$http', '$q', function($http, $q) {

  var REST_SERVICE_URI = '/base-app/api/v1/dragonball/users/';

  var factory = {
    fetchAllDragonBallUsers : fetchAllDragonBallUsers,
    createDragonBallUser : createDragonBallUser,
    updateDragonBallUser : updateDragonBallUser,
    deleteDragonBallUser : deleteDragonBallUser
  };

  return factory;

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

  function createDragonBallUser(dragonBallUser) {
    var deferred = $q.defer();
    $http.post(REST_SERVICE_URI, dragonBallUser)
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

  function updateDragonBallUser(dragonBallUser, id) {
    var deferred = $q.defer();
    $http.put(REST_SERVICE_URI + id, dragonBallUser)
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

  function deleteDragonBallUser(id) {
    var deferred = $q.defer();
    $http.delete(REST_SERVICE_URI + id)
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

} ]);