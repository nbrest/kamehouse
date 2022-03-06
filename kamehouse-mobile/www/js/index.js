
/**
 * Functions for kamehouse-mobile app index page.
 * 
 * @author nbrest
 */
 function mainIndexMobile() {
  logger.info("Started initializing mobile app index");
  bannerUtils.setRandomAllBanner();
  setDeviceStartup();
} 

/**
 * Set listeners for index page.
 */
function setDeviceStartup() {
  document.addEventListener("deviceready", onDeviceReady, false);
  function onDeviceReady() {
    window.open = cordova.InAppBrowser.open;
    cordovaManager.openBrowser('mediaServer');
  }
}

/** Call main. */
$(document).ready(mainIndexMobile);