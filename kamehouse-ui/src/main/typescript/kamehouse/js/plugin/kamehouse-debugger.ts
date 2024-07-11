/** 
 * Handles the debugger functionality including the debugger wrapper to the kameHouse.http.
 * It renders the api calls in the debugger table.
 * 
 * @author nbrest
 */
class KameHouseDebugger {
  
  http: DebuggerHttpClient;

  #requests = [];
  #toggleDebuggerModalHtml = null;
  #debuggerHttpClientDivTemplate = null;
  #showRequestData = true;
  #showResponseData = false;

  constructor() {
    this.#toggleDebuggerModalHtml = this.getToggleDebuggerModalHtml();
  }
  
  /**
   * Load kamehouse debugger plugin.
   */
  async load() {
    kameHouse.logger.info("Started initializing kameHouseDebugger", null);
    this.http = new DebuggerHttpClient(this);
    this.importKameHouseDebuggerCss();
    await this.#loadDebuggerHttpClientTemplate();
    this.#renderDebugMode();
  }

  /** Import debugger-http-client css*/
  importKameHouseDebuggerCss() {
    kameHouse.util.dom.append(kameHouse.util.dom.getHead(), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/plugin/kamehouse-debugger.css">');
  }

  /** 
   * Toggle debug mode. 
   */
  toggleDebugMode() {
    kameHouse.plugin.modal.basicModal.openAutoCloseable(this.#toggleDebuggerModalHtml, 1000);
    const message = "Toggled debug mode";
    kameHouse.logger.info(message, kameHouse.logger.getGreenText(message));
    const debugModeDiv = document.getElementById("debug-mode");
    kameHouse.util.dom.toggleClassOnElement(debugModeDiv, "hidden-kh");
  }

  /**
   * Set the log level of the console.
   */
  setConsoleLogLevel() {
    const logLevelDropdown = document.getElementById("debug-mode-log-level-dropdown") as HTMLSelectElement;
    const logLevel = logLevelDropdown.value;
    let logLevelName = "";
    for (const option of logLevelDropdown.options) {
      if (option.selected === true) {
        logLevelName = option.textContent;
      }
    }

    kameHouse.logger.setLogLevel(logLevel);
    
    const message = "Set log level to " + logLevelName;
    
    if (logLevelName == "ERROR") {
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
    }
    if (logLevelName == "WARN") {
      kameHouse.logger.warn(message, kameHouse.logger.getYellowText(message));
    }
    if (logLevelName == "INFO") {
      kameHouse.logger.info(message, kameHouse.logger.getBlueText(message)); 
    }
    if (logLevelName == "DEBUG") {
      kameHouse.logger.debug(message, kameHouse.logger.getGreenText(message)); 
    }
    if (logLevelName == "TRACE") {
      kameHouse.logger.trace(message, kameHouse.logger.getCyanText(message));
    }
    kameHouse.logger.trace("Setting kh-log-level cookie to " + logLevel, null);
    kameHouse.util.cookies.setCookie("kh-log-level", logLevel, null);
  }

  /**
   * Render the specified html snippet into the custom div of the debugger.
   */
  renderCustomDebugger(htmlSnippet, callback) {
    kameHouse.util.dom.loadById("debug-mode-custom-wrapper", htmlSnippet, callback);
  }

  /**
   * Displays the list of the N previous requests.
   */
  displayPreviousRequestsTable(requestData, responseBody, responseCode, responseHeaders) {
    const responseData = {
      responseCode: responseCode,
      headers: responseHeaders,
      responseBody: null,
      timestamp: null
    };
    const request = {
      requestData: requestData,
      responseData: responseData
    };
    let trimmedResponseBody = kameHouse.json.stringify(responseBody, null, null);
    if (!kameHouse.core.isEmpty(trimmedResponseBody) && trimmedResponseBody.length > 1000) {
      trimmedResponseBody = trimmedResponseBody.slice(0, 1000) + "... [trimmed]";
    }
    request.responseData.responseBody = trimmedResponseBody;
    request.responseData.timestamp = kameHouse.util.time.getTimestamp(null);
    while (this.#requests.length >= 7) {
      this.#requests.shift();
    }
    this.#requests.push(request);
    kameHouse.util.collapsibleDiv.resize("debugger-http-client-previous-requests");
    kameHouse.util.dom.setTextById('debugger-http-client-previous-requests-val', kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(this.#requests, null, 2)));
  }

  /**
   * Display debugger http client response data.
   */
  displayResponseData(responseBody, responseCode, responseDescription, responseHeaders, requestData) {
    this.displayRequestData(requestData);
    const responseTimestamp = kameHouse.util.time.getTimestamp(null);
    kameHouse.util.dom.setHtmlById("debugger-http-client-res-code-val", responseCode);
    kameHouse.util.dom.setHtmlById("debugger-http-client-res-timestamp-val", responseTimestamp);
    kameHouse.util.dom.setHtmlById("debugger-http-client-res-headers-val", kameHouse.json.stringify(responseHeaders, null, null));
    kameHouse.util.dom.setTextById("debugger-http-client-res-body-val", kameHouse.json.stringify(responseBody, null, 2));
  }  

  /**
   * Display debugger http client request data.
   */
  displayRequestData(requestData) {
    if (kameHouse.core.isEmpty(requestData)) {
      return;
    }
    kameHouse.util.dom.setHtmlById('debugger-http-client-req-timestamp-val', requestData.timestamp);
    kameHouse.util.dom.setHtmlById('debugger-http-client-req-method-val', requestData.method);
    kameHouse.util.dom.setHtmlById('debugger-http-client-req-url-val', requestData.url);
    kameHouse.util.dom.setHtmlById('debugger-http-client-req-config-val', kameHouse.json.stringify(requestData.config, null, null));
    kameHouse.util.dom.setHtmlById('debugger-http-client-req-headers-val', kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(requestData.headers, null, null)));
    kameHouse.util.dom.setTextById('debugger-http-client-req-body-val', kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(requestData.body, null, 2)));
    kameHouse.util.dom.setHtmlById('debugger-http-client-res-code-val', null);
    kameHouse.util.dom.setHtmlById('debugger-http-client-res-timestamp-val', null);
    kameHouse.util.dom.setHtmlById('debugger-http-client-res-headers-val', null);
    kameHouse.util.dom.setTextById('debugger-http-client-res-body-val', null);
  }

  /**
   * Get toggle debugger modal html.
   */
  getToggleDebuggerModalHtml() {
    const img = kameHouse.util.dom.getImg({
      src: "/kame-house/img/dbz/freezer.png",
      className: "debug-mode-btn",
      alt: "Debug Mode modal"
    });
    const text = "Toggled debug mode!";
    const div = kameHouse.util.dom.getDiv(null, null);
    kameHouse.util.dom.append(div, img);
    kameHouse.util.dom.append(div, text);
    return div;
  }

  /**
   * Toggle view/hide response data.
   */
  toggleRequestData() {
    this.#showRequestData = !this.#showRequestData;
    this.#updateRequestDataVisibility();
  }

  /**
   * Toggle view/hide response data.
   */
  toggleResponseData() {
    this.#showResponseData = !this.#showResponseData;
    this.#updateResponseDataVisibility();
  }

  /**
   * Update request data visibility.
   */
  #updateRequestDataVisibility() {
    const requestDataElements = document.getElementsByClassName("debug-mode-request-data");
    for (const requestDataElement of requestDataElements) {
      if (this.#showRequestData) {
        kameHouse.util.dom.classListRemove(requestDataElement, "hidden-kh");
      } else {
        kameHouse.util.dom.classListAdd(requestDataElement, "hidden-kh");
      }
    }
    const requestDataHeader = document.getElementById("debugger-http-client-req-header");
    if (this.#showRequestData) {
      kameHouse.util.dom.classListAdd(requestDataHeader, "debug-mode-client-table-header-btn-expanded");
    } else {
      kameHouse.util.dom.classListRemove(requestDataHeader, "debug-mode-client-table-header-btn-expanded");
    }    
  }  

  /**
   * Update response data visibility.
   */
  #updateResponseDataVisibility() {
    const responseDataElements = document.getElementsByClassName("debug-mode-response-data");
    for (const responseDataElement of responseDataElements) {
      if (this.#showResponseData) {
        kameHouse.util.dom.classListRemove(responseDataElement, "hidden-kh");
      } else {
        kameHouse.util.dom.classListAdd(responseDataElement, "hidden-kh");
      }
    }
    const responseDataHeader = document.getElementById("debugger-http-client-res-header");
    if (this.#showResponseData) {
      kameHouse.util.dom.classListAdd(responseDataHeader, "debug-mode-client-table-header-btn-expanded");
    } else {
      kameHouse.util.dom.classListRemove(responseDataHeader, "debug-mode-client-table-header-btn-expanded");
    }
  }

  /**
   * Loads the debugger http client html snippet into a variable to be reused as a template on render.
   */
  async #loadDebuggerHttpClientTemplate() {
    this.#debuggerHttpClientDivTemplate = await kameHouse.util.fetch.loadHtmlSnippet('/kame-house/kamehouse/html/plugin/kamehouse-debugger-http-client-table.html');
    kameHouse.logger.debug("Loaded debuggerHttpClientDivTemplate", null);
  }

  /**
   * Render debug mode div and it's button.
   */
  #renderDebugMode() {
    kameHouse.util.dom.loadById("debug-mode-wrapper", "/kame-house/kamehouse/html/plugin/kamehouse-debugger.html", () => {
      kameHouse.util.module.setModuleLoaded("kameHouseDebugger");
      this.#emptyDebuggerHttpClientDiv();
      kameHouse.util.dom.setHtmlById("debugger-http-client", this.#debuggerHttpClientDivTemplate);
      this.displayRequestData({});
      this.#setConsoleLogLevelDropdown();
      kameHouse.core.configDynamicHtml();
    });
  }

  /**
   * Empty debugger http client div.
   */
  #emptyDebuggerHttpClientDiv() {
    const debuggerHttpClientDiv = document.getElementById("debugger-http-client");
    kameHouse.util.dom.empty(debuggerHttpClientDiv);
  }

  /**
   * Set console log level dropdown.
   */
  #setConsoleLogLevelDropdown() {
    const currentLogLevel = kameHouse.logger.getLogLevel();
    kameHouse.logger.debug("Updating debugger console log level dropdown to " + currentLogLevel, null);
    const logLevelDropdown = document.getElementById("debug-mode-log-level-dropdown") as HTMLSelectElement;
    let option: HTMLOptionElement;
    for (option of logLevelDropdown.options) {
      if (option.value == currentLogLevel.toString()) {
        option.selected = true;
      } else {
        option.selected = false;
      }
    }
  }
}

/**
 * Functionality that executes api requests to render in the debugger api table.
 * 
 * Dependencies: timeUtils, logger, httpClient
 * 
 * @author nbrest
 */
class DebuggerHttpClient {

  #GET = "GET";
  #POST = "POST";
  #PUT = "PUT";
  #DELETE = "DELETE";

  #kameHouseDebugger = null;

  constructor(kameHouseDebugger) {
    this.#kameHouseDebugger = kameHouseDebugger;
  }

  /** 
   * Execute a GET request, update the debugger http client 
   * and perform the specified success or error functions 
   * data is any extra data I want to pass to the success and error functions
   */
  get(config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    const requestData = this.#createRequestDataForLog(this.#GET, config, url, requestHeaders, requestBody);
    this.#kameHouseDebugger.displayRequestData(requestData);
    kameHouse.http.get(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#processResponse(responseBody, responseCode, responseDescription, responseHeaders, successCallback, requestData),
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#processResponse(responseBody, responseCode, responseDescription, responseHeaders, errorCallback, requestData)
      );
  }

  /** 
   * Execute a PUT request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  put(config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    const requestData = this.#createRequestDataForLog(this.#PUT, config, url, requestHeaders, requestBody);
    this.#kameHouseDebugger.displayRequestData(requestData);
    kameHouse.http.put(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#processResponse(responseBody, responseCode, responseDescription, responseHeaders, successCallback, requestData),
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#processResponse(responseBody, responseCode, responseDescription, responseHeaders, errorCallback, requestData)
    );
  }

  /** 
   * Execute a POST request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  post(config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    const requestData = this.#createRequestDataForLog(this.#POST, config, url, requestHeaders, requestBody);
    this.#kameHouseDebugger.displayRequestData(requestData);
    kameHouse.http.post(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#processResponse(responseBody, responseCode, responseDescription, responseHeaders, successCallback, requestData),
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#processResponse(responseBody, responseCode, responseDescription, responseHeaders, errorCallback, requestData)
      );
  }

  /** 
   * Execute a DELETE request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  delete(config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    const requestData = this.#createRequestDataForLog(this.#DELETE, config, url, requestHeaders, requestBody);
    this.#kameHouseDebugger.displayRequestData(requestData);
    kameHouse.http.delete(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#processResponse(responseBody, responseCode, responseDescription, responseHeaders, successCallback, requestData),
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#processResponse(responseBody, responseCode, responseDescription, responseHeaders, errorCallback, requestData)
      );
  }

  /**
   * Creates a data object that contains the data already received and the request info to eventually log in the requests table.
   */
  #createRequestDataForLog(method, config, url, requestHeaders, requestBody) {
    const requestData = {
      method: method,
      url: url,
      config: config,
      headers: requestHeaders,
      body: requestBody,
      timestamp: kameHouse.util.time.getTimestamp(null)
    };
    return requestData;
  }

  /** Process the response of the api call */
  #processResponse(responseBody, responseCode, responseDescription, responseHeaders, responseCallback, requestData) {
    this.#kameHouseDebugger.displayResponseData(responseBody, responseCode, responseDescription, responseHeaders, requestData);
    this.#kameHouseDebugger.displayPreviousRequestsTable(requestData, responseBody, responseCode, responseHeaders);
    if (kameHouse.core.isFunction(responseCallback)) {
      responseCallback(responseBody, responseCode, responseDescription, responseHeaders);
    }
  }
}

kameHouse.ready(() => {
  kameHouse.addPlugin("debugger", new KameHouseDebugger());
});