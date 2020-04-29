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
  modules.apiCallTable = true;
  apiCallTable = new ApiCallTable();
  apiCallTable.displayRequestData(null, null, null);
}

/** Import api-call-table css*/
function importApiCallTableCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/api-call-table.css">');
}

function ApiCallTable() {
  var self = this;

  /** 
   * Execute a GET request, update the api call table 
   * and perform the specified success or error functions 
   */
  this.get = function httpGet(url, successCallback, errorCallback) {
    logger.traceFunctionCall();
    self.displayRequestData(url, "GET", null);
    httpClient.get(url, null,
      function success(responseBody, responseCode, responseDescription) {
        self.displayResponseData(responseBody, responseCode);
        if (isFunction(successCallback)) { 
          successCallback(responseBody, responseCode, responseDescription);
        }
      },
      function error(responseBody, responseCode, responseDescription) {
        self.displayResponseData(responseBody, responseCode);
        if (isFunction(errorCallback)) {
          errorCallback(responseBody, responseCode, responseDescription);
        }
      });
  }

  /** 
   * Execute a POST request, update the api call table 
   * and perform the specified success or error functions 
   */
  this.post = function httpPost(url, requestBody, successCallback, errorCallback) {
    logger.traceFunctionCall();
    apiCallTable.displayRequestData(url, "POST", requestBody);
    var requestHeaders = httpClient.getCsrfRequestHeadersObject();
    httpClient.post(url, requestHeaders, requestBody,
      function success(responseBody, responseCode, responseDescription) {
        apiCallTable.displayResponseData(responseBody, responseCode);
        if (isFunction(successCallback)) {
          successCallback(responseBody, responseCode, responseDescription);
        }
      },
      function error(responseBody, responseCode, responseDescription) {
        apiCallTable.displayResponseData(responseBody, responseCode);
        if (isFunction(errorCallback)) {
          errorCallback(responseBody, responseCode, responseDescription);
        }
      });
  }

  /** 
   * Execute a POST request with url parameters, update the api call table 
   * and perform the specified success or error functions 
   */
  this.postUrlEncoded = function httpPostUrlEncoded(url, requestParam, successCallback, errorCallback) {
    logger.traceFunctionCall();
    var urlEncoded = encodeURI(url + "?" + requestParam);
    apiCallTable.displayRequestData(urlEncoded, "POST", null);
    var requestHeaders = httpClient.getUrlEncodedHeaders();
    httpClient.post(urlEncoded, requestHeaders, null,
      function success(responseBody, responseCode, responseDescription) {
        apiCallTable.displayResponseData(responseBody, responseCode);
        if (isFunction(successCallback)) {
          successCallback(responseBody, responseCode, responseDescription);
        }
      },
      function error(responseBody, responseCode, responseDescription) {
        apiCallTable.displayResponseData(responseBody, responseCode);
        if (isFunction(errorCallback)) {
          errorCallback(responseBody, responseCode, responseDescription);
        }
      });
  }

  /** 
   * Execute a DELETE request, update the api call table 
   * and perform the specified success or error functions 
   */
  this.delete = function httpDelete(url, requestBody, successCallback, errorCallback) {
    logger.traceFunctionCall();
    apiCallTable.displayRequestData(url, "DELETE", requestBody);
    var requestHeaders = httpClient.getCsrfRequestHeadersObject();
    httpClient.delete(url, requestHeaders, requestBody,
      function success(responseBody, responseCode, responseDescription) {
        apiCallTable.displayResponseData(responseBody, responseCode);
        if (isFunction(successCallback)) {
          successCallback(responseBody, responseCode, responseDescription);
        }
      },
      function error(responseBody, responseCode, responseDescription) {
        apiCallTable.displayResponseData(responseBody, responseCode);
        if (isFunction(errorCallback)) {
          errorCallback(responseBody, responseCode, responseDescription);
        }
      });
  }

  /**
   * Display api call table response data.
   */
  this.displayResponseData = function displayResponseData(responseBody, responseCode) {
    logger.traceFunctionCall();
    var responseTimestamp = timeUtils.getTimestamp();
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
    var requestTimestamp = timeUtils.getTimestamp();
    var $apiCallTableDiv = $("#api-call-table");
    var $apiCallTable = $('<table id="aco-table" class="table table-bordered table-responsive table-bordered-kh">');
    // Request Data header row.
    var $requestDataHeaderRow = $("<tr>");
    $requestDataHeaderRow.append($('<th class="txt-c-d-kh" colspan="2">').text("Request Data"));
    $apiCallTable.append($requestDataHeaderRow);
    // Request Timestamp row.
    var $requestTimestampRow = $("<tr>");
    $requestTimestampRow.append($('<td>').text("Timestamp"));
    $requestTimestampRow.append($('<td id="aco-req-timestamp-val">').text(requestTimestamp));
    $apiCallTable.append($requestTimestampRow);
    // Url row.
    var $urlRow = $("<tr>");
    $urlRow.append($('<td>').text("Url"));
    $urlRow.append($('<td id="aco-req-url-val">').text(url));
    $apiCallTable.append($urlRow);
    // Request Type row.
    var $requestTypeRow = $("<tr>");
    $requestTypeRow.append($('<td>').text("Type"));
    $requestTypeRow.append($('<td id="aco-req-type-val">').text(requestType));
    $apiCallTable.append($requestTypeRow);
    // Request Body row.
    var $requestBodyRow = $("<tr>");
    $requestBodyRow.append($('<td>').text("Body"));
    $requestBodyRow.append($('<td id="aco-req-body-val">').text(JSON.stringify(requestBody, null, 2)));
    $apiCallTable.append($requestBodyRow);
    // Response Data header row.
    var $responseDataHeaderRow = $("<tr>");
    $responseDataHeaderRow.append($('<th class="txt-c-d-kh" colspan="2">').text("Response Data"));
    $apiCallTable.append($responseDataHeaderRow);
    // Response Code row.
    var $responseCodeRow = $("<tr>");
    $responseCodeRow.append($('<td>').text("Response Code"));
    $responseCodeRow.append($('<td id="aco-res-code-val">').text(null));
    $apiCallTable.append($responseCodeRow);
    // Response Time row.
    var $responseTimestampRow = $("<tr>");
    $responseTimestampRow.append($('<td>').text("Timestamp"));
    $responseTimestampRow.append($('<td id="aco-res-timestamp-val">').text(null));
    $apiCallTable.append($responseTimestampRow);
    $apiCallTableDiv.append($apiCallTable);
    // Output payload.
    var $outputPayloadButton = $('<button class="collapsible-kh">');
    $outputPayloadButton.text("Response Body");
    var $outputPayloadContent = $('<div class="collapsible-kh-content">');
    $outputPayloadContent.append($('<pre id="aco-res-body-val" class="collapsible-kh-content-pre">').text(JSON.stringify(null, null, 2)));
    $apiCallTableDiv.append($outputPayloadButton);
    $apiCallTableDiv.append($outputPayloadContent);
    self.setCollapsibleContent();
  }

  /**
   * Empty api call table div.
   */
  this.emptyApiCallTableDiv = function emptyApiCallTableDiv() {
    logger.traceFunctionCall();
    var $apiCallTableDiv = $("#api-call-table");
    $apiCallTableDiv.empty();
  }

  /**
   * Set collapsible content listeners.
   */
  this.setCollapsibleContent = function setCollapsibleContent() {
    var collapsibleElements = document.getElementsByClassName("collapsible-kh");
    var i;
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
    var content = this.nextElementSibling;
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