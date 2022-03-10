
/**
 * Functions for kamehouse-mobile app index page.
 * 
 * @author nbrest
 */
 function mainIndexMobile() {
  logger.info("Started initializing mobile app index");
  bannerUtils.setRandomAllBanner();
  moduleUtils.waitForModules(["cordovaManager", "debuggerHttpClient", "kameHouseMobileTabsManager"], () => {
    setDeviceStartup();
    kameHouseMobileTabsManager.openTab('tab-home');
  });
} 

/**
 * Set listeners for index page.
 */
function setDeviceStartup() {
  logger.info("Setting device startup");
  document.addEventListener("deviceready", onDeviceReady, false);
  function onDeviceReady() {
    window.open = cordova.InAppBrowser.open;
    cordovaManager.openBrowser('mediaServer');
  }
}

/** Call main. */
$(document).ready(mainIndexMobile);