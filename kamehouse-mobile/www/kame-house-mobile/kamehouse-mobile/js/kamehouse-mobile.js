
/**
 * Global functions for kamehouse-mobile app.
 * 
 * @author nbrest
 */
function KameHouseMobile() {
  this.load = load;

  function load() {
    kameHouse.logger.info("Started initializing kamehouse-mobile.js");
    kameHouse.extension.mobile.core = new KameHouseMobileCore();
    kameHouse.extension.mobile.core.init();
    kameHouse.extension.mobile.configManager = new KameHouseMobileConfigManager();
    kameHouse.extension.mobile.configManager.init();
  }
} 

/**
 * Functionality for the native kamehouse mobile app.
 */
function KameHouseMobileCore() {

  this.init = init;
  this.disableWebappOnlyElements = disableWebappOnlyElements;
  this.getBackendServer = getBackendServer;
  this.testBackendConnectivity = testBackendConnectivity;
  this.mobileHttpRequst = mobileHttpRequst;
  this.setMobileBuildVersion = setMobileBuildVersion;
  this.openBrowser = openBrowser;
  this.overrideWindowOpen = overrideWindowOpen;

  const GET = "GET";
  const POST = "POST";
  const PUT = "PUT";
  const DELETE = "DELETE";
  const DEFAULT_TIMEOUT_SECONDS = 60;
  
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
    if (!kameHouse.core.isEmpty(mobileConfig) && !kameHouse.core.isEmpty(mobileConfig.backend)
          && !kameHouse.core.isEmpty(mobileConfig.backend.servers)) {
        mobileConfig.backend.servers.forEach((server) => {
        if (server.name === mobileConfig.backend.selected) {
          backendServer = server.url;
        }
      });
    }
    if (backendServer == null) {
      const message = "Couldn't find backend server url in the config. Mobile app config manager may not have completed initialization yet.";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
    }
    return backendServer;
  }

  function getBackendCredentials() {
    const mobileConfig = kameHouse.extension.mobile.config;
    const credentials = {};
    if (!kameHouse.core.isEmpty(mobileConfig) && !kameHouse.core.isEmpty(mobileConfig.backend)
          && !kameHouse.core.isEmpty(mobileConfig.backend.servers)) {
        mobileConfig.backend.servers.forEach((server) => {
        if (server.name === mobileConfig.backend.selected) {
          kameHouse.logger.debug("Selecting credentials for username: " + server.username + " and server: " + server.name);
          credentials.username = server.username;
          credentials.password = server.password;
        }
      });
    } else {
      const message = "Unable to get backend credentials. Mobile config manager may not be initialized yet";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
    }
    return credentials;
  }

  function testBackendConnectivity() {
    kameHouse.logger.info("Testing backend connectivity");
    kameHouse.plugin.modal.loadingWheelModal.open("Testing login credentials...");
    const LOGIN_URL = "/kame-house/login";
    const credentials = getBackendCredentials();
    const loginData = {
      username : credentials.username,
      password : credentials.password
    }
    const config = kameHouse.http.getConfig();
    config.timeout = 15;
    kameHouse.plugin.debugger.http.post(config, LOGIN_URL, kameHouse.http.getUrlEncodedHeaders(), loginData,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.plugin.modal.loadingWheelModal.close();
        if (responseBody.includes("KameHouse - Login")) {
          const message = "Backend connectivity test error - redirected back to login";
          kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
          kameHouse.plugin.modal.basicModal.openAutoCloseable(getBackentConectivityErrorModalHtml(" Invalid credentials"), 1000);
          return;
        }
        kameHouse.logger.info("Backend connectivity test successful");
        kameHouse.plugin.modal.basicModal.openAutoCloseable(getBackentConectivitySuccessModalHtml(" Success!"), 1000);
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.plugin.modal.loadingWheelModal.close();
        if (responseCode == 401 || responseCode == 403) {
          const message = "Backend connectivity test error - invalid credentials";
          kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
          kameHouse.plugin.modal.basicModal.openAutoCloseable(getBackentConectivityErrorModalHtml(" Invalid credentials"), 1000);
          return;
        }
        const message = "Error connecting to the backend. Response code: " + responseCode;
        kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
        kameHouse.plugin.modal.basicModal.openAutoCloseable(getBackentConectivityErrorModalHtml(message), 2000);
      }
    );
  }

  function getBackentConectivitySuccessModalHtml(message) {
    const img = kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/dbz/goku-gray-dark.png",
      className: "img-btn-kh",
      alt: "Success modal",
      onClick: () => {return;}
    });
    const div = kameHouse.util.dom.getDiv();
    kameHouse.util.dom.append(div, img);
    kameHouse.util.dom.append(div, message);
    return div;
  }

  function getBackentConectivityErrorModalHtml(message) {
    const img = kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/other/delete-red.png",
      className: "img-btn-kh",
      alt: "Error modal",
      onClick: () => {return;}
    });
    const div = kameHouse.util.dom.getDiv();
    kameHouse.util.dom.append(div, img);
    kameHouse.util.dom.append(div, message);
    return div;
  }

  /** 
   * Http request to be sent from the mobile app.
   */
  function mobileHttpRequst(httpMethod, config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    let requestUrl = getBackendServer() + url;   
    const options = {
      method: httpMethod,
      data: ""
    };
    
    if (!kameHouse.core.isEmpty(requestHeaders)) {
      options.headers = requestHeaders;
    }
    setMobileBasicAuthHeader(config);
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
    setMobileTimeout(config);
    cordova.plugin.http.setServerTrustMode('nocheck',
     () => {
      cordova.plugin.http.sendRequest(requestUrl, options, 
        (response) => { processMobileSuccess(response, successCallback); } ,
        (response) => { processMobileError(response, errorCallback); }
      );
    }, () => {
      const message = "Error setting cordova ssl trustmode to nocheck";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
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
     errorCallback(responseBody, responseCode, responseDescription, responseHeaders);
  }  

  function isJsonResponse(headers) {
    if (kameHouse.core.isEmpty(headers)) {
      return false;
    }
    let isJson = false;
    for (const [key, value] of Object.entries(headers)) {
      if (!kameHouse.core.isEmpty(key) && key.toLowerCase() == "content-type" 
        && !kameHouse.core.isEmpty(value) && value.toLowerCase() == "application/json") {
          isJson = true;
      }
    }
    return isJson;
  }

  /**
   * Set request timeout. Only supported for android in the cordova http plugin at the moment.
   */
  function setMobileTimeout(config) {
    if (!kameHouse.core.isEmpty(config.timeout)) {
      kameHouse.logger.debug("Setting timeout for mobile http request");
      cordova.plugin.http.setRequestTimeout(config.timeout);
      cordova.plugin.http.setReadTimeout(config.timeout);
    } else {
      kameHouse.logger.debug("Using default timeout for mobile http request");
      cordova.plugin.http.setRequestTimeout(DEFAULT_TIMEOUT_SECONDS);
    }
  }

  /**
   * Set basic auth header.
   */
  function setMobileBasicAuthHeader(config) {
    if (config.sendBasicAuthMobile) {
      const credentials = getBackendCredentials();
      if (!kameHouse.core.isEmpty(credentials.username) && !kameHouse.core.isEmpty(credentials.password)) {
        kameHouse.logger.debug("Setting basicAuth header for mobile http request with username: " + credentials.username);
        cordova.plugin.http.useBasicAuth(credentials.username, credentials.password);
      }
    } else {
      kameHouse.logger.debug("Unsetting basicAuth header for mobile http request");
      cordova.plugin.http.setHeader('Authorization', null);
    }
  }

  /**
   * Set data serializer.
   */
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
    + "'options' : '" + kameHouse.logger.maskSensitiveData(JSON.stringify(options)) + "' ]");
  }

  function setMobileBuildVersion() { 
    setAppVersion();
    setGitCommitHash();
    setBuildDate();
  }

  async function setAppVersion() {
    const pom = await kameHouse.util.fetch.loadFile('/kame-house-mobile/pom.xml');
    const versionPrefix = "<version>";
    const versionSuffix = "-KAMEHOUSE-SNAPSHOT";
    const tempVersion = pom.slice(pom.indexOf(versionPrefix) + versionPrefix.length);
    const appVersion = tempVersion.slice(0, tempVersion.indexOf(versionSuffix));
    kameHouse.logger.info("Mobile app version: " + appVersion);
    const mobileBuildVersion = document.getElementById("mobile-build-version");
    kameHouse.util.dom.setInnerHtml(mobileBuildVersion, appVersion);
  }

  async function setGitCommitHash() {
    const gitHash = await kameHouse.util.fetch.loadFile('/kame-house-mobile/git-commit-hash.txt');
    kameHouse.logger.info("Mobile git hash: " + gitHash);
    const gitHashDiv = document.getElementById("mobile-git-hash");
    kameHouse.util.dom.setInnerHtml(gitHashDiv, gitHash);
  }

  async function setBuildDate() {
    const buildDate = await kameHouse.util.fetch.loadFile('/kame-house-mobile/build-date.txt');
    kameHouse.logger.info("Mobile build date: " + buildDate);
    const buildDateDiv = document.getElementById("mobile-build-date");
    kameHouse.util.dom.setInnerHtml(buildDateDiv, buildDate);
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
   * Get complete url from lookup.
   * 
   * @deprecated.
   */
  function getServerUrl(urlLookup) {
    const server = kameHouse.extension.mobile.configManager.getBackend().servers.find(server => server.name === urlLookup);
    const serverEntity = {};
    serverEntity.name = server.name;
    serverEntity.url = server.url;
    kameHouse.logger.trace("Server entity: " + JSON.stringify(serverEntity));
    return serverEntity;
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
  this.reGenerateMobileConfigFile = reGenerateMobileConfigFile;
  this.updateMobileConfigFromView = updateMobileConfigFromView;
  this.setBackendViewFromDropdown = setBackendViewFromDropdown;
  this.refreshBackendServerViewFromConfig = refreshBackendServerViewFromConfig;
  this.confirmResetDefaults = confirmResetDefaults;
  this.resetDefaults = resetDefaults;
  this.getMobileConfigSelectedBackendServer = getMobileConfigSelectedBackendServer;

  const mobileConfigFile = "kamehouse-mobile-config.json";
  const mobileConfigFileType = window.PERSISTENT;
  const mobileConfigFileSize = 1*1024*1024; //1 mb

  let isCurrentlyPersistingConfig = false;
  let backendDefaultConfig = null;
  let encryptionKey = null;

  function init() {
    // waitFor kameHouseDebugger fixed vlc player page not loading the proper credentials on mobile app
    kameHouse.util.module.waitForModules(["kameHouseDebugger"], async () => {
      kameHouse.logger.info("Initializing mobile config manager");
      initGlobalMobileConfig();
      await loadEncryptionKey();
      await loadBackendDefaultConfig();
      readMobileConfigFile();
    });
  }

  function setKameHouseMobileModuleLoaded() {
    kameHouse.logger.info("Finished kameHouseMobile module initialization");
    kameHouse.util.module.setModuleLoaded("kameHouseMobile");
  }

  function initGlobalMobileConfig() {
    kameHouse.extension.mobile.config = {};
    kameHouse.extension.mobile.config.backend = {};
  }

  function getMobileConfig() {
    return kameHouse.extension.mobile.config;
  }

  function setMobileConfig(val) {
    kameHouse.extension.mobile.config = val;
  }

  function getMobileConfigBackend() {
    return kameHouse.extension.mobile.config.backend;
  }

  function setMobileConfigBackend(val) {
    kameHouse.extension.mobile.config.backend = val;
  }

  function getMobileConfigSelectedBackendServer() {
    const backend = getMobileConfigBackend();
    let backendServer = null;
    if (!kameHouse.core.isEmpty(backend) && !kameHouse.core.isEmpty(backend.servers)) {
        backend.servers.forEach((server) => {
        if (server.name === backend.selected) {
          backendServer = server;
        }
      });
    }
    if (backendServer == null) {
      const message = "Couldn't find backend server in the config";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
    } else {
      kameHouse.logger.info("Selected backend server from the config: " + kameHouse.logger.maskSensitiveData(JSON.stringify(backendServer)));      
    }
    return backendServer;
  }

  async function loadBackendDefaultConfig() {
    backendDefaultConfig = JSON.parse(await kameHouse.util.fetch.loadFile('/kame-house-mobile/json/config/backend.json'));
    kameHouse.logger.info("backend default config: " + kameHouse.logger.maskSensitiveData(JSON.stringify(backendDefaultConfig)));
    setMobileConfigBackend(JSON.parse(JSON.stringify(backendDefaultConfig)));
  }
  
  async function loadEncryptionKey() {
    encryptionKey = await kameHouse.util.fetch.loadFile('/kame-house-mobile/encryption.key');
    kameHouse.logger.info("Loaded encryption key");
  }

  /**
   * Returns true if the config has all the required properties.
   */
  function isValidMobileConfigFile(mobileConfig) {
    return mobileConfig != null 
      && mobileConfig.backend != null;
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
          try {
          const fileContent = JSON.stringify(getMobileConfig());
          kameHouse.logger.info("Encrypting file");
          const encryptedFileContent = CryptoJS.AES.encrypt(fileContent, encryptionKey).toString();
          kameHouse.logger.info("File content to write: " + fileContent);
          const blob = new Blob([encryptedFileContent]);
          fileWriter.write(blob);
          } catch(e) {
            kameHouse.logger.info("Error writing config file " + mobileConfigFile + ". Error: " + JSON.stringify(e)); 
          }
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
      kameHouse.logger.error("Error reading file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
      setKameHouseMobileModuleLoaded();
      createMobileConfigFile();
    }
  
    // readMobileConfigFile callback
    function successCallback(fs) {
      fs.root.getFile(mobileConfigFile, {}, function(fileEntry) {
        fileEntry.file(function(file) {
          const reader = new FileReader();
          reader.onloadend = function(e) {
            let mobileConfig = null;
            try {
              const encryptedFileContent = this.result;
              kameHouse.logger.info("Decrypting file");
              const fileContent = CryptoJS.AES.decrypt(encryptedFileContent, encryptionKey).toString(CryptoJS.enc.Utf8);
              kameHouse.logger.info("File content read: " + kameHouse.logger.maskSensitiveData(fileContent));
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
    kameHouse.logger.info("Updating entire mobile config file from view");
    updateSelectedBackendServerInConfig();
    updateBackendServerUrlInConfig();
    updateBackendServerCredentialsInConfig();
    kameHouse.logger.info("Mobile config: " + kameHouse.logger.maskSensitiveData(JSON.stringify(getMobileConfig())));
    reGenerateMobileConfigFile(false);
  }

  /** Set the backend server in the view from the selected dropdown. then trigger a mobile config update */
  function setBackendViewFromDropdown() {
    kameHouse.logger.info("Setting backend server view from dropdown");
    const backendServerInput = document.getElementById("backend-server-input"); 
    const backendServerDropdown = document.getElementById("backend-server-dropdown");
    kameHouse.util.dom.setValue(backendServerInput, backendServerDropdown.value);
    updateSelectedBackendServerInConfig();
    refreshBackendServerViewFromConfig();
    updateMobileConfigFromView();
  }

  /**
   * Refresh backend server tab view values from the config.
   */
  function refreshBackendServerViewFromConfig() {
    kameHouse.logger.info("Refreshing settings tab view values from the config");
    const selectedServer = getMobileConfigSelectedBackendServer();

    const usernameInput = document.getElementById("backend-username-input");
    kameHouse.util.dom.setValue(usernameInput, selectedServer.username);
    const passwordInput = document.getElementById("backend-password-input"); 
    kameHouse.util.dom.setValue(passwordInput, selectedServer.password);

    const backendServerInput = document.getElementById("backend-server-input");
    const backendServerDropdown = document.getElementById("backend-server-dropdown");

    if (backendServerInput.value != "") {
      backendServerDropdown.options[backendServerDropdown.options.length-1].selected = true;
    }
    const editableServers = ["WiFi Hotspot", "Dev Intellij", "Dev Eclipse", "Dev Tomcat HTTP", "Custom Server"];
    for (let i = 0; i < backendServerDropdown.options.length; ++i) {
      if (backendServerDropdown.options[i].textContent === selectedServer.name) {
        backendServerDropdown.options[i].selected = true;
        backendServerInput.value = selectedServer.url;
        if (editableServers.includes(backendServerDropdown.options[i].textContent)) {
          backendServerInput.readOnly = false;
        } else {
          backendServerInput.readOnly = true;
        }
      } else {
        backendServerDropdown.options[i].selected = false;
      }
    }
  }

  function updateSelectedBackendServerInConfig() {
    // Update backend.selected in config
    const backend = getMobileConfigBackend();
    const backendServerDropdown = document.getElementById("backend-server-dropdown");
    for (let i = 0; i < backendServerDropdown.options.length; ++i) {
      if (backendServerDropdown.options[i].selected == true) {
        kameHouse.logger.info("Setting selected backend in the config to: " + backendServerDropdown.options[i].textContent);
        backend.selected = backendServerDropdown.options[i].textContent;
      }
    }
  }

  function updateBackendServerUrlInConfig() {
    // Update backend.servers[selected].url (for editable servers) in config
    const selectedBackendServer = getMobileConfigSelectedBackendServer();
    const backendServerInput = document.getElementById("backend-server-input");
    kameHouse.logger.info("Setting selected backend server url in the config to: " + backendServerInput.value);
    selectedBackendServer.url = backendServerInput.value;
  }

  function updateBackendServerCredentialsInConfig() {
    // Update backend.servers[] selected server credentials in config
    const selectedBackendServer = getMobileConfigSelectedBackendServer();
    const username = document.getElementById("backend-username-input").value;
    kameHouse.logger.info("Setting selected backend server username in the config to: " + username);
    selectedBackendServer.username = username;
    const password = document.getElementById("backend-password-input").value; 
    selectedBackendServer.password = password;
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
    kameHouse.plugin.modal.basicModal.close();
    initGlobalMobileConfig();
    setMobileConfigBackend(JSON.parse(JSON.stringify(backendDefaultConfig)));
    reGenerateMobileConfigFile(false);
    kameHouse.plugin.modal.basicModal.openAutoCloseable("Settings reset to defaults", 1000);
    refreshBackendServerViewFromConfig();
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
      kameHouse.logger.error("Error regenerating file " + mobileConfigFile + ". Error: " + JSON.stringify(error));
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
              try {
                const fileContent = JSON.stringify(getMobileConfig());
                kameHouse.logger.info("Encrypting file");
                const encryptedFileContent = CryptoJS.AES.encrypt(fileContent, encryptionKey).toString();
                kameHouse.logger.info("File content to write: " + kameHouse.logger.maskSensitiveData(fileContent));
                const blob = new Blob([encryptedFileContent]);
                fileWriter.write(blob);
                isCurrentlyPersistingConfig = false;
                if (openResultModal) {
                  kameHouse.plugin.modal.basicModal.openAutoCloseable("Settings saved", 1000);
                }
              } catch(e) {
                kameHouse.logger.info("Error writing config file " + mobileConfigFile + ". Error: " + JSON.stringify(e)); 
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
    this.setHeader = setHeader;
    this.setRequestTimeout = setRequestTimeout;
    this.setReadTimeout = setReadTimeout;

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

    function setHeader(key, value) {
      kameHouse.logger.info("Called setHeader on cordova mock with " + key + ":" + value);
    }

    function setRequestTimeout(val) {
      kameHouse.logger.info("Called setHeader on cordova mock with " + val);
    }

    function setReadTimeout(val) {
      kameHouse.logger.info("Called setHeader on cordova mock with " + val);
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