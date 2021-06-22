/**
 * Functionality that renders the api-call-table div 
 * and executes api requests.
 * 
 * Dependencies: timeUtils, logger, httpClient
 * 
 * @author nbrest
 */
var apiCallTable;

function main() {
  importApiCallTableCss();
  moduleUtils.waitForModules(["logger", "httpClient"], () => {
    logger.info("Started initializing api call table");
    apiCallTable = new ApiCallTable();
    moduleUtils.setModuleLoaded("apiCallTable");
    apiCallTable.displayRequestData(null, null, null);
  });
}

/** Import api-call-table css*/
function importApiCallTableCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/api-call-table.css">');
}

function ApiCallTable() {
  let self = this;
  this.requests = [];
  this.apiCallTableDivTemplate;  
  
  /**
   * Loads the api call table html snippet into a variable to be reused as a template on render.
   */
  this.loadApiCallTableTemplate = async () => {
    const response = await fetch('/kame-house/html-snippets/api-call-table.html');
    self.apiCallTableDivTemplate = await response.text();
  }
  self.loadApiCallTableTemplate();

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
    $('#aco-previous-requests-pre').text(JSON.stringify(self.requests, null, 2));
    self.setCollapsibleContent();
  }

  /**
   * Display api call table response data.
   */
  this.displayResponseData = function displayResponseData(responseBody, responseCode) {
    logger.trace(arguments.callee.name);
    let responseTimestamp = timeUtils.getTimestamp();
    $("#aco-res-code-val").text(responseCode);
    $("#aco-res-timestamp-val").text(responseTimestamp);
    $("#aco-res-body-val").text(JSON.stringify(responseBody, null, 2));
    self.setCollapsibleContent();
  }

  /**
   * Display api call table request data.
   */
  this.displayRequestData = function displayRequestData(url, requestType, requestBody) {
    logger.trace(arguments.callee.name);
    self.emptyApiCallTableDiv();
    document.getElementById("api-call-table").innerHTML = self.apiCallTableDivTemplate;
    let requestTimestamp = timeUtils.getTimestamp();
    $('#aco-req-timestamp-val').text(requestTimestamp);
    $('#aco-req-url-val').text(url);
    $('#aco-req-type-val').text(requestType);
    $('#aco-req-body-val').text(JSON.stringify(requestBody, null, 2));
    $('#aco-res-code-val').text(null);
    $('#aco-res-timestamp-val').text(null);
    $('#aco-res-body-val').text(JSON.stringify(null, null, 2));
    self.setCollapsibleContent();
  }

  /**
   * Empty api call table div.
   */
  this.emptyApiCallTableDiv = () => {
    let $apiCallTableDiv = $("#api-call-table");
    $apiCallTableDiv.empty();
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