/**
 * Login manager.
 * 
 * @author nbrest
 */
function LoginManager() {

  this.load = load;

  /**
   * Load the login manager extension. 
   */
  function load() {
    kameHouse.logger.info("Started initializing LoginManager");
    kameHouse.util.banner.setRandomAllBanner();
    setAlertMessages();
  }

  /**
   * Set alert messages.
   */
  function setAlertMessages() {
    const urlParams = new URLSearchParams(window.location.search);
    const unauthorizedPageAccess = urlParams.get('unauthorizedPageAccess');
    const error = urlParams.get('error');
    const logout = urlParams.get('logout');
    if (!kameHouse.core.isEmpty(unauthorizedPageAccess)) {
      const element = document.getElementById("login-alert-group-unauthorized");
      kameHouse.util.dom.classListRemove(element, "hidden-kh");
    }
    if (!kameHouse.core.isEmpty(error)) {
      const element = document.getElementById("login-alert-group-error");
      kameHouse.util.dom.classListRemove(element, "hidden-kh");
    }
    if (!kameHouse.core.isEmpty(logout)) {
      const element = document.getElementById("login-alert-group-logout");
      kameHouse.util.dom.classListRemove(element, "hidden-kh");
    }
  }
}

$(document).ready(() => {kameHouse.addExtension("loginManager", new LoginManager())});