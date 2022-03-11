
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
  moduleUtils.waitForModules([ "debuggerHttpClient"], () => {
    cordovaManager.init();
  });
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

  const inAppBrowserConfigManager = new InAppBrowserConfigManager();

  this.init = init;
  this.openBrowser = openBrowser;
  this.overrideWindowOpen = overrideWindowOpen;
  this.getInAppBrowserConfig = inAppBrowserConfigManager.getInAppBrowserConfig;
  this.setInAppBrowserConfig = inAppBrowserConfigManager.setInAppBrowserConfig;

  this.createInAppBrowserConfigFile = inAppBrowserConfigManager.createInAppBrowserConfigFile;
  this.writeInAppBrowserConfigFile = inAppBrowserConfigManager.writeInAppBrowserConfigFile;
  this.readInAppBrowserConfigFile = inAppBrowserConfigManager.readInAppBrowserConfigFile;
  this.deleteInAppBrowserConfigFile = inAppBrowserConfigManager.deleteInAppBrowserConfigFile;

  async function init() {
    logger.info("Initializing cordova manager");
    setCordovaMock();
    await inAppBrowserConfigManager.loadDefaultConfig();
    moduleUtils.setModuleLoaded("cordovaManager");
    //testFileManagement();
  }

  /**
   * Test file operations.
   */
  function testFileManagement() {
    setTimeout(() => {inAppBrowserConfigManager.createInAppBrowserConfigFile();}, 1000);
    setTimeout(() => {inAppBrowserConfigManager.writeInAppBrowserConfigFile();}, 10000);
    setTimeout(() => {inAppBrowserConfigManager.readInAppBrowserConfigFile();}, 15000);
    setTimeout(() => {inAppBrowserConfigManager.deleteInAppBrowserConfigFile();}, 20000);
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
    const urlEntry = inAppBrowserConfigManager.getInAppBrowserConfig().urls.find(urlEntity => urlEntity.name === urlLookup);
    openInAppBrowser(urlEntry);
  }

  /**
   * Open the InAppBrowser with the specified url.
   */
  function openInAppBrowser(urlEntry) {
    logger.info("Start loading url " + urlEntry.url);
    const target = inAppBrowserConfigManager.getInAppBrowserConfig().target;
    const options = inAppBrowserConfigManager.getInAppBrowserConfig().options;
    const inAppBrowserInstance = cordova.InAppBrowser.open(urlEntry.url, target, options);
    basicKamehouseModal.setHtml(getOpenBrowserMessage(urlEntry));
    basicKamehouseModal.setErrorMessage(false);
    basicKamehouseModal.open();
    setInAppBrowserEventListeners(inAppBrowserInstance, urlEntry);
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
  function setInAppBrowserEventListeners(inAppBrowserInstance, urlEntry) {

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

    /**
     * Mock cordova api.
     */
    function CordovaMock() {

      this.InAppBrowser = new InAppBrowserMock();
      
      /**
       * Mock of an InAppBrowser.
       */
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

      /**
       * Mock of a InAppBrowserInstance.
       */
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

/**
 * Manage the configuration of the InAppBrowser, including the configuration of the servers.
 */
function InAppBrowserConfigManager() {
  
  this.getInAppBrowserConfig = getInAppBrowserConfig;
  this.setInAppBrowserConfig = setInAppBrowserConfig;
  this.loadDefaultConfig = loadDefaultConfig;
  this.createInAppBrowserConfigFile = createInAppBrowserConfigFile;
  this.writeInAppBrowserConfigFile = writeInAppBrowserConfigFile;
  this.readInAppBrowserConfigFile = readInAppBrowserConfigFile;
  this.deleteInAppBrowserConfigFile = deleteInAppBrowserConfigFile;

  const inAppBrowserConfigFile = "in-app-browser-config.json";
  const inAppBrowserConfigFileType = window.PERSISTENT;
  const inAppBrowserConfigFileSize = 5*1024*1024; //50 mb

  inAppBrowserConfig = null;
  inAppBrowserDefaultConfig = null;

  async function loadDefaultConfig() {
    inAppBrowserDefaultConfig = JSON.parse(await fetchUtils.loadJsonConfig('/json/in-app-browser-config.json'));
    logger.info("inAppBrowserConfig defaults: " + JSON.stringify(inAppBrowserDefaultConfig));
    if (inAppBrowserConfig == null) {
      logger.info("inAppBrowserConfig not set. Initializing from default values");
      inAppBrowserConfig = inAppBrowserDefaultConfig;
    }
  }

  function getInAppBrowserConfig() {
    return inAppBrowserConfig;
  }

  function setInAppBrowserConfig(val) {
    inAppBrowserConfig = val;
  }

  /**
   * Create in app browser config file.
   */
  function createInAppBrowserConfigFile() {
    logger.info("createInAppBrowserConfigFile");
    try {
      window.requestFileSystem(inAppBrowserConfigFileType, inAppBrowserConfigFileSize, successCallback, errorCallback);

      function successCallback(fs) {
        fs.root.getFile(inAppBrowserConfigFile, {create: true, exclusive: true}, (fileEntry) => {
          logger.info("File " + fileEntry.name + " created successfully");
        }, errorCallback);
      }
    
      function errorCallback(error) {
        logger.info("Error creating file " + inAppBrowserConfigFile + ". Error: " + JSON.stringify(error));
      }
    } catch (error) {
      logger.info("Error creating file " + inAppBrowserConfigFile + ". Error: " + JSON.stringify(error));
    }
  }
  
  /**
   * Write in app browser config file.
   */
  function writeInAppBrowserConfigFile() {
    logger.info("writeInAppBrowserConfigFile");
    try {
      window.requestFileSystem(inAppBrowserConfigFileType, inAppBrowserConfigFileSize, successCallback, errorCallback);

      function successCallback(fs) {
        fs.root.getFile(inAppBrowserConfigFile, {create: true}, (fileEntry) => {
          fileEntry.createWriter((fileWriter) => {
            logger.info("File content to write: " + JSON.stringify(inAppBrowserConfig));
            const blob = new Blob([JSON.stringify(inAppBrowserConfig)]);
            fileWriter.write(blob);
          }, errorCallback);
        }, errorCallback);
      }
  
      function errorCallback(error) {
        logger.info("Error writing file " + inAppBrowserConfigFile + ". Error: " + JSON.stringify(error));
      }
    } catch (error) {
      logger.info("Error writing file " + inAppBrowserConfigFile + ". Error: " + JSON.stringify(error));
    }
  }
  
  /**
   * Read in app browser config file.
   */
   function readInAppBrowserConfigFile() {
    logger.info("readInAppBrowserConfigFile");
    try {
      window.requestFileSystem(inAppBrowserConfigFileType, inAppBrowserConfigFileSize, successCallback, errorCallback);

      function successCallback(fs) {
        fs.root.getFile(inAppBrowserConfigFile, {}, function(fileEntry) {
          fileEntry.file(function(file) {
            const reader = new FileReader();
            reader.onloadend = function(e) {
              const fileContent = this.result;
              logger.info("file content read: " + fileContent);
              setInAppBrowserConfig(JSON.parse(fileContent));
            };
            reader.readAsText(file);
          }, errorCallback);
        }, errorCallback);
      }
    
      function errorCallback(error) {
        logger.info("Error reading file " + inAppBrowserConfigFile + ". Error: " + JSON.stringify(error));
      }
    } catch (error) {
      logger.info("Error reading file " + inAppBrowserConfigFile + ". Error: " + JSON.stringify(error));
    }
  }	
  
  /**
   * Delete in app browser config file.
   */
   function deleteInAppBrowserConfigFile() {
    logger.info("deleteInAppBrowserConfigFile");
    try {
      window.requestFileSystem(inAppBrowserConfigFileType, inAppBrowserConfigFileSize, successCallback, errorCallback);
  
      function successCallback(fs) {
        fs.root.getFile(inAppBrowserConfigFile, {create: false}, (fileEntry) => {
          fileEntry.remove(() => {
            logger.info("File " + fileEntry.name + " deleted successfully");
          }, errorCallback);
        }, errorCallback);
      }
    
      function errorCallback(error) {
        logger.info("Error deleting file " + inAppBrowserConfigFile + ". Error: " + JSON.stringify(error));
      }
    } catch (error) {
      logger.info("Error deleting file " + inAppBrowserConfigFile + ". Error: " + JSON.stringify(error));
    }
  }
}

/** Call main. */
$(document).ready(mainGlobalMobile);