
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
  let inAppBrowserConfig = null;

  this.init = init;
  this.openBrowser = openBrowser;
  this.overrideWindowOpen = overrideWindowOpen;

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
   * Override the default window.open to open the inappbrowser.
   */
  function overrideWindowOpen() {
    window.open = cordova.InAppBrowser.open;
  }

  /**
   * Open inAppBrowser with
   */
  function openBrowser(urlLookup) {
    const urlEntry = inAppBrowserConfig.urls.find(urlEntity => urlEntity.name === urlLookup);
    openInAppBrowser(urlEntry);
  }

  /**
   * Open the InAppBrowser with the specified url.
   */
  function openInAppBrowser(urlEntry) {
    logger.info("Start loading url " + urlEntry.url);
    const target = inAppBrowserConfig.target;
    const options = inAppBrowserConfig.options;
    let inAppBrowserInstance = cordova.InAppBrowser.open(urlEntry.url, target, options);
    basicKamehouseModal.setHtml(getOpenBrowserMessage(urlEntry));
    basicKamehouseModal.setErrorMessage(false);
    basicKamehouseModal.open();
    setInAppBrowserInstanceListeners(inAppBrowserInstance, urlEntry);
  }

  /**
   * Get the open browser message for the modal.
   */
  function getOpenBrowserMessage(urlEntry) {
    const openBrowserMessage = domUtils.getSpan({}, "Opening " + urlEntry.name);
    domUtils.append(openBrowserMessage, domUtils.getBr());
    domUtils.append(openBrowserMessage, domUtils.getBr());
    domUtils.append(openBrowserMessage, urlEntry.url);
    domUtils.append(openBrowserMessage, domUtils.getBr());
    domUtils.append(openBrowserMessage, domUtils.getBr());
    domUtils.append(openBrowserMessage, "Please Wait ...");
    return openBrowserMessage;
  }

  /**
   * Set listeners for the events handled by the InAppBrowser.
   */
  function setInAppBrowserInstanceListeners(inAppBrowserInstance, urlEntry) {
    inAppBrowserInstance.addEventListener('loadstop', (params) => {
      logger.info("Executing event loadstop for url: '" + urlEntry.url + "'. with params " + JSON.stringify(params));
      inAppBrowserInstance.show();
      
    });

    inAppBrowserInstance.addEventListener('loaderror', (params) => {
      const errorMessage = "Error loading url '" + urlEntry.url + "'. with params " + JSON.stringify(params);
      logger.error("Executing event loaderror. " + errorMessage);
      basicKamehouseModal.setHtml(errorMessage);
      basicKamehouseModal.setErrorMessage(true);
      basicKamehouseModal.open();
      inAppBrowserInstance.close();
    });

    inAppBrowserInstance.addEventListener('loadstart', (params) => {
      logger.info("Executing event loadstart for url: '" + urlEntry.url + "'. with params " + JSON.stringify(params));
    });

    inAppBrowserInstance.addEventListener('exit', (params) => {
      logger.info("Executing event exit for url: '" + urlEntry.url + "'. with params " + JSON.stringify(params));
      if (!basicKamehouseModal.isErrorMessage()) {
        basicKamehouseModal.close(); 
        basicKamehouseModal.reset();
      }
    });

    inAppBrowserInstance.addEventListener('message', (params) => {
      logger.info("Executing event message for url: '" + urlEntry.url + "'. with params " + JSON.stringify(params));
    });
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
          logger.info("Called open in InAppBrowserMock with url " + url);
          setTimeout(() => {
            alert("cordova.InAppBrowser.open() call with:\n\nurl:\n" + url + "\n\ntarget:\n" + target + "\n\noptions:\n" + options);
          }, 100);
          setTimeout(() => {
            logger.info("Simulating successful exit event from InAppBrowserInstanceMock closing the modal");
            basicKamehouseModal.close(); 
            basicKamehouseModal.reset();
          }, 3000);
          return new InAppBrowserInstanceMock();
        }
      }

      function InAppBrowserInstanceMock() {
        this.addEventListener = addEventListener;
        this.close = close;
        this.show = show;
    
        function addEventListener(eventName, callback) {
          logger.info("Called addEventListener on the InAppBrowserInstanceMock for event " + eventName);
        }

        function show() {
          logger.info("Called show on the InAppBrowserInstanceMock");
        }

        function close() {
          logger.info("Called close on the InAppBrowserInstanceMock");
        }
      }
    }    
  }
}

/** Call main. */
$(document).ready(mainGlobalMobile);