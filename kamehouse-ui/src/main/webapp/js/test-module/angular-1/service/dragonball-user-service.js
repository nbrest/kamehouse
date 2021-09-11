'use strict';

/**
 * DragonBallUserService.
 * 
 * @author nbrest
 */
angular.module('myApp').service('dragonBallUserService', [ '$http', '$q', function($http, $q) {

  const REST_SERVICE_URI = '/kame-house-testmodule/api/v1/test-module/dragonball/users/';

  const dragonBallUserService = {
    fetchAllDragonBallUsers: fetchAllDragonBallUsers,
    createDragonBallUser: createDragonBallUser,
    updateDragonBallUser: updateDragonBallUser,
    deleteDragonBallUser: deleteDragonBallUser
  };
  return dragonBallUserService;

  /**
   * Get all DragonBallUsers.
   */
  function fetchAllDragonBallUsers() {
    const deferred = $q.defer();
    $http.get(REST_SERVICE_URI)
      .then(
        (response) => deferred.resolve(response.data),
        (errResponse) => {
          console.error('Error while fetching all DragonBallUsers');
          deferred.reject(errResponse);
        }
    );
    return deferred.promise;
  }

  /**
   * Create a DragonBallUser.
   */
  function createDragonBallUser(dragonBallUser) {
    const deferred = $q.defer();
    const config = generateConfig();
    $http.post(REST_SERVICE_URI, dragonBallUser, config)
      .then(
        (response) => deferred.resolve(response.data),
        (errResponse) => {
          console.error('Error while creating DragonBallUser');
          deferred.reject(errResponse);
        }
    );
    return deferred.promise;
  }
  
  /**
   * Updates a DragonBallUser.
   */
  function updateDragonBallUser(dragonBallUser, id) {
    const deferred = $q.defer();
    const config = generateConfig();
    $http.put(REST_SERVICE_URI + id, dragonBallUser, config)
      .then(
        (response) => deferred.resolve(response.data),
        (errResponse) => {
          console.error('Error while updating DragonBallUser');
          deferred.reject(errResponse);
        }
    );
    return deferred.promise;
  }

  /**
   * Deletes a DragonBallUser.
   */
  function deleteDragonBallUser(id) {
    const deferred = $q.defer();
    const config = generateConfig();
    $http.delete(REST_SERVICE_URI + id, config)
      .then(
        (response) => deferred.resolve(response.data),
        (errResponse) => {
          console.error('Error while deleting DragonBallUser');
          deferred.reject(errResponse);
        }
    );
    return deferred.promise;
  }

  /**
   * Generate config object to pass to the http requests.
   */
  function generateConfig() {
    const config = {};
    config.headers = {};
    return config;
  }
}]);