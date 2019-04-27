/**
 * Admin Test APIs functions.
 * 
 * @author nbrest
 */

var main = function() { 
  displayRequestPayload(null, null, null, null);
};

function executeGet(url) {
  console.debug(getTimestamp() + " : Executing GET on " + url);
  //console.debug(url);
  $.get(url)
    .success(function(result) {
      displayRequestPayload(result, url, "GET", null);
    })
    .error(function(jqXHR, textStatus, errorThrown) {
      console.error(JSON.stringify(jqXHR));
      displayErrorExecutingRequest();
    });
  scrollToTop();
}

function executeAdminVlcPost(url, command, file) {
  var requestBody = JSON.stringify({
    command: command,
    file: file
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
      //console.debug(JSON.stringify(data));
      //console.debug(JSON.stringify(data, null, 2));
      displayRequestPayload(data, url, "POST", requestBody);
    },
    error: function(data) {
      console.error(JSON.stringify(data));
      displayErrorExecutingRequest(); 
    }
    });
  scrollToTop();
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
      console.debug(JSON.stringify(data));
      displayRequestPayload(data, url, "DELETE", requestBody);
    },
    error: function(data) {
      console.error(JSON.stringify(data));
      displayErrorExecutingRequest(); 
    }
    });
  scrollToTop();
}

/**
 * Display api call output.
 */
function displayRequestPayload(apiResponsePayload, url, requestType, requestBody) {
  emptyApiCallOutputDiv();
  //console.debug(apiResponsePayload);
  var $apiCallOutput = $("#api-call-output");
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
  // Output row.
  var $outputRow = $("<tr>");
  $outputRow.append($('<td>').text("Output"));
  $outputRow.append($('<td>').append($('<pre style="color:white;">').text(JSON.stringify(apiResponsePayload, null, 2))));
  $apiCallOutputTable.append($outputRow);
  $apiCallOutput.append($apiCallOutputTable);
}

/**
 * Display error executing the request.
 */
function displayErrorExecutingRequest() {
  emptyApiCallOutputDiv();
  var $apiCallOutput = $("#api-call-output");
  var $errorTable = $('<table class="table table-bordered table-responsive table-ehcache">');
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
 * Scroll to the top of the screen.
 */
function scrollToTop() {
  $('html, body').animate({scrollTop:0}, '10');
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