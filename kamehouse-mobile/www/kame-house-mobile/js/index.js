
/**
 * Functions for kamehouse-mobile app index page.
 * 
 * @author nbrest
 */

function MobileAppIndex() {

  this.init = init;

  function init() {
    kameHouse.logger.info("Started initializing kamehouse-mobile app index page");
    kameHouse.util.banner.setRandomAllBanner();
    kameHouse.util.module.waitForModules(["kameHouseMobile", "kameHouseDebugger", "kameHouseMobileTabsManager"], () => {
      kameHouseMobileTabsManager.openTab('tab-config');
      setDeviceStartup();
    });
    kameHouse.util.module.waitForModules(["kameHouseDebugger"], () => {
      kameHouse.plugin.debugger.renderCustomDebugger("/kame-house-mobile/html-snippets/debug-mode-custom.html");
    });
  }

  /**
   * Set listeners for index page.
   */
  function setDeviceStartup() {
    kameHouse.logger.info("Setting up deviceready");
    document.addEventListener("deviceready", onDeviceReady, false);
  }

  /**
   * Open media server on startup.
   */
  function onDeviceReady() {
    kameHouse.extension.mobile.core.overrideWindowOpen();
    const openOnStartup = kameHouse.extension.mobile.configManager.getInAppBrowserConfig().openOnStartup;
    if (openOnStartup) {
      kameHouse.extension.mobile.core.openBrowser('vlc');
    }
  }
} 

/** Call main. */
$(document).ready(() => {new MobileAppIndex().init();});