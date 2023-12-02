/**
 * Groot login manager. 
 */
function GrootLogin() {
  this.load = load;

  /**
   * Load the groot login manager extension.
   */
  function load() {
    kameHouse.util.banner.setRandomAllBanner();
    const urlParams = new URLSearchParams(window.location.search);
    const referrer = urlParams.get('referrer');
    if (!kameHouse.core.isEmpty(referrer)) {
      kameHouse.util.dom.setValue(document.getElementById('login-referrer'), referrer);
    }
  }
}

$(document).ready(() => {
  kameHouse.addExtension("grootLogin", new GrootLogin());
});