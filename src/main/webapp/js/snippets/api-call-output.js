/**
 * Functionality that renders the api-call-output div.
 */
function main() {
  importApiCallOutputCss();
  initKameHouse(initApiCallOutput);
}

/** Init function. */
function initApiCallOutput() {
  displayRequestData(null, null, null);
}

/** Import api-call-output css*/
function importApiCallOutputCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/api-call-output.css">');
}

/**
 * Display api call response data.
 */
function displayResponseData(responseBody, responseCode) {
  logger.traceFunctionCall();
  var responseTimestamp = timeUtils.getTimestamp();
  $("#aco-res-code-val").text(responseCode);
  $("#aco-res-timestamp-val").text(responseTimestamp);
  $("#aco-res-body-val").text(JSON.stringify(responseBody, null, 2));
  setCollapsibleContent();
}

/**
 * Display api call request data.
 */
function displayRequestData(url, requestType, requestBody) {
  logger.traceFunctionCall();
  emptyApiCallOutputDiv();
  var requestTimestamp = timeUtils.getTimestamp();
  var $apiCallOutput = $("#api-call-output");
  var $apiCallOutputTable = $('<table id="aco-table" class="table table-bordered table-responsive table-bordered-kh">');
  // Request Data header row.
  var $requestDataHeaderRow = $("<tr>");
  $requestDataHeaderRow.append($('<th class="txt-c-d-kh" colspan="2">').text("Request Data"));
  $apiCallOutputTable.append($requestDataHeaderRow);
  // Request Timestamp row.
  var $requestTimestampRow = $("<tr>");
  $requestTimestampRow.append($('<td>').text("Timestamp"));
  $requestTimestampRow.append($('<td id="aco-req-timestamp-val">').text(requestTimestamp));
  $apiCallOutputTable.append($requestTimestampRow);
  // Url row.
  var $urlRow = $("<tr>");
  $urlRow.append($('<td>').text("Url"));
  $urlRow.append($('<td id="aco-req-url-val">').text(url));
  $apiCallOutputTable.append($urlRow);
  // Request Type row.
  var $requestTypeRow = $("<tr>");
  $requestTypeRow.append($('<td>').text("Type"));
  $requestTypeRow.append($('<td id="aco-req-type-val">').text(requestType));
  $apiCallOutputTable.append($requestTypeRow);
  // Request Body row.
  var $requestBodyRow = $("<tr>");
  $requestBodyRow.append($('<td>').text("Body"));
  $requestBodyRow.append($('<td id="aco-req-body-val">').text(JSON.stringify(requestBody, null, 2)));
  $apiCallOutputTable.append($requestBodyRow);
  // Response Data header row.
  var $responseDataHeaderRow = $("<tr>");
  $responseDataHeaderRow.append($('<th class="txt-c-d-kh" colspan="2">').text("Response Data"));
  $apiCallOutputTable.append($responseDataHeaderRow);
  // Response Code row.
  var $responseCodeRow = $("<tr>");
  $responseCodeRow.append($('<td>').text("Response Code"));
  $responseCodeRow.append($('<td id="aco-res-code-val">').text(null));
  $apiCallOutputTable.append($responseCodeRow);
  // Response Time row.
  var $responseTimestampRow = $("<tr>");
  $responseTimestampRow.append($('<td>').text("Timestamp"));
  $responseTimestampRow.append($('<td id="aco-res-timestamp-val">').text(null));
  $apiCallOutputTable.append($responseTimestampRow);
  $apiCallOutput.append($apiCallOutputTable);
  // Output payload.
  var $outputPayloadButton = $('<button class="collapsible-kh">');
  $outputPayloadButton.text("Response Body");
  var $outputPayloadContent = $('<div class="collapsible-kh-content">');
  $outputPayloadContent.append($('<pre id="aco-res-body-val" class="collapsible-kh-content-pre">').text(JSON.stringify(null, null, 2)));
  $apiCallOutput.append($outputPayloadButton);
  $apiCallOutput.append($outputPayloadContent);
  setCollapsibleContent();
}

/**
 * Empty api call output div.
 */
function emptyApiCallOutputDiv() {
  logger.traceFunctionCall();
  var $apiCallOutput = $("#api-call-output");
  $apiCallOutput.empty();
}

/**
 * Set collapsible content listeners.
 */
function setCollapsibleContent() {
  var collapsibleElements = document.getElementsByClassName("collapsible-kh");
  var i;
  for (i = 0; i < collapsibleElements.length; i++) {
    collapsibleElements[i].removeEventListener("click", collapsibleContentListener);
    collapsibleElements[i].addEventListener("click", collapsibleContentListener);
  }
}

/**
 * Function to toggle height of the collapsible elements from null to it's scrollHeight.
 */
function collapsibleContentListener() {
  this.classList.toggle("collapsible-kh-active");
  var content = this.nextElementSibling;
  if (content.style.maxHeight) {
    content.style.maxHeight = null;
  } else {
    content.style.maxHeight = content.scrollHeight + "px";
  }
}

/**
 * Call main.
 */
$(document).ready(main);