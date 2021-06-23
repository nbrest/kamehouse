/** 
 * Handles the debugger including the api call table functionality.
 * 
 * @author nbrest
 */
var debuggerHttpClient;
var kameHouseDebugger;

function main() {
  importKameHouseDebuggerCss();
  kameHouseDebugger = new KameHouseDebugger();
  kameHouseDebugger.init();
  moduleUtils.waitForModules(["logger", "httpClient", "kameHouseDebugger"], () => {
    logger.info("Started initializing api call table");
    debuggerHttpClient = new DebuggerHttpClient();
    debuggerHttpClient.init();
  });
}

/** Import debugger-http-client css*/
function importKameHouseDebuggerCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/kamehouse-debugger.css">');
}

/** 
 * Handles the debugger functionality.
 * 
 * @author nbrest
 */
function KameHouseDebugger() {
  let self = this;
  
  this.init = () => {
    self.renderDebugMode();
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
    });
  }

  /**
   * Render the specified html snippet into the custom div of the debugger.
   */
  this.renderCustomDebugger = (htmlSnippet) => {
    $("#debug-mode-custom-wrapper").load(htmlSnippet);
  }
}

/**
 * Functionality that renders the debugger-http-client-table div 
 * and executes api requests.
 * 
 * Dependencies: timeUtils, logger, httpClient
 * 
 * @author nbrest
 */
function DebuggerHttpClient() {
  let self = this;
  this.requests = [];
  this.debuggerHttpClientDivTemplate;  
  
  this.init = async () => {
    await self.loadDebuggerHttpClientTemplate();
    self.displayRequestData(null, null, null);
    moduleUtils.setModuleLoaded("debuggerHttpClient");
  }

  /**
   * Loads the api call table html snippet into a variable to be reused as a template on render.
   */
  this.loadDebuggerHttpClientTemplate = async () => {
    const response = await fetch('/kame-house/html-snippets/kamehouse-debugger-http-client-table.html');
    self.debuggerHttpClientDivTemplate = await response.text();
    logger.debug("Loaded debuggerHttpClientDivTemplate");
  }

  /** 
   * Execute a GET request, update the api call table 
   * and perform the specified success or error functions 
   * data is any extra data I want to pass to the success and error functions
   */
  this.get = function httpGet(url, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    self.displayRequestData(url, "GET", null);
    let dataWithRequestInfo = self.createDataWithRequestInfo(data, url, "GET", null);
    httpClient.get(url, null,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a GET request with url encoded parameters, update the api call table 
   * and perform the specified success or error functions 
   */
  this.getUrlEncoded = function httpGetUrlEncoded(url, requestParam, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    let urlEncoded = encodeURI(url + "?" + requestParam);
    self.displayRequestData(urlEncoded, "GET", null);
    let dataWithRequestInfo = self.createDataWithRequestInfo(data, urlEncoded, "GET", null);
    let requestHeaders = httpClient.getUrlEncodedHeaders();
    httpClient.get(urlEncoded, requestHeaders,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a PUT request, update the api call table 
   * and perform the specified success or error functions 
   */
  this.put = function httpPut(url, requestBody, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    self.displayRequestData(url, "PUT", requestBody);
    let requestHeaders = httpClient.getApplicationJsonHeaders();
    let dataWithRequestInfo = self.createDataWithRequestInfo(data, url, "PUT", requestBody);
    httpClient.put(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
    );
  }

  /** 
   * Execute a POST request, update the api call table 
   * and perform the specified success or error functions 
   */
  this.post = function httpPost(url, requestBody, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    self.displayRequestData(url, "POST", requestBody);
    let requestHeaders = httpClient.getApplicationJsonHeaders();
    let dataWithRequestInfo = self.createDataWithRequestInfo(data, url, "POST", requestBody);
    httpClient.post(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a POST request with url parameters, update the api call table 
   * and perform the specified success or error functions 
   */
  this.postUrlEncoded = function httpPostUrlEncoded(url, requestParam, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    let urlEncoded = encodeURI(url + "?" + requestParam);
    self.displayRequestData(urlEncoded, "POST", null);
    let requestHeaders = httpClient.getUrlEncodedHeaders();
    let dataWithRequestInfo = self.createDataWithRequestInfo(data, urlEncoded, "POST", null);
    httpClient.post(urlEncoded, requestHeaders, null,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback, dataWithRequestInfo),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback, dataWithRequestInfo)
      );
  }

  /** 
   * Execute a DELETE request, update the api call table 
   * and perform the specified success or error functions 
   */
  this.delete = function httpDelete(url, requestBody, successCallback, errorCallback, data) {
    logger.trace(arguments.callee.name);
    self.displayRequestData(url, "DELETE", requestBody);
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
    self.displayResponseData(responseBody, responseCode);
    self.displayPreviousRequestsTable(dataWithRequestInfo, responseBody, responseCode);
    if (isFunction(responseCallback)) {
      responseCallback(responseBody, responseCode, responseDescription, dataWithRequestInfo.data);
    }
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
    $('#debugger-http-client-previous-requests-pre').text(JSON.stringify(self.requests, null, 2));
    self.setCollapsibleContent();
  }

  /**
   * Display api call table response data.
   */
  this.displayResponseData = function displayResponseData(responseBody, responseCode) {
    logger.trace(arguments.callee.name);
    let responseTimestamp = timeUtils.getTimestamp();
    $("#debugger-http-client-res-code-val").text(responseCode);
    $("#debugger-http-client-res-timestamp-val").text(responseTimestamp);
    $("#debugger-http-client-res-body-val").text(JSON.stringify(responseBody, null, 2));
    self.setCollapsibleContent();
  }

  /**
   * Display api call table request data.
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
    self.setCollapsibleContent();
  }

  /**
   * Empty api call table div.
   */
  this.emptyDebuggerHttpClientDiv = () => {
    let $debuggerHttpClientDiv = $("#debugger-http-client");
    $debuggerHttpClientDiv.empty();
  }

  /**
   * Set collapsible content listeners.
   */
  this.setCollapsibleContent = () => {
    let collapsibleElements = document.getElementsByClassName("collapsible-kh");
    let i;
    for (i = 0; i < collapsibleElements.length; i++) {
      collapsibleElements[i].removeEventListener("click", self.collapsibleContentListener);
      collapsibleElements[i].addEventListener("click", self.collapsibleContentListener);
    }
  }

  /**
   * Function to toggle height of the collapsible elements from null to it's scrollHeight.
   */
  this.collapsibleContentListener = function collapsibleContentListener() {
    // Can't use self here, need to use this
    this.classList.toggle("collapsible-kh-active");
    let content = this.nextElementSibling;
    if (content.style.maxHeight) {
      content.style.maxHeight = null;
    } else {
      content.style.maxHeight = content.scrollHeight + "px";
    }
  }
}

/**
 * Call main.
 */
$(document).ready(main);