
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
    kameHouse.logger.info("Started initializing kamehouse-mobile.js", null);
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

  #POST = "POST";
  #DEFAULT_TIMEOUT_SECONDS = 60;

  #mockLocalhostServer = null;
  
  constructor() {
    this.#mockLocalhostServer = new MockLocalhostServer();
  }

  /**
   * Init kamehouse mobile core.
   */
  init() {
    this.#setCordovaMock();
    kameHouse.util.mobile.configureApp();
  }

  /**
   * Login to the kamehouse server.
   */
  login() {
    kameHouse.logger.info("Logging in to KameHouse...", null);
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
          kameHouse.plugin.modal.basicModal.openAutoCloseable(this.#getErrorModalHtml("Invalid credentials"), 1000);
          return;
        }
        kameHouse.logger.info("Login successful", null);
        kameHouse.plugin.modal.basicModal.openAutoCloseable(this.#getSuccessModalHtml("Success!"), 1000);
        this.#setSuccessfulLoginView();
        this.#setSuccessfulLoginConfig();
        kameHouse.extension.mobile.configManager.reGenerateMobileConfigFile(false);
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.plugin.modal.loadingWheelModal.close();
        if (responseCode == 401 || responseCode == 403) {
          const message = "Login error - invalid credentials";
          kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
          kameHouse.plugin.modal.basicModal.openAutoCloseable(this.#getErrorModalHtml("Invalid credentials"), 1000);
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
    kameHouse.logger.info("Logging out of KameHouse", null);
    kameHouse.plugin.modal.loadingWheelModal.open("Logging out of KameHouse...");
    const LOGOUT_URL = "/kame-house/logout";
    const config = kameHouse.http.getConfig();
    config.timeout = 15;
    kameHouse.plugin.debugger.http.get(config, LOGOUT_URL, kameHouse.http.getUrlEncodedHeaders(), null,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.plugin.modal.loadingWheelModal.close();
        if (responseBody.includes("KameHouse - Login")) {
          kameHouse.logger.info("Logout successful", null);
          kameHouse.plugin.modal.basicModal.openAutoCloseable(this.#getSuccessModalHtml(" Success!"), 1000);
          this.#setSuccessfulLogoutView();
          this.#setSuccessfulLogoutConfig();
          kameHouse.extension.mobile.configManager.reGenerateMobileConfigFile(false);
          kameHouse.cordova.plugin.http.clearCookies();
          return;
        }
        const message = "Logout error: " + kameHouse.json.stringify(responseBody, null, null);
        kameHouse.logger.error(message, null);
        kameHouse.plugin.modal.basicModal.openAutoCloseable(this.#getErrorModalHtml("Error logging out. Try again later"), 1000);
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
      headers: null,
      data: ""
    };
    
    if (!kameHouse.core.isEmpty(requestHeaders)) {
      options.headers = requestHeaders;
    }
    this.#setMobileBasicAuthHeader();
    this.#setDataSerializer(options, httpMethod, requestHeaders);
    this.#setData(options, httpMethod, requestHeaders, requestBody);
    if (this.#shouldEncodeUrlParams(httpMethod, requestHeaders, requestBody)) {
      requestUrl = requestUrl + "?" + kameHouse.http.urlEncodeParams(requestBody);
    }
    this.#logMobileHttpRequest(httpMethod, config, requestUrl, requestHeaders, requestBody, options);
    this.#setMobileTimeout(config);
    if (this.#skipSslCheck()) {
      kameHouse.logger.trace("Skipping SSL check for mobile request", null);
      kameHouse.cordova.plugin.http.setServerTrustMode('nocheck',
      () => { // success
        this.#sendMobileHttpRequest(config, requestUrl, options, successCallback, errorCallback);
      },
      () => { // error
        const message = "Error setting cordova ssl trustmode to nocheck. Trying mobile http request anyway";
        kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
        this.#sendMobileHttpRequest(config, requestUrl, options, successCallback, errorCallback);
      });
    } else {
      kameHouse.logger.trace("Enabling SSL check for mobile request", null);
      kameHouse.cordova.plugin.http.setServerTrustMode('default',
      () => { // success
        this.#sendMobileHttpRequest(config, requestUrl, options, successCallback, errorCallback);
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
      kameHouse.logger.trace("Selected backend server from the config: " + kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(selectedBackendServer, null, null)), null);      
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
    window.open = kameHouse.cordova.InAppBrowser.open;
  }

  /**
   * Is POST request.
   */
  #isPostRequest(httpMethod) {
    return httpMethod == this.#POST;
  } 

  /**
   * True if the request needs url encoded params.
   */
  #shouldEncodeUrlParams(httpMethod, requestHeaders, requestBody) {
    return kameHouse.http.isUrlEncodedRequest(requestHeaders) && !this.#isPostRequest(httpMethod) && !kameHouse.core.isEmpty(requestBody);
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
    const credentials = {
      username: null,
      password: null
    };
    if (!kameHouse.core.isEmpty(selectedBackendServer)) {
      kameHouse.logger.trace("Selecting credentials for username: " + selectedBackendServer.username + " and server: " + selectedBackendServer.name, null);
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
    kameHouse.util.dom.classListAddById('backend-username-password', "hidden-kh");
    kameHouse.util.dom.classListAddById('backend-login-btn', "hidden-kh");
    kameHouse.util.dom.classListRemoveById('backend-logout-btn', "hidden-kh");
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
    const img = kameHouse.util.dom.getImg({
      src: "/kame-house/img/dbz/goku.png",
      className: "img-btn-kh status-modal-btn",
      alt: "Success modal"
    });
    const div = kameHouse.util.dom.getDiv(null, null);
    kameHouse.util.dom.append(div, img);
    kameHouse.util.dom.append(div, message);
    return div;
  }

  /**
   * Get error modal html.
   */
  #getErrorModalHtml(message) {
    const img = kameHouse.util.dom.getImg({
      src: "/kame-house/img/other/cancel-shallow-red-dark.png",
      className: "img-btn-kh status-modal-btn",
      alt: "Error modal"
    });
    const div = kameHouse.util.dom.getDiv(null, null);
    kameHouse.util.dom.append(div, img);
    kameHouse.util.dom.append(div, message);
    return div;
  }

  /**
   * Set successful logout view.
   */
  #setSuccessfulLogoutView() {
    const usernameInput = document.getElementById("backend-username-input") as HTMLInputElement;
    usernameInput.value = "";
    const passwordInput = document.getElementById("backend-password-input") as HTMLInputElement;
    passwordInput.value = "";
    kameHouse.util.dom.classListRemoveById('backend-username-password', "hidden-kh");
    kameHouse.util.dom.classListRemoveById('backend-login-btn', "hidden-kh");
    kameHouse.util.dom.classListAddById('backend-logout-btn', "hidden-kh");
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
  #sendMobileHttpRequest(config, requestUrl, options, successCallback, errorCallback) {
    kameHouse.cordova.plugin.http.sendRequest(requestUrl, options, 
      (response) => { this.#processMobileSuccess(config, requestUrl, response, successCallback); },
      (response) => { this.#processMobileError(config, requestUrl, response, errorCallback); }
    );
  }

  /** Process a successful response from the api call */
  #processMobileSuccess(config, url, response, successCallback) {
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
    kameHouse.logger.logHttpResponse(config, url, responseBody, responseCode, responseDescription, responseHeaders);
    successCallback(responseBody, responseCode, responseDescription, responseHeaders);
  }

  /** Process an error response from the api call */
  #processMobileError(config, url, response, errorCallback) {
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
     kameHouse.logger.logApiError(config, url, responseBody, responseCode, responseDescription, responseHeaders);
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
    for (const [key, value] of Object.entries<String>(headers)) {
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
      kameHouse.logger.trace("Setting timeout for mobile http request to " + config.timeout, null);
      kameHouse.cordova.plugin.http.setRequestTimeout(config.timeout);
      kameHouse.cordova.plugin.http.setReadTimeout(config.timeout);
    } else {
      kameHouse.logger.trace("Using default timeout for mobile http request", null);
      kameHouse.cordova.plugin.http.setRequestTimeout(this.#DEFAULT_TIMEOUT_SECONDS);
      kameHouse.cordova.plugin.http.setReadTimeout(this.#DEFAULT_TIMEOUT_SECONDS);
    }
  }

  /**
   * Set basic auth header.
   */
  #setMobileBasicAuthHeader() {
    if (this.isLoggedIn()) {
      const credentials = this.#getBackendCredentials();
      if (!kameHouse.core.isEmpty(credentials.username) && !kameHouse.core.isEmpty(credentials.password)) {
        kameHouse.logger.trace("Setting basicAuth header for mobile http request with username: " + credentials.username, null);
        kameHouse.cordova.plugin.http.useBasicAuth(credentials.username, credentials.password);
      }
    } else {
      kameHouse.logger.trace("User is not logged in. Unsetting basicAuth header for mobile http request", null);
      kameHouse.cordova.plugin.http.setHeader('Authorization', null);
    }
  }

  /**
   * Set data.
   */  
  #setData(options, httpMethod, requestHeaders, requestBody) {
    if (kameHouse.core.isEmpty(requestBody)) {
      return;
    }
    if (!kameHouse.http.isUrlEncodedRequest(requestHeaders)) {
      options.data = requestBody;   
      return;
    } 
    if (this.#isPostRequest(httpMethod)) {
      // http method is POST and urlEncoded
      options.data = requestBody;
    } 
  }

  /**
   * Set data serializer.
   */
  #setDataSerializer(options, httpMethod, headers) {
    kameHouse.logger.trace("Setting data serializer to 'utf8'", null);
    options.serializer = 'utf8';
    if (kameHouse.core.isEmpty(headers)) {
      return;
    }
    for (const [key, value] of Object.entries<String>(headers)) {
      if (!kameHouse.core.isEmpty(key) && key.toLowerCase() == "content-type" && !kameHouse.core.isEmpty(value)) {
        if (value.toLowerCase() == "application/json") {
          kameHouse.logger.trace("Overriding data serializer to 'json'", null);
          options.serializer = 'json';
        }
        if (value.toLowerCase() == "application/x-www-form-urlencoded") {
          // For GET, PUT, DELETE url encoded data in this http plugin, the data in the options object must be set to "" and serializer to utf8 and directly set the encoded url parameters in the request url.
          // For POST url encoded requests, the data in the options object must be a json object and the serializer needs to be set to urlencoded
          if (this.#isPostRequest(httpMethod)) {
            kameHouse.logger.trace("Overriding data serializer to 'urlencoded'", null);
            options.serializer = 'urlencoded';
          }
        }
      }
    }
  }

  /**
   * Log a mobile http request.
   */
  #logMobileHttpRequest(httpMethod, config, url, requestHeaders, requestBody, options) {
    kameHouse.logger.debug("Http request (Mobile): [ " 
    + "'id' : '" + config.requestId + "', "
    + "'url' : '" + url + "', "
    + "'method' : '" + httpMethod + "', "
    + "'config' : '" + kameHouse.json.stringify(config, null, null) + "', "
    + "'options' : '" + kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(options, null, null)) + "', "
    + "'headers' : '" + kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(requestHeaders, null, null)) + "', "
    + "'body' : '" + kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(requestBody, null, null)) + "'"
    + "]", null);    
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
    kameHouse.logger.info("Mobile app version: " + appVersion, null);
    const mobileBuildVersion = document.getElementById("mobile-build-version");
    kameHouse.util.dom.setHtml(mobileBuildVersion, appVersion);
  }

  /**
   * Set mobile app git commit hash.
   */
  async #setGitCommitHash() {
    const gitHash = await kameHouse.util.fetch.loadFile('/kame-house-mobile/git-commit-hash.txt');
    kameHouse.logger.info("Mobile git hash: " + gitHash, null);
    const gitHashDiv = document.getElementById("mobile-git-hash");
    kameHouse.util.dom.setHtml(gitHashDiv, gitHash);
  }

  /**
   * Set mobile app build date.
   */
  async #setBuildDate() {
    const buildDate = await kameHouse.util.fetch.loadFile('/kame-house-mobile/build-date.txt');
    kameHouse.logger.info("Mobile build date: " + buildDate, null);
    const buildDateDiv = document.getElementById("mobile-build-date");
    kameHouse.util.dom.setHtml(buildDateDiv, buildDate);
  }

  /**
   * Mock cordova when cordova is not available. For example when testing in a laptop's browser through apache httpd.
   */
  #setCordovaMock() {
    const urlParams = new URLSearchParams(window.location.search);
    const mockCordova = urlParams.get('mockCordova');
    if (mockCordova) {
      kameHouse.logger.info("Mocking cordova object", null);
      kameHouse.cordova = new CordovaMock();
    }
  }   

  /**
   * Get complete url from lookup.
   * 
   * @deprecated.
   */
  #getServerUrl(urlLookup) {
    const server = kameHouse.extension.mobile.configManager.getBackend().servers.find(server => server.name === urlLookup);
    const serverEntity = {
      name: server.name,
      url: server.url
    };
    kameHouse.logger.trace("Server entity: " + kameHouse.json.stringify(serverEntity, null, null), null);
    return serverEntity;
  }

  /**
   * Open the InAppBrowser with the specified url.
   * @deprecated
   */
  #openInAppBrowser(serverEntity) {
    kameHouse.logger.info("Start loading url " + serverEntity.url, null);
    const target = kameHouse.extension.mobile.configManager.getInAppBrowserConfig().target;
    const options = kameHouse.extension.mobile.configManager.getInAppBrowserConfig().options;
    const inAppBrowserInstance = kameHouse.cordova.InAppBrowser.open(serverEntity.url, target, options);
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
      kameHouse.logger.info("Executing event loadstop for url: '" + serverEntity.url + "'. with params " + kameHouse.json.stringify(params, null, null), null);
      inAppBrowserInstance.show();
    });

    inAppBrowserInstance.addEventListener('loaderror', (params) => {
      const errorMessage = "Error loading url '" + serverEntity.url + "'. with params " + kameHouse.json.stringify(params, null, null);
      kameHouse.logger.error("Executing event loaderror. " + errorMessage, null);
      kameHouse.plugin.modal.basicModal.setHtml(errorMessage);
      kameHouse.plugin.modal.basicModal.setIsErrorMessage(true);
      kameHouse.plugin.modal.basicModal.open();
      inAppBrowserInstance.close();
    });

    inAppBrowserInstance.addEventListener('loadstart', (params) => {
      kameHouse.logger.info("Executing event loadstart for url: '" + serverEntity.url + "'. with params " + kameHouse.json.stringify(params, null, null), null);
    });

    inAppBrowserInstance.addEventListener('exit', (params) => {
      kameHouse.logger.info("Executing event exit for url: '" + serverEntity.url + "'. with params " + kameHouse.json.stringify(params, null, null), null);
      if (!kameHouse.plugin.modal.basicModal.isErrorMessage()) {
        kameHouse.plugin.modal.basicModal.close(); 
        kameHouse.plugin.modal.basicModal.reset();
      }
    });

    inAppBrowserInstance.addEventListener('message', (params) => {
      kameHouse.logger.info("Executing event message for url: '" + serverEntity.url + "'. with params " + kameHouse.json.stringify(params, null, null), null);
    });
  }

} // KameHouseMobileCore

/**
 * Manage the configuration of the mobile app.
 * 
 * @author nbrest
 */
class KameHouseMobileConfigManager {

  #mobileConfigFile = "kamehouse-mobile-config.json";
  #mobileConfigFileType = window.PERSISTENT;
  #mobileConfigFileSize = 1*1024*1024; //1 mb

  #isCurrentlyPersistingConfig = false;
  #backendDefaultConfig = null;
  #encryptionKey = null;

  /**
   * Init kamehouse mobile config manager.
   */
  init() {
    // waitFor kameHouseDebugger fixed vlc player page not loading the proper credentials on mobile app
    kameHouse.util.module.waitForModules(["kameHouseDebugger"], async () => {
      kameHouse.logger.info("Initializing mobile config manager", null);
      this.#initGlobalMobileConfig();
      await this.#loadEncryptionKey();
      await this.#loadBackendDefaultConfig();
      this.#readMobileConfigFile();
    });
  }

  /**
   * Re generate mobile config file.
   */
  reGenerateMobileConfigFile(openResultModal) {
    if (this.#isCurrentlyPersistingConfig) {
      kameHouse.logger.warn("A regenerate file is already in progress, skipping this call", null);
      return;
    }
    this.#isCurrentlyPersistingConfig = true;
    kameHouse.logger.info("Regenerating file " + this.#mobileConfigFile, null);
    try {
      // request file to delete
      window.requestFileSystem(this.#mobileConfigFileType, this.#mobileConfigFileSize, 
        (fs) => {this.#deleteFile(fs, openResultModal);}, 
        (error) => {this.#errorDeleteFileCallback(error, openResultModal);}
      );
    } catch (error) {
      kameHouse.logger.error("Error regenerating file " + this.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error, null, null), null);
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
    kameHouse.logger.info("Updating entire mobile config file from view", null);
    this.#updateSelectedBackendServerInConfig();
    this.#updateBackendServerUrlInConfig();
    this.#updateBackendSslCheckInConfig();
    this.#updateBackendServerCredentialsInConfig();
    kameHouse.logger.debug("Mobile config: " + kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(this.#getMobileConfig(), null, null)), null);
    this.reGenerateMobileConfigFile(false);
  }

  /** Set the backend server in the view from the selected dropdown. then trigger a mobile config update */
  setBackendViewFromDropdown() {
    kameHouse.logger.info("Setting backend server view from dropdown", null);
    const backendServerInput = document.getElementById("backend-server-input"); 
    const backendServerDropdown = document.getElementById("backend-server-dropdown") as HTMLSelectElement;
    kameHouse.util.dom.setValue(backendServerInput, backendServerDropdown.value);
    this.#updateSelectedBackendServerInConfig();
    this.refreshBackendServerViewFromConfig();
    this.updateMobileConfigFromView();
  }

  /**
   * Refresh backend server tab view values from the config.
   */
  refreshBackendServerViewFromConfig() {
    kameHouse.logger.info("Refreshing settings tab view values from the config", null);
    const selectedServer = kameHouse.extension.mobile.core.getSelectedBackendServer();

    const backendServerDropdown = document.getElementById("backend-server-dropdown") as HTMLSelectElement;
    const backendServerInput = document.getElementById("backend-server-input") as HTMLInputElement;

    if (backendServerInput.value != "") {
      backendServerDropdown.options[backendServerDropdown.options.length-1].selected = true;
    }
    const editableServers = ["WiFi Hotspot", "Dev Apache", "Dev Tomcat HTTP", "Custom Server"];
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

    const backendServerSkipSslCheckCheckbox = document.getElementById("backend-skip-ssl-check-checkbox") as HTMLInputElement;
    backendServerSkipSslCheckCheckbox.checked = selectedServer.skipSslCheck;

    const usernameInput = document.getElementById("backend-username-input");
    kameHouse.util.dom.setValue(usernameInput, selectedServer.username);

    const passwordInput = document.getElementById("backend-password-input"); 
    kameHouse.util.dom.setValue(passwordInput, selectedServer.password);

    if (kameHouse.extension.mobile.core.isLoggedIn()) {
      kameHouse.util.dom.classListAddById('backend-username-password', "hidden-kh");
      kameHouse.util.dom.classListAddById('backend-login-btn', "hidden-kh");
      kameHouse.util.dom.classListRemoveById('backend-logout-btn', "hidden-kh");
    } else {
      kameHouse.util.dom.classListRemoveById('backend-username-password', "hidden-kh");
      kameHouse.util.dom.classListRemoveById('backend-login-btn', "hidden-kh");
      kameHouse.util.dom.classListAddById('backend-logout-btn', "hidden-kh");
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
    kameHouse.logger.info("Resetting config to default values", null);
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
    kameHouse.logger.info("Finished kameHouseMobile module initialization", null);
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
    kameHouse.logger.info("backend default config: " + kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(this.#backendDefaultConfig, null, null)), null);
    this.#setMobileConfigBackend(this.#backendDefaultConfig);
  }
  
  /**
   * Load encryption key.
   */
  async #loadEncryptionKey() {
    this.#encryptionKey = await kameHouse.util.fetch.loadFile('/kame-house-mobile/encryption.key');
    kameHouse.logger.debug("Loaded encryption key", null);
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
    kameHouse.logger.info("Creating file " + this.#mobileConfigFile, null);
    try {
      window.requestFileSystem(this.#mobileConfigFileType, this.#mobileConfigFileSize, 
        (fs) => this.#createMobileConfigFileSuccessCallback(fs), 
        (error) => this.#createMobileConfigFileErrorCallback(error)
      );
    } catch (error) {
      kameHouse.logger.error("Error creating file " + this.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error, null, null), null);
    }
  }
  
  /**
   * createMobileConfigFile callback.
   */
  #createMobileConfigFileSuccessCallback(fs) {
    fs.root.getFile(this.#mobileConfigFile, {create: true, exclusive: true}, (fileEntry) => {
      kameHouse.logger.info("File " + fileEntry.name + " created successfully", null);
    }, (error) => this.#createMobileConfigFileErrorCallback(error));
  }

  /**
   * createMobileConfigFile callback.
   */
  #createMobileConfigFileErrorCallback(error) {
    kameHouse.logger.error("Error creating file " + this.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error, null, null), null);
  }
  
  /**
   * --------------------------------------------------------------------------
   * Write config functions
   */  
  /**
   * Write kamehouse-mobile config file to the filesystem.
   */
  #writeMobileConfigFile() {
    kameHouse.logger.info("Writing to file " + this.#mobileConfigFile, null);
    try {
      window.requestFileSystem(this.#mobileConfigFileType, this.#mobileConfigFileSize, 
        (fs) => this.#writeMobileConfigFileSuccessCallback(fs), 
        (error) => this.#writeMobileConfigFileErrorCallback(error));
    } catch (error) {
      kameHouse.logger.error("Error writing file " + this.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error, null, null), null);
    }
  }

  /**
   * writeMobileConfigFile callback.
   */
  #writeMobileConfigFileSuccessCallback(fs) {
    fs.root.getFile(this.#mobileConfigFile, {create: true}, (fileEntry) => {
      fileEntry.createWriter((fileWriter) => {
        try {
        const fileContent = kameHouse.json.stringify(this.#getMobileConfig(), null, null);
        kameHouse.logger.debug("Encrypting file", null);
        const encryptedFileContent = CryptoJS.AES.encrypt(fileContent, this.#encryptionKey).toString();
        kameHouse.logger.debug("File content to write: " + fileContent, null);
        const blob = new Blob([encryptedFileContent]);
        fileWriter.write(blob);
        } catch(e) {
          kameHouse.logger.error("Error writing config file " + this.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(e, null, null), null); 
        }
      }, (error) => this.#writeMobileConfigFileErrorCallback(error));
    }, (error) => this.#writeMobileConfigFileErrorCallback(error));
  }

  /**
   * writeMobileConfigFile callback.
   */
  #writeMobileConfigFileErrorCallback(error) {
    kameHouse.logger.error("Error writing file " + this.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error, null, null), null);
  }
  
  /**
   * --------------------------------------------------------------------------
   * Read config functions
   */
  /**
   * Read kamehouse-mobile config file.
   */
  #readMobileConfigFile() {
    kameHouse.logger.info("Reading file " + this.#mobileConfigFile, null);
    try {
      window.requestFileSystem(this.#mobileConfigFileType, this.#mobileConfigFileSize, 
        (fs) => this.#readMobileConfigFileSuccessCallback(fs), 
        (error) => this.#readMobileConfigFileErrorCallback(error));
    } catch (error) {
      kameHouse.logger.error("Error reading file " + this.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error, null, null), null);
      this.#setKameHouseMobileModuleLoaded();
      this.#createMobileConfigFile();
    }
  }

  /**
   * readMobileConfigFile callback.
   */
  #readMobileConfigFileSuccessCallback(fs) {
    fs.root.getFile(this.#mobileConfigFile, {}, (fileEntry) => {
      fileEntry.file((file) => {
        const reader = new FileReader();
        reader.onloadend = (e) => {
          let mobileConfig = null;
          try {
            const encryptedFileContent = reader.result;
            kameHouse.logger.debug("Decrypting file", null);
            const fileContent = CryptoJS.AES.decrypt(encryptedFileContent, this.#encryptionKey).toString(CryptoJS.enc.Utf8);
            kameHouse.logger.debug("File content read: " + kameHouse.logger.maskSensitiveData(fileContent), null);
            mobileConfig = kameHouse.json.parse(fileContent);
          } catch (error) {
            mobileConfig = null;
            kameHouse.logger.error("Error parsing file content as json. Error " + kameHouse.json.stringify(error, null, null), null);
          }
          if (this.#isValidMobileConfigFile(mobileConfig)) {
            kameHouse.logger.info("Setting mobile config from file", null);
            this.#setMobileConfig(mobileConfig);
          } else {
            kameHouse.logger.warn("Mobile config file read from file is invalid. Re generating it", null);
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
    kameHouse.logger.error("Error reading file " + this.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error, null, null), null);
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
    kameHouse.logger.info("Deleting file " + this.#mobileConfigFile, null);
    try {
      window.requestFileSystem(this.#mobileConfigFileType, this.#mobileConfigFileSize, 
        (fs) => this.#deleteMobileConfigFileSuccessCallback(fs), 
        (error) => this.#deleteMobileConfigFileErrorCallback(error));
    } catch (error) {
      kameHouse.logger.error("Error deleting file " + this.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error, null, null), null);
    }
  }

  /**
   * deleteMobileConfigFile callback.
   */
  #deleteMobileConfigFileSuccessCallback(fs) {
    fs.root.getFile(this.#mobileConfigFile, {create: false}, (fileEntry) => {
      fileEntry.remove(() => {
        kameHouse.logger.info("File " + fileEntry.name + " deleted successfully", null);
      }, (error) => this.#deleteMobileConfigFileErrorCallback(error));
    }, (error) => this.#deleteMobileConfigFileErrorCallback(error));
  }

  /**
   * deleteMobileConfigFile callback.
   */
  #deleteMobileConfigFileErrorCallback(error) {
    kameHouse.logger.error("Error deleting file " + this.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error, null, null), null);
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
    const backendServerDropdown = document.getElementById("backend-server-dropdown") as HTMLSelectElement;
    for (let i = 0; i < backendServerDropdown.options.length; ++i) {
      if (backendServerDropdown.options[i].selected == true) {
        kameHouse.logger.debug("Setting selected backend in the config to: " + backendServerDropdown.options[i].textContent, null);
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
    const backendServerInput = document.getElementById("backend-server-input") as HTMLInputElement;
    kameHouse.logger.debug("Setting selected backend server url in the config to: " + backendServerInput.value, null);
    selectedBackendServer.url = backendServerInput.value;
  }

  /**
   * Update backend ssl check in config.
   */
  #updateBackendSslCheckInConfig() {
    const selectedBackendServer = kameHouse.extension.mobile.core.getSelectedBackendServer();
    const backendServerSkipSslCheckbox = document.getElementById("backend-skip-ssl-check-checkbox") as HTMLInputElement;
    kameHouse.logger.debug("Setting selected backend server skip ssl check in the config to: " + backendServerSkipSslCheckbox.checked, null);
    selectedBackendServer.skipSslCheck = backendServerSkipSslCheckbox.checked;
  }

  /**
   * Update backend server credentials in config.
   */
  #updateBackendServerCredentialsInConfig() {
    // Update backend.servers[] selected server credentials in config
    const selectedBackendServer = kameHouse.extension.mobile.core.getSelectedBackendServer();
    const username = (document.getElementById("backend-username-input") as HTMLInputElement).value;
    kameHouse.logger.debug("Setting selected backend server username in the config to: " + username, null);
    selectedBackendServer.username = username;
    const password = (document.getElementById("backend-password-input") as HTMLInputElement).value; 
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
      mobileClass: null,
      backgroundImg: null,
      html: "Yes",
      data: null,
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
    fs.root.getFile(this.#mobileConfigFile, {create: false}, 
      (fileEntry) => {
        fileEntry.remove(
          () => {
            kameHouse.logger.info("File " + fileEntry.name + " deleted successfully", null);
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
    kameHouse.logger.error("Error deleting file " + this.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error, null, null), null);
    this.#requestRecreateFile(openResultModal);
  }

  /**
   * --------------------------------------------------------------------------
   * 2. Recreate the file.
   */
  #requestRecreateFile(openResultModal) {
    window.requestFileSystem(this.#mobileConfigFileType, this.#mobileConfigFileSize, 
      (fs) => {this.#recreateFile(fs, openResultModal);}, 
      (error) => {this.#errorRecreateFileCallback(error, openResultModal);}
    );
  }

  /**
   * requestRecreateFile success callback.
   */
  #recreateFile(fs, openResultModal) {
    fs.root.getFile(this.#mobileConfigFile, {create: true, exclusive: true}, 
      (fileEntry) => {
        kameHouse.logger.info("File " + fileEntry.name + " recreated successfully", null);
        this.#requestRewriteFile(openResultModal);
      }, 
      (error) => {this.#errorRecreateFileCallback(error, openResultModal)}
    );
  }

  /**
   * recreateFile error callback.
   */
  #errorRecreateFileCallback(error, openResultModal) {
    kameHouse.logger.error("Error recreating file " + this.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error, null, null), null);
    this.#requestRewriteFile(openResultModal);
  }

  /**
   * --------------------------------------------------------------------------
   * 3. Rewrite file.
   */
  #requestRewriteFile(openResultModal) {
    window.requestFileSystem(this.#mobileConfigFileType, this.#mobileConfigFileSize, 
      (fs) => {this.#rewriteFile(fs, openResultModal);}, 
      (error) => {this.#errorRewriteFileCallback(error, openResultModal);}
    );
  }

  /**
   * requestRewriteFile success callback.
   */
  #rewriteFile(fs, openResultModal) {
    fs.root.getFile(this.#mobileConfigFile, {create: true}, 
      (fileEntry) => {
        fileEntry.createWriter(
          (fileWriter) => {
            try {
              const fileContent = kameHouse.json.stringify(this.#getMobileConfig(), null, null);
              kameHouse.logger.debug("Encrypting file", null);
              const encryptedFileContent = CryptoJS.AES.encrypt(fileContent, this.#encryptionKey).toString();
              kameHouse.logger.debug("File content to write: " + kameHouse.logger.maskSensitiveData(fileContent), null);
              const blob = new Blob([encryptedFileContent]);
              fileWriter.write(blob);
              this.#isCurrentlyPersistingConfig = false;
              if (openResultModal) {
                kameHouse.plugin.modal.basicModal.openAutoCloseable("Settings saved", 1000);
              }
            } catch(e) {
              kameHouse.logger.error("Error writing config file " + this.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(e, null, null), null); 
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
    kameHouse.logger.error("Error rewriting file " + this.#mobileConfigFile + ". Error: " + kameHouse.json.stringify(error, null, null), null);
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
  testFileManagement() {
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
    kameHouse.logger.info("Using mock localhost server for http request to: " + url, null);
    const responseBody = await this.#mockResponseBody(httpMethod, config, url, requestHeaders, requestBody);
    const responseCode = this.#mockResponseCode(httpMethod, config, url, requestHeaders, requestBody, responseBody);
    const responseDescription = this.#mockResponseDescription(httpMethod, config, url, requestHeaders, requestBody, responseBody);
    const responseHeaders = this.#mockResponseHeaders(httpMethod, config, url, requestHeaders, requestBody, responseBody);
    kameHouse.logger.logHttpResponse(config, url, responseBody, responseCode, responseDescription, responseHeaders);
    if (this.#isErrorResponseCode(responseCode)) {
      kameHouse.logger.debug("Executing errorCallback with mock response", null);
      errorCallback(responseBody, responseCode, responseDescription, responseHeaders); 
    } else {
      kameHouse.logger.debug("Executing successCallback with mock response", null);
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
    if (this.#isCrudEntityUrl(url)) {
      requestUrl = this.#getCrudBaseUrl(url);
    }
    if (this.#isModifiedUrl(requestUrl)) {
      requestUrl = requestUrl + "-modified";
    }
    kameHouse.logger.debug("Loading respose body from file: " + requestUrl, null);
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
      kameHouse.logger.debug("Returning crud entity: " + kameHouse.json.stringify(selectedCrudEntity, null, null), null);
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
    if (this.#isServerModificationRequest(httpMethod, url)) {
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
    const responseString = kameHouse.json.stringify(responseBody, null, null);
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

  plugin = {
    http: null
  };
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
    kameHouse.logger.trace("Called setServerTrustMode on cordova mock with " + trustMode, null);
    successCallback();
  }

  /**
   * Send http request.
   */
  sendRequest(requestUrl: string, options: Object, successCallback: Function, errorCallback: Function) {
    kameHouse.logger.trace("Called sendRequest on cordova mock with requestUrl: " + requestUrl + " and options " + kameHouse.json.stringify(options, null, null) + ". Mocking response", null);
    if (requestUrl.includes("/kame-house/api/v1/ui/session/status")) {
      return this.#mockSessionStatus(successCallback);
    }
    if (requestUrl.includes("/kame-house-groot/api/v1/commons/session/status.php")) {
      return this.#mockGrootSessionStatus(successCallback);
    }
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
    kameHouse.logger.trace("Called useBasicAuth on cordova mock", null);
  }

  /**
   * Set data serializer.
   */
  setDataSerializer(serializationType) {
    kameHouse.logger.trace("Called setDataSerializer on cordova mock with " + serializationType, null);
  }

  /**
   * Set header.
   */
  setHeader(key, value) {
    kameHouse.logger.trace("Called setHeader on cordova mock with " + key + ":" + value, null);
  }

  /**
   * Set request timeout.
   */
  setRequestTimeout(val) {
    kameHouse.logger.trace("Called setHeader on cordova mock with " + val, null);
  }

  /**
   * Set read timeout.
   */
  setReadTimeout(val) {
    kameHouse.logger.trace("Called setHeader on cordova mock with " + val, null);
  }

  /**
   * Mock session status.
   */
  #mockSessionStatus(successCallback) {
    const mockResponse = {
      status: 200,
      headers: {
        "Content-Type": "application/json"
      },
      data: {
        username: 'seiya',
        firstName: 'Seiya',
        lastName: 'Pegaso',
        server: 'Namek',
        sessionId: 'c7asf98-7ee7g-7547-9c47-ed450d982ae7',
        buildVersion: '9.99.9-r2d2c3p0',
        buildDate: '2099-99-99 00:00:00',
        roles: [
          'ROLE_NAMEKIAN',
          'ROLE_SAIYAJIN',
          'ROLE_KAMISAMA'
        ]
      }
    };
    successCallback(mockResponse);
  }

  /**
   * Mock groot session status.
   */
  #mockGrootSessionStatus(successCallback) {
    const mockResponse = {
      status: 200,
      headers: {
        "Content-Type": "application/json"
      },
      data: {
        server: "Namek",
        username: "seiya",
        isLinuxHost: false,
        isLinuxDockerHost: false,
        isDockerContainer: false,
        dockerControlHost: false,
        roles: [
          "ROLE_SAIYAJIN", 
          "ROLE_KAMISAMA"
        ]
      }
    };
    successCallback(mockResponse);    
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
    kameHouse.logger.trace("Called open in InAppBrowserMock with url " + url, null);
    setTimeout(() => {
      alert("kameHouse.cordova.InAppBrowser.open() call with:\n\nurl:\n" + url + "\n\ntarget:\n" + target + "\n\noptions:\n" + options);
    }, 100);
    setTimeout(() => {
      kameHouse.logger.info("Simulating successful exit event from InAppBrowserInstanceMock closing the modal", null);
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
    kameHouse.logger.trace("Called addEventListener on the InAppBrowserInstanceMock for event " + eventName, null);
  }

  /**
   * Show browser.
   */
  show() {
    kameHouse.logger.trace("Called show on the InAppBrowserInstanceMock", null);
  }

  /**
   * Close browser.
   */
  close() {
    kameHouse.logger.trace("Called close on the InAppBrowserInstanceMock", null);
  }
} // CordovaInAppBrowserInstanceMock

kameHouse.ready(() => {
  kameHouse.addExtension("mobile", new KameHouseMobile());
});