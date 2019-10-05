/**
 * Admin Server Management functions.
 * 
 * @author nbrest
 */

var main = function() {  
  importServerManagementCss();
};

function importServerManagementCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/server-management.css">');
}

/** General REST function calls ---------------------------------------------------------- */

function doGet(url) {
  log("DEBUG", "Executing GET on " + url);
  $.get(url)
    .success(function(result) {
      displayRequestPayload(result, url, "GET", null);
    })
    .error(function(jqXHR, textStatus, errorThrown) {
      log("ERROR", JSON.stringify(jqXHR));
      displayErrorExecutingRequest();
    });
  setCollapsibleContent();
}

function doPost(url, requestBody) {
  log("DEBUG", "Executing POST on " + url);
  var requestHeaders = getCsrfRequestHeadersObject();
  $.ajax({
    type: "POST",
    url: url,
    data: requestBody,
    headers: requestHeaders,
    success: function(data) {
      displayRequestPayload(data, url, "POST", requestBody);
    },
    error: function(data) {
      log("ERROR", JSON.stringify(data));
      displayErrorExecutingRequest(); 
    }
    });
  setCollapsibleContent();
}

/** Execute a POST request to the specified url with the specified request url parameters. */
function doPostUrlEncoded(url, requestParam) {
  log("DEBUG", "Executing POST on " + url + " with requestParam " + JSON.stringify(requestParam));
  var requestHeaders = getUrlEncodedHeaders();
  $.ajax({
    type: "POST",
    url: url,
    data: requestParam,
    headers: requestHeaders,
    success: function (data) {
      displayRequestPayload(data, url, "POST", requestParam);
    },
    error: function (data) {
      log("ERROR", JSON.stringify(data));
      displayErrorExecutingRequest();
    }
  });
  setCollapsibleContent();
}

function doDelete(url, requestBody) {
  log("DEBUG", "Executing DELETE on " + url);
  var requestHeaders = getCsrfRequestHeadersObject();
  $.ajax({
    type: "DELETE",
    url: url,
    data: requestBody,
    headers: requestHeaders,
    success: function(data) {
      displayRequestPayload(data, url, "DELETE", requestBody);
    },
    error: function(data) {
      log("ERROR", JSON.stringify(data));
      displayErrorExecutingRequest(); 
    }
    });
}

/** Power management functions ---------------------------------------------------------- */

function execAdminShutdown(url, time) {
  log("TRACE", "Shutdown delay: " + time);
  var requestParam = "delay=" + time;
  doPostUrlEncoded(url, requestParam);
}

/**
 * Call main.
 */
$(document).ready(main);