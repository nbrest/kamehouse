/**
 * Admin Server Management functions.
 * 
 * @author nbrest
 */

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
  requestHeaders = getCsrfRequestHeadersObject();
  $.ajax({
    type: "POST",
    url: url,
    data: requestBody,
    headers: requestHeaders,
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
  requestHeaders = getCsrfRequestHeadersObject();
  $.ajax({
    type: "DELETE",
    url: url,
    data: requestBody,
    headers: requestHeaders,
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
 * Call main.
 */
$(document).ready(main);