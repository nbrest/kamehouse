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
  let debuggerHttpClientDivTemplate;  
  
  async function load() {
    kameHouse.logger.info("Started initializing kameHouseDebugger");
    kameHouse.plugin.debugger.http = new DebuggerHttpClient();
    importKameHouseDebuggerCss();
    await loadDebuggerHttpClientTemplate();
    renderDebugMode();
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
    kameHouse.logger.info("Toggled debug mode");
    const debugModeDiv = document.getElementById("debug-mode");
    kameHouse.util.dom.classListToggle(debugModeDiv, "hidden-kh");
  }
  
  /**
   * Set the log level of the console.
   */
  function setConsoleLogLevel() {
    const logLevel = document.getElementById("debug-mode-log-level-dropdown").value;
    kameHouse.logger.setLogLevel(logLevel);
    
    kameHouse.logger.error("Set log level to " + logLevel);
    kameHouse.logger.warn("Set log level to " + logLevel);
    kameHouse.logger.info("Set log level to " + logLevel);
    kameHouse.logger.debug("Set log level to " + logLevel);
    kameHouse.logger.trace("Set log level to " + logLevel);
  }

  /**
   * Render debug mode div and it's button.
   */
  function renderDebugMode() {
    kameHouse.util.dom.load($("#debug-mode-wrapper"), "/kame-house/kamehouse/html/plugin/kamehouse-debugger.html", () => {
      kameHouse.util.module.setModuleLoaded("kameHouseDebugger");
      displayRequestData(null, null, null, null);
    });
  }

  /**
   * Render the specified html snippet into the custom div of the debugger.
   */
  function renderCustomDebugger(htmlSnippet) {
    kameHouse.util.dom.load($("#debug-mode-custom-wrapper"), htmlSnippet);
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
    request.responseData.responseBody = responseBody;
    request.responseData.timestamp = kameHouse.util.time.getTimestamp();
    while (requests.length >= 7) {
      requests.shift();
    }
    requests.push(request);
    kameHouse.util.dom.setText($('#debugger-http-client-previous-requests-val'), JSON.stringify(requests, null, 2));
    kameHouse.util.collapsibleDiv.setCollapsibleContent();
  }

  /**
   * Display debugger http client response data.
   */
  function displayResponseData(responseBody, responseCode, responseDescription, responseHeaders) {
    const responseTimestamp = kameHouse.util.time.getTimestamp();
    kameHouse.util.dom.setHtml($("#debugger-http-client-res-code-val"), responseCode);
    kameHouse.util.dom.setHtml($("#debugger-http-client-res-timestamp-val"), responseTimestamp);
    kameHouse.util.dom.setHtml($("#debugger-http-client-res-headers-val"), JSON.stringify(responseHeaders));
    kameHouse.util.dom.setText($("#debugger-http-client-res-body-val"), JSON.stringify(responseBody, null, 2));
    kameHouse.util.collapsibleDiv.setCollapsibleContent();
  }

  /**
   * Display debugger http client request data.
   */
  function displayRequestData(url, requestType, requestHeaders, requestBody) {
    emptyDebuggerHttpClientDiv();
    kameHouse.util.dom.setInnerHtml(document.getElementById("debugger-http-client"), debuggerHttpClientDivTemplate);
    const requestTimestamp = kameHouse.util.time.getTimestamp();
    kameHouse.util.dom.setHtml($('#debugger-http-client-req-timestamp-val'), requestTimestamp);
    kameHouse.util.dom.setHtml($('#debugger-http-client-req-url-val'), url);
    kameHouse.util.dom.setHtml($('#debugger-http-client-req-type-val'), requestType);
    kameHouse.util.dom.setHtml($('#debugger-http-client-req-headers-val'), JSON.stringify(requestHeaders));
    kameHouse.util.dom.setText($('#debugger-http-client-req-body-val'), JSON.stringify(requestBody, null, 2));
    kameHouse.util.dom.setHtml($('#debugger-http-client-res-code-val'), null);
    kameHouse.util.dom.setHtml($('#debugger-http-client-res-timestamp-val'), null);
    kameHouse.util.dom.setText($('#debugger-http-client-res-body-val'), JSON.stringify(null, null, 2));
    kameHouse.util.collapsibleDiv.setCollapsibleContent();
  }

  /**
   * Empty debugger http client div.
   */
  function emptyDebuggerHttpClientDiv() {
    const $debuggerHttpClientDiv = $("#debugger-http-client");
    kameHouse.util.dom.empty($debuggerHttpClientDiv);
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
  function get(url, requestHeaders, requestBody, successCallback, errorCallback) {
    const requestData = createRequestDataForLog(url, GET, requestHeaders, requestBody);
    kameHouse.plugin.debugger.displayRequestData(url, GET, requestHeaders, requestBody);
    kameHouse.http.get(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, successCallback, requestData),
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, errorCallback, requestData)
      );
  }

  /** 
   * Execute a PUT request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function put(url, requestHeaders, requestBody, successCallback, errorCallback) {
    const requestData = createRequestDataForLog(url, PUT, requestHeaders, requestBody);
    kameHouse.plugin.debugger.displayRequestData(url, PUT, requestHeaders, requestBody);
    kameHouse.http.put(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, successCallback, requestData),
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, errorCallback, requestData)
    );
  }

  /** 
   * Execute a POST request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function post(url, requestHeaders, requestBody, successCallback, errorCallback) {
    const requestData = createRequestDataForLog(url, POST, requestHeaders, requestBody);
    kameHouse.plugin.debugger.displayRequestData(url, POST, requestHeaders, requestBody);
    kameHouse.http.post(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, successCallback, requestData),
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, errorCallback, requestData)
      );
  }

  /** 
   * Execute a DELETE request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function httpDelete(url, requestHeaders, requestBody, successCallback, errorCallback) {
    const requestData = createRequestDataForLog(url, DELETE, requestHeaders, requestBody);
    kameHouse.plugin.debugger.displayRequestData(url, DELETE, requestHeaders, requestBody);
    kameHouse.http.delete(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, successCallback, requestData),
      (responseBody, responseCode, responseDescription, responseHeaders) => processResponse(responseBody, responseCode, responseDescription, responseHeaders, errorCallback, requestData)
      );
  }

  /**
   * Creates a data object that contains the data already received and the request info to eventually log in the requests table.
   */
  function createRequestDataForLog(url, method, requestHeaders, requestBody) {
    const requestData = {};
    requestData.url = url;
    requestData.method = method;
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