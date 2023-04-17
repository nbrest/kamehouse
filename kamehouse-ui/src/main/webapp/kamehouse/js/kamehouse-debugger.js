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
    kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/kamehouse-debugger.css">');
  }

  /**
   * Loads the debugger http client html snippet into a variable to be reused as a template on render.
   */
  async function loadDebuggerHttpClientTemplate() {
    debuggerHttpClientDivTemplate = await kameHouse.util.fetch.loadHtmlSnippet('/kame-house/kamehouse/html/kamehouse-debugger-http-client-table.html');
    kameHouse.logger.debug("Loaded debuggerHttpClientDivTemplate");
  }

  /** 
   * Toggle debug mode. 
   */
  function toggleDebugMode() {
    kameHouse.logger.debug("Toggled debug mode");
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
    kameHouse.util.dom.load($("#debug-mode-button-wrapper"), "/kame-house/kamehouse/html/kamehouse-debugger-button.html");
    kameHouse.util.dom.load($("#debug-mode-wrapper"), "/kame-house/kamehouse/html/kamehouse-debugger.html", () => {
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
  function displayPreviousRequestsTable(dataWithRequestInfo, responseBody, responseCode) {
    const request = {};
    request.requestData = dataWithRequestInfo.requestData;
    request.responseData = {};
    request.responseData.responseCode = responseCode;
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
  function displayResponseData(responseBody, responseCode) {
    const responseTimestamp = kameHouse.util.time.getTimestamp();
    kameHouse.util.dom.setHtml($("#debugger-http-client-res-code-val"), responseCode);
    kameHouse.util.dom.setHtml($("#debugger-http-client-res-timestamp-val"), responseTimestamp);
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
  this.getUrlEncoded = getUrlEncoded;
  this.put = put;
  this.putUrlEncoded = putUrlEncoded;
  this.post = post;
  this.postUrlEncoded = postUrlEncoded;
  this.delete = httpDelete;
  this.deleteUrlEncoded = deleteUrlEncoded;

  const GET = "GET";
  const POST = "POST";
  const PUT = "PUT";
  const DELETE = "DELETE";

  /** 
   * Execute a GET request, update the debugger http client 
   * and perform the specified success or error functions 
   * data is any extra data I want to pass to the success and error functions
   */
  function get(url, successCallback, errorCallback, data) {
    const dataWithRequestInfo = createDataWithRequestInfo(data, url, GET, null);
    kameHouse.plugin.debugger.displayRequestData(url, GET, null, null);
    kameHouse.http.get(url, null,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a GET request with url encoded parameters, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function getUrlEncoded(url, requestParam, successCallback, errorCallback, data) {
    const urlEncoded = encodeURI(url + "?" + requestParam);
    const dataWithRequestInfo = createDataWithRequestInfo(data, urlEncoded, GET, null);
    const requestHeaders = kameHouse.http.getUrlEncodedHeaders();
    kameHouse.plugin.debugger.displayRequestData(urlEncoded, GET, requestHeaders, null);
    kameHouse.http.get(urlEncoded, requestHeaders,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a PUT request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function put(url, requestBody, successCallback, errorCallback, data) {
    let requestHeaders = null;
    if (!kameHouse.core.isEmpty(requestBody)) {
      requestHeaders = kameHouse.http.getApplicationJsonHeaders();
    }
    const dataWithRequestInfo = createDataWithRequestInfo(data, url, PUT, requestBody);
    kameHouse.plugin.debugger.displayRequestData(url, PUT, requestHeaders, requestBody);
    kameHouse.http.put(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
    );
  }

  /** 
   * Execute a PUT request with url parameters, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function putUrlEncoded(url, requestParam, successCallback, errorCallback, data) {
    let requestHeaders = null;
    if (!kameHouse.core.isEmpty(requestParam)) {
      requestHeaders = kameHouse.http.getUrlEncodedHeaders();
    }
    const dataWithRequestInfo = createDataWithRequestInfo(data, url, PUT, requestParam);
    kameHouse.plugin.debugger.displayRequestData(url, PUT, requestHeaders, requestParam);
    kameHouse.http.put(url, requestHeaders, requestParam,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a POST request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function post(url, requestBody, successCallback, errorCallback, data) {
    let requestHeaders = null;
    if (!kameHouse.core.isEmpty(requestBody)) {
      requestHeaders = kameHouse.http.getApplicationJsonHeaders();
    }
    const dataWithRequestInfo = createDataWithRequestInfo(data, url, POST, requestBody);
    kameHouse.plugin.debugger.displayRequestData(url, POST, requestHeaders, requestBody);
    kameHouse.http.post(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a POST request with url parameters, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function postUrlEncoded(url, requestParam, successCallback, errorCallback, data) {
    let requestHeaders = null;
    if (!kameHouse.core.isEmpty(requestParam)) {
      requestHeaders = kameHouse.http.getUrlEncodedHeaders();
    }
    const dataWithRequestInfo = createDataWithRequestInfo(data, url, POST, requestParam);
    kameHouse.plugin.debugger.displayRequestData(url, POST, requestHeaders, requestParam);
    kameHouse.http.post(url, requestHeaders, requestParam,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a DELETE request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function httpDelete(url, requestBody, successCallback, errorCallback, data) {
    let requestHeaders = null;
    if (!kameHouse.core.isEmpty(requestBody)) {
      requestHeaders = kameHouse.http.getApplicationJsonHeaders();
    }
    const dataWithRequestInfo = createDataWithRequestInfo(data, url, DELETE, requestBody);
    kameHouse.plugin.debugger.displayRequestData(url, DELETE, requestHeaders, requestBody);
    kameHouse.http.delete(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a DELETE request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function deleteUrlEncoded(url, requestParam, successCallback, errorCallback, data) {
    let requestHeaders = null;
    if (!kameHouse.core.isEmpty(requestParam)) {
      requestHeaders = kameHouse.http.getApplicationJsonHeaders();
    }
    const dataWithRequestInfo = createDataWithRequestInfo(data, url, DELETE, requestParam);
    kameHouse.plugin.debugger.displayRequestData(url, DELETE, requestHeaders, requestParam);
    kameHouse.http.delete(url, requestHeaders, requestParam,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /**
   * Creates a data object that contains the data already received and the request info to eventually log in the requests table.
   */
  function createDataWithRequestInfo(data, url, method, requestBody) {
    const dataWithRequestInfo = {};
    dataWithRequestInfo.data = data;
    dataWithRequestInfo.requestData = {};
    dataWithRequestInfo.requestData.url = url;
    dataWithRequestInfo.requestData.method = method;
    dataWithRequestInfo.requestData.requestBody = requestBody;
    dataWithRequestInfo.requestData.timestamp = kameHouse.util.time.getTimestamp();
    return dataWithRequestInfo;
  }

  /** Process the response of the api call */
  function processResponse(responseBody, responseCode, responseDescription, responseCallback, dataWithRequestInfo) {
    kameHouse.plugin.debugger.displayResponseData(responseBody, responseCode);
    kameHouse.plugin.debugger.displayPreviousRequestsTable(dataWithRequestInfo, responseBody, responseCode);
    if (kameHouse.core.isFunction(responseCallback)) {
      responseCallback(responseBody, responseCode, responseDescription, dataWithRequestInfo.data);
    }
  }
}

/**
 * Call main.
 */
$(document).ready(() => {
  kameHouse.addPlugin("debugger", new KameHouseDebugger());
});