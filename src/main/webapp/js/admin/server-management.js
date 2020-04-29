/**
 * Admin Server Management functions.
 * 
 * Dependencies: logger, httpClient, apiCallTable.
 * 
 * @author nbrest
 */
var serverManager;

var main = function() {  
  importServerManagementCss();
  var loadingModules = ["logger", "httpClient", "apiCallTable"];
  waitForModules(loadingModules, function () {
    serverManager = new ServerManager();
  });
};

function importServerManagementCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/server-management.css">');
}

function ServerManager() {
  var self = this;

  /** General REST function calls ---------------------------------------------------------- */
  this.doGet = function doGet(url) {
    logger.traceFunctionCall();
    apiCallTable.displayRequestData(url, "GET", null);
    httpClient.get(url, null,
      function success(responseBody, responseCode, responseDescription) {
        apiCallTable.displayResponseData(responseBody, responseCode);
      },
      function error(responseBody, responseCode, responseDescription) {
        apiCallTable.displayResponseData(responseBody, responseCode);
      });
  }

  this.doPost = function doPost(url, requestBody) {
    logger.traceFunctionCall();
    apiCallTable.displayRequestData(url, "POST", requestBody);
    var requestHeaders = httpClient.getCsrfRequestHeadersObject();
    httpClient.post(url, requestHeaders, requestBody,
      function success(responseBody, responseCode, responseDescription) {
        apiCallTable.displayResponseData(responseBody, responseCode);
      },
      function error(responseBody, responseCode, responseDescription) {
        apiCallTable.displayResponseData(responseBody, responseCode);
      });
  }
 
  this.doPostUrlEncoded = function doPostUrlEncoded(url, requestParam) {
    logger.traceFunctionCall();
    var urlEncoded = encodeURI(url + "?" + requestParam);
    apiCallTable.displayRequestData(urlEncoded, "POST", null);
    var requestHeaders = httpClient.getUrlEncodedHeaders();
    httpClient.post(urlEncoded, requestHeaders, null,
      function success(responseBody, responseCode, responseDescription) {
        apiCallTable.displayResponseData(responseBody, responseCode);
      },
      function error(responseBody, responseCode, responseDescription) {
        apiCallTable.displayResponseData(responseBody, responseCode);
      });
  }

  this.doDelete = function doDelete(url, requestBody) {
    logger.traceFunctionCall();
    apiCallTable.displayRequestData(url, "DELETE", requestBody);
    var requestHeaders = httpClient.getCsrfRequestHeadersObject();
    httpClient.delete(url, requestHeaders, requestBody,
      function success(responseBody, responseCode, responseDescription) {
        apiCallTable.displayResponseData(responseBody, responseCode);
      },
      function error(responseBody, responseCode, responseDescription) {
        apiCallTable.displayResponseData(responseBody, responseCode);
      });
  }

  /** Power management functions ---------------------------------------------------------- */

  this.execAdminShutdown = function execAdminShutdown(url, time) {
    logger.traceFunctionCall();
    logger.trace("Shutdown delay: " + time);
    var requestParam = "delay=" + time;
    self.doPostUrlEncoded(url, requestParam);
  }
}

/**
 * Call main.
 */
$(document).ready(main);