'use strict';

/**
 * DragonBallUserService.
 * 
 * @author nbrest
 */
angular.module('myApp').service('dragonBallUserService', [ '$q', function($q) {

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
    const config = kameHouse.http.getConfig();
    kameHouse.http.get(config, REST_SERVICE_URI, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => deferred.resolve(responseBody),
      (responseBody, responseCode, responseDescription, responseHeaders) => deferred.reject(responseBody)
    );
    return deferred.promise;
  }

  /**
   * Create a DragonBallUser.
   */
  function createDragonBallUser(dragonBallUser) {
    const deferred = $q.defer();
    const config = kameHouse.http.getConfig();
    kameHouse.http.post(config, REST_SERVICE_URI, kameHouse.http.getApplicationJsonHeaders(), dragonBallUser, 
          (responseBody, responseCode, responseDescription, responseHeaders) => deferred.resolve(responseBody),
          (responseBody, responseCode, responseDescription, responseHeaders) => deferred.reject(responseBody)
        );
    return deferred.promise;
  }
  
  /**
   * Updates a DragonBallUser.
   */
  function updateDragonBallUser(dragonBallUser, id) {
    const deferred = $q.defer();
    const config = kameHouse.http.getConfig();
    kameHouse.http.put(config, REST_SERVICE_URI + id, kameHouse.http.getApplicationJsonHeaders(), dragonBallUser, 
          (responseBody, responseCode, responseDescription, responseHeaders) => deferred.resolve(responseBody),
          (responseBody, responseCode, responseDescription, responseHeaders) => deferred.reject(responseBody)
        );
    return deferred.promise;
  }

  /**
   * Deletes a DragonBallUser.
   */
  function deleteDragonBallUser(id) {
    const deferred = $q.defer();
    const config = kameHouse.http.getConfig();
    kameHouse.http.delete(config, REST_SERVICE_URI + id, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => deferred.resolve(responseBody),
      (responseBody, responseCode, responseDescription, responseHeaders) => deferred.reject(responseBody)
    );
    return deferred.promise;
  }
}]);