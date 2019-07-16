/**
 * Admin Server Management functions.
 * 
 * @author nbrest
 */

function setCollapsibleContent() {
  var coll = document.getElementsByClassName("collapsible");
  var i;

  for (i = 0; i < coll.length; i++) {
    coll[i].addEventListener("click", function() {
      this.classList.toggle("active-collapsible");
      var content = this.nextElementSibling;
      if (content.style.maxHeight){
        content.style.maxHeight = null;
      } else {
        content.style.maxHeight = content.scrollHeight + "px";
      } 
    });
  }  
}

var main = function() { 
  displayRequestPayload(null, null, null, null);
};

function executeGet(url) {
  console.debug(getTimestamp() + " : Executing GET on " + url);
  $.get(url)
    .success(function(result) {
      displayRequestPayload(result, url, "GET", null);
    })
    .error(function(jqXHR, textStatus, errorThrown) {
      console.error(JSON.stringify(jqXHR));
      displayErrorExecutingRequest();
    });
  setCollapsibleContent();
}

function executeAdminShutdownPost(url, command, time) {
  var requestBody = JSON.stringify({
    command: command,
    time: time
  });
  executePost(url, requestBody);
}

function executePost(url, requestBody) {
  console.debug(getTimestamp() + " : Executing POST on " + url);
  $.ajax({
    type: "POST",
    url: url,
    data: requestBody,
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
    success: function(data) {
      displayRequestPayload(data, url, "POST", requestBody);
    },
    error: function(data) {
      console.error(JSON.stringify(data));
      displayErrorExecutingRequest(); 
    }
    });
  setCollapsibleContent();
}

function executeDelete(url, requestBody) {
  console.debug(getTimestamp() + " : Executing DELETE on " + url);
  $.ajax({
    type: "DELETE",
    url: url,
    data: requestBody,
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
    success: function(data) {
      displayRequestPayload(data, url, "DELETE", requestBody);
    },
    error: function(data) {
      console.error(JSON.stringify(data));
      displayErrorExecutingRequest(); 
    }
    });
}

/**
 * Display command output.
 */
function displayRequestPayload(apiResponsePayload, url, requestType, requestBody) {
  emptyCommandlOutputDiv();
  var $apiCallOutput = $("#command-output");
  var $apiCallOutputTable = $('<table class="table table-bordered table-responsive">');
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
  var $outputPayloadButton = $('<button class="collapsible">');
  $outputPayloadButton.text("Output Payload");
  $outputPayloadContent = $('<div class="content">');
  $outputPayloadContent.append($('<pre style="color:white;">').text(JSON.stringify(apiResponsePayload, null, 2)));
  $apiCallOutput.append($outputPayloadButton);
  $apiCallOutput.append($outputPayloadContent);
  setCollapsibleContent();
}

/**
 * Display error executing the request.
 */
function displayErrorExecutingRequest() {
  emptyCommandlOutputDiv();
  var $apiCallOutput = $("#command-output");
  var $errorTable = $('<table class="table table-bordered table-responsive table-ehcache">');
  var $errorTableRow = $("<tr>");
  $errorTableRow.append($('<td>').text(getTimestamp() +
    " : Error executing api request. Please check server logs."));
  $errorTable.append($errorTableRow);
  $apiCallOutput.append($errorTable);
  console.error(getTimestamp() + " : Error executing api request. Please check server logs.");
}

/**
 * Empty command output div.
 */
function emptyCommandlOutputDiv() {
  var $apiCallOutput = $("#command-output");
  $apiCallOutput.empty();
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
 * Call main.
 */
$(document).ready(main);