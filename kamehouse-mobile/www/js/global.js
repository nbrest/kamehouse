
/**
 * Global functions for kamehouse-mobile app.
 * 
 * @author nbrest
 */
const cordovaManager = new CordovaManager();
const coreMobileUtils = new CoreMobileUtils();

function mainGlobalMobile() {
  logger.info("Started initializing mobile global");
  coreMobileUtils.loadHeaderAndFooter();
  cordovaManager.init();
} 

/**
 * Main generic functionality specific to the mobile app.
 */
function CoreMobileUtils() {
  this.loadHeaderAndFooter = loadHeaderAndFooter;

  function loadHeaderAndFooter() {
    fetchUtils.getScript("/js/header-footer/header-footer.js", () => renderHeaderAndFooter());
  }
}

/**
 * Entity to interact with cordova's api.
 */
function CordovaManager() {

  this.init = init;
  this.openBrowser = openBrowser;

  let inAppBrowserConfig = null;

  async function init() {
    logger.info("Initializing cordova manager");
    setCordovaMock();
    await loadConfig();
    moduleUtils.setModuleLoaded("cordovaManager");
  }

  async function loadConfig() {
    inAppBrowserConfig = JSON.parse(await fetchUtils.loadJsonConfig('/json/in-app-browser.json'));
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
   * Mock cordova when cordova is not available. For example when testing in a laptop's browser through apache httpd.
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