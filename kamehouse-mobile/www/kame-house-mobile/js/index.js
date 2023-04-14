
/**
 * Functions for kamehouse-mobile app index page.
 * 
 * @author nbrest
 */
const coreMobileUtils = new CoreMobileUtils();

function mainIndexMobile() {
  logger.info("Started initializing mobile app index");
  bannerUtils.setRandomAllBanner();
  moduleUtils.waitForModules(["cordova", "debuggerHttpClient"], () => {
    coreMobileUtils.loadHeader();
  });
  moduleUtils.waitForModules(["mobileConfigManager", "cordovaManager", "debuggerHttpClient", "kameHouseMobileTabsManager"], () => {
    kameHouseMobileTabsManager.openTab('tab-home');
    setDeviceStartup();
  });
  moduleUtils.waitForModules(["kameHouseDebugger"], () => {
    kameHouseDebugger.renderCustomDebugger("/kame-house-mobile/html-snippets/debug-mode-custom.html");
  });
} 

/**
 * Main generic functionality specific to the mobile app.
 */
function CoreMobileUtils() {
  this.loadHeader = loadHeader;

  function loadHeader() {
    fetchUtils.getScript("/kame-house-mobile/js/header.js", () => renderMobileHeader());
  }
}

/**
 * Set listeners for index page.
 */
function setDeviceStartup() {
  logger.info("Setting up deviceready");
  document.addEventListener("deviceready", onDeviceReady, false);
}

/**
 * Open media server on startup.
 */
function onDeviceReady() {
  cordovaManager.overrideWindowOpen();
  const openOnStartup = mobileConfigManager.getInAppBrowserConfig().openOnStartup;
  if (openOnStartup) {
    cordovaManager.openBrowser('vlc');
  }
}

/** Call main. */
$(document).ready(mainIndexMobile);