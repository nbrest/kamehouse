
/**
 * Functions for kamehouse-mobile app index page.
 * 
 * @author nbrest
 */
 function mainIndexMobile() {
  logger.info("Started initializing mobile app index");
  bannerUtils.setRandomAllBanner();
  moduleUtils.waitForModules(["cordovaManager", "debuggerHttpClient", "kameHouseMobileTabsManager"], () => {
    kameHouseMobileTabsManager.openTab('tab-home');
    setDeviceStartup();
  });
} 

/**
 * Set listeners for index page.
 */
function setDeviceStartup() {
  logger.info("Setting up deviceready");
  document.addEventListener("deviceready", onDeviceReady, false);
  
  /**
   * Open media server on startup.
   */
  function onDeviceReady() {
    cordovaManager.overrideWindowOpen();
    cordovaManager.openBrowser('mediaServer');
  }
}

/** Call main. */
$(document).ready(mainIndexMobile);