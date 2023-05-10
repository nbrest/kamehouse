
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
    kameHouse.util.module.waitForModules([ "mobileTabsManager"], () => {
      kameHouse.plugin.mobileTabsManager.openTab('tab-backend');
    });
    kameHouse.util.module.waitForModules(["kameHouseMobile", "mobileTabsManager"], () => {
      handleUrlParams();
      kameHouse.extension.mobile.core.setMobileBuildVersion();
    });
  }

  function handleUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const unauthorizedPageAccess = urlParams.get('unauthorizedPageAccess');
    if (!kameHouse.core.isEmpty(unauthorizedPageAccess)) {
      kameHouse.logger.info("Tried to access a page without proper authorization. Redirected to settings page");
      kameHouse.plugin.modal.basicModal.openAutoCloseable("User is not authorized to access the page. Login with an authorized user", 5000);
    }
  }
} 

$(document).ready(() => {
  kameHouse.addExtension("mobileSettings", new KameHouseMobileSettings());
});