/** 
 * Handles the debugger functionality including the debugger wrapper to the kameHouse.http.
 * 
 * @author nbrest
 */

/** 
 * Handles the debugger functionality and renders the api calls in the debugger table.
 */
function KameHouseDebugger() {
  
  this.load = load;
  this.importKameHouseDebuggerCss = importKameHouseDebuggerCss;
  this.toggleDebugMode = toggleDebugMode;
  this.setConsoleLogLevel = setConsoleLogLevel;
  this.renderCustomDebugger = renderCustomDebugger;
  this.displayPreviousRequestsTable = displayPreviousRequestsTable;
  this.displayResponseData = displayResponseData;
  this.displayRequestData = displayRequestData;

  const requests = [];
  const debuggerModalHtml = getDebuggerModalHtml();

  let debuggerHttpClientDivTemplate;  
  
  async function load() {
    kameHouse.logger.info("Started initializing kameHouseDebugger");
    kameHouse.plugin.debugger.http = new DebuggerHttpClient();
    importKameHouseDebuggerCss();
    await loadDebuggerHttpClientTemplate();
    renderDebugMode();
  }

  function getDebuggerModalHtml() {
    const img = kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/other/debug-btn-success.png",
      className: "debug-mode-btn",
      alt: "Debug Mode modal",
      onClick: () => {return;}
    });
    const text = "Toggled debug mode!";
    const div = kameHouse.util.dom.getDiv();
    kameHouse.util.dom.append(div, img);
    kameHouse.util.dom.append(div, text);
    return div;
  }

  /** Import debugger-http-client css*/
  function importKameHouseDebuggerCss() {
    kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/plugin/kamehouse-debugger.css">');
  }

  /**
   * Loads the debugger http client html snippet into a variable to be reused as a template on render.
   */
  async function loadDebuggerHttpClientTemplate() {
    debuggerHttpClientDivTemplate = await kameHouse.util.fetch.loadHtmlSnippet('/kame-house/kamehouse/html/plugin/kamehouse-debugger-http-client-table.html');
    kameHouse.logger.debug("Loaded debuggerHttpClientDivTemplate");
  }

  /** 
   * Toggle debug mode. 
   */
  function toggleDebugMode() {
    kameHouse.plugin.modal.basicModal.openAutoCloseable(debuggerModalHtml, 1000);
    const message = "Toggled debug mode";
    kameHouse.logger.info(message, kameHouse.logger.getGreenText(message));
    const debugModeDiv = document.getElementById("debug-mode");
    kameHouse.util.dom.classListToggle(debugModeDiv, "hidden-kh");
  }
  
  /**
   * Set the log level of the console.
   */
  function setConsoleLogLevel() {
    const logLevelDropdown = document.getElementById("debug-mode-log-level-dropdown");
    const logLevel = logLevelDropdown.value;
    let logLevelName = "";
    for (let i = 0; i < logLevelDropdown.options.length; ++i) {
      if (logLevelDropdown.options[i].selected === true) {
        logLevelName = logLevelDropdown.options[i].textContent;
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
    kameHouse.logger.trace("Setting kh-log-level cookie to " + logLevel);
    kameHouse.util.cookies.setCookie("kh-log-level", logLevel);
  }

  /**
   * Render debug mode div and it's button.
   */
  function renderDebugMode() {
    kameHouse.util.dom.load($("#debug-mode-wrapper"), "/kame-house/kamehouse/html/plugin/kamehouse-debugger.html", () => {
      kameHouse.util.module.setModuleLoaded("kameHouseDebugger");
      displayRequestData(null, null, null, null);
      setConsoleLogLevelDropdown();
    });
  }

  /**
   * Render the specified html snippet into the custom div of the debugger.
   */
  function renderCustomDebugger(htmlSnippet, callback) {
    kameHouse.util.dom.load($("#debug-mode-custom-wrapper"), htmlSnippet, callback);
  }

  /**
   * Displays the list of the N previous requests.
   */
  function displayPreviousRequestsTable(requestData, responseBody, responseCode, responseHeaders) {
    const request = {};
    request.requestData = requestData;
    request.responseData = {};
    request.responseData.responseCode = responseCode;
    request.responseData.headers = responseHeaders;
    let trimmedResponseBody = kameHouse.json.stringify(responseBody);
    if (!kameHouse.core.isEmpty(trimmedResponseBody) && trimmedResponseBody.length > 1000) {
      trimmedResponseBody = trimmedResponseBody.slice(0, 1000) + "... [trimmed]";
    }
    request.responseData.responseBody = trimmedResponseBody;
    request.responseData.timestamp = kameHouse.util.time.getTimestamp();
    while (requests.length >= 7) {
      requests.shift();
    }
    requests.push(request);
    kameHouse.util.dom.setText($('#debugger-http-client-previous-requests-val'), kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(requests, null, 2)));
    kameHouse.util.collapsibleDiv.setCollapsibleContent();
  }

  /**
   * Display debugger http client response data.
   */
  function displayResponseData(responseBody, responseCode, responseDescription, responseHeaders) {
    const responseTimestamp = kameHouse.util.time.getTimestamp();
    kameHouse.util.dom.setHtml($("#debugger-http-client-res-code-val"), responseCode);
    kameHouse.util.dom.setHtml($("#debugger-http-client-res-timestamp-val"), responseTimestamp);
    kameHouse.util.dom.setHtml($("#debugger-http-client-res-headers-val"), kameHouse.json.stringify(responseHeaders));
    kameHouse.util.dom.setText($("#debugger-http-client-res-body-val"), kameHouse.json.stringify(responseBody, null, 2));
    kameHouse.util.collapsibleDiv.setCollapsibleContent();
  }

  /**
   * Display debugger http client request data.
   */
  function displayRequestData(method, config, url, requestHeaders, requestBody) {
    emptyDebuggerHttpClientDiv();
    kameHouse.util.dom.setInnerHtml(document.getElementById("debugger-http-client"), debuggerHttpClientDivTemplate);
    const requestTimestamp = kameHouse.util.time.getTimestamp();
    kameHouse.util.dom.setHtml($('#debugger-http-client-req-timestamp-val'), requestTimestamp);
    kameHouse.util.dom.setHtml($('#debugger-http-client-req-method-val'), method);
    kameHouse.util.dom.setHtml($('#debugger-http-client-req-url-val'), url);
    kameHouse.util.dom.setHtml($('#debugger-http-client-req-config-val'), kameHouse.json.stringify(config));
    kameHouse.util.dom.setHtml($('#debugger-http-client-req-headers-val'), kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(requestHeaders)));
    kameHouse.util.dom.setText($('#debugger-http-client-req-body-val'), kameHouse.logger.maskSensitiveData(kameHouse.json.stringify(requestBody, null, 2)));
    kameHouse.util.dom.setHtml($('#debugger-http-client-res-code-val'), null);
    kameHouse.util.dom.setHtml($('#debugger-http-client-res-timestamp-val'), null);
    kameHouse.util.dom.setText($('#debugger-http-client-res-body-val'), kameHouse.json.stringify(null, null, 2));
    kameHouse.util.collapsibleDiv.setCollapsibleContent();
  }

  /**
   * Empty debugger http client div.
   */
  function emptyDebuggerHttpClientDiv() {
    const $debuggerHttpClientDiv = $("#debugger-http-client");
    kameHouse.util.dom.empty($debuggerHttpClientDiv);
  }

  function setConsoleLogLevelDropdown() {
    const currentLogLevel = kameHouse.logger.getLogLevel();
    kameHouse.logger.debug("Updating debugger console log level dropdown to " + currentLogLevel);
    const logLevelDropdown = document.getElementById("debug-mode-log-level-dropdown");
    for (let i = 0; i < logLevelDropdown.options.length; ++i) {
      if (logLevelDropdown.options[i].value == currentLogLevel) {
        logLevelDropdown.options[i].selected = true;
      } else {
        logLevelDropdown.options[i].selected = false;
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
function DebuggerHttpClient() {

  this.get = get;
  this.put = put;
  this.post = post;
  this.delete = httpDelete;

  const GET = "GET";
  const POST = "POST";
  const PUT = "PUT";
  const DELETE = "DELETE";

  /** 
   * Execute a GET request, update the debugger http client 
   * and perform the specified success or error functions 
   * data is any extra data I want to pass to the success and error functions
   */
  function get(config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    const requestData = createRequestDataForLog(GET, config, url,requestHeaders, requestBody);
    kameHouse.plugin.debugger.displayRequestData(GET, config, url, requestHeaders, requestBody);
    kameHouse.http.get(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, successCallback, requestData),
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, errorCallback, requestData)
      );
  }

  /** 
   * Execute a PUT request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function put(config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    const requestData = createRequestDataForLog(PUT, config, url, requestHeaders, requestBody);
    kameHouse.plugin.debugger.displayRequestData(PUT, config, url, requestHeaders, requestBody);
    kameHouse.http.put(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, successCallback, requestData),
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, errorCallback, requestData)
    );
  }

  /** 
   * Execute a POST request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function post(config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    const requestData = createRequestDataForLog(POST, config, url, requestHeaders, requestBody);
    kameHouse.plugin.debugger.displayRequestData(POST, config, url, requestHeaders, requestBody);
    kameHouse.http.post(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, successCallback, requestData),
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, errorCallback, requestData)
      );
  }

  /** 
   * Execute a DELETE request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function httpDelete(config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    const requestData = createRequestDataForLog(DELETE, config, url, requestHeaders, requestBody);
    kameHouse.plugin.debugger.displayRequestData(DELETE, config, url, requestHeaders, requestBody);
    kameHouse.http.delete(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, successCallback, requestData),
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, errorCallback, requestData)
      );
  }

  /**
   * Creates a data object that contains the data already received and the request info to eventually log in the requests table.
   */
  function createRequestDataForLog(method, config, url, requestHeaders, requestBody) {
    const requestData = {};
    requestData.method = method;
    requestData.url = url;
    requestData.config = config;
    requestData.headers = requestHeaders;
    requestData.requestBody = requestBody;
    requestData.timestamp = kameHouse.util.time.getTimestamp();
    return requestData;
  }

  /** Process the response of the api call */
  function processResponse(responseBody, responseCode, responseDescription, responseHeaders, responseCallback, requestData) {
    kameHouse.plugin.debugger.displayResponseData(responseBody, responseCode, responseDescription, responseHeaders);
    kameHouse.plugin.debugger.displayPreviousRequestsTable(requestData, responseBody, responseCode, responseHeaders);
    if (kameHouse.core.isFunction(responseCallback)) {
      responseCallback(responseBody, responseCode, responseDescription, responseHeaders);
    }
  }
}

$(document).ready(() => {
  kameHouse.addPlugin("debugger", new KameHouseDebugger());
});