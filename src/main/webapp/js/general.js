/**
 * General functions for all pages.
 * 
 * @author nbrest
 */
function main() {
  importNewsletter();
}

/**
 * Site under construction message.
 */
function siteUnderCostructionAlert() {
  alert('The site is still under construction and this functionality has not been implemented yet.');
}

/**
 * Import newsletter content.
 */
function importNewsletter() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/newsletter.css">');
  $("#newsletter").load("/kame-house/html/newsletter.html");
}

/**
 * Display api call output.
 */
function displayRequestPayload(apiResponsePayload, url, requestType, requestBody) {
  emptyApiCallOutputDiv();
  //console.debug(apiResponsePayload);
  var $apiCallOutput = $("#api-call-output");
  var $apiCallOutputTable = $('<table class="table table-bordered table-responsive table-bordered-kh">');
  // Request Type row.
  var $requestTypeRow = $("<tr>");
  $requestTypeRow.append($('<td>').text("Request Type"));
  $requestTypeRow.append($('<td>').text(requestType));
  $apiCallOutputTable.append($requestTypeRow);
  // Url row.
  var $urlRow = $("<tr>");
  $urlRow.append($('<td>').text("Url"));
  $urlRow.append($('<td>').text(url));
  $apiCallOutputTable.append($urlRow);
  // Request Body row.
  var $requestBodyRow = $("<tr>");
  $requestBodyRow.append($('<td>').text("Request Body"));
  $requestBodyRow.append($('<td>').text(JSON.stringify(requestBody, null, 2)));
  $apiCallOutputTable.append($requestBodyRow);
  // Time row.
  var $timeRow = $("<tr>");
  $timeRow.append($('<td>').text("Time"));
  $timeRow.append($('<td>').text(getTimestamp()));
  $apiCallOutputTable.append($timeRow);
  $apiCallOutput.append($apiCallOutputTable);
  // Output payload.
  var $outputPayloadButton = $('<button class="collapsible-kh">');
  $outputPayloadButton.text("Output Payload");
  $outputPayloadContent = $('<div class="collapsible-kh-content">');
  $outputPayloadContent.append($('<pre class="collapsible-kh-content-pre">').text(JSON.stringify(apiResponsePayload, null, 2)));
  $apiCallOutput.append($outputPayloadButton);
  $apiCallOutput.append($outputPayloadContent);
  setCollapsibleContent();
}

/**
 * Display error executing the request.
 */
function displayErrorExecutingRequest() {
  emptyApiCallOutputDiv();
  var $apiCallOutput = $("#api-call-output");
  var $errorTable = $('<table class="table table-bordered table-responsive table-bordered-kh">');
  var $errorTableRow = $("<tr>");
  $errorTableRow.append($('<td>').text(getTimestamp() +
    " : Error executing api request. Please check server logs."));
  $errorTable.append($errorTableRow);
  $apiCallOutput.append($errorTable);
  console.error(getTimestamp() + " : Error executing api request. Please check server logs.");
}

/**
 * Empty api call output div.
 */
function emptyApiCallOutputDiv() {
  var $apiCallOutput = $("#api-call-output");
  $apiCallOutput.empty();
}

/**
 * Set collapsible content.
 */
function setCollapsibleContent() {
  var coll = document.getElementsByClassName("collapsible-kh");
  var i;

  for (i = 0; i < coll.length; i++) {
    coll[i].addEventListener("click", function() {
      this.classList.toggle("collapsible-kh-active");
      var content = this.nextElementSibling;
      if (content.style.maxHeight){
        content.style.maxHeight = null;
      } else {
        content.style.maxHeight = content.scrollHeight + "px";
      } 
    });
  }  
}

/**
 * Get timestamp.
 */
function getTimestamp() {
  return new Date().toISOString().replace("T", " ").slice(0, 19);
}

/**
 * Get CSRF token.
 */
function getCsrfToken() {
  var token = $("meta[name='_csrf']").attr("content");
  return token;
}

/**
 * Get CSRF header.
 */
function getCsrfHeader() {
  var header = $("meta[name='_csrf_header']").attr("content");
  return header;
}

/**
 * Get CSRF standard requestHeaders object.
 */
function getCsrfRequestHeadersObject() {
  var csrfHeader = getCsrfHeader();
  var csrfToken = getCsrfToken();
  var requestHeaders = {};
  requestHeaders.Accept = 'application/json';
  requestHeaders['Content-Type'] = 'application/json';
  requestHeaders[csrfHeader] = csrfToken;
  //console.log(JSON.stringify(requestHeaders));
  return requestHeaders;
}

/**
 * Call main.
 */
$(document).ready(main);