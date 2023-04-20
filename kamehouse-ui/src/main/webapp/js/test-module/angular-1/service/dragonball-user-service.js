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
    kameHouse.util.mobile.exec(
      () => {
        $http.get(REST_SERVICE_URI).then(
          (response) => deferred.resolve(response.data),
          (errResponse) => deferred.reject(errResponse)
        );
      },
      () => {
        kameHouse.http.get(REST_SERVICE_URI, null, null, 
          (responseBody, responseCode, responseDescription, responseHeaders) => deferred.resolve(responseBody),
          (responseBody, responseCode, responseDescription, responseHeaders) => deferred.reject(responseBody)
        );
      }
    );
    return deferred.promise;
  }

  /**
   * Create a DragonBallUser.
   */
  function createDragonBallUser(dragonBallUser) {
    const deferred = $q.defer();
    kameHouse.util.mobile.exec(
      () => {
        const config = generateConfig();
        $http.post(REST_SERVICE_URI, dragonBallUser, config).then(
          (response) => deferred.resolve(response.data),
          (errResponse) => deferred.reject(errResponse)
        );
      },
      () => {
        kameHouse.http.post(REST_SERVICE_URI, kameHouse.http.getApplicationJsonHeaders(), dragonBallUser, 
          (responseBody, responseCode, responseDescription, responseHeaders) => deferred.resolve(responseBody),
          (responseBody, responseCode, responseDescription, responseHeaders) => deferred.reject(responseBody)
        );
      }
    );
    return deferred.promise;
  }
  
  /**
   * Updates a DragonBallUser.
   */
  function updateDragonBallUser(dragonBallUser, id) {
    const deferred = $q.defer();
    kameHouse.util.mobile.exec(
      () => {
        const config = generateConfig();
        $http.put(REST_SERVICE_URI + id, dragonBallUser, config).then(
          (response) => deferred.resolve(response.data),
          (errResponse) => deferred.reject(errResponse)
        );
      },
      () => {
        kameHouse.http.put(REST_SERVICE_URI + id, kameHouse.http.getApplicationJsonHeaders(), dragonBallUser, 
          (responseBody, responseCode, responseDescription, responseHeaders) => deferred.resolve(responseBody),
          (responseBody, responseCode, responseDescription, responseHeaders) => deferred.reject(responseBody)
        );
      }
    );
    return deferred.promise;
  }

  /**
   * Deletes a DragonBallUser.
   */
  function deleteDragonBallUser(id) {
    const deferred = $q.defer();
    kameHouse.util.mobile.exec(
      () => {
        const config = generateConfig();
        $http.delete(REST_SERVICE_URI + id, config).then(
          (response) => deferred.resolve(response.data),
          (errResponse) => deferred.reject(errResponse)
        );
      },
      () => {
        kameHouse.http.delete(REST_SERVICE_URI + id, null, null, 
          (responseBody, responseCode, responseDescription, responseHeaders) => deferred.resolve(responseBody),
          (responseBody, responseCode, responseDescription, responseHeaders) => deferred.reject(responseBody)
        );
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