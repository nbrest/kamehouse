/** 
 * Handles the debugger functionality including the debugger wrapper to the httpClient.
 * 
 * @author nbrest
 */
var debuggerHttpClient;
var kameHouseDebugger;

function main() {
  importKameHouseDebuggerCss();
  moduleUtils.waitForModules(["logger", "httpClient"], () => {
    logger.info("Started initializing kameHouseDebugger");
    kameHouseDebugger = new KameHouseDebugger();
    kameHouseDebugger.init();
  });
  
  moduleUtils.waitForModules(["kameHouseDebugger"], () => {
    logger.info("Started initializing debuggerHttpClient");
    debuggerHttpClient = new DebuggerHttpClient();
    debuggerHttpClient.init();
  });
}

/** Import debugger-http-client css*/
function importKameHouseDebuggerCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/kamehouse-debugger.css">');
}

/** 
 * Handles the debugger functionality and renders the api calls in the debugger table.
 * 
 * @author nbrest
 */
function KameHouseDebugger() {
  let self = this;
  this.requests = [];
  this.debuggerHttpClientDivTemplate;  
  
  this.init = async () => {
    await self.loadDebuggerHttpClientTemplate();
    self.renderDebugMode();
  }

  /**
   * Loads the debugger http client html snippet into a variable to be reused as a template on render.
   */
  this.loadDebuggerHttpClientTemplate = async () => {
    const response = await fetch('/kame-house/html-snippets/kamehouse-debugger-http-client-table.html');
    self.debuggerHttpClientDivTemplate = await response.text();
    logger.debug("Loaded debuggerHttpClientDivTemplate");
  }

  /** 
   * Toggle debug mode. 
   */
  this.toggleDebugMode = () => {
    logger.debug("Toggled debug mode")
    let debugModeDiv = document.getElementById("debug-mode");
    debugModeDiv.classList.toggle("hidden-kh");
  }
  
  /**
   * Set the log level of the console.
   */
  this.setConsoleLogLevel = () => {
    let logLevel = document.getElementById("debug-mode-log-level-dropdown").value;
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
  this.renderDebugMode = () => {
    $("#debug-mode-button-wrapper").load("/kame-house/html-snippets/kamehouse-debugger-button.html");
    $("#debug-mode-wrapper").load("/kame-house/html-snippets/kamehouse-debugger.html", () => {
      moduleUtils.setModuleLoaded("kameHouseDebugger");
      self.displayRequestData(null, null, null);
    });
  }

  /**
   * Render the specified html snippet into the custom div of the debugger.
   */
  this.renderCustomDebugger = (htmlSnippet) => {
    $("#debug-mode-custom-wrapper").load(htmlSnippet);
  }

  /**
   * Displays the list of the N previous requests.
   */
   this.displayPreviousRequestsTable = (dataWithRequestInfo, responseBody, responseCode) => {
    let request = {};
    request.requestData = dataWithRequestInfo.requestData;
    request.responseData = {};
    request.responseData.responseCode = responseCode;
    request.responseData.responseBody = responseBody;
    request.responseData.timestamp = timeUtils.getTimestamp();
    while (self.requests.length >= 7) {
      self.requests.shift();
    }
    self.requests.push(request);
    $('#debugger-http-client-previous-requests-val').text(JSON.stringify(self.requests, null, 2));
    collapsibleDivUtils.setCollapsibleContent();
  }

  /**
   * Display debugger http client response data.
   */
  this.displayResponseData = function displayResponseData(responseBody, responseCode) {
    logger.trace(arguments.callee.name);
    let responseTimestamp = timeUtils.getTimestamp();
    $("#debugger-http-client-res-code-val").text(responseCode);
    $("#debugger-http-client-res-timestamp-val").text(responseTimestamp);
    $("#debugger-http-client-res-body-val").text(JSON.stringify(responseBody, null, 2));
    collapsibleDivUtils.setCollapsibleContent();
  }

  /**
   * Display debugger http client request data.
   */
  this.displayRequestData = function displayRequestData(url, requestType, requestBody) {
    logger.trace(arguments.callee.name);
    self.emptyDebuggerHttpClientDiv();
    document.getElementById("debugger-http-client").innerHTML = self.debuggerHttpClientDivTemplate;
    let requestTimestamp = timeUtils.getTimestamp();
    $('#debugger-http-client-req-timestamp-val').text(requestTimestamp);
    $('#debugger-http-client-req-url-val').text(url);
    $('#debugger-http-client-req-type-val').text(requestType);
    $('#debugger-http-client-req-body-val').text(JSON.stringify(requestBody, null, 2));
    $('#debugger-http-client-res-code-val').text(null);
    $('#debugger-http-client-res-timestamp-val').text(null);
    $('#debugger-http-client-res-body-val').text(JSON.stringify(null, null, 2));
    collapsibleDivUtils.setCollapsibleContent();
  }

  /**
   * Empty debugger http client div.
   */
  this.emptyDebuggerHttpClientDiv = () => {
    let $debuggerHttpClientDiv = $("#debugger-http-client");
    $debuggerHttpClientDiv.empty();
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
  let self = this;
  
  this.init = () => {
    moduleUtils.setModuleLoaded("debuggerHttpClient");
  }

  /** 
   * Execute a GET request, update the debugger http client 
   * and perform the specified success or error functions 
   * data is any extra data I want to pass to the success and error functions
   */
  this.get = function httpGet(url, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    kameHouseDebugger.displayRequestData(url, "GET", null);
    let dataWithRequestInfo = self.createDataWithRequestInfo(data, url, "GET", null);
    httpClient.get(url, null,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a GET request with url encoded parameters, update the debugger http client 
   * and perform the specified success or error functions 
   */
  this.getUrlEncoded = function httpGetUrlEncoded(url, requestParam, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    let urlEncoded = encodeURI(url + "?" + requestParam);
    kameHouseDebugger.displayRequestData(urlEncoded, "GET", null);
    let dataWithRequestInfo = self.createDataWithRequestInfo(data, urlEncoded, "GET", null);
    let requestHeaders = httpClient.getUrlEncodedHeaders();
    httpClient.get(urlEncoded, requestHeaders,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a PUT request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  this.put = function httpPut(url, requestBody, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    kameHouseDebugger.displayRequestData(url, "PUT", requestBody);
    let requestHeaders = httpClient.getApplicationJsonHeaders();
    let dataWithRequestInfo = self.createDataWithRequestInfo(data, url, "PUT", requestBody);
    httpClient.put(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
    );
  }

  /** 
   * Execute a POST request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  this.post = function httpPost(url, requestBody, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    kameHouseDebugger.displayRequestData(url, "POST", requestBody);
    let requestHeaders = httpClient.getApplicationJsonHeaders();
    let dataWithRequestInfo = self.createDataWithRequestInfo(data, url, "POST", requestBody);
    httpClient.post(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a POST request with url parameters, update the debugger http client 
   * and perform the specified success or error functions 
   */
  this.postUrlEncoded = function httpPostUrlEncoded(url, requestParam, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    let urlEncoded = encodeURI(url + "?" + requestParam);
    kameHouseDebugger.displayRequestData(urlEncoded, "POST", null);
    let requestHeaders = httpClient.getUrlEncodedHeaders();
    let dataWithRequestInfo = self.createDataWithRequestInfo(data, urlEncoded, "POST", null);
    httpClient.post(urlEncoded, requestHeaders, null,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a DELETE request, update the debugger http client 
   * and perform the specified success or error functions 
   */
  this.delete = function httpDelete(url, requestBody, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    kameHouseDebugger.displayRequestData(url, "DELETE", requestBody);
    let requestHeaders = httpClient.getApplicationJsonHeaders();
    let dataWithRequestInfo = self.createDataWithRequestInfo(data, url, "DELETE", null);
    httpClient.delete(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /**
   * Creates a data object that contains the data already received and the request info to eventually log in the requests table.
   */
  this.createDataWithRequestInfo = (data, url, method, requestBody) => {
    let dataWithRequestInfo = {};
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