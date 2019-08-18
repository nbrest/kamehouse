/**
 * Functionality that renders the api-call-output div.
 */
function main() { 
  importApiCallOutputCss();
  displayRequestPayload(null, null, null, null);
}

/** Import api-call-output css*/
function importApiCallOutputCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/api-call-output.css">');
}

/**
 * Display api call output.
 */
function displayRequestPayload(apiResponsePayload, url, requestType, requestBody) {
  emptyApiCallOutputDiv();
  // TODO: Move the static HTML creation logic to an html file and load it, as I do with newsletter.
  // Only update the HTML with jquery for the dynamic fields that I need to update.
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
 * Call main.
 */
$(document).ready(main);