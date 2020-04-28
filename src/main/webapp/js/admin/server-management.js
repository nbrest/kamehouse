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
  logger.traceFunctionCall();
  displayRequestData(url, "GET", null);
  httpClient.get(url, null, 
    function success(responseBody, responseCode, responseDescription) {
      displayResponseData(responseBody, responseCode);
    },
    function error(responseBody, responseCode, responseDescription) {
      displayResponseData(responseBody, responseCode);
    });
}

function doPost(url, requestBody) {
  logger.traceFunctionCall();
  displayRequestData(url, "POST", requestBody);
  var requestHeaders = httpClient.getCsrfRequestHeadersObject();
  httpClient.post(url, requestHeaders, requestBody, 
    function success(responseBody, responseCode, responseDescription) {
      displayResponseData(responseBody, responseCode);
    },
    function error(responseBody, responseCode, responseDescription) {
      displayResponseData(responseBody, responseCode);
    });
}

/** Execute a POST request to the specified url with the specified request url parameters. */
function doPostUrlEncoded(url, requestParam) {
  logger.traceFunctionCall();
  var urlEncoded = encodeURI(url + "?" + requestParam);
  displayRequestData(urlEncoded, "POST", null);
  var requestHeaders = httpClient.getUrlEncodedHeaders();
  httpClient.post(urlEncoded, requestHeaders, null,
    function success(responseBody, responseCode, responseDescription) {
      displayResponseData(responseBody, responseCode);
    },
    function error(responseBody, responseCode, responseDescription) {
      displayResponseData(responseBody, responseCode);
    });
}

function doDelete(url, requestBody) {
  logger.traceFunctionCall();
  displayRequestData(url, "DELETE", requestBody);
  var requestHeaders = httpClient.getCsrfRequestHeadersObject();
  httpClient.delete(url, requestHeaders, requestBody,
    function success(responseBody, responseCode, responseDescription) {
      displayResponseData(responseBody, responseCode);
    },
    function error(responseBody, responseCode, responseDescription) {
      displayResponseData(responseBody, responseCode);
    });
}

/** Power management functions ---------------------------------------------------------- */

function execAdminShutdown(url, time) {
  logger.traceFunctionCall();
  logger.trace("Shutdown delay: " + time);
  var requestParam = "delay=" + time;
  doPostUrlEncoded(url, requestParam);
}

/**
 * Call main.
 */
$(document).ready(main);