
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
    kameHouse.util.module.waitForModules(["kameHouseMobile", "kameHouseDebugger", "mobileTabsManager"], () => {
      kameHouse.plugin.mobileTabsManager.openTab('tab-backend');
      kameHouse.extension.mobile.core.setMobileBuildVersion();
    });
  }
} 

$(document).ready(() => {
  kameHouse.addExtension("mobileSettings", new KameHouseMobileSettings());
});