/**
 * Groot login manager. 
 * 
 * @author nbrest
 */
class GrootLogin {

  /**
   * Load the groot login manager extension.
   */
  load() {
    kameHouse.util.banner.setRandomAllBanner(null);
    const urlParams = new URLSearchParams(window.location.search);
    const referrer = urlParams.get('referrer');
    if (!kameHouse.core.isEmpty(referrer)) {
      kameHouse.util.dom.setValueById('login-referrer', referrer);
    }
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("grootLogin", new GrootLogin());
});