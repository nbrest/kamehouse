/**
 * Functionality that renders the api-call-table div 
 * and executes api requests.
 * 
 * Dependencies: logger, timeUtils, httpClient
 * 
 * @author nbrest
 */
var apiCallTable;

 function main() {
  importApiCallTableCss();
  var loadingModules = ["timeUtils","logger","httpClient"];
  waitForModules(loadingModules, initApiCallTable);
}

/** Init function. */
function initApiCallTable() {
  logger.info("Started initializing api call table");
  apiCallTable = new ApiCallTable();
  modules.apiCallTable = true;
  apiCallTable.displayRequestData(null, null, null);
}

/** Import api-call-table css*/
function importApiCallTableCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/api-call-table.css">');
}

function ApiCallTable() {
  let self = this;

  /** 
   * Execute a GET request, update the api call table 
   * and perform the specified success or error functions 
   */
  this.get = function httpGet(url, successCallback, errorCallback) {
    logger.traceFunctionCall();
    self.displayRequestData(url, "GET", null);
    httpClient.get(url, null,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback)
      );
  }

  /** 
   * Execute a GET request with url encoded parameters, update the api call table 
   * and perform the specified success or error functions 
   */
  this.getUrlEncoded = function httpGetUrlEncoded(url, requestParam, successCallback, errorCallback) {
    logger.traceFunctionCall(); 
    let urlEncoded = encodeURI(url + "?" + requestParam);
    self.displayRequestData(urlEncoded, "GET", null);
    let requestHeaders = httpClient.getUrlEncodedHeaders();
    httpClient.get(urlEncoded, requestHeaders,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback)
      );
  }

  /** 
   * Execute a POST request, update the api call table 
   * and perform the specified success or error functions 
   */
  this.post = function httpPost(url, requestBody, successCallback, errorCallback) {
    logger.traceFunctionCall();
    self.displayRequestData(url, "POST", requestBody);
    let requestHeaders = httpClient.getApplicationJsonHeaders();
    httpClient.post(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback)
      );
  }

  /** 
   * Execute a POST request with url parameters, update the api call table 
   * and perform the specified success or error functions 
   */
  this.postUrlEncoded = function httpPostUrlEncoded(url, requestParam, successCallback, errorCallback) {
    logger.traceFunctionCall();
    let urlEncoded = encodeURI(url + "?" + requestParam);
    self.displayRequestData(urlEncoded, "POST", null);
    let requestHeaders = httpClient.getUrlEncodedHeaders();
    httpClient.post(urlEncoded, requestHeaders, null,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback)
      );
  }

  /** 
   * Execute a DELETE request, update the api call table 
   * and perform the specified success or error functions 
   */
  this.delete = function httpDelete(url, requestBody, successCallback, errorCallback) {
    logger.traceFunctionCall();
    self.displayRequestData(url, "DELETE", requestBody);
    let requestHeaders = httpClient.getApplicationJsonHeaders();
    httpClient.delete(url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, successCallback),
      (responseBody, responseCode, responseDescription) => processResponse(responseBody, responseCode, responseDescription, errorCallback)
      );
  }

  /** Process the response of the api call */
  function processResponse(responseBody, responseCode, responseDescription, responseCallback) {
    self.displayResponseData(responseBody, responseCode);
    if (isFunction(responseCallback)) {
      responseCallback(responseBody, responseCode, responseDescription);
    }
  }

  /**
   * Display api call table response data.
   */
  this.displayResponseData = function displayResponseData(responseBody, responseCode) {
    logger.traceFunctionCall();
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
    logger.traceFunctionCall();
    self.emptyApiCallTableDiv();
    let requestTimestamp = timeUtils.getTimestamp();
    let $apiCallTableDiv = $("#api-call-table");
    let $apiCallTable = $('<table id="aco-table" class="table table-bordered-kh table-responsive-kh table-responsive">');
    // Request Data header row.
    let $requestDataHeaderRow = $("<tr>");
    $requestDataHeaderRow.append($('<th class="txt-c-d-kh" colspan="2">').text("Request Data"));
    $apiCallTable.append($requestDataHeaderRow);
    // Request Timestamp row.
    let $requestTimestampRow = $("<tr>");
    $requestTimestampRow.append($('<td>').text("Timestamp"));
    $requestTimestampRow.append($('<td id="aco-req-timestamp-val">').text(requestTimestamp));
    $apiCallTable.append($requestTimestampRow);
    // Url row.
    let $urlRow = $("<tr>");
    $urlRow.append($('<td>').text("Url"));
    $urlRow.append($('<td id="aco-req-url-val">').text(url));
    $apiCallTable.append($urlRow);
    // Request Type row.
    let $requestTypeRow = $("<tr>");
    $requestTypeRow.append($('<td>').text("Type"));
    $requestTypeRow.append($('<td id="aco-req-type-val">').text(requestType));
    $apiCallTable.append($requestTypeRow);
    // Request Body row.
    let $requestBodyRow = $("<tr>");
    $requestBodyRow.append($('<td>').text("Body"));
    $requestBodyRow.append($('<td id="aco-req-body-val">').text(JSON.stringify(requestBody, null, 2)));
    $apiCallTable.append($requestBodyRow);
    // Response Data header row.
    let $responseDataHeaderRow = $("<tr>");
    $responseDataHeaderRow.append($('<th class="txt-c-d-kh" colspan="2">').text("Response Data"));
    $apiCallTable.append($responseDataHeaderRow);
    // Response Code row.
    let $responseCodeRow = $("<tr>");
    $responseCodeRow.append($('<td>').text("Response Code"));
    $responseCodeRow.append($('<td id="aco-res-code-val">').text(null));
    $apiCallTable.append($responseCodeRow);
    // Response Time row.
    let $responseTimestampRow = $("<tr>");
    $responseTimestampRow.append($('<td>').text("Timestamp"));
    $responseTimestampRow.append($('<td id="aco-res-timestamp-val">').text(null));
    $apiCallTable.append($responseTimestampRow);
    $apiCallTableDiv.append($apiCallTable);
    // Output payload.
    let $outputPayloadButton = $('<button class="collapsible-kh">');
    $outputPayloadButton.text("Response Body");
    let $outputPayloadContent = $('<div class="collapsible-kh-content">');
    $outputPayloadContent.append($('<pre id="aco-res-body-val" class="collapsible-kh-content-pre">').text(JSON.stringify(null, null, 2)));
    $apiCallTableDiv.append($outputPayloadButton);
    $apiCallTableDiv.append($outputPayloadContent);
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