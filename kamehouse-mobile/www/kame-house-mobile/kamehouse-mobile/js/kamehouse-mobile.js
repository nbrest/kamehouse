
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
    kameHouse.extension.mobile.core.init();
    kameHouse.extension.mobile.configManager = new KameHouseMobileConfigManager();
    await kameHouse.extension.mobile.configManager.init();
  }
} 

/**
 * Functionality for the native kamehouse mobile app.
 */
function KameHouseMobileCore() {

  this.init = init;
  this.disableWebappOnlyElements = disableWebappOnlyElements;
  this.getBackendServer = getBackendServer;
  this.getBackendCredentials = getBackendCredentials;
  this.testBackendConnectivity = testBackendConnectivity;
  this.mobileHttpRequst = mobileHttpRequst;
  this.openBrowser = openBrowser;
  this.overrideWindowOpen = overrideWindowOpen;

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

  function testBackendConnectivity() {
    kameHouse.logger.info("Testing backend connectivity");
    kameHouse.plugin.modal.loadingWheelModal.open();
    const LOGIN_URL = "/kame-house/login";
    const credentials = getBackendCredentials();
    const loginData = {
      username : credentials.username,
      password : credentials.password
    }
    kameHouse.plugin.debugger.http.post(LOGIN_URL, kameHouse.http.getUrlEncodedHeaders(), loginData,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.plugin.modal.loadingWheelModal.close();
        if (responseBody.includes("KameHouse - Login")) {
          kameHouse.logger.error("Backend connectivity test error - redirected back to login");
          kameHouse.plugin.modal.basicModal.openAutoCloseable("Invalid credentials", 1000);
          return;
        }
        kameHouse.logger.info("Backend connectivity test successful");
        kameHouse.plugin.modal.basicModal.openAutoCloseable("Success!", 1000);
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.plugin.modal.loadingWheelModal.close();
        if (responseCode == 401 || responseCode == 403) {
          kameHouse.logger.error("Backend connectivity test error - invalid credentials");
          kameHouse.plugin.modal.basicModal.openAutoCloseable("Invalid credentials", 1000);
          return;
        }
        kameHouse.logger.error("Error connecting to the backend. Response code: " + responseCode);
        kameHouse.plugin.modal.basicModal.openAutoCloseable("Error connecting to the backend. Response code: " + responseCode, 2000);
      }
    );
  }

  /** 
   * Http request to be sent from the mobile app.
   */
  function mobileHttpRequst(httpMethod, url, requestHeaders, requestBody, successCallback, errorCallback) {
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
        (response) => { processMobileSuccess(response, successCallback); } ,
        (response) => { processMobileError(response, errorCallback); }
      );
    }, () => {
      kameHouse.logger.error("Error setting cordova ssl trustmode to nocheck");
      cordova.plugin.http.sendRequest(requestUrl, options, 
        (response) => { processMobileSuccess(response, successCallback); } ,
        (response) => { processMobileError(response, errorCallback); }
      );
    });
  }

  /** Process a successful response from the api call */
  function processMobileSuccess(response, successCallback) {
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
    const responseHeaders = response.headers;
    kameHouse.logger.logHttpResponse(responseBody, responseCode, responseDescription, responseHeaders);
    successCallback(responseBody, responseCode, responseDescription, responseHeaders);
  }

  /** Process an error response from the api call */
  function processMobileError(response, errorCallback) {
     /**
     * error: error message
     * status: http status code
     * url: request url
     * response headers: header map object
      */
     const responseBody = response.error;
     const responseCode = response.status;
     const responseDescription = null;
     const responseHeaders = response.headers;
     kameHouse.logger.logApiError(responseBody, responseCode, responseDescription, responseHeaders, null);
     errorCallback(JSON.stringify(responseBody), responseCode, responseDescription, responseHeaders);
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
   * Get complete url from lookup.
   */
  function getServerUrl(urlLookup) {
    const server = kameHouse.extension.mobile.configManager.getServers().find(server => server.name === urlLookup);
    const serverEntity = {};
    serverEntity.name = server.name;
    serverEntity.url = server.url;
    kameHouse.logger.trace("Server entity: " + JSON.stringify(serverEntity));
    return serverEntity;
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
   * @deprecated
   */
  function overrideWindowOpen() {
    window.open = cordova.InAppBrowser.open;
  }

  /**
   * Open inAppBrowser with
   * @deprecated
   */
  function openBrowser(urlLookup) {
    const serverEntity = getServerUrl(urlLookup);
    openInAppBrowser(serverEntity);
  }

  /**
   * Open the InAppBrowser with the specified url.
   * @deprecated
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
   * @deprecated
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
   * @deprecated
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
  this.reGenerateMobileConfigFile = reGenerateMobileConfigFile;
  this.updateMobileConfigFromView = updateMobileConfigFromView;
  this.setBackendFromDropdown = setBackendFromDropdown;
  this.refreshSettingsView = refreshSettingsView;
  this.confirmResetDefaults = confirmResetDefaults;
  this.resetDefaults = resetDefaults;

  const mobileConfigFile = "kamehouse-mobile-config.json";
  const mobileConfigFileType = window.PERSISTENT;
  const mobileConfigFileSize = 5*1024*1024; //50 mb

  let isCurrentlyPersistingConfig = false;
  let serversDefaultConfig = null;
  let credentialsDefaultConfig = null;

  async function init() {
    kameHouse.logger.info("Initializing mobile config manager");
    initGlobalMobileConfig();
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
    kameHouse.extension.mobile.config.servers = {};
    kameHouse.extension.mobile.config.credentials = {};
  }

  function getMobileConfig() {
    return kameHouse.extension.mobile.config;
  }

  function setMobileConfig(val) {
    kameHouse.extension.mobile.config = val;
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
      && mobileConfig.servers != null 
      && mobileConfig.credentials != null;
  }

  /**
   * Create kamehouse-mobile config file in the device's storage.
   */
  function createMobileConfigFile() {
    kameHouse.logger.info("Creating file " + mobileConfigFile);
    try {
      window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, 
        successCallback, 
        errorCallback
      );
    } catch (error) {
      kameHouse.logger.info("Error creating file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
    }

    // createMobileConfigFile callback
    function successCallback(fs) {
      fs.root.getFile(mobileConfigFile, {create: true, exclusive: true}, (fileEntry) => {
        kameHouse.logger.info("File " + fileEntry.name + " created successfully");
      }, errorCallback);
    }
  
    // createMobileConfigFile callback
    function errorCallback(error) {
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
    } catch (error) {
      kameHouse.logger.info("Error writing file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
    }

    // writeMobileConfigFile callback
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
  
    // writeMobileConfigFile callback
    function errorCallback(error) {
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
    } catch (error) {
      kameHouse.logger.info("Error reading file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      setKameHouseMobileModuleLoaded();
      createMobileConfigFile();
    }
  
    // readMobileConfigFile callback
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
              reGenerateMobileConfigFile(false);
            }
            setKameHouseMobileModuleLoaded();
          };
          reader.readAsText(file);
        }, errorCallback);
      }, errorCallback);
    }
  
    // readMobileConfigFile callback
    function errorCallback(error) {
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
    } catch (error) {
      kameHouse.logger.info("Error deleting file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
    }

    // deleteMobileConfigFile callback
    function successCallback(fs) {
      fs.root.getFile(mobileConfigFile, {create: false}, (fileEntry) => {
        fileEntry.remove(() => {
          kameHouse.logger.info("File " + fileEntry.name + " deleted successfully");
        }, errorCallback);
      }, errorCallback);
    }
  
    // deleteMobileConfigFile callback
    function errorCallback(error) {
      kameHouse.logger.info("Error deleting file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
    }
  }

  /**
   * Update the mobile config file from the view in the config tab.
   */
  function updateMobileConfigFromView() {
    kameHouse.logger.info("Updating mobile config file from view");

    // Set servers
    updateServer("backend");

    // Update credentials
    updateBackendCredentials();

    kameHouse.logger.info("servers: " + JSON.stringify(getServers()));
    kameHouse.logger.info("credentials: " + JSON.stringify(getCredentials()));
    reGenerateMobileConfigFile(false);
  }

  /**
   * Update backend credentials from the input fields.
   */
  function updateBackendCredentials() {
    const credentials = getCredentials();
    const username = document.getElementById("backend-username-input").value;
    credentials.username = username;
    const password = document.getElementById("backend-password-input").value; 
    credentials.password = password;
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
   * Refresh settings tab view values.
   */
  function refreshSettingsView() {
    kameHouse.logger.info("Refreshing settings tab view values");

    // servers
    setServerInput("backend");

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

    setBackendCredentialsInput();
  }

  /**
   * Set the credentials view from the config.
   */
  function setBackendCredentialsInput() {
    const credentials = getCredentials();
    const usernameInput = document.getElementById("backend-username-input");
    kameHouse.util.dom.setValue(usernameInput, credentials.username);
    const passwordInput = document.getElementById("backend-password-input"); 
    kameHouse.util.dom.setValue(passwordInput, credentials.password);
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
    initGlobalMobileConfig();
    setServers(JSON.parse(JSON.stringify(serversDefaultConfig)));
    setCredentials(JSON.parse(JSON.stringify(credentialsDefaultConfig)));
    reGenerateMobileConfigFile(false);
    refreshSettingsView();
    kameHouse.plugin.modal.basicModal.close();
    kameHouse.plugin.modal.basicModal.openAutoCloseable("Settings reset to defaults", 1000);
  }

  /**
   * Re generate kamehouse-mobile config file process.
   * 1. delete file if it exists
   * 2. recreate the file
   * 3. rewrite the file
   */
  /** 
   * 1. Delete the file 
   * */
  function reGenerateMobileConfigFile(openResultModal) {
    if (isCurrentlyPersistingConfig) {
      kameHouse.logger.info("A regenerate file is already in progress, skipping this call");
      return;
    }
    isCurrentlyPersistingConfig = true;
    kameHouse.logger.info("Regenerating file " + mobileConfigFile);
    try {
      // request file to delete
      window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, 
        (fs) => {deleteFile(fs, openResultModal);}, 
        (error) => {errorDeleteFileCallback(error, openResultModal);}
      );
    } catch (error) {
      kameHouse.logger.info("Error regenerating file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      isCurrentlyPersistingConfig = false;
      if (openResultModal) {
        kameHouse.plugin.modal.basicModal.openAutoCloseable("Error saving settings", 1000);
      }
    }

    // reGenerateMobileConfigFile success callback
    function deleteFile(fs, openResultModal) {
      fs.root.getFile(mobileConfigFile, {create: false}, 
        (fileEntry) => {
          fileEntry.remove(
            () => {
              kameHouse.logger.info("File " + fileEntry.name + " deleted successfully");
              requestRecreateFile(openResultModal);
            }, 
            (error) => {errorDeleteFileCallback(error, openResultModal)}
          );
        }, 
        (error) => {errorDeleteFileCallback(error, openResultModal)}
      );
    }

    // deleteFile error callback
    function errorDeleteFileCallback(error, openResultModal) {
      kameHouse.logger.info("Error deleting file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      requestRecreateFile(openResultModal);
    }
  }

  /**
   * 2. Recreate the file.
   */
  function requestRecreateFile(openResultModal) {
    window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, 
      (fs) => {recreateFile(fs, openResultModal);}, 
      (error) => {errorRecreateFileCallback(error, openResultModal);}
    );
  
    // requestRecreateFile success callback
    function recreateFile(fs, openResultModal) {
      fs.root.getFile(mobileConfigFile, {create: true, exclusive: true}, 
        (fileEntry) => {
          kameHouse.logger.info("File " + fileEntry.name + " recreated successfully");
          requestRewriteFile(openResultModal);
        }, 
        (error) => {errorRecreateFileCallback(error, openResultModal)}
      );
    }

    // recreateFile error callback
    function errorRecreateFileCallback(error, openResultModal) {
      kameHouse.logger.info("Error recreating file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      requestRewriteFile(openResultModal);
    }
  }

  /**
   * 3. Rewrite file.
   */
  function requestRewriteFile(openResultModal) {
    window.requestFileSystem(mobileConfigFileType, mobileConfigFileSize, 
      (fs) => {rewriteFile(fs, openResultModal);}, 
      (error) => {errorRewriteFileCallback(error, openResultModal);}
    );

    // requestRewriteFile success callback
    function rewriteFile(fs, openResultModal) {
      fs.root.getFile(mobileConfigFile, {create: true}, 
        (fileEntry) => {
          fileEntry.createWriter(
            (fileWriter) => {
              const fileContent = JSON.stringify(getMobileConfig());
              kameHouse.logger.info("File content to write: " + fileContent);
              const blob = new Blob([fileContent]);
              fileWriter.write(blob);
              isCurrentlyPersistingConfig = false;
              if (openResultModal) {
                kameHouse.plugin.modal.basicModal.openAutoCloseable("Settings saved", 1000);
              }
            }, 
            (error) => {errorRewriteFileCallback(error, openResultModal)}
          );
        }, 
        (error) => {errorRewriteFileCallback(error, openResultModal)}
      );
    }

    // rewriteFile error callback
    function errorRewriteFileCallback(error, openResultModal) {
      kameHouse.logger.info("Error rewriting file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      isCurrentlyPersistingConfig = false;
      if (openResultModal) {
        kameHouse.plugin.modal.basicModal.openAutoCloseable("Error saving settings", 1000);
      }
    }
  }

  /**
   * Test file operations.
   * @deprecated
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
   * @deprecated
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
   * @deprecated
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