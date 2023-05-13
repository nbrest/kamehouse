
/**
 * Functions for kamehouse-mobile app settings page.
 * 
 * @author nbrest
 */
function KameHouseMobileSettings() {

  this.load = load;
  
  function load() {
    kameHouse.logger.info("Started initializing kamehouse-mobile app settings page");
    kameHouse.util.banner.setRandomAllBanner();
    kameHouse.util.module.waitForModules(["mobileTabsManager"], () => {
      kameHouse.plugin.mobileTabsManager.openTab('tab-backend');
    });
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger", "kameHouseMobile", "mobileTabsManager"], () => {
      handleUrlParams();
      kameHouse.plugin.debugger.renderCustomDebugger("/kame-house-mobile/kamehouse-mobile/plugin/html/settings-debug-mode-custom.html", () => {
        kameHouse.extension.mobile.core.setMobileBuildVersion();
      });
    });
  }

  function handleUrlParams() {
    kameHouse.logger.info("Settings query string: " + window.location.search);
    const urlParams = new URLSearchParams(window.location.search);
    const requestTimeout = urlParams.get('requestTimeout');
    if (!kameHouse.core.isEmpty(requestTimeout)) {
      const message = "Unable to connect to the backend. Try again later";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
      kameHouse.plugin.modal.basicModal.openAutoCloseable(message, 5000);
      return;
    }
    const unauthorizedPageAccess = urlParams.get('unauthorizedPageAccess');
    if (!kameHouse.core.isEmpty(unauthorizedPageAccess)) {
      const message = "User is not authorized to access the page. Login with an authorized user";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
      kameHouse.plugin.modal.basicModal.openAutoCloseable(message, 5000);
      return;
    }
  }
} 

$(document).ready(() => {
  kameHouse.addExtension("mobileSettings", new KameHouseMobileSettings());
});