
/**
 * Global functions for kamehouse-mobile app.
 * 
 * @author nbrest
 */
function KameHouseMobile() {
  this.load = load;

  async function load() {
    kameHouse.logger.info("Started initializing kamehouse-mobile.js");
    kameHouse.extension.mobile.core = new KameHouseMobileCore();
    kameHouse.extension.mobile.configManager = new KameHouseMobileConfigManager();
    kameHouse.extension.mobile.core.init();
    await kameHouse.extension.mobile.configManager.init();
  }
} 

/**
 * Functionality for the native kamehouse mobile app.
 */
function KameHouseMobileCore() {

  this.init = init;
  this.openBrowser = openBrowser;
  this.overrideWindowOpen = overrideWindowOpen;
  this.disableWebappOnlyElements = disableWebappOnlyElements;
  this.getBackendServer = getBackendServer;
  this.getBackendCredentials = getBackendCredentials;
  this.mobileHttpRequst = mobileHttpRequst;

  const GET = "GET";
  const POST = "POST";
  const PUT = "PUT";
  const DELETE = "DELETE";
  
  function init() {
    setCordovaMock();
    disableWebappOnlyElements();
  }

  function disableWebappOnlyElements() {
    $(document).ready(() => {
      kameHouse.logger.debug("Disabling webapp only elements in mobile app view");
      const mobileOnlyElements = document.getElementsByClassName("kh-mobile-hidden");
      for (const mobileOnlyElement of mobileOnlyElements) {
        kameHouse.util.dom.classListAdd(mobileOnlyElement, "hidden-kh");
        kameHouse.util.dom.classListRemove(mobileOnlyElement, "kh-mobile-hidden");
      }
    });
  }

  function getBackendServer() {
    const mobileConfig = kameHouse.extension.mobile.config;
    let backendServer = null;
    if (!kameHouse.core.isEmpty(mobileConfig) && !kameHouse.core.isEmpty(mobileConfig.servers)) {
      mobileConfig.servers.forEach((server) => {
        if (server.name === "backend") {
          backendServer = server.url;
        }
      });
    }
    if (backendServer == null) {
      kameHouse.logger.error("Couldn't find backend server url in the config. Mobile app config manager may not have completed initialization yet.");
    }
    return backendServer;
  }

  function getBackendCredentials() {
    if (!kameHouse.core.isEmpty(kameHouse.extension.mobile.config) && !kameHouse.core.isEmpty(kameHouse.extension.mobile.config.credentials)) {
      return kameHouse.extension.mobile.config.credentials;
    }
    kameHouse.logger.warn("Could not retrieve credentials from the mobile config");
    return {};
  }

  /** 
   * Http request to be sent from the mobile app.
   */
  function mobileHttpRequst(httpMethod, url, requestHeaders, requestBody, successCallback, errorCallback, customData) {
    let requestUrl = getBackendServer() + url;   
    const options = {
      method: httpMethod,
      data: ""
    };
    
    if (!kameHouse.core.isEmpty(requestHeaders)) {
      options.headers = requestHeaders;
    }
    setMobileBasicAuthHeader();
    setDataSerializer(requestHeaders, httpMethod);
    if (kameHouse.http.isUrlEncodedRequest(requestHeaders)) {
      if (httpMethod == GET || httpMethod == PUT || httpMethod == DELETE) {
        if (!kameHouse.core.isEmpty(requestBody)) {
          requestUrl = requestUrl + "?" + kameHouse.http.urlEncodeParams(requestBody);
        }
      } else {
        // http method is POST and urlEncoded
        if (!kameHouse.core.isEmpty(requestBody)) {
          options.data = requestBody;
        }
      }
    } else {
      if (!kameHouse.core.isEmpty(requestBody)) {
        options.data = requestBody;
      }
    }
    logMobileHttpRequest(requestUrl, options);
    cordova.plugin.http.setServerTrustMode('nocheck',
     () => {
      cordova.plugin.http.sendRequest(requestUrl, options, 
        (response) => { processMobileSuccess(response, successCallback, customData); } ,
        (response) => { processMobileError(response, errorCallback, customData); }
      );
    }, () => {
      kameHouse.logger.error('Error setting cordova ssl trustmode to nocheck. Unable to execute http ' + httpMethod + ' request to ' + requestUrl);
    });
  }

  /** Process a successful response from the api call */
  function processMobileSuccess(response, successCallback, customData) {
    /**
     * data: response body
     * status: http status code
     * url: request url
     * response headers: header map object
     */
    let responseBody;
    if (isJsonResponse(response.headers)) {
      responseBody = JSON.parse(response.data);
    } else {
      responseBody = response.data;
    }
    const responseCode = response.status;
    const responseDescription = null;
    kameHouse.logger.logHttpResponse(responseBody, responseCode, responseDescription);
    successCallback(responseBody, responseCode, responseDescription, customData);
  }

  /** Process an error response from the api call */
  function processMobileError(response, errorCallback, customData) {
     /**
     * error: error message
     * status: http status code
     * url: request url
     * response headers: header map object
      */
     const responseBody = response.error;
     const responseCode = response.status;
     const responseDescription = null;
     kameHouse.logger.logHttpResponse(responseBody, responseCode, responseDescription);
     kameHouse.logger.logApiError(responseBody, responseCode, responseDescription, null);
     errorCallback(JSON.stringify(responseBody), responseCode, responseDescription, customData);
  }  

  function isJsonResponse(headers) {
    if (kameHouse.core.isEmpty(headers)) {
      return false;
    }
    let isJson = false;
    for (const [key, value] of Object.entries(headers)) {
      if (!kameHouse.core.isEmpty(key) && key.toLowerCase() == "content-type" 
        && !kameHouse.core.isEmpty(value) && value.toLowerCase() == "application/json") {
          kameHouse.logger.trace("Response is json");
          isJson = true;
      }
    }
    return isJson;
  }

  function setMobileBasicAuthHeader() {
    const credentials = getBackendCredentials();
    if (!kameHouse.core.isEmpty(credentials.username) && !kameHouse.core.isEmpty(credentials.password)) {
      kameHouse.logger.debug("Setting basicAuth header for mobile http request");
      cordova.plugin.http.useBasicAuth(credentials.username, credentials.password);
    }
  }

  function setDataSerializer(headers, httpMethod) {
    kameHouse.logger.debug("Setting data serializer to 'utf8'");
    cordova.plugin.http.setDataSerializer('utf8');
    if (kameHouse.core.isEmpty(headers)) {
      return;
    }
    for (const [key, value] of Object.entries(headers)) {
      if (!kameHouse.core.isEmpty(key) && key.toLowerCase() == "content-type" && !kameHouse.core.isEmpty(value)) {
        if (value.toLowerCase() == "application/json") {
          kameHouse.logger.debug("Overriding data serializer to 'json'");
          cordova.plugin.http.setDataSerializer('json');
        }
        if (value.toLowerCase() == "application/x-www-form-urlencoded") {
          // For GET, PUT, DELETE url encoded data in this http plugin, the data in the options object must be set to "" and serializer to utf8 and directly set the encoded url parameters in the request url.
          // For POST url encoded requests, the data in the options object must be a json object and the serializer needs to be set to urlencoded
          if (httpMethod != GET && httpMethod != PUT && httpMethod != DELETE) {
            kameHouse.logger.debug("Overriding data serializer to 'urlencoded'");
            cordova.plugin.http.setDataSerializer('urlencoded');
          }
        }
      }
    }
  }

  /**
   * Log a mobile http request.
   */
  function logMobileHttpRequest(url, options) {
    kameHouse.logger.debug("mobile http request: [ " 
    + "'url' : '" + url + "', "
    + "'options' : '" + JSON.stringify(options) + "' ]");
  }
  
  /**
   * Mock cordova when cordova is not available. For example when testing in a laptop's browser through apache httpd.
   */
  function setCordovaMock() {
    const urlParams = new URLSearchParams(window.location.search);
    const mockCordova = urlParams.get('mockCordova');
    if (mockCordova) {
      kameHouse.logger.info("Mocking cordova object");
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
    const server = kameHouse.extension.mobile.configManager.getServers().find(server => server.name === urlLookup);
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
    kameHouse.logger.trace("Server entity: " + JSON.stringify(serverEntity));
    return serverEntity;
  }

  /**
   * Open the InAppBrowser with the specified url.
   */
  function openInAppBrowser(serverEntity) {
    kameHouse.logger.info("Start loading url " + serverEntity.url);
    const target = kameHouse.extension.mobile.configManager.getInAppBrowserConfig().target;
    const options = kameHouse.extension.mobile.configManager.getInAppBrowserConfig().options;
    const inAppBrowserInstance = cordova.InAppBrowser.open(serverEntity.url, target, options);
    if (target == "_system") {
      kameHouse.plugin.modal.basicModal.openAutoCloseable(getOpenBrowserMessage(serverEntity), 2000);
    } else {
      kameHouse.plugin.modal.basicModal.setHtml(getOpenBrowserMessage(serverEntity));
      kameHouse.plugin.modal.basicModal.setErrorMessage(false);
      kameHouse.plugin.modal.basicModal.open();
      setInAppBrowserEventListeners(inAppBrowserInstance, serverEntity);
    }
  }

  /**
   * Get the open browser message for the modal.
   */
  function getOpenBrowserMessage(serverEntity) {
    const openBrowserMessage = kameHouse.util.dom.getSpan({}, "Opening " + serverEntity.name);
    kameHouse.util.dom.append(openBrowserMessage, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(openBrowserMessage, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(openBrowserMessage, "Please Wait ...");
    return openBrowserMessage;
  }

  /**
   * Set listeners for the events handled by the InAppBrowser when the target isn't _system.
   */
  function setInAppBrowserEventListeners(inAppBrowserInstance, serverEntity) {

    inAppBrowserInstance.addEventListener('loadstop', (params) => {
      kameHouse.logger.info("Executing event loadstop for url: '" + serverEntity.url + "'. with params " + JSON.stringify(params));
      inAppBrowserInstance.show();
    });

    inAppBrowserInstance.addEventListener('loaderror', (params) => {
      const errorMessage = "Error loading url '" + serverEntity.url + "'. with params " + JSON.stringify(params);
      kameHouse.logger.error("Executing event loaderror. " + errorMessage);
      kameHouse.plugin.modal.basicModal.setHtml(errorMessage);
      kameHouse.plugin.modal.basicModal.setErrorMessage(true);
      kameHouse.plugin.modal.basicModal.open();
      inAppBrowserInstance.close();
    });

    inAppBrowserInstance.addEventListener('loadstart', (params) => {
      kameHouse.logger.info("Executing event loadstart for url: '" + serverEntity.url + "'. with params " + JSON.stringify(params));
    });

    inAppBrowserInstance.addEventListener('exit', (params) => {
      kameHouse.logger.info("Executing event exit for url: '" + serverEntity.url + "'. with params " + JSON.stringify(params));
      if (!kameHouse.plugin.modal.basicModal.isErrorMessage()) {
        kameHouse.plugin.modal.basicModal.close(); 
        kameHouse.plugin.modal.basicModal.reset();
      }
    });

    inAppBrowserInstance.addEventListener('message', (params) => {
      kameHouse.logger.info("Executing event message for url: '" + serverEntity.url + "'. with params " + JSON.stringify(params));
    });
  } 
}

/**
 * Manage the configuration of the mobile app.
 */
function KameHouseMobileConfigManager() {
  
  this.init = init;
  this.getServers = getServers;
  this.getCredentials = getCredentials;
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
  let credentialsDefaultConfig = null;

  async function init() {
    kameHouse.logger.info("Initializing mobile config manager");
    initGlobalMobileConfig();
    await loadInAppBrowserDefaultConfig();
    await loadServersDefaultConfig();
    await loadCredentialsDefaultConfig();
    readMobileConfigFile();
  }

  function setKameHouseMobileModuleLoaded() {
    kameHouse.logger.info("Finished kameHouseMobile module initialization");
    kameHouse.util.module.setModuleLoaded("kameHouseMobile");
  }

  function initGlobalMobileConfig() {
    kameHouse.extension.mobile.config = {};
    kameHouse.extension.mobile.config.inAppBrowser = {};
    kameHouse.extension.mobile.config.servers = {};
    kameHouse.extension.mobile.config.credentials = {};
  }

  function getMobileConfig() {
    return kameHouse.extension.mobile.config;
  }

  function setMobileConfig(val) {
    kameHouse.extension.mobile.config = val;
  }

  function getInAppBrowserConfig() {
    return kameHouse.extension.mobile.config.inAppBrowser;
  }

  function setInAppBrowserConfig(val) {
    kameHouse.extension.mobile.config.inAppBrowser = val;
  }

  function getServers() {
    return kameHouse.extension.mobile.config.servers;
  }

  function setServers(val) {
    kameHouse.extension.mobile.config.servers = val;
  }

  function getCredentials() {
    return kameHouse.extension.mobile.config.credentials;
  }

  function setCredentials(val) {
    kameHouse.extension.mobile.config.credentials = val;
  }

  async function loadInAppBrowserDefaultConfig() {
    inAppBrowserDefaultConfig = JSON.parse(await kameHouse.util.fetch.loadJsonConfig('/kame-house-mobile/json/config/in-app-browser.json'));
    kameHouse.logger.info("inAppBrowserConfig default config: " + JSON.stringify(inAppBrowserDefaultConfig));
    setInAppBrowserConfig(JSON.parse(JSON.stringify(inAppBrowserDefaultConfig)));
  }

  async function loadServersDefaultConfig() {
    serversDefaultConfig = JSON.parse(await kameHouse.util.fetch.loadJsonConfig('/kame-house-mobile/json/config/servers.json'));
    kameHouse.logger.info("servers default config: " + JSON.stringify(serversDefaultConfig));
    setServers(JSON.parse(JSON.stringify(serversDefaultConfig)));
  }

  async function loadCredentialsDefaultConfig() {
    credentialsDefaultConfig = JSON.parse(await kameHouse.util.fetch.loadJsonConfig('/kame-house-mobile/json/config/credentials.json'));
    kameHouse.logger.info("credentials default config: " + JSON.stringify(credentialsDefaultConfig));
    setCredentials(JSON.parse(JSON.stringify(credentialsDefaultConfig)));
  }
  
  /**
   * Returns true if the config has all the required properties.
   */
  function isValidMobileConfigFile(mobileConfig) {
    return mobileConfig != null 
      && mobileConfig.inAppBrowser != null 
      && mobileConfig.servers != null 
      && mobileConfig.credentials != null;
  }

  /**
   * Create kamehouse-mobile config file in the device's storage.
   */
  function createMobileConfigFile() {
    kameHouse.logger.info("Creating file " + mobileConfigFile);
    try {
      window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, successCallback, errorCallback);

      function successCallback(fs) {
        fs.root.getFile(mobileConfigFile, {create: true, exclusive: true}, (fileEntry) => {
          kameHouse.logger.info("File " + fileEntry.name + " created successfully");
        }, errorCallback);
      }
    
      function errorCallback(error) {
        kameHouse.logger.info("Error creating file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      }
    } catch (error) {
      kameHouse.logger.info("Error creating file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
    }
  }
  
  /**
   * Write kamehouse-mobile config file to the filesystem.
   */
  function writeMobileConfigFile() {
    kameHouse.logger.info("Writing to file " + mobileConfigFile);
    try {
      window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, successCallback, errorCallback);

      function successCallback(fs) {
        fs.root.getFile(mobileConfigFile, {create: true}, (fileEntry) => {
          fileEntry.createWriter((fileWriter) => {
            const fileContent = JSON.stringify(getMobileConfig());
            kameHouse.logger.info("File content to write: " + fileContent);
            const blob = new Blob([fileContent]);
            fileWriter.write(blob);
          }, errorCallback);
        }, errorCallback);
      }
  
      function errorCallback(error) {
        kameHouse.logger.info("Error writing file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      }
    } catch (error) {
      kameHouse.logger.info("Error writing file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
    }
  }
  
  /**
   * Read kamehouse-mobile config file.
   */
   function readMobileConfigFile() {
    kameHouse.logger.info("Reading file " + mobileConfigFile);
    try {
      window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, successCallback, errorCallback);

      function successCallback(fs) {
        fs.root.getFile(mobileConfigFile, {}, function(fileEntry) {
          fileEntry.file(function(file) {
            const reader = new FileReader();
            reader.onloadend = function(e) {
              const fileContent = this.result;
              kameHouse.logger.info("file content read: " + fileContent);
              let mobileConfig = null;
              try {
                mobileConfig = JSON.parse(fileContent);
              } catch(e) {
                mobileConfig = null;
                kameHouse.logger.error("Error parsing file content as json. Error " + JSON.stringify(e));
              }
              if (isValidMobileConfigFile(mobileConfig)) {
                kameHouse.logger.info("Setting mobile config from file");
                setMobileConfig(mobileConfig);
              } else {
                kameHouse.logger.warn("Mobile config file read from file is invalid. Re generating it");
                reGenerateMobileConfigFile();
              }
              setKameHouseMobileModuleLoaded();
            };
            reader.readAsText(file);
          }, errorCallback);
        }, errorCallback);
      }
    
      function errorCallback(error) {
        kameHouse.logger.info("Error reading file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
        setKameHouseMobileModuleLoaded();
        createMobileConfigFile();
      }
    } catch (error) {
      kameHouse.logger.info("Error reading file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      setKameHouseMobileModuleLoaded();
      createMobileConfigFile();
    }
  }	
  
  /**
   * Delete kamehouse-mobile config file.
   */
   function deleteMobileConfigFile() {
    kameHouse.logger.info("Deleting file " + mobileConfigFile);
    try {
      window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, successCallback, errorCallback);
  
      function successCallback(fs) {
        fs.root.getFile(mobileConfigFile, {create: false}, (fileEntry) => {
          fileEntry.remove(() => {
            kameHouse.logger.info("File " + fileEntry.name + " deleted successfully");
          }, errorCallback);
        }, errorCallback);
      }
    
      function errorCallback(error) {
        kameHouse.logger.info("Error deleting file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      }
    } catch (error) {
      kameHouse.logger.info("Error deleting file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
    }
  }

  /**
   * Re generate kamehouse-mobile config file.
   */
  function reGenerateMobileConfigFile() {
    if (isCurrentlyPersistingConfig) {
      kameHouse.logger.info("A regenerate file is already in progress, skipping this call");
      return;
    }
    isCurrentlyPersistingConfig = true;
    kameHouse.logger.info("Regenerating file " + mobileConfigFile);
    try {
      window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, successDeleteFileCallback, errorDeleteFileCallback);
  
      function successDeleteFileCallback(fs) {
        fs.root.getFile(mobileConfigFile, {create: false}, (fileEntry) => {
          fileEntry.remove(() => {
            kameHouse.logger.info("File " + fileEntry.name + " deleted successfully");
            createFile();
          }, errorDeleteFileCallback);
        }, errorDeleteFileCallback);
      }
    
      function errorDeleteFileCallback(error) {
        kameHouse.logger.info("Error deleting file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
        createFile();
      }

      function createFile() {
        window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, successCreateFileCallback, errorCreateFileCallback);
      }

      function successCreateFileCallback(fs) {
        fs.root.getFile(mobileConfigFile, {create: true, exclusive: true}, (fileEntry) => {
          kameHouse.logger.info("File " + fileEntry.name + " created successfully");
          writeFile();
        }, errorCreateFileCallback);
      }

      function errorCreateFileCallback(error) {
        kameHouse.logger.info("Error creating file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
        writeFile();
      }

      function writeFile() {
        window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, successWriteFileCallback, errorWriteFileCallback);
      }

      function successWriteFileCallback(fs) {
        fs.root.getFile(mobileConfigFile, {create: true}, (fileEntry) => {
          fileEntry.createWriter((fileWriter) => {
            const fileContent = JSON.stringify(getMobileConfig());
            kameHouse.logger.info("File content to write: " + fileContent);
            const blob = new Blob([fileContent]);
            fileWriter.write(blob);
            isCurrentlyPersistingConfig = false;
          }, errorCreateFileCallback);
        }, errorCreateFileCallback);
      }

      function errorWriteFileCallback(error) {
        kameHouse.logger.info("Error writing file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
        isCurrentlyPersistingConfig = false;
      }

    } catch (error) {
      kameHouse.logger.info("Error regenerating file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      isCurrentlyPersistingConfig = false;
    }
  }

  /**
   * Update the mobile config from the view in the config tab.
   */
  function updateMobileConfigFromView() {
    kameHouse.logger.info("Updating mobile config from view");
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
    if (!kameHouse.core.isEmpty(inAppBrowserTargetDropdown.value)) {
      inAppBrowserConfig.target = inAppBrowserTargetDropdown.value;
    }

    // Set InAppBrowser options clearcache
    const inAppBrowserClearCacheCheckbox = document.getElementById("iab-clearcache-checkbox");
    if (inAppBrowserClearCacheCheckbox.checked) {
      inAppBrowserConfig.options = inAppBrowserConfig.options.replace("clearcache=no", "clearcache=yes");
    } else {
      inAppBrowserConfig.options = inAppBrowserConfig.options.replace("clearcache=yes", "clearcache=no");
    }

    // Update credentials
    updateBackendCredentials();

    kameHouse.logger.info("servers: " + JSON.stringify(getServers()));
    kameHouse.logger.info("inAppBrowser.options: " + inAppBrowserConfig.options);
    kameHouse.logger.info("inAppBrowser.target: " + inAppBrowserConfig.target);
    kameHouse.logger.info("inAppBrowser.openOnStartup: " + inAppBrowserConfig.openOnStartup);
    kameHouse.logger.info("credentials: " + JSON.stringify(getCredentials()));
    reGenerateMobileConfigFile();
  }

  /**
   * Update backend credentials from the input fields.
   */
  function updateBackendCredentials() {
    const credentials = getCredentials();
    const username = document.getElementById("backend-username-input").value;
    if (!kameHouse.core.isEmpty(username)) {
      credentials.username = username;
    }
    const password = document.getElementById("backend-password-input").value; 
    if (!kameHouse.core.isEmpty(password)) {
      credentials.password = password;
    }
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
    kameHouse.logger.info("Setting web vlc player from dropdown");
    const webVlcServerInput = document.getElementById("web-vlc-server-input"); 
    const webVlcServerDropdown = document.getElementById("web-vlc-server-dropdown");
    if (!kameHouse.core.isEmpty(webVlcServerDropdown.value)) {
      kameHouse.util.dom.setValue(webVlcServerInput, webVlcServerDropdown.value);
      updateMobileConfigFromView();
    }
  }

  /** Set the backend server in the config from the dropdown menu in the config page */
  function setBackendFromDropdown() {
    kameHouse.logger.info("Setting backend server from dropdown");
    const backendServerInput = document.getElementById("backend-server-input"); 
    const backendServerDropdown = document.getElementById("backend-server-dropdown");
    if (!kameHouse.core.isEmpty(backendServerDropdown.value)) {
      kameHouse.util.dom.setValue(backendServerInput, backendServerDropdown.value);
      updateMobileConfigFromView();
    }    
  }

  /**
   * Refresh config tab view values.
   */
  function refreshConfigTabView() {
    kameHouse.logger.info("Refreshing config tab view values");
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

    setBackendCredentialsInput();
  }

  /**
   * Set the credentials view from the config.
   */
  function setBackendCredentialsInput() {
    const credentials = getCredentials();
    const usernameInput = document.getElementById("backend-username-input");
    if (!kameHouse.core.isEmpty(credentials.username)) {
      kameHouse.util.dom.setValue(usernameInput, credentials.username);
    }
    const passwordInput = document.getElementById("backend-password-input"); 
    if (!kameHouse.core.isEmpty(credentials.password)) {
      kameHouse.util.dom.setValue(passwordInput, credentials.password);
    }
  }

  /**
   * Set the server input field value in the view from the config.
   */
  function setServerInput(serverName) {
    const servers = getServers();
    const server = servers.find(server => server.name === serverName);
    const serverInput = document.getElementById(serverName + "-server-input");
    kameHouse.util.dom.setValue(serverInput, server.url);
  }

  /**
   * Open confirm reset config modal.
   */
  function confirmResetDefaults() {
    kameHouse.plugin.modal.basicModal.setHtml(getResetConfigModalMessage());
    kameHouse.plugin.modal.basicModal.open();
  }
  
  /**
   * Get the message to reset the config.
   */
  function getResetConfigModalMessage() {
    const resetConfigModalMessage = kameHouse.util.dom.getSpan({}, "Are you sure you want to reset the configuration? ");
    kameHouse.util.dom.append(resetConfigModalMessage, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(resetConfigModalMessage, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(resetConfigModalMessage, getConfirmResetConfigButton());
    return resetConfigModalMessage;
  }

  /**
   * Get the button to confirm resetting the config.
   */
  function getConfirmResetConfigButton() {
    return kameHouse.util.dom.getButton({
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
    kameHouse.logger.info("Resetting config to default values");
    setServers(JSON.parse(JSON.stringify(serversDefaultConfig)));
    setInAppBrowserConfig(JSON.parse(JSON.stringify(inAppBrowserDefaultConfig)));
    setCredentials(JSON.parse(JSON.stringify(credentialsDefaultConfig)));
    refreshConfigTabView();
    reGenerateMobileConfigFile();
    kameHouse.plugin.modal.basicModal.close();
    kameHouse.plugin.modal.basicModal.openAutoCloseable("Config reset to default values", 2000);
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

  this.plugin = {};
  this.plugin.http = new CordovaHttpPluginMock();
  this.InAppBrowser = new CordovaInAppBrowserMock();
  
  function CordovaHttpPluginMock() {
    this.setServerTrustMode = setServerTrustMode;
    this.sendRequest = sendRequest;
    this.useBasicAuth = useBasicAuth;
    this.setDataSerializer = setDataSerializer;

    function setServerTrustMode(trustMode, successCallback) {
      kameHouse.logger.info("Called setServerTrustMode on cordova mock with " + trustMode);
      successCallback();
    }

    function sendRequest(requestUrl, options, successCallback, errorCallback) {
      kameHouse.logger.info("Called sendRequest on cordova mock with requestUrl: " + requestUrl + " and options " + JSON.stringify(options) + ". Mocking error response");
      const mockResponse = {
        error : '{"message":"mocked cordova http error response"}',
        status: 999
      };
      errorCallback(mockResponse);
    }

    function useBasicAuth() {
      kameHouse.logger.info("Called useBasicAuth on cordova mock");
    }

    function setDataSerializer(serializationType) {
      kameHouse.logger.info("Called setDataSerializer on cordova mock with " + serializationType);
    }
  }

  /**
   * Mock of an InAppBrowser.
   */
  function CordovaInAppBrowserMock() {

    this.open = open;

    function open(url, target, options) {
      kameHouse.logger.info("Called open in InAppBrowserMock with url " + url);
      setTimeout(() => {
        alert("cordova.InAppBrowser.open() call with:\n\nurl:\n" + url + "\n\ntarget:\n" + target + "\n\noptions:\n" + options);
      }, 100);
      setTimeout(() => {
        kameHouse.logger.info("Simulating successful exit event from InAppBrowserInstanceMock closing the modal");
        kameHouse.plugin.modal.basicModal.close(); 
        kameHouse.plugin.modal.basicModal.reset();
      }, 3000);
      return new CordovaInAppBrowserInstanceMock();
    }
  }

  /**
   * Mock of a InAppBrowserInstance.
   */
  function CordovaInAppBrowserInstanceMock() {

    this.addEventListener = addEventListener;
    this.close = close;
    this.show = show;

    function addEventListener(eventName, callback) {
      kameHouse.logger.info("Called addEventListener on the InAppBrowserInstanceMock for event " + eventName);
    }

    function show() {
      kameHouse.logger.info("Called show on the InAppBrowserInstanceMock");
    }

    function close() {
      kameHouse.logger.info("Called close on the InAppBrowserInstanceMock");
    }
  }
}

$(document).ready(() => {
  kameHouse.addExtension("mobile", new KameHouseMobile());
});