/** 
 * Handles the debugger functionality including the debugger wrapper to the httpClient.
 * 
 * @author nbrest
 */
var debuggerHttpClient;
var kameHouseDebugger;

function main() {
  importKameHouseDebuggerCss();
  logger.info("Started initializing kameHouseDebugger");
  kameHouseDebugger = new KameHouseDebugger();
  kameHouseDebugger.init();
  
  moduleUtils.waitForModules(["kameHouseDebugger"], () => {
    logger.info("Started initializing debuggerHttpClient");
    debuggerHttpClient = new DebuggerHttpClient();
    debuggerHttpClient.init();
  });
}

/** Import debugger-http-client css*/
function importKameHouseDebuggerCss() {
  domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/kamehouse-debugger.css">');
}

/** 
 * Handles the debugger functionality and renders the api calls in the debugger table.
 * 
 * @author nbrest
 */
function KameHouseDebugger() {

  this.init = init;
  this.toggleDebugMode = toggleDebugMode;
  this.setConsoleLogLevel = setConsoleLogLevel;
  this.renderCustomDebugger = renderCustomDebugger;
  this.displayPreviousRequestsTable = displayPreviousRequestsTable;
  this.displayResponseData = displayResponseData;
  this.displayRequestData = displayRequestData;

  const requests = [];
  let debuggerHttpClientDivTemplate;  
  
  async function init() {
    await loadDebuggerHttpClientTemplate();
    renderDebugMode();
  }

  /**
   * Loads the debugger http client html snippet into a variable to be reused as a template on render.
   */
  async function loadDebuggerHttpClientTemplate() {
    debuggerHttpClientDivTemplate = await fetchUtils.loadHtmlSnippet('/kame-house/html-snippets/kamehouse-debugger-http-client-table.html');
    logger.debug("Loaded debuggerHttpClientDivTemplate");
  }

  /** 
   * Toggle debug mode. 
   */
  function toggleDebugMode() {
    logger.debug("Toggled debug mode")
    const debugModeDiv = document.getElementById("debug-mode");
    domUtils.classListToggle(debugModeDiv, "hidden-kh");
  }
  
  /**
   * Set the log level of the console.
   */
  function setConsoleLogLevel() {
    const logLevel = document.getElementById("debug-mode-log-level-dropdown").value;
    logger.setLogLevel(logLevel);
    
    logger.error("Set log level to " + logLevel);
    logger.warn("Set log level to " + logLevel);
    logger.info("Set log level to " + logLevel);
    logger.debug("Set log level to " + logLevel);
    logger.trace("Set log level to " + logLevel);
  }

  /**
   * Render debug mode div and it's button.
   */
  function renderDebugMode() {
    domUtils.load($("#debug-mode-button-wrapper"), "/kame-house/html-snippets/kamehouse-debugger-button.html");
    domUtils.load($("#debug-mode-wrapper"), "/kame-house/html-snippets/kamehouse-debugger.html", () => {
      moduleUtils.setModuleLoaded("kameHouseDebugger");
      displayRequestData(null, null, null);
    });
  }

  /**
   * Render the specified html snippet into the custom div of the debugger.
   */
  function renderCustomDebugger(htmlSnippet) {
    domUtils.load($("#debug-mode-custom-wrapper"), htmlSnippet);
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
    request.responseData.timestamp = timeUtils.getTimestamp();
    while (requests.length >= 7) {
      requests.shift();
    }
    requests.push(request);
    domUtils.setHtml($('#debugger-http-client-previous-requests-val'), JSON.stringify(requests, null, 2));
    collapsibleDivUtils.setCollapsibleContent();
  }

  /**
   * Display debugger http client response data.
   */
  function displayResponseData(responseBody, responseCode) {
    logger.trace(arguments.callee.name);
    const responseTimestamp = timeUtils.getTimestamp();
    domUtils.setHtml($("#debugger-http-client-res-code-val"), responseCode);
    domUtils.setHtml($("#debugger-http-client-res-timestamp-val"), responseTimestamp);
    domUtils.setHtml($("#debugger-http-client-res-body-val"), JSON.stringify(responseBody, null, 2));
    collapsibleDivUtils.setCollapsibleContent();
  }

  /**
   * Display debugger http client request data.
   */
  function displayRequestData(url, requestType, requestBody) {
    logger.trace(arguments.callee.name);
    emptyDebuggerHttpClientDiv();
    domUtils.setInnerHtml(document.getElementById("debugger-http-client"), debuggerHttpClientDivTemplate);
    const requestTimestamp = timeUtils.getTimestamp();
    domUtils.setHtml($('#debugger-http-client-req-timestamp-val'), requestTimestamp);
    domUtils.setHtml($('#debugger-http-client-req-url-val'), url);
    domUtils.setHtml($('#debugger-http-client-req-type-val'), requestType);
    domUtils.setHtml($('#debugger-http-client-req-body-val'), JSON.stringify(requestBody, null, 2));
    domUtils.setHtml($('#debugger-http-client-res-code-val'), null);
    domUtils.setHtml($('#debugger-http-client-res-timestamp-val'), null);
    domUtils.setHtml($('#debugger-http-client-res-body-val'), JSON.stringify(null, null, 2));
    collapsibleDivUtils.setCollapsibleContent();
  }

  /**
   * Empty debugger http client div.
   */
  function emptyDebuggerHttpClientDiv() {
    const $debuggerHttpClientDiv = $("#debugger-http-client");
    domUtils.empty($debuggerHttpClientDiv);
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

  this.init = init;
  this.get = get;
  this.getUrlEncoded = getUrlEncoded;
  this.put = put;
  this.putUrlEncoded = putUrlEncoded;
  this.post = post;
  this.postUrlEncoded = postUrlEncoded;
  this.delete = httpDelete;
  this.deleteUrlEncoded = deleteUrlEncoded;

  function init() {
    moduleUtils.setModuleLoaded("debuggerHttpClient");
  }

  /** 
   * Execute a GET request, update the debugger http client 
   * and perform the specified success or error functions 
   * data is any extra data I want to pass to the success and error functions
   */
  function get(url, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    kameHouseDebugger.displayRequestData(url, "GET", null);
    const dataWithRequestInfo = createDataWithRequestInfo(data, url, "GET", null);
    httpClient.get(url, null,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a GET request with url encoded parameters, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function getUrlEncoded(url, requestParam, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    const urlEncoded = encodeURI(url + "?" + requestParam);
    kameHouseDebugger.displayRequestData(urlEncoded, "GET", null);
    const dataWithRequestInfo = createDataWithRequestInfo(data, urlEncoded, "GET", null);
    const requestHeaders = httpClient.getUrlEncodedHeaders();
    httpClient.get(urlEncoded, requestHeaders,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a PUT request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function put(url, requestBody, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    kameHouseDebugger.displayRequestData(url, "PUT", requestBody);
    const requestHeaders = httpClient.getApplicationJsonHeaders();
    const dataWithRequestInfo = createDataWithRequestInfo(data, url, "PUT", requestBody);
    httpClient.put(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
    );
  }

  /** 
   * Execute a PUT request with url parameters, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function putUrlEncoded(url, requestParam, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    const urlEncoded = encodeURI(url + "?" + requestParam);
    kameHouseDebugger.displayRequestData(urlEncoded, "PUT", null);
    const requestHeaders = httpClient.getUrlEncodedHeaders();
    const dataWithRequestInfo = createDataWithRequestInfo(data, urlEncoded, "PUT", null);
    httpClient.put(urlEncoded, requestHeaders, null,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a POST request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function post(url, requestBody, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    kameHouseDebugger.displayRequestData(url, "POST", requestBody);
    const requestHeaders = httpClient.getApplicationJsonHeaders();
    const dataWithRequestInfo = createDataWithRequestInfo(data, url, "POST", requestBody);
    httpClient.post(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a POST request with url parameters, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function postUrlEncoded(url, requestParam, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    const urlEncoded = encodeURI(url + "?" + requestParam);
    kameHouseDebugger.displayRequestData(urlEncoded, "POST", null);
    const requestHeaders = httpClient.getUrlEncodedHeaders();
    const dataWithRequestInfo = createDataWithRequestInfo(data, urlEncoded, "POST", null);
    httpClient.post(urlEncoded, requestHeaders, null,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a DELETE request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function httpDelete(url, requestBody, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    kameHouseDebugger.displayRequestData(url, "DELETE", requestBody);
    const requestHeaders = httpClient.getApplicationJsonHeaders();
    const dataWithRequestInfo = createDataWithRequestInfo(data, url, "DELETE", null);
    httpClient.delete(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a DELETE request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  function deleteUrlEncoded(url, requestParam, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    const urlEncoded = encodeURI(url + "?" + requestParam);
    kameHouseDebugger.displayRequestData(urlEncoded, "POST", null);
    const requestHeaders = httpClient.getUrlEncodedHeaders();
    const dataWithRequestInfo = createDataWithRequestInfo(data, urlEncoded, "DELETE", null);
    httpClient.delete(urlEncoded, requestHeaders, null,
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
    dataWithRequestInfo.requestData.timestamp = timeUtils.getTimestamp();
    return dataWithRequestInfo;
  }

  /** Process the response of the api call */
  function processResponse(responseBody, responseCode, responseDescription, responseCallback, dataWithRequestInfo) {
    kameHouseDebugger.displayResponseData(responseBody, responseCode);
    kameHouseDebugger.displayPreviousRequestsTable(dataWithRequestInfo, responseBody, responseCode);
    if (isFunction(responseCallback)) {
      responseCallback(responseBody, responseCode, responseDescription, dataWithRequestInfo.data);
    }
  }
}

/**
 * Call main.
 */
$(document).ready(main);