
/**
 * Global functions for kamehouse-mobile app.
 * 
 * @author nbrest
 */
const cordovaManager = new CordovaManager();
const coreMobileUtils = new CoreMobileUtils();
const mobileConfigManager = new MobileConfigManager();

function mainGlobalMobile() {
  logger.info("Started initializing mobile global");
  moduleUtils.waitForModules([ "debuggerHttpClient"], () => {
    coreMobileUtils.loadHeaderAndFooter();
    mobileConfigManager.init();
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

  this.init = init;
  this.openBrowser = openBrowser;
  this.overrideWindowOpen = overrideWindowOpen;

  async function init() {
    logger.info("Initializing cordova manager");
    setCordovaMock();
    moduleUtils.setModuleLoaded("cordovaManager");
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
    const serverEntity = mobileConfigManager.getServers().find(server => server.name === urlLookup);
    openInAppBrowser(serverEntity);
  }

  /**
   * Open the InAppBrowser with the specified url.
   */
  function openInAppBrowser(serverEntity) {
    logger.info("Start loading url " + serverEntity.url);
    const target = mobileConfigManager.getInAppBrowserConfig().target;
    const options = mobileConfigManager.getInAppBrowserConfig().options;
    const inAppBrowserInstance = cordova.InAppBrowser.open(serverEntity.url, target, options);
    if (target == "_system") {
      basicKamehouseModal.openAutoCloseable(getOpenBrowserMessage(serverEntity), 7000);
    } else {
      basicKamehouseModal.setHtml(getOpenBrowserMessage(serverEntity));
      basicKamehouseModal.setErrorMessage(false);
      basicKamehouseModal.open();
      setInAppBrowserEventListeners(inAppBrowserInstance, serverEntity);
    }
  }

  /**
   * Get the open browser message for the modal.
   */
  function getOpenBrowserMessage(serverEntity) {
    const openBrowserMessage = domUtils.getSpan({}, "Opening " + serverEntity.name);
    domUtils.append(openBrowserMessage, domUtils.getBr());
    domUtils.append(openBrowserMessage, domUtils.getBr());
    domUtils.append(openBrowserMessage, "Please Wait ...");
    return openBrowserMessage;
  }

  /**
   * Set listeners for the events handled by the InAppBrowser when the target isn't _system.
   */
  function setInAppBrowserEventListeners(inAppBrowserInstance, serverEntity) {

    inAppBrowserInstance.addEventListener('loadstop', (params) => {
      logger.info("Executing event loadstop for url: '" + serverEntity.url + "'. with params " + JSON.stringify(params));
      inAppBrowserInstance.show();
    });

    inAppBrowserInstance.addEventListener('loaderror', (params) => {
      const errorMessage = "Error loading url '" + serverEntity.url + "'. with params " + JSON.stringify(params);
      logger.error("Executing event loaderror. " + errorMessage);
      basicKamehouseModal.setHtml(errorMessage);
      basicKamehouseModal.setErrorMessage(true);
      basicKamehouseModal.open();
      inAppBrowserInstance.close();
    });

    inAppBrowserInstance.addEventListener('loadstart', (params) => {
      logger.info("Executing event loadstart for url: '" + serverEntity.url + "'. with params " + JSON.stringify(params));
    });

    inAppBrowserInstance.addEventListener('exit', (params) => {
      logger.info("Executing event exit for url: '" + serverEntity.url + "'. with params " + JSON.stringify(params));
      if (!basicKamehouseModal.isErrorMessage()) {
        basicKamehouseModal.close(); 
        basicKamehouseModal.reset();
      }
    });

    inAppBrowserInstance.addEventListener('message', (params) => {
      logger.info("Executing event message for url: '" + serverEntity.url + "'. with params " + JSON.stringify(params));
    });
  } 
}

/**
 * Manage the configuration of the mobile app.
 */
function MobileConfigManager() {
  
  this.init = init;
  this.getServers = getServers;
  this.getInAppBrowserConfig = getInAppBrowserConfig;
  this.reGenerateMobileConfigFile = reGenerateMobileConfigFile;
  this.updateMobileConfigFromView = updateMobileConfigFromView;
  this.setVlcPlayerFromDropdown = setVlcPlayerFromDropdown;

  const mobileConfigFile = "kamehouse-mobile-config.json";
  const mobileConfigFileType = window.PERSISTENT;
  const mobileConfigFileSize = 5*1024*1024; //50 mb

  let isCurrentlyPersistingConfig = false;
  let inAppBrowserDefaultConfig = null;
  let serversDefaultConfig = null;

  async function init() {
    logger.info("Initializing mobile config manager");
    initGlobalMobileConfig();
    await loadInAppBrowserDefaultConfig();
    await loadServersDefaultConfig();
    readMobileConfigFile();
  }

  function setMobileConfigManagerModuleLoaded() {
    logger.info("Finished mobileConfigManager module initialization");
    moduleUtils.setModuleLoaded("mobileConfigManager");
  }

  function initGlobalMobileConfig() {
    global.mobile = {};
    global.mobile.config = {};
    global.mobile.config.inAppBrowser = {};
    global.mobile.config.servers = {};
  }

  function getMobileConfig() {
    return global.mobile.config;
  }

  function setMobileConfig(val) {
    global.mobile.config = val;
  }

  function getInAppBrowserConfig() {
    return global.mobile.config.inAppBrowser;
  }

  function setInAppBrowserConfig(val) {
    global.mobile.config.inAppBrowser = val;
  }

  function getServers() {
    return global.mobile.config.servers;
  }

  function setServers(val) {
    global.mobile.config.servers = val;
  }

  async function loadInAppBrowserDefaultConfig() {
    inAppBrowserDefaultConfig = JSON.parse(await fetchUtils.loadJsonConfig('/json/config/in-app-browser.json'));
    logger.info("inAppBrowserConfig default config: " + JSON.stringify(inAppBrowserDefaultConfig));
    setInAppBrowserConfig(inAppBrowserDefaultConfig);
  }

  async function loadServersDefaultConfig() {
    serversDefaultConfig = JSON.parse(await fetchUtils.loadJsonConfig('/json/config/servers.json'));
    logger.info("servers default config: " + JSON.stringify(serversDefaultConfig));
    setServers(serversDefaultConfig);
  }

  /**
   * Returns true if the config has all the required properties.
   */
  function isValidMobileConfigFile(mobileConfig) {
    return mobileConfig != null && mobileConfig.inAppBrowser != null && mobileConfig.servers != null;
  }

  /**
   * Create kamehouse-mobile config file in the device's storage.
   */
  function createMobileConfigFile() {
    logger.info("Creating file " + mobileConfigFile);
    try {
      window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, successCallback, errorCallback);

      function successCallback(fs) {
        fs.root.getFile(mobileConfigFile, {create: true, exclusive: true}, (fileEntry) => {
          logger.info("File " + fileEntry.name + " created successfully");
        }, errorCallback);
      }
    
      function errorCallback(error) {
        logger.info("Error creating file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      }
    } catch (error) {
      logger.info("Error creating file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
    }
  }
  
  /**
   * Write kamehouse-mobile config file to the filesystem.
   */
  function writeMobileConfigFile() {
    logger.info("Writing to file " + mobileConfigFile);
    try {
      window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, successCallback, errorCallback);

      function successCallback(fs) {
        fs.root.getFile(mobileConfigFile, {create: true}, (fileEntry) => {
          fileEntry.createWriter((fileWriter) => {
            const fileContent = JSON.stringify(getMobileConfig());
            logger.info("File content to write: " + fileContent);
            const blob = new Blob([fileContent]);
            fileWriter.write(blob);
          }, errorCallback);
        }, errorCallback);
      }
  
      function errorCallback(error) {
        logger.info("Error writing file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      }
    } catch (error) {
      logger.info("Error writing file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
    }
  }
  
  /**
   * Read kamehouse-mobile config file.
   */
   function readMobileConfigFile() {
    logger.info("Reading file " + mobileConfigFile);
    try {
      window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, successCallback, errorCallback);

      function successCallback(fs) {
        fs.root.getFile(mobileConfigFile, {}, function(fileEntry) {
          fileEntry.file(function(file) {
            const reader = new FileReader();
            reader.onloadend = function(e) {
              const fileContent = this.result;
              logger.info("file content read: " + fileContent);
              let mobileConfig = null;
              try {
                mobileConfig = JSON.parse(fileContent);
              } catch(e) {
                mobileConfig = null;
                logger.error("Error parsing file content as json. Error " + JSON.stringify(e));
              }
              if (isValidMobileConfigFile(mobileConfig)) {
                logger.info("Setting mobile config from file");
                setMobileConfig(mobileConfig);
              } else {
                logger.warn("Mobile config file read from file is invalid. Re generating it");
                reGenerateMobileConfigFile();
              }
              setMobileConfigManagerModuleLoaded();
            };
            reader.readAsText(file);
          }, errorCallback);
        }, errorCallback);
      }
    
      function errorCallback(error) {
        logger.info("Error reading file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
        setMobileConfigManagerModuleLoaded();
        createMobileConfigFile();
      }
    } catch (error) {
      logger.info("Error reading file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      setMobileConfigManagerModuleLoaded();
      createMobileConfigFile();
    }
  }	
  
  /**
   * Delete kamehouse-mobile config file.
   */
   function deleteMobileConfigFile() {
    logger.info("Deleting file " + mobileConfigFile);
    try {
      window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, successCallback, errorCallback);
  
      function successCallback(fs) {
        fs.root.getFile(mobileConfigFile, {create: false}, (fileEntry) => {
          fileEntry.remove(() => {
            logger.info("File " + fileEntry.name + " deleted successfully");
          }, errorCallback);
        }, errorCallback);
      }
    
      function errorCallback(error) {
        logger.info("Error deleting file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      }
    } catch (error) {
      logger.info("Error deleting file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
    }
  }

  /**
   * Re generate kamehouse-mobile config file.
   */
  function reGenerateMobileConfigFile() {
    if (isCurrentlyPersistingConfig) {
      logger.info("A regenerate file is already in progress, skipping this call");
      return;
    }
    isCurrentlyPersistingConfig = true;
    logger.info("Regenerating file " + mobileConfigFile);
    try {
      window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, successDeleteFileCallback, errorDeleteFileCallback);
  
      function successDeleteFileCallback(fs) {
        fs.root.getFile(mobileConfigFile, {create: false}, (fileEntry) => {
          fileEntry.remove(() => {
            logger.info("File " + fileEntry.name + " deleted successfully");
            createFile();
          }, errorDeleteFileCallback);
        }, errorDeleteFileCallback);
      }
    
      function errorDeleteFileCallback(error) {
        logger.info("Error deleting file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
        createFile();
      }

      function createFile() {
        window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, successCreateFileCallback, errorCreateFileCallback);
      }

      function successCreateFileCallback(fs) {
        fs.root.getFile(mobileConfigFile, {create: true, exclusive: true}, (fileEntry) => {
          logger.info("File " + fileEntry.name + " created successfully");
          writeFile();
        }, errorCreateFileCallback);
      }

      function errorCreateFileCallback(error) {
        logger.info("Error creating file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
        writeFile();
      }

      function writeFile() {
        window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, successWriteFileCallback, errorWriteFileCallback);
      }

      function successWriteFileCallback(fs) {
        fs.root.getFile(mobileConfigFile, {create: true}, (fileEntry) => {
          fileEntry.createWriter((fileWriter) => {
            const fileContent = JSON.stringify(getMobileConfig());
            logger.info("File content to write: " + fileContent);
            const blob = new Blob([fileContent]);
            fileWriter.write(blob);
            isCurrentlyPersistingConfig = false;
          }, errorCreateFileCallback);
        }, errorCreateFileCallback);
      }

      function errorWriteFileCallback(error) {
        logger.info("Error writing file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
        isCurrentlyPersistingConfig = false;
      }

    } catch (error) {
      logger.info("Error regenerating file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      isCurrentlyPersistingConfig = false;
    }
  }

  /**
   * Update the mobile config from the view in the config tab.
   */
  function updateMobileConfigFromView() {
    // Set vlc
    logger.info("Updating mobile config from view");
    const vlcServer = getServers().find(server => server.name === "vlc");
    const vlcServerInput = document.getElementById("vlc-server-input"); 
    vlcServer.url = vlcServerInput.value;
    logger.info("vlcServer: " + JSON.stringify(vlcServer));

    // Set InAppBrowser target
    const inAppBrowserConfig = getInAppBrowserConfig();
    const inAppBrowserTargetDropdown = document.getElementById("iab-target-dropdown");
    if (!isEmpty(inAppBrowserTargetDropdown.value) && inAppBrowserTargetDropdown.value != "") {
      inAppBrowserConfig.target = inAppBrowserTargetDropdown.value;
      logger.info("inAppBrowserConfig.target: " + inAppBrowserConfig.target);
    }

    // Set InAppBrowser options clearcache
    const inAppBrowserClearCacheCheckbox = document.getElementById("iab-clearcache-checkbox");
    if (inAppBrowserClearCacheCheckbox.checked) {
      inAppBrowserConfig.options = inAppBrowserConfig.options.replace("clearcache=no", "clearcache=yes");
    } else {
      inAppBrowserConfig.options = inAppBrowserConfig.options.replace("clearcache=yes", "clearcache=no");
    }
    logger.info("inAppBrowserConfig.options: " + inAppBrowserConfig.options);

    reGenerateMobileConfigFile();
  }

  /**
   * Set the vlc player in the config from the dropdown menu in the config page.
   */
  function setVlcPlayerFromDropdown() {
    logger.info("Setting vlc player from dropdown");
    const vlcServerInput = document.getElementById("vlc-server-input"); 
    const vlcServerDropdown = document.getElementById("vlc-server-dropdown");
    if (!isEmpty(vlcServerDropdown.value) && vlcServerDropdown.value != "") {
      domUtils.setValue(vlcServerInput, vlcServerDropdown.value);
      updateMobileConfigFromView();
    }
  }

  /**
   * Test file operations.
   */
  function testFileManagement() {
    setTimeout(() => { createMobileConfigFile(); }, 1000);
    setTimeout(() => { writeMobileConfigFile(); }, 10000);
    setTimeout(() => { readMobileConfigFile(); }, 15000);
    setTimeout(() => { deleteMobileConfigFile(); }, 20000);
  }
}

/**
 * Mock cordova api to test layout and everything else using an apache httpd server.
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

/** Call main. */
$(document).ready(mainGlobalMobile);