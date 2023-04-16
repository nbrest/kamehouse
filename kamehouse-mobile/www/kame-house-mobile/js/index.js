
/**
 * Functions for kamehouse-mobile app index page.
 * 
 * @author nbrest
 */

function MobileAppIndex() {

  this.init = init;

  function init() {
    logger.info("Started initializing kamehouse-mobile app index page");
    bannerUtils.setRandomAllBanner();
    moduleUtils.waitForModules(["mobileConfigManager", "cordovaManager", "debuggerHttpClient", "kameHouseMobileTabsManager"], () => {
      kameHouseMobileTabsManager.openTab('tab-config');
      setDeviceStartup();
    });
    moduleUtils.waitForModules(["kameHouseDebugger"], () => {
      kameHouseDebugger.renderCustomDebugger("/kame-house-mobile/html-snippets/debug-mode-custom.html");
    });
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
} 

/** Call main. */
$(document).ready(() => {new MobileAppIndex().init();});