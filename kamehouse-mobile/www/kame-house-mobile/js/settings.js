
/**
 * Functions for kamehouse-mobile app settings page.
 * 
 * @author nbrest
 */
class KameHouseMobileSettings {
  
  /**
   * Load the kamehouse mobile settings extension.
   */
  load() {
    kameHouse.logger.info("Started initializing kamehouse-mobile app settings page");
    kameHouse.util.banner.setRandomAllBanner();
    kameHouse.util.module.waitForModules(["mobileTabsManager"], () => {
      kameHouse.plugin.mobileTabsManager.openTab('tab-backend');
      kameHouse.core.setButtonBackgrounds();
      kameHouse.core.disablePageRefreshOnForms();
    });
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger", "kameHouseMobile", "mobileTabsManager"], () => {
      this.#handleUrlParams();
      kameHouse.plugin.debugger.renderCustomDebugger("/kame-house-mobile/kamehouse-mobile/plugin/html/settings-debug-mode-custom.html", () => {
        kameHouse.extension.mobile.core.setMobileBuildVersion();
        kameHouse.core.setButtonBackgrounds();
      });
    });
  }

  /**
   * Handle settings url parameters.
   */
  #handleUrlParams() {
    kameHouse.logger.debug("Settings query string: " + window.location.search);
    const urlParams = new URLSearchParams(window.location.search);
    const requestTimeout = urlParams.get('requestTimeout');
    const sslError = urlParams.get('sslError');
    const responseCode = urlParams.get('responseCode');
    if (!kameHouse.core.isEmpty(requestTimeout)) {
      const message = "Unable to connect to the backend. Try again later. responseCode: " + responseCode;
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
      kameHouse.plugin.modal.basicModal.openAutoCloseable(message, 5000);
      return;
    }
    if (!kameHouse.core.isEmpty(sslError)) {
      const message = "SSL error connecting to the backend. Check SSL certificate on server or skip ssl validation. responseCode: " + responseCode;
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
      kameHouse.plugin.modal.basicModal.openAutoCloseable(message, 5000);
      return;
    }
    const unauthorizedPageAccess = urlParams.get('unauthorizedPageAccess');
    if (!kameHouse.core.isEmpty(unauthorizedPageAccess)) {
      const message = "User is not authorized to access the page. Login with an authorized user. responseCode: " + responseCode;
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
      kameHouse.plugin.modal.basicModal.openAutoCloseable(message, 5000);
      return;
    }
  }
} 

kameHouse.ready(() => {
  kameHouse.addExtension("mobileSettings", new KameHouseMobileSettings());
});