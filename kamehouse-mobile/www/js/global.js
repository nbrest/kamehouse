
/**
 * Global functions for kamehouse-mobile app.
 * 
 * @author nbrest
 */
const cordovaManager = new CordovaManager();

function mainGlobalMobile() {
  logger.info("Started initializing mobile global");
  cordovaManager.init();
} 

function CordovaManager() {

  this.init = init;
  this.openBrowser = openBrowser;

  let inAppBrowserConfig = null;

  async function init() {
    setCordovaMock();
    await loadConfig();
    moduleUtils.setModuleLoaded("cordovaManager");
  }

  async function loadConfig() {
    inAppBrowserConfig = JSON.parse(await fetchUtils.loadJsonConfig('/config/in-app-browser.json'));
    logger.trace("inAppBrowserConfig" + JSON.stringify(inAppBrowserConfig));
  }

  /**
   * Open inAppBrowser with
   */
  function openBrowser(urlLookup) {
    const urlEntry = inAppBrowserConfig.urls.find(urlEntity => urlEntity.name === urlLookup);
    openInAppBrowser(urlEntry.url);
  }

  /**
   * Open the InAppBrowser with the specified url.
   */
  function openInAppBrowser(url) {
    const target = inAppBrowserConfig.target;
    const options = inAppBrowserConfig.options;
    cordova.InAppBrowser.open(url, target, options);
  }

  /**
   * Mock cordova when it's not set.
   */
  function setCordovaMock() {
    const urlParams = new URLSearchParams(window.location.search);
    const mockCordova = urlParams.get('mockCordova');
    if (mockCordova) {
      logger.info("Mocking cordova object");
      cordova = new CordovaMock();
    }
    function CordovaMock() {
      this.InAppBrowser = new InAppBrowserMock();
      
      function InAppBrowserMock() {
        this.open = open;
    
        function open(url, target, options) {
          alert("cordova.InAppBrowser.open() call with:\n\nurl:\n" + url + "\n\ntarget:\n" + target + "\n\noptions:\n" + options);
        }
      }
    }  
  }
}

/** Call main. */
$(document).ready(mainGlobalMobile);