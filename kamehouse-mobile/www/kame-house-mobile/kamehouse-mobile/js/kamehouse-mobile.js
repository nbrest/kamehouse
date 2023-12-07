
/**
 * Global functions for kamehouse-mobile app.
 * 
 * @author nbrest
 */
class KameHouseMobile {

  /**
   * Load the kamehouse mobile extension.
   */
  load() {
    kameHouse.logger.info("Started initializing kamehouse-mobile.js");
    kameHouse.extension.mobile.core = new KameHouseMobileCore();
    kameHouse.extension.mobile.core.init();
    kameHouse.extension.mobile.configManager = new KameHouseMobileConfigManager();
    kameHouse.extension.mobile.configManager.init();
  }

} // KameHouseMobile

/**
 * Functionality for the native kamehouse mobile app.
 * 
 * @author nbrest
 */
class KameHouseMobileCore {

  static #GET = "GET";
  static #PUT = "PUT";
  static #DELETE = "DELETE";
  static #DEFAULT_TIMEOUT_SECONDS = 60;

  #mockLocalhostServer = null;
  
  constructor() {
    this.#mockLocalhostServer = new MockLocalhostServer();
  }

  /**
   * Init kamehouse mobile core.
   */
  init() {
    this.#setCordovaMock();
    this.disableWebappOnlyElements();
  }

  /**
   * Disable webapp only elements.
   */
  disableWebappOnlyElements() {
    $(document).ready(() => {
      kameHouse.logger.debug("Disabling webapp only elements in mobile app view");
      const mobileOnlyElements = document.getElementsByClassName("kh-mobile-hidden");
      for (const mobileOnlyElement of mobileOnlyElements) {
        kameHouse.util.dom.classListAdd(mobileOnlyElement, "hidden-kh");
        kameHouse.util.dom.classListRemove(mobileOnlyElement, "kh-mobile-hidden");
      }
    });
  }

  /**
   * Login to the kamehouse server.
   */
  login() {
    kameHouse.logger.info("Logging in to KameHouse...");
    kameHouse.plugin.modal.loadingWheelModal.open("Logging in to KameHouse...");
    const LOGIN_URL = "/kame-house/login";
    const credentials = this.#getBackendCredentials();
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
          const message = "Login error - invalid credentials. Redirected back to login";
          kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
          kameHouse.plugin.modal.basicModal.openAutoCloseable(this.#getErrorModalHtml(" Invalid credentials"), 1000);
          return;
        }
        kameHouse.logger.info("Login successful");
        kameHouse.plugin.modal.basicModal.openAutoCloseable(this.#getSuccessModalHtml(" Success!"), 1000);
        this.#setSuccessfulLoginView();
        this.#setSuccessfulLoginConfig();
        kameHouse.extension.mobile.configManager.reGenerateMobileConfigFile(false);
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.plugin.modal.loadingWheelModal.close();
        if (responseCode == 401 || responseCode == 403) {
          const message = "Login error - invalid credentials";
          kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
          kameHouse.plugin.modal.basicModal.openAutoCloseable(this.#getErrorModalHtml(" Invalid credentials"), 1000);
          return;
        }
        const message = "Error connecting to the backend to login. Response code: " + responseCode;
        kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
        kameHouse.plugin.modal.basicModal.openAutoCloseable(this.#getErrorModalHtml(message), 2000);
      }
    );
  }

  /**
   * Logout of kamehouse server.
   */
  logout() {
    kameHouse.logger.info("Logging out of KameHouse");
    kameHouse.plugin.modal.loadingWheelModal.open("Logging out of KameHouse...");
    const LOGOUT_URL = "/kame-house/logout";
    const config = kameHouse.http.getConfig();
    config.timeout = 15;
    kameHouse.plugin.debugger.http.get(config, LOGOUT_URL, kameHouse.http.getUrlEncodedHeaders(), null,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.plugin.modal.loadingWheelModal.close();
        if (responseBody.includes("KameHouse - Login")) {
          kameHouse.logger.info("Logout successful");
          kameHouse.plugin.modal.basicModal.openAutoCloseable(this.#getSuccessModalHtml(" Success!"), 1000);
          this.#setSuccessfulLogoutView();
          this.#setSuccessfulLogoutConfig();
          kameHouse.extension.mobile.configManager.reGenerateMobileConfigFile(false);
          cordova.plugin.http.clearCookies();
          return;
        }
        const message = "Logout error: " + kameHouse.json.stringify(responseBody);
        kameHouse.logger.error(message);
        kameHouse.plugin.modal.basicModal.openAutoCloseable(this.#getErrorModalHtml(" Error logging out. Try again later"), 1000);
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.plugin.modal.loadingWheelModal.close();
        const message = "Error connecting to the backend to logout. Response code: " + responseCode;
        kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
        kameHouse.plugin.modal.basicModal.openAutoCloseable(this.#getErrorModalHtml(message), 2000);
      }
    );
  }  

  /** 
   * Http request to be sent from the mobile app.
   */
  mobileHttpRequst(httpMethod, config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    if (this.#isMockBackendSelected()) {
      return this.#mockLocalhostServer.httpRequest(httpMethod, config, url, requestHeaders, requestBody, successCallback, errorCallback);
    }
    let requestUrl = this.getSelectedBackendServerUrl() + url;   
    const options = {
      method: httpMethod,
      data: ""
    };
    
    if (!kameHouse.core.isEmpty(requestHeaders)) {
      options.headers = requestHeaders;
    }
    this.#setMobileBasicAuthHeader();
    this.#setDataSerializer(requestHeaders, httpMethod);
    if (kameHouse.http.isUrlEncodedRequest(requestHeaders)) {
      if (httpMethod == KameHouseMobileCore.#GET || httpMethod == KameHouseMobileCore.#PUT || httpMethod == KameHouseMobileCore.#DELETE) {
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
    this.#logMobileHttpRequest(requestUrl, options);
    this.#setMobileTimeout(config);
    if (this.#skipSslCheck()) {
      kameHouse.logger.trace("Skipping SSL check for mobile request");
      cordova.plugin.http.setServerTrustMode('nocheck',
      () => { // success
        this.#sendMobileHttpRequest(requestUrl, options, successCallback, errorCallback);
      },
      () => { // error
        const message = "Error setting cordova ssl trustmode to nocheck. Trying mobile http request anyway";
        kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
        this.#sendMobileHttpRequest(requestUrl, options, successCallback, errorCallback);
      });
    } else {
      kameHouse.logger.trace("Enabling SSL check for mobile request");
      cordova.plugin.http.setServerTrustMode('default',
      () => { // success
        this.#sendMobileHttpRequest(requestUrl, options, successCallback, errorCallback);
      },
      () => { // error
        const message = "Error setting cordova ssl trustmode to default. Can't proceed with mobile http request to " + requestUrl;
        kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
      });
    }
  }

  /**
   * Get selected backend server.
   */
  getSelectedBackendServer() {
    const mobileConfig = kameHouse.extension.mobile.config;
    let selectedBackendServer = null;
    if (!kameHouse.core.isEmpty(mobileConfig) && !kameHouse.core.isEmpty(mobileConfig.backend)
          && !kameHouse.core.isEmpty(mobileConfig.backend.servers)) {
      mobileConfig.backend.servers.forEach((server) => {
        if (server.name === mobileConfig.backend.selected) {
          selectedBackendServer = server;
        }
      });
    }
    if (selectedBackendServer == null) {
      const message = "Couldn't find selected backend server in the config. Mobile app config manager may not have completed initialization yet";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
    } else {
      kameHouse.logger.debug("Selected backend server from the config: " + kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(selectedBackendServer)));      
    }
    return selectedBackendServer;
  }

  /**
   * Get selected backend server url.
   */
  getSelectedBackendServerUrl() {
    const selectedBackendServer = this.getSelectedBackendServer();
    if (kameHouse.core.isEmpty(selectedBackendServer) || kameHouse.core.isEmpty(selectedBackendServer.url)) {
      const message = "Couldn't find backend server url in the config. Mobile app config manager may not have completed initialization yet";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
      return null;
    }
    return selectedBackendServer.url;
  }

  /**
   * Check if the user is logged in.
   */
  isLoggedIn() {
    const selectedBackendServer = this.getSelectedBackendServer();
    if (kameHouse.core.isEmpty(selectedBackendServer) || selectedBackendServer.isLoggedIn == null) {
      const message = "Couldn't find backend server isLoggedIn in the config. Mobile app config manager may not have completed initialization yet. Defaulting to false";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
      return false;
    }
    return selectedBackendServer.isLoggedIn;
  }

  /**
   * Set mobile app build version.
   */
  setMobileBuildVersion() { 
    this.#setAppVersion();
    this.#setGitCommitHash();
    this.#setBuildDate();
  }

  /**
   * Open inAppBrowser with
   * @deprecated
   */
  openBrowser(urlLookup) {
    const serverEntity = this.#getServerUrl(urlLookup);
    this.#openInAppBrowser(serverEntity);
  }

  /**
   * Override the default window.open to open the inappbrowser.
   * @deprecated
   */
  overrideWindowOpen() {
    window.open = cordova.InAppBrowser.open;
  }

  /**
   * Check if it should skip ssl check.
   */
  #skipSslCheck() {
    const selectedBackendServer = this.getSelectedBackendServer();
    if (kameHouse.core.isEmpty(selectedBackendServer) || selectedBackendServer.skipSslCheck == null) {
      const message = "Couldn't find backend server skipSslCheck in the config. Mobile app config manager may not have completed initialization yet. Defaulting to false";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
      return false;
    }
    return selectedBackendServer.skipSslCheck;
  }

  /**
   * Check if it's the mocked backend server selected.
   */
  #isMockBackendSelected() {
    const mobileConfig = kameHouse.extension.mobile.config;
    if (!kameHouse.core.isEmpty(mobileConfig) && !kameHouse.core.isEmpty(mobileConfig.backend)
          && !kameHouse.core.isEmpty(mobileConfig.backend.servers)) {
      if (mobileConfig.backend.selected === "Mock Localhost") {
        return true;
      }
    }
    return false;
  }

  /**
   * Get backend server credentials.
   */
  #getBackendCredentials() {
    const selectedBackendServer = this.getSelectedBackendServer();
    const credentials = {};
    if (!kameHouse.core.isEmpty(selectedBackendServer)) {
      kameHouse.logger.debug("Selecting credentials for username: " + selectedBackendServer.username + " and server: " + selectedBackendServer.name);
      credentials.username = selectedBackendServer.username;
      credentials.password = selectedBackendServer.password;
    } else {
      const message = "Unable to get backend credentials. Mobile config manager may not be initialized yet";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
    }
    return credentials;
  }

  /**
   * Set successful login view.
   */
  #setSuccessfulLoginView() {
    kameHouse.util.dom.addClass($('#backend-username-password'), "hidden-kh");
    kameHouse.util.dom.addClass($('#backend-login-btn'), "hidden-kh");
    kameHouse.util.dom.removeClass($('#backend-logout-btn'), "hidden-kh");
  }

  /**
   * Set successful login config.
   */
  #setSuccessfulLoginConfig() {
    const selectedBackendServer = this.getSelectedBackendServer();
    selectedBackendServer.isLoggedIn = true;
  }

  /**
   * Get success modal html.
   */
  #getSuccessModalHtml(message) {
    const img = kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/dbz/goku.png",
      className: "img-btn-kh",
      alt: "Success modal",
      onClick: () => {return;}
    });
    const div = kameHouse.util.dom.getDiv();
    kameHouse.util.dom.append(div, img);
    kameHouse.util.dom.append(div, message);
    return div;
  }

  /**
   * Get error modal html.
   */
  #getErrorModalHtml(message) {
    const img = kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/other/cancel-shallow-red-dark.png",
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
   * Set successful logout view.
   */
  #setSuccessfulLogoutView() {
    const usernameInput = document.getElementById("backend-username-input");
    usernameInput.value = "";
    const passwordInput = document.getElementById("backend-password-input");
    passwordInput.value = "";
    kameHouse.util.dom.removeClass($('#backend-username-password'), "hidden-kh");
    kameHouse.util.dom.removeClass($('#backend-login-btn'), "hidden-kh");
    kameHouse.util.dom.addClass($('#backend-logout-btn'), "hidden-kh");
  }

  /**
   * Set successful logout config.
   */
  #setSuccessfulLogoutConfig() {
    const selectedBackendServer = this.getSelectedBackendServer();
    selectedBackendServer.isLoggedIn = false;
    selectedBackendServer.username = "";
    selectedBackendServer.password = "";
  }

  /**
   * Send mobile http request.
   */
  #sendMobileHttpRequest(requestUrl, options, successCallback, errorCallback) {
    cordova.plugin.http.sendRequest(requestUrl, options, 
      (response) => { this.#processMobileSuccess(response, successCallback); },
      (response) => { this.#processMobileError(response, errorCallback); }
    );
  }

  /** Process a successful response from the api call */
  #processMobileSuccess(response, successCallback) {
    /**
     * data: response body
     * status: http status code
     * url: request url
     * response headers: header map object
     */
    let responseBody;
    if (this.#isJsonResponse(response.headers)) {
      responseBody = kameHouse.json.parse(response.data);
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
  #processMobileError(response, errorCallback) {
     /**
     * error: error message
     * status: http status code
     * url: request url
     * response headers: header map object
     * 
     * advanced-http-plugin error response codes:
     *  GENERIC: -1,
     *  SSL_EXCEPTION: -2,
     *  SERVER_NOT_FOUND: -3,
     *  TIMEOUT: -4,
     *  UNSUPPORTED_URL: -5,
     *  NOT_CONNECTED: -6,
     *  POST_PROCESSING_FAILED: -7,
     *  ABORTED: -8,
     */
     const responseBody = response.error;
     const responseCode = response.status;
     const responseDescription = null;
     const responseHeaders = response.headers;
     kameHouse.logger.logApiError(responseBody, responseCode, responseDescription, responseHeaders, null);
     errorCallback(responseBody, responseCode, responseDescription, responseHeaders);
  }  

  /**
   * Check if it's a json response.
   */
  #isJsonResponse(headers) {
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
  #setMobileTimeout(config) {
    if (!kameHouse.core.isEmpty(config.timeout)) {
      kameHouse.logger.debug("Setting timeout for mobile http request to " + config.timeout);
      cordova.plugin.http.setRequestTimeout(config.timeout);
      cordova.plugin.http.setReadTimeout(config.timeout);
    } else {
      kameHouse.logger.debug("Using default timeout for mobile http request");
      cordova.plugin.http.setRequestTimeout(KameHouseMobileCore.#DEFAULT_TIMEOUT_SECONDS);
      cordova.plugin.http.setReadTimeout(KameHouseMobileCore.#DEFAULT_TIMEOUT_SECONDS);
    }
  }

  /**
   * Set basic auth header.
   */
  #setMobileBasicAuthHeader() {
    if (this.isLoggedIn()) {
      const credentials = this.#getBackendCredentials();
      if (!kameHouse.core.isEmpty(credentials.username) && !kameHouse.core.isEmpty(credentials.password)) {
        kameHouse.logger.debug("Setting basicAuth header for mobile http request with username: " + credentials.username);
        cordova.plugin.http.useBasicAuth(credentials.username, credentials.password);
      }
    } else {
      kameHouse.logger.debug("User is not logged in. Unsetting basicAuth header for mobile http request");
      cordova.plugin.http.setHeader('Authorization', null);
    }
  }

  /**
   * Set data serializer.
   */
  #setDataSerializer(headers, httpMethod) {
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
          if (httpMethod != KameHouseMobileCore.#GET && httpMethod != KameHouseMobileCore.#PUT && httpMethod != KameHouseMobileCore.#DELETE) {
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
  #logMobileHttpRequest(url, options) {
    kameHouse.logger.debug("mobile http request: [ " 
    + "'url' : '" + url + "', "
    + "'options' : '" + kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(options)) + "' ]");
  }

  /**
   * Set mobile app release version.
   */
  async #setAppVersion() {
    const pom = await kameHouse.util.fetch.loadFile('/kame-house-mobile/pom.xml');
    const versionPrefix = "<version>";
    const versionSuffix = "-KAMEHOUSE-SNAPSHOT";
    const tempVersion = pom.slice(pom.indexOf(versionPrefix) + versionPrefix.length);
    const appVersion = tempVersion.slice(0, tempVersion.indexOf(versionSuffix));
    kameHouse.logger.info("Mobile app version: " + appVersion);
    const mobileBuildVersion = document.getElementById("mobile-build-version");
    kameHouse.util.dom.setInnerHtml(mobileBuildVersion, appVersion);
  }

  /**
   * Set mobile app git commit hash.
   */
  async #setGitCommitHash() {
    const gitHash = await kameHouse.util.fetch.loadFile('/kame-house-mobile/git-commit-hash.txt');
    kameHouse.logger.info("Mobile git hash: " + gitHash);
    const gitHashDiv = document.getElementById("mobile-git-hash");
    kameHouse.util.dom.setInnerHtml(gitHashDiv, gitHash);
  }

  /**
   * Set mobile app build date.
   */
  async #setBuildDate() {
    const buildDate = await kameHouse.util.fetch.loadFile('/kame-house-mobile/build-date.txt');
    kameHouse.logger.info("Mobile build date: " + buildDate);
    const buildDateDiv = document.getElementById("mobile-build-date");
    kameHouse.util.dom.setInnerHtml(buildDateDiv, buildDate);
  }

  /**
   * Mock cordova when cordova is not available. For example when testing in a laptop's browser through apache httpd.
   */
  #setCordovaMock() {
    const urlParams = new URLSearchParams(window.location.search);
    const mockCordova = urlParams.get('mockCordova');
    if (mockCordova) {
      kameHouse.logger.info("Mocking cordova object");
      cordova = new CordovaMock();
    }
  }   

  /**
   * Get complete url from lookup.
   * 
   * @deprecated.
   */
  #getServerUrl(urlLookup) {
    const server = kameHouse.extension.mobile.configManager.getBackend().servers.find(server => server.name === urlLookup);
    const serverEntity = {};
    serverEntity.name = server.name;
    serverEntity.url = server.url;
    kameHouse.logger.trace("Server entity: " + kameHouse.json.stringify(serverEntity));
    return serverEntity;
  }

  /**
   * Open the InAppBrowser with the specified url.
   * @deprecated
   */
  #openInAppBrowser(serverEntity) {
    kameHouse.logger.info("Start loading url " + serverEntity.url);
    const target = kameHouse.extension.mobile.configManager.getInAppBrowserConfig().target;
    const options = kameHouse.extension.mobile.configManager.getInAppBrowserConfig().options;
    const inAppBrowserInstance = cordova.InAppBrowser.open(serverEntity.url, target, options);
    if (target == "_system") {
      kameHouse.plugin.modal.basicModal.openAutoCloseable(this.#getOpenBrowserMessage(serverEntity), 2000);
    } else {
      kameHouse.plugin.modal.basicModal.setHtml(this.#getOpenBrowserMessage(serverEntity));
      kameHouse.plugin.modal.basicModal.setIsErrorMessage(false);
      kameHouse.plugin.modal.basicModal.open();
      this.#setInAppBrowserEventListeners(inAppBrowserInstance, serverEntity);
    }
  }

  /**
   * Get the open browser message for the modal.
   * @deprecated
   */
  #getOpenBrowserMessage(serverEntity) {
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
  #setInAppBrowserEventListeners(inAppBrowserInstance, serverEntity) {

    inAppBrowserInstance.addEventListener('loadstop', (params) => {
      kameHouse.logger.info("Executing event loadstop for url: '" + serverEntity.url + "'. with params " + kameHouse.json.stringify(params));
      inAppBrowserInstance.show();
    });

    inAppBrowserInstance.addEventListener('loaderror', (params) => {
      const errorMessage = "Error loading url '" + serverEntity.url + "'. with params " + kameHouse.json.stringify(params);
      kameHouse.logger.error("Executing event loaderror. " + errorMessage);
      kameHouse.plugin.modal.basicModal.setHtml(errorMessage);
      kameHouse.plugin.modal.basicModal.setIsErrorMessage(true);
      kameHouse.plugin.modal.basicModal.open();
      inAppBrowserInstance.close();
    });

    inAppBrowserInstance.addEventListener('loadstart', (params) => {
      kameHouse.logger.info("Executing event loadstart for url: '" + serverEntity.url + "'. with params " + kameHouse.json.stringify(params));
    });

    inAppBrowserInstance.addEventListener('exit', (params) => {
      kameHouse.logger.info("Executing event exit for url: '" + serverEntity.url + "'. with params " + kameHouse.json.stringify(params));
      if (!kameHouse.plugin.modal.basicModal.isErrorMessage()) {
        kameHouse.plugin.modal.basicModal.close(); 
        kameHouse.plugin.modal.basicModal.reset();
      }
    });

    inAppBrowserInstance.addEventListener('message', (params) => {
      kameHouse.logger.info("Executing event message for url: '" + serverEntity.url + "'. with params " + kameHouse.json.stringify(params));
    });
  }

} // KameHouseMobileCore

/**
 * Manage the configuration of the mobile app.
 * 
 * @author nbrest
 */
class KameHouseMobileConfigManager {

  static #mobileConfigFile = "kamehouse-mobile-config.json";
  static #mobileConfigFileType = window.PERSISTENT;
  static #mobileConfigFileSize = 1*1024*1024; //1 mb

  #isCurrentlyPersistingConfig = false;
  #backendDefaultConfig = null;
  #encryptionKey = null;

  /**
   * Init kamehouse mobile config manager.
   */
  init() {
    // waitFor kameHouseDebugger fixed vlc player page not loading the proper credentials on mobile app
    kameHouse.util.module.waitForModules(["kameHouseDebugger"], async () => {
      kameHouse.logger.info("Initializing mobile config manager");
      this.#initGlobalMobileConfig();
      await this.#loadEncryptionKey();
      await this.#loadBackendDefaultConfig();
      this.#readMobileConfigFile();
    });
  }

  reGenerateMobileConfigFile(openResultModal) {
    if (this.#isCurrentlyPersistingConfig) {
      kameHouse.logger.warn("A regenerate file is already in progress, skipping this call");
      return;
    }
    this.#isCurrentlyPersistingConfig = true;
    kameHouse.logger.info("Regenerating file " + KameHouseMobileConfigManager.#mobileConfigFile);
    try {
      // request file to delete
      window.requestFileSystem(KameHouseMobileConfigManager.#mobileConfigFileType, KameHouseMobileConfigManager.#mobileConfigFileSize, 
        (fs) => {this.#deleteFile(fs, openResultModal);}, 
        (error) => {this.#errorDeleteFileCallback(error, openResultModal);}
      );
    } catch (error) {
      kameHouse.logger.error("Error regenerating file " + KameHouseMobileConfigManager.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error));
      this.#isCurrentlyPersistingConfig = false;
      if (openResultModal) {
        kameHouse.plugin.modal.basicModal.openAutoCloseable("Error saving settings", 1000);
      }
    }
  }

  /**
   * Update the mobile config file from the view in the config tab.
   */
  updateMobileConfigFromView() {
    kameHouse.logger.info("Updating entire mobile config file from view");
    this.#updateSelectedBackendServerInConfig();
    this.#updateBackendServerUrlInConfig();
    this.#updateBackendSslCheckInConfig();
    this.#updateBackendServerCredentialsInConfig();
    kameHouse.logger.debug("Mobile config: " + kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(this.#getMobileConfig())));
    this.reGenerateMobileConfigFile(false);
  }

  /** Set the backend server in the view from the selected dropdown. then trigger a mobile config update */
  setBackendViewFromDropdown() {
    kameHouse.logger.info("Setting backend server view from dropdown");
    const backendServerInput = document.getElementById("backend-server-input"); 
    const backendServerDropdown = document.getElementById("backend-server-dropdown");
    kameHouse.util.dom.setValue(backendServerInput, backendServerDropdown.value);
    this.#updateSelectedBackendServerInConfig();
    this.refreshBackendServerViewFromConfig();
    this.updateMobileConfigFromView();
  }

  /**
   * Refresh backend server tab view values from the config.
   */
  refreshBackendServerViewFromConfig() {
    kameHouse.logger.info("Refreshing settings tab view values from the config");
    const selectedServer = kameHouse.extension.mobile.core.getSelectedBackendServer();

    const backendServerDropdown = document.getElementById("backend-server-dropdown");
    const backendServerInput = document.getElementById("backend-server-input");

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

    const backendServerSkipSslCheckCheckbox = document.getElementById("backend-skip-ssl-check-checkbox");
    backendServerSkipSslCheckCheckbox.checked = selectedServer.skipSslCheck;

    const usernameInput = document.getElementById("backend-username-input");
    kameHouse.util.dom.setValue(usernameInput, selectedServer.username);

    const passwordInput = document.getElementById("backend-password-input"); 
    kameHouse.util.dom.setValue(passwordInput, selectedServer.password);

    if (kameHouse.extension.mobile.core.isLoggedIn()) {
      kameHouse.util.dom.addClass($('#backend-username-password'), "hidden-kh");
      kameHouse.util.dom.addClass($('#backend-login-btn'), "hidden-kh");
      kameHouse.util.dom.removeClass($('#backend-logout-btn'), "hidden-kh");
    } else {
      kameHouse.util.dom.removeClass($('#backend-username-password'), "hidden-kh");
      kameHouse.util.dom.removeClass($('#backend-login-btn'), "hidden-kh");
      kameHouse.util.dom.addClass($('#backend-logout-btn'), "hidden-kh");
    }
  }

  /**
   * Open confirm reset config modal.
   */
  confirmResetDefaults() {
    kameHouse.plugin.modal.basicModal.setHtml(this.#getResetConfigModalMessage());
    kameHouse.plugin.modal.basicModal.open();
  }

  /**
   * Reset config to default values.
   */
  resetDefaults() {
    kameHouse.logger.info("Resetting config to default values");
    kameHouse.plugin.modal.basicModal.close();
    this.#initGlobalMobileConfig();
    this.#setMobileConfigBackend(this.#backendDefaultConfig);
    this.reGenerateMobileConfigFile(false);
    kameHouse.plugin.modal.basicModal.openAutoCloseable("Settings reset to defaults", 1000);
    this.refreshBackendServerViewFromConfig();
  }
    
  /**
   * Set kamehouse mobile module loaded.
   */
  #setKameHouseMobileModuleLoaded() {
    kameHouse.logger.info("Finished kameHouseMobile module initialization");
    kameHouse.util.module.setModuleLoaded("kameHouseMobile");
  }

  /**
   * Init global mobile config.
   */
  #initGlobalMobileConfig() {
    kameHouse.extension.mobile.config = {};
    kameHouse.extension.mobile.config.backend = {};
  }

  /**
   * Get mobile config.
   */
  #getMobileConfig() {
    return kameHouse.extension.mobile.config;
  }

  /**
   * Set mobile config.
   */
  #setMobileConfig(val) {
    kameHouse.extension.mobile.config = val;
  }

  /**
   * Get mobile config backend.
   */
  #getMobileConfigBackend() {
    return kameHouse.extension.mobile.config.backend;
  }

  /**
   * Set mobile config backend.
   */
  #setMobileConfigBackend(val) {
    kameHouse.extension.mobile.config.backend = val;
  }

  /**
   * Load backend default config.
   */
  async #loadBackendDefaultConfig() {
    this.#backendDefaultConfig = kameHouse.json.parse(await kameHouse.util.fetch.loadFile('/kame-house-mobile/json/config/backend.json'));
    kameHouse.logger.info("backend default config: " + kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(this.#backendDefaultConfig)));
    this.#setMobileConfigBackend(this.#backendDefaultConfig);
  }
  
  /**
   * Load encryption key.
   */
  async #loadEncryptionKey() {
    this.#encryptionKey = await kameHouse.util.fetch.loadFile('/kame-house-mobile/encryption.key');
    kameHouse.logger.debug("Loaded encryption key");
  }

  /**
   * Returns true if the config has all the required properties.
   */
  #isValidMobileConfigFile(mobileConfig) {
    return mobileConfig != null 
      && mobileConfig.backend != null;
  }

  /**
   * --------------------------------------------------------------------------
   * Create config functions
   */
  /**
   * Create kamehouse-mobile config file in the device's storage.
   */
  #createMobileConfigFile() {
    kameHouse.logger.info("Creating file " + KameHouseMobileConfigManager.#mobileConfigFile);
    try {
      window.requestFileSystem(KameHouseMobileConfigManager.#mobileConfigFileType, KameHouseMobileConfigManager.#mobileConfigFileSize, 
        (fs) => this.#createMobileConfigFileSuccessCallback(fs), 
        (error) => this.#createMobileConfigFileErrorCallback(error)
      );
    } catch (error) {
      kameHouse.logger.error("Error creating file " + KameHouseMobileConfigManager.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error));
    }
  }
  
  /**
   * createMobileConfigFile callback.
   */
  #createMobileConfigFileSuccessCallback(fs) {
    fs.root.getFile(KameHouseMobileConfigManager.#mobileConfigFile, {create: true, exclusive: true}, (fileEntry) => {
      kameHouse.logger.info("File " + fileEntry.name + " created successfully");
    }, (error) => this.#createMobileConfigFileErrorCallback(error));
  }

  /**
   * createMobileConfigFile callback.
   */
  #createMobileConfigFileErrorCallback(error) {
    kameHouse.logger.error("Error creating file " + KameHouseMobileConfigManager.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error));
  }
  
  /**
   * --------------------------------------------------------------------------
   * Write config functions
   */  
  /**
   * Write kamehouse-mobile config file to the filesystem.
   */
  #writeMobileConfigFile() {
    kameHouse.logger.info("Writing to file " + KameHouseMobileConfigManager.#mobileConfigFile);
    try {
      window.requestFileSystem(KameHouseMobileConfigManager.#mobileConfigFileType, KameHouseMobileConfigManager.#mobileConfigFileSize, 
        (fs) => this.#writeMobileConfigFileSuccessCallback(fs), 
        (error) => this.#writeMobileConfigFileErrorCallback(error));
    } catch (error) {
      kameHouse.logger.error("Error writing file " + KameHouseMobileConfigManager.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error));
    }
  }

  /**
   * writeMobileConfigFile callback.
   */
  #writeMobileConfigFileSuccessCallback(fs) {
    fs.root.getFile(KameHouseMobileConfigManager.#mobileConfigFile, {create: true}, (fileEntry) => {
      fileEntry.createWriter((fileWriter) => {
        try {
        const fileContent = kameHouse.json.stringify(this.#getMobileConfig());
        kameHouse.logger.debug("Encrypting file");
        const encryptedFileContent = CryptoJS.AES.encrypt(fileContent, this.#encryptionKey).toString();
        kameHouse.logger.debug("File content to write: " + fileContent);
        const blob = new Blob([encryptedFileContent]);
        fileWriter.write(blob);
        } catch(e) {
          kameHouse.logger.error("Error writing config file " + KameHouseMobileConfigManager.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(e)); 
        }
      }, (error) => this.#writeMobileConfigFileErrorCallback(error));
    }, (error) => this.#writeMobileConfigFileErrorCallback(error));
  }

  /**
   * writeMobileConfigFile callback.
   */
  #writeMobileConfigFileErrorCallback(error) {
    kameHouse.logger.error("Error writing file " + KameHouseMobileConfigManager.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error));
  }
  
  /**
   * --------------------------------------------------------------------------
   * Read config functions
   */
  /**
   * Read kamehouse-mobile config file.
   */
  #readMobileConfigFile() {
    kameHouse.logger.info("Reading file " + KameHouseMobileConfigManager.#mobileConfigFile);
    try {
      window.requestFileSystem(KameHouseMobileConfigManager.#mobileConfigFileType, KameHouseMobileConfigManager.#mobileConfigFileSize, 
        (fs) => this.#readMobileConfigFileSuccessCallback(fs), 
        (error) => this.#readMobileConfigFileErrorCallback(error));
    } catch (error) {
      kameHouse.logger.error("Error reading file " + KameHouseMobileConfigManager.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error));
      this.#setKameHouseMobileModuleLoaded();
      this.#createMobileConfigFile();
    }
  }

  /**
   * readMobileConfigFile callback.
   */
  #readMobileConfigFileSuccessCallback(fs) {
    fs.root.getFile(KameHouseMobileConfigManager.#mobileConfigFile, {}, (fileEntry) => {
      fileEntry.file((file) => {
        const reader = new FileReader();
        reader.onloadend = (e) => {
          let mobileConfig = null;
          try {
            const encryptedFileContent = reader.result;
            kameHouse.logger.debug("Decrypting file");
            const fileContent = CryptoJS.AES.decrypt(encryptedFileContent, this.#encryptionKey).toString(CryptoJS.enc.Utf8);
            kameHouse.logger.debug("File content read: " + kameHouse.logger.maskSensitiveData(fileContent));
            mobileConfig = kameHouse.json.parse(fileContent);
          } catch (error) {
            mobileConfig = null;
            kameHouse.logger.error("Error parsing file content as json. Error " + kameHouse.json.stringify(error));
          }
          if (this.#isValidMobileConfigFile(mobileConfig)) {
            kameHouse.logger.info("Setting mobile config from file");
            this.#setMobileConfig(mobileConfig);
          } else {
            kameHouse.logger.warn("Mobile config file read from file is invalid. Re generating it");
            this.reGenerateMobileConfigFile(false);
          }
          this.#setKameHouseMobileModuleLoaded();
        };
        reader.readAsText(file);
      }, (error) => this.#readMobileConfigFileErrorCallback(error));
    }, (error) => this.#readMobileConfigFileErrorCallback(error));
  }

  /**
   * readMobileConfigFile callback.
   */
  #readMobileConfigFileErrorCallback(error) {
    kameHouse.logger.error("Error reading file " + KameHouseMobileConfigManager.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error));
    this.#setKameHouseMobileModuleLoaded();
    this.#createMobileConfigFile();
  }

  /**
   * --------------------------------------------------------------------------
   * Delete config functions
   */
  /**
   * Delete kamehouse-mobile config file.
   */
  #deleteMobileConfigFile() {
    kameHouse.logger.info("Deleting file " + KameHouseMobileConfigManager.#mobileConfigFile);
    try {
      window.requestFileSystem(KameHouseMobileConfigManager.#mobileConfigFileType, KameHouseMobileConfigManager.#mobileConfigFileSize, 
        (fs) => this.#deleteMobileConfigFileSuccessCallback(fs), 
        (error) => this.#deleteMobileConfigFileErrorCallback(error));
    } catch (error) {
      kameHouse.logger.error("Error deleting file " + KameHouseMobileConfigManager.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error));
    }
  }

  /**
   * deleteMobileConfigFile callback.
   */
  #deleteMobileConfigFileSuccessCallback(fs) {
    fs.root.getFile(KameHouseMobileConfigManager.#mobileConfigFile, {create: false}, (fileEntry) => {
      fileEntry.remove(() => {
        kameHouse.logger.info("File " + fileEntry.name + " deleted successfully");
      }, (error) => this.#deleteMobileConfigFileErrorCallback(error));
    }, (error) => this.#deleteMobileConfigFileErrorCallback(error));
  }

  /**
   * deleteMobileConfigFile callback.
   */
  #deleteMobileConfigFileErrorCallback(error) {
    kameHouse.logger.error("Error deleting file " + KameHouseMobileConfigManager.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error));
  }

  /**
   * --------------------------------------------------------------------------
   */

  /**
   * Update selected backend server in config.
   */
  #updateSelectedBackendServerInConfig() {
    // Update backend.selected in config
    const backend = this.#getMobileConfigBackend();
    const backendServerDropdown = document.getElementById("backend-server-dropdown");
    for (let i = 0; i < backendServerDropdown.options.length; ++i) {
      if (backendServerDropdown.options[i].selected == true) {
        kameHouse.logger.debug("Setting selected backend in the config to: " + backendServerDropdown.options[i].textContent);
        backend.selected = backendServerDropdown.options[i].textContent;
      }
    }
  }

  /**
   * Update backend server url in config.
   */
  #updateBackendServerUrlInConfig() {
    // Update backend.servers[selected].url (for editable servers) in config
    const selectedBackendServer = kameHouse.extension.mobile.core.getSelectedBackendServer();
    const backendServerInput = document.getElementById("backend-server-input");
    kameHouse.logger.debug("Setting selected backend server url in the config to: " + backendServerInput.value);
    selectedBackendServer.url = backendServerInput.value;
  }

  /**
   * Update backend ssl check in config.
   */
  #updateBackendSslCheckInConfig() {
    const selectedBackendServer = kameHouse.extension.mobile.core.getSelectedBackendServer();
    const backendServerSkipSslCheckbox = document.getElementById("backend-skip-ssl-check-checkbox");
    kameHouse.logger.debug("Setting selected backend server skip ssl check in the config to: " + backendServerSkipSslCheckbox.checked);
    selectedBackendServer.skipSslCheck = backendServerSkipSslCheckbox.checked;
  }

  /**
   * Update backend server credentials in config.
   */
  #updateBackendServerCredentialsInConfig() {
    // Update backend.servers[] selected server credentials in config
    const selectedBackendServer = kameHouse.extension.mobile.core.getSelectedBackendServer();
    const username = document.getElementById("backend-username-input").value;
    kameHouse.logger.debug("Setting selected backend server username in the config to: " + username);
    selectedBackendServer.username = username;
    const password = document.getElementById("backend-password-input").value; 
    selectedBackendServer.password = password;
  }
  
  /**
   * Get the message to reset the config.
   */
  #getResetConfigModalMessage() {
    const resetConfigModalMessage = kameHouse.util.dom.getSpan({}, "Are you sure you want to reset the configuration? ");
    kameHouse.util.dom.append(resetConfigModalMessage, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(resetConfigModalMessage, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(resetConfigModalMessage, this.#getConfirmResetConfigButton());
    return resetConfigModalMessage;
  }

  /**
   * Get the button to confirm resetting the config.
   */
  #getConfirmResetConfigButton() {
    return kameHouse.util.dom.getButton({
      attr: {
        class: "mobile-btn-kh reset-cfg-btn-kh",
      },
      html: "Yes",
      click: () => {this.resetDefaults()}
    });
  }

  /**
   * --------------------------------------------------------------------------
   * Re generate kamehouse-mobile config file process.
   * 1. delete file if it exists
   * 2. recreate the file
   * 3. rewrite the file
   */
  /** 
   * --------------------------------------------------------------------------
   * 1. Delete the file 
   */
  /**
   * reGenerateMobileConfigFile success callback.
   */
  #deleteFile(fs, openResultModal) {
    fs.root.getFile(KameHouseMobileConfigManager.#mobileConfigFile, {create: false}, 
      (fileEntry) => {
        fileEntry.remove(
          () => {
            kameHouse.logger.info("File " + fileEntry.name + " deleted successfully");
            this.#requestRecreateFile(openResultModal);
          }, 
          (error) => {this.#errorDeleteFileCallback(error, openResultModal)}
        );
      }, 
      (error) => {this.#errorDeleteFileCallback(error, openResultModal)}
    );
  }

  /**
   * deleteFile error callback.
   */
  #errorDeleteFileCallback(error, openResultModal) {
    kameHouse.logger.error("Error deleting file " + KameHouseMobileConfigManager.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error));
    this.#requestRecreateFile(openResultModal);
  }

  /**
   * --------------------------------------------------------------------------
   * 2. Recreate the file.
   */
  #requestRecreateFile(openResultModal) {
    window.requestFileSystem(KameHouseMobileConfigManager.#mobileConfigFileType, KameHouseMobileConfigManager.#mobileConfigFileSize, 
      (fs) => {this.#recreateFile(fs, openResultModal);}, 
      (error) => {this.#errorRecreateFileCallback(error, openResultModal);}
    );
  }

  /**
   * requestRecreateFile success callback.
   */
  #recreateFile(fs, openResultModal) {
    fs.root.getFile(KameHouseMobileConfigManager.#mobileConfigFile, {create: true, exclusive: true}, 
      (fileEntry) => {
        kameHouse.logger.info("File " + fileEntry.name + " recreated successfully");
        this.#requestRewriteFile(openResultModal);
      }, 
      (error) => {this.#errorRecreateFileCallback(error, openResultModal)}
    );
  }

  /**
   * recreateFile error callback.
   */
  #errorRecreateFileCallback(error, openResultModal) {
    kameHouse.logger.error("Error recreating file " + KameHouseMobileConfigManager.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error));
    this.#requestRewriteFile(openResultModal);
  }

  /**
   * --------------------------------------------------------------------------
   * 3. Rewrite file.
   */
  #requestRewriteFile(openResultModal) {
    window.requestFileSystem(KameHouseMobileConfigManager.#mobileConfigFileType, KameHouseMobileConfigManager.#mobileConfigFileSize, 
      (fs) => {this.#rewriteFile(fs, openResultModal);}, 
      (error) => {this.#errorRewriteFileCallback(error, openResultModal);}
    );
  }

  /**
   * requestRewriteFile success callback.
   */
  #rewriteFile(fs, openResultModal) {
    fs.root.getFile(KameHouseMobileConfigManager.#mobileConfigFile, {create: true}, 
      (fileEntry) => {
        fileEntry.createWriter(
          (fileWriter) => {
            try {
              const fileContent = kameHouse.json.stringify(this.#getMobileConfig());
              kameHouse.logger.debug("Encrypting file");
              const encryptedFileContent = CryptoJS.AES.encrypt(fileContent, this.#encryptionKey).toString();
              kameHouse.logger.debug("File content to write: " + kameHouse.logger.maskSensitiveData(fileContent));
              const blob = new Blob([encryptedFileContent]);
              fileWriter.write(blob);
              this.#isCurrentlyPersistingConfig = false;
              if (openResultModal) {
                kameHouse.plugin.modal.basicModal.openAutoCloseable("Settings saved", 1000);
              }
            } catch(e) {
              kameHouse.logger.error("Error writing config file " + KameHouseMobileConfigManager.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(e)); 
            }
          }, 
          (error) => {this.#errorRewriteFileCallback(error, openResultModal)}
        );
      }, 
      (error) => {this.#errorRewriteFileCallback(error, openResultModal)}
    );
  }

  /**
   * rewriteFile error callback.
   */
  #errorRewriteFileCallback(error, openResultModal) {
    kameHouse.logger.error("Error rewriting file " + KameHouseMobileConfigManager.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error));
    this.#isCurrentlyPersistingConfig = false;
    if (openResultModal) {
      kameHouse.plugin.modal.basicModal.openAutoCloseable("Error saving settings", 1000);
    }
  }
  /**
   * --------------------------------------------------------------------------
   */

  /**
   * Test file operations.
   * @deprecated
   */
  #testFileManagement() {
    setTimeout(() => { this.#createMobileConfigFile(); }, 1000);
    setTimeout(() => { this.#writeMobileConfigFile(); }, 10000);
    setTimeout(() => { this.#readMobileConfigFile(); }, 15000);
    setTimeout(() => { this.#deleteMobileConfigFile(); }, 20000);
  }
}

/**
 * Mock object that simulates http requests to localhost on mobile device, to be able to test all functionality offline.
 * 
 * @author nbrest
 */
class MockLocalhostServer {

  /**
   * Execute mock local server http request.
   */
  async httpRequest(httpMethod, config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    kameHouse.logger.debug("Using mock localhost server for http request to: " + url);
    const responseBody = await this.#mockResponseBody(httpMethod, config, url, requestHeaders, requestBody);
    const responseCode = this.#mockResponseCode(httpMethod, config, url, requestHeaders, requestBody, responseBody);
    const responseDescription = this.#mockResponseDescription(httpMethod, config, url, requestHeaders, requestBody, responseBody);
    const responseHeaders = this.#mockResponseHeaders(httpMethod, config, url, requestHeaders, requestBody, responseBody);
    kameHouse.logger.logHttpResponse(responseBody, responseCode, responseDescription, responseHeaders);
    if (this.#isErrorResponseCode(responseCode)) {
      kameHouse.logger.debug("Executing errorCallback with mock response");
      errorCallback(responseBody, responseCode, responseDescription, responseHeaders); 
    } else {
      kameHouse.logger.debug("Executing successCallback with mock response");
      successCallback(responseBody, responseCode, responseDescription, responseHeaders);
    }
  }

  /**
   * Mock response body.
   */
  async #mockResponseBody(httpMethod, config, url, requestHeaders, requestBody) {
    if (this.#isServerModificationRequest(httpMethod, url)) {
      return this.#getServerModificationErrorResponseBody(httpMethod, url);
    }
    let requestUrl = url;
    if (isCrudEntityUrl(url)) {
      requestUrl = this.#getCrudBaseUrl(url);
    }
    if (this.#isModifiedUrl(requestUrl)) {
      requestUrl = requestUrl + "-modified";
    }
    kameHouse.logger.debug("Loading respose body from file: " + requestUrl);
    const responseBody = await kameHouse.util.fetch.loadFileWithTimeout(requestUrl, 8000);
    // Usually kamehouse apis would return header content-type application/json, so the response body
    // would be mapped to a json object. here I need to map it manually
    const responseBodyParsed = kameHouse.json.parse(responseBody);
    if (responseBodyParsed == null) {
      return responseBody;
    }
    if (this.#isCrudEntityUrl(url)) {
      return this.#getCrudEntityResponseBody(httpMethod, this.#getCrudEntityId(url), responseBodyParsed);
    }
    return responseBodyParsed;
  }

  /**
   * Check if it's a modified url.
   */
  #isModifiedUrl(url) {
    const MODIFIED_URLS = [
      "/kame-house-vlcrc/api/v1/vlc-rc/players",
    ];
    return MODIFIED_URLS.includes(url);
  }

  /**
   * Check if it's a server modification request.
   */
  #isServerModificationRequest(httpMethod, url) {
    const ALLOWED_NON_GET_URLS = [
      "/kame-house/login",
      "/kame-house-admin/api/v1/admin/power-management/wol",
      "/kame-house-tennisworld/api/v1/tennis-world/bookings",
      "/kame-house-tennisworld/api/v1/tennis-world/scheduled-bookings",
      "/kame-house-testmodule/api/v1/test-module/test-scheduler/sample-job"
    ];
    return httpMethod != "GET" && !ALLOWED_NON_GET_URLS.includes(url);
  }

  /**
   * Get server modification error response.
   */
  #getServerModificationErrorResponseBody(httpMethod, url) {
    const errorResponse = {
      code: 503,
      message: "Gomen-Chai. Server modifications are not supported in this mock server. '" + httpMethod + " " + url + "' unavailable"
    };
    return errorResponse;
  }

  /**
   * API calls that end with a number are crud id urls.
   */
  #isCrudEntityUrl(url) {
    const crudIdUrl = new RegExp("/kame-house.*/.*/[0-9]+","g");
    if (crudIdUrl.test(url)) {
      return true;
    }
    return false;
  }

  /**
   * Get crud entity id.
   */
  #getCrudEntityId(url) {
    return url.split("/").pop();
  }

  /**
   * Get crud base url.
   */
  #getCrudBaseUrl(url) {
    return url.slice(0, -(this.#getCrudEntityId(url).length + 1));
  }

  /**
   * Get crud entity response body.
   */
  #getCrudEntityResponseBody(httpMethod, id, responseBody) {
    if (httpMethod == "GET") {
      let selectedCrudEntity = null;
      responseBody.forEach((crudEntity) => {
        if (crudEntity.id == id) {
          selectedCrudEntity = crudEntity;
        }
      });
      kameHouse.logger.debug("Returning crud entity: " + kameHouse.json.stringify(selectedCrudEntity));
      return selectedCrudEntity;
    }
    const errorResponse = {
      message: "Mocked error response for CRUD modifications"
    };
    return errorResponse;
  }

  /**
   * Mock response code.
   */
  #mockResponseCode(httpMethod, config, url, requestHeaders, requestBody, responseBody) {
    if (isServerModificationRequest(httpMethod, url)) {
      return "503";
    }
    if (this.#isFetchErrorResponse(responseBody)) {
      return "404";
    }
    return "200";
  }

  /**
   * Mock response description.
   */
  #mockResponseDescription(httpMethod, config, url, requestHeaders, requestBody, responseBody) {
    return "";
  }

  /**
   * Mock response headers.
   */
  #mockResponseHeaders(httpMethod, config, url, requestHeaders, requestBody, responseBody) {
    return {};
  }

  /**
   * Check if there's a fetch error response.
   */
  #isFetchErrorResponse(responseBody) {
    const responseString = kameHouse.json.stringify(responseBody);
    if (!kameHouse.core.isEmpty(responseString) && responseString.includes("Error executing fetch to")) {
      return true;
    }
    return false;
  }

  /**
   * Check if it's an error response code.
   */
  #isErrorResponseCode(responseCode) {
    if (responseCode == "200" || responseCode == "201") {
      return false;
    }
    return true;
  }

} // MockLocalhostServer

/**
 * Mock cordova api to test layout and everything else using an apache httpd server.
 * 
 * @author nbrest
 */
 class CordovaMock {

  plugin = {};
  InAppBrowser = null;
 
  constructor() {
    this.plugin.http = new CordovaHttpPluginMock();
    this.InAppBrowser = new CordovaInAppBrowserMock();
  }

} // CordovaMock

/**
 * Cordova plugin mock.
 * 
 * @author nbrest
 */
class CordovaHttpPluginMock {

  /**
   * Set server trust mode.
   */
  setServerTrustMode(trustMode, successCallback) {
    kameHouse.logger.info("Called setServerTrustMode on cordova mock with " + trustMode);
    successCallback();
  }

  /**
   * Send http request.
   */
  sendRequest(requestUrl, options, successCallback, errorCallback) {
    kameHouse.logger.info("Called sendRequest on cordova mock with requestUrl: " + requestUrl + " and options " + kameHouse.json.stringify(options) + ". Mocking error response");
    const mockResponse = {
      error : '{"code":999, "message":"mocked cordova http error response"}',
      status: 999
    };
    errorCallback(mockResponse);
  }

  /**
   * Use basic auth.
   */
  useBasicAuth() {
    kameHouse.logger.info("Called useBasicAuth on cordova mock");
  }

  /**
   * Set data serializer.
   */
  setDataSerializer(serializationType) {
    kameHouse.logger.info("Called setDataSerializer on cordova mock with " + serializationType);
  }

  /**
   * Set header.
   */
  setHeader(key, value) {
    kameHouse.logger.info("Called setHeader on cordova mock with " + key + ":" + value);
  }

  /**
   * Set request timeout.
   */
  setRequestTimeout(val) {
    kameHouse.logger.info("Called setHeader on cordova mock with " + val);
  }

  /**
   * Set read timeout.
   */
  setReadTimeout(val) {
    kameHouse.logger.info("Called setHeader on cordova mock with " + val);
  }

} // CordovaHttpPluginMock

/**
 * Mock of an InAppBrowser.
 * 
 * @author nbrest
 * 
 * @deprecated
 */
class CordovaInAppBrowserMock {

  /**
   * Open url.
   */
  open(url, target, options) {
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

} // CordovaInAppBrowserMock

/**
 * Mock of a InAppBrowserInstance.
 * 
 * @author nbrest
 * 
 * @deprecated
 */
class CordovaInAppBrowserInstanceMock {

  /**
   * Add browser event listener.
   */
  addEventListener(eventName, callback) {
    kameHouse.logger.info("Called addEventListener on the InAppBrowserInstanceMock for event " + eventName);
  }

  /**
   * Show browser.
   */
  show() {
    kameHouse.logger.info("Called show on the InAppBrowserInstanceMock");
  }

  /**
   * Close browser.
   */
  close() {
    kameHouse.logger.info("Called close on the InAppBrowserInstanceMock");
  }
} // CordovaInAppBrowserInstanceMock

$(document).ready(() => {
  kameHouse.addExtension("mobile", new KameHouseMobile());
});