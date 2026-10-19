/**
 * KameHouse ZTV lock screen applet.
 *
 * Exposes the native android foreground service that renders the ZTV player
 * controls in the lock screen notification.
 *
 * @author nbrest
 */
var exec = require('cordova/exec');

var ZtvLockscreen = {

  /**
   * Start the ZTV lock screen applet.
   *
   * @param {Object} config with the backend server url, credentials, skipSslCheck
   *                        and the ztv player hostname.
   * @param {Function} successCallback
   * @param {Function} errorCallback
   */
  start: function (config, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'ZtvLockscreen', 'start', [config || {}]);
  },

  /**
   * Stop the ZTV lock screen applet and remove its notification.
   *
   * @param {Function} successCallback
   * @param {Function} errorCallback
   */
  stop: function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'ZtvLockscreen', 'stop', []);
  },

  /**
   * Check if the ZTV lock screen applet is currently running.
   *
   * @param {Function} successCallback receives true if the applet is running.
   * @param {Function} errorCallback
   */
  isRunning: function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'ZtvLockscreen', 'isRunning', []);
  }
};

module.exports = ZtvLockscreen;
