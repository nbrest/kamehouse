
/**
 * Global functions for kamehouse-mobile app.
 * 
 * @author nbrest
 */
const cordovaManager = new CordovaManager();
const mobileConfigManager = new MobileConfigManager();

function mainGlobalMobile() {
  logger.info("Started initializing mobile app global");
  moduleUtils.waitForModules(["cordova"], () => {
    mobileConfigManager.init();
    cordovaManager.init();
  });
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
    const serverEntity = getServerUrl(urlLookup);
    openInAppBrowser(serverEntity);
  }

  /**
   * Get complete url from lookup.
   */
  function getServerUrl(urlLookup) {
    const server = mobileConfigManager.getServers().find(server => server.name === urlLookup);
    const serverEntity = {};
    serverEntity.name = server.name;
    serverEntity.url = server.url;
    if (urlLookup == "docker-demo") {
      serverEntity.url = serverEntity.url + "/kame-house/";
    }
    if (urlLookup == "tw-booking") {
      serverEntity.url = serverEntity.url + "/kame-house/tennisworld/booking-response.html";
    }
    if (urlLookup == "vm-ubuntu") {
      serverEntity.url = serverEntity.url + "/kame-house/";
    }
    if (urlLookup == "web-vlc") {
      serverEntity.url = serverEntity.url + "/kame-house/vlc-player";
    }
    if (urlLookup == "wol") {
      serverEntity.url = serverEntity.url + "/kame-house/admin/wake-on-lan.html";
    }
    logger.trace("Server entity: " + JSON.stringify(serverEntity));
    return serverEntity;
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
      basicKamehouseModal.openAutoCloseable(getOpenBrowserMessage(serverEntity), 2000);
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
  this.setBackendFromDropdown = setBackendFromDropdown;
  this.setWebVlcPlayerFromDropdown = setWebVlcPlayerFromDropdown;
  this.refreshConfigTabView = refreshConfigTabView;
  this.confirmResetDefaults = confirmResetDefaults;
  this.resetDefaults = resetDefaults;

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
    kameHouse.mobile.config = {};
    kameHouse.mobile.config.inAppBrowser = {};
    kameHouse.mobile.config.servers = {};
  }

  function getMobileConfig() {
    return kameHouse.mobile.config;
  }

  function setMobileConfig(val) {
    kameHouse.mobile.config = val;
  }

  function getInAppBrowserConfig() {
    return kameHouse.mobile.config.inAppBrowser;
  }

  function setInAppBrowserConfig(val) {
    kameHouse.mobile.config.inAppBrowser = val;
  }

  function getServers() {
    return kameHouse.mobile.config.servers;
  }

  function setServers(val) {
    kameHouse.mobile.config.servers = val;
  }

  async function loadInAppBrowserDefaultConfig() {
    inAppBrowserDefaultConfig = JSON.parse(await fetchUtils.loadJsonConfig('/kame-house-mobile/json/config/in-app-browser.json'));
    logger.info("inAppBrowserConfig default config: " + JSON.stringify(inAppBrowserDefaultConfig));
    setInAppBrowserConfig(JSON.parse(JSON.stringify(inAppBrowserDefaultConfig)));
  }

  async function loadServersDefaultConfig() {
    serversDefaultConfig = JSON.parse(await fetchUtils.loadJsonConfig('/kame-house-mobile/json/config/servers.json'));
    logger.info("servers default config: " + JSON.stringify(serversDefaultConfig));
    setServers(JSON.parse(JSON.stringify(serversDefaultConfig)));
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
    logger.info("Updating mobile config from view");
    const inAppBrowserConfig = getInAppBrowserConfig();

    // Set servers
    updateServer("backend");
    updateServer("jenkins");
    updateServer("tw-booking");
    updateServer("web-vlc");
    updateServer("wol");

    // Set InAppBrowser open on startup
    const inAppBrowserOpenOnStartupCheckbox = document.getElementById("iab-open-on-startup-checkbox");
    inAppBrowserConfig.openOnStartup = inAppBrowserOpenOnStartupCheckbox.checked;

    // Set InAppBrowser target
    const inAppBrowserTargetDropdown = document.getElementById("iab-target-dropdown");
    if (!isEmpty(inAppBrowserTargetDropdown.value) && inAppBrowserTargetDropdown.value != "") {
      inAppBrowserConfig.target = inAppBrowserTargetDropdown.value;
    }

    // Set InAppBrowser options clearcache
    const inAppBrowserClearCacheCheckbox = document.getElementById("iab-clearcache-checkbox");
    if (inAppBrowserClearCacheCheckbox.checked) {
      inAppBrowserConfig.options = inAppBrowserConfig.options.replace("clearcache=no", "clearcache=yes");
    } else {
      inAppBrowserConfig.options = inAppBrowserConfig.options.replace("clearcache=yes", "clearcache=no");
    }

    logger.info("servers: " + JSON.stringify(getServers()));
    logger.info("inAppBrowser.options: " + inAppBrowserConfig.options);
    logger.info("inAppBrowser.target: " + inAppBrowserConfig.target);
    logger.info("inAppBrowser.openOnStartup: " + inAppBrowserConfig.openOnStartup);
    reGenerateMobileConfigFile();
  }

  /**
   * Update the server url in the config from the input.
   */
  function updateServer(serverName) {
    const servers = getServers();
    const server = servers.find(server => server.name === serverName);
    const serverInput = document.getElementById(serverName + "-server-input"); 
    server.url = serverInput.value;
  }

  /**
   * Set the web vlc player in the config from the dropdown menu in the config page.
   */
  function setWebVlcPlayerFromDropdown() {
    logger.info("Setting web vlc player from dropdown");
    const webVlcServerInput = document.getElementById("web-vlc-server-input"); 
    const webVlcServerDropdown = document.getElementById("web-vlc-server-dropdown");
    if (!isEmpty(webVlcServerDropdown.value) && webVlcServerDropdown.value != "") {
      domUtils.setValue(webVlcServerInput, webVlcServerDropdown.value);
      updateMobileConfigFromView();
    }
  }

  /** Set the backend server in the config from the dropdown menu in the config page */
  function setBackendFromDropdown() {
    logger.info("Setting backend server from dropdown");
    const backendServerInput = document.getElementById("backend-server-input"); 
    const backendServerDropdown = document.getElementById("backend-server-dropdown");
    if (!isEmpty(backendServerDropdown.value) && backendServerDropdown.value != "") {
      domUtils.setValue(backendServerInput, backendServerDropdown.value);
      updateMobileConfigFromView();
    }    
  }

  /**
   * Refresh config tab view values.
   */
  function refreshConfigTabView() {
    logger.info("Refreshing config tab view values");
    const inAppBrowserConfig = getInAppBrowserConfig();

    // servers
    setServerInput("backend");
    setServerInput("jenkins");
    setServerInput("tw-booking");
    setServerInput("web-vlc");
    setServerInput("wol");

    // Backend dropdown
    const backendServerInputValue = document.getElementById("backend-server-input").value;
    const backendServerDropdown = document.getElementById("backend-server-dropdown");
    if (backendServerInputValue != "") {
      backendServerDropdown.options[backendServerDropdown.options.length-1].selected = true;
    }
    for (let i = 0; i < backendServerDropdown.options.length; ++i) {
      if (backendServerDropdown.options[i].value === backendServerInputValue) {
        backendServerDropdown.options[i].selected = true;
      }
    }

    // Web VLC Player dropdown
    const webVlcPlayerInputValue = document.getElementById("web-vlc-server-input").value;
    const webVlcPlayerDropdown = document.getElementById("web-vlc-server-dropdown");
    if (webVlcPlayerInputValue != "") {
      webVlcPlayerDropdown.options[webVlcPlayerDropdown.options.length-1].selected = true;
    }
    for (let i = 0; i < webVlcPlayerDropdown.options.length; ++i) {
      if (webVlcPlayerDropdown.options[i].value === webVlcPlayerInputValue) {
        webVlcPlayerDropdown.options[i].selected = true;
      }
    }

    // InAppBrowser open on startup
    const openOnStartup = inAppBrowserConfig.openOnStartup;
    const inAppBrowserOpenOnStartupCheckbox = document.getElementById("iab-open-on-startup-checkbox");
    if (openOnStartup) {
      inAppBrowserOpenOnStartupCheckbox.checked = true;
    } else {
      inAppBrowserOpenOnStartupCheckbox.checked = false;
    }

    // InAppBrowser target
    
    const inAppBrowserTarget = inAppBrowserConfig.target;
    const inAppBrowserTargetDropdown = document.getElementById("iab-target-dropdown");
    for (let i = 0; i < inAppBrowserTargetDropdown.options.length; ++i) {
      if (inAppBrowserTargetDropdown.options[i].value === inAppBrowserTarget) {
        inAppBrowserTargetDropdown.options[i].selected = true;
      }
    }

    // InAppBrowser options
    const inAppBrowserOptionsArray = inAppBrowserConfig.options.split(",");
    const inAppBrowserClearCacheCheckbox = document.getElementById("iab-clearcache-checkbox");
    inAppBrowserOptionsArray.forEach((inAppBrowserOption) => {
      if (inAppBrowserOption == "clearcache=no") {
        inAppBrowserClearCacheCheckbox.checked = false;
      }
      if (inAppBrowserOption == "clearcache=yes") {
        inAppBrowserClearCacheCheckbox.checked = true;
      }
    });
  }

  /**
   * Set the server input field value in the view from the config.
   */
  function setServerInput(serverName) {
    const servers = getServers();
    const server = servers.find(server => server.name === serverName);
    const serverInput = document.getElementById(serverName + "-server-input");
    domUtils.setValue(serverInput, server.url);
  }

  /**
   * Open confirm reset config modal.
   */
  function confirmResetDefaults() {
    basicKamehouseModal.setHtml(getResetConfigModalMessage());
    basicKamehouseModal.open();
  }
  
  /**
   * Get the message to reset the config.
   */
  function getResetConfigModalMessage() {
    const resetConfigModalMessage = domUtils.getSpan({}, "Are you sure you want to reset the configuration? ");
    domUtils.append(resetConfigModalMessage, domUtils.getBr());
    domUtils.append(resetConfigModalMessage, domUtils.getBr());
    domUtils.append(resetConfigModalMessage, getConfirmResetConfigButton());
    return resetConfigModalMessage;
  }

  /**
   * Get the button to confirm resetting the config.
   */
  function getConfirmResetConfigButton() {
    return domUtils.getButton({
      attr: {
        class: "mobile-btn-kh reset-cfg-btn-kh",
      },
      html: "Yes",
      click: resetDefaults
    });
  }

  /**
   * Reset config to default values.
   */
  function resetDefaults() {
    logger.info("Resetting config to default values");
    setServers(JSON.parse(JSON.stringify(serversDefaultConfig)));
    setInAppBrowserConfig(JSON.parse(JSON.stringify(inAppBrowserDefaultConfig)));
    refreshConfigTabView();
    reGenerateMobileConfigFile();
    basicKamehouseModal.close();
    basicKamehouseModal.openAutoCloseable("Config reset to default values", 2000);
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