
/**
 * Functions for kamehouse-mobile app index page.
 * 
 * @author nbrest
 */
function KameHouseMobileSettings() {

  this.load = load;

  function load() {
    kameHouse.logger.info("Started initializing kamehouse-mobile app index page");
    kameHouse.util.banner.setRandomAllBanner();
    kameHouse.util.module.waitForModules(["kameHouseMobile", "kameHouseDebugger", "mobileTabsManager"], () => {
      kameHouse.plugin.mobileTabsManager.openTab('tab-backend');
      setDeviceStartup();
    });
  }

  /**
   * Set listeners for settings page.
   */
  function setDeviceStartup() {
    kameHouse.logger.info("Setting up deviceready");
    document.addEventListener("deviceready", onDeviceReady, false);
  }

  function onDeviceReady() {
    kameHouse.logger.trace("Add any device startup logic needed here");
  }
} 

$(document).ready(() => {
  kameHouse.addExtension("mobileSettings", new KameHouseMobileSettings());
});