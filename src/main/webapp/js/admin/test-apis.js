/**
 * Admin Test APIs functions.
 * 
 * Dependencies: logger, httpClient, apiCallTable.
 * 
 * @author nbrest
 */
var apiTester;

var main = function () {
  importTestApisCss();
  var loadingModules = ["logger", "httpClient", "apiCallTable"];
  waitForModules(loadingModules, function(){
    apiTester = new ApiTester();
  });
};

/** Import test apis css */
function importTestApisCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/test-apis.css">');
}

function ApiTester() {
  var self = this;

  /** Executes a get request and displays the api call output. */
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
    scrollToTop();
  }

  /** Execute a POST request to the specified url with the specified request url parameters. */
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

  /** Reload VLC with the file passed as a parameter. */
  this.loadFileInVlc = function loadFileInVlc(url, file) {
    logger.traceFunctionCall();
    logger.debug("Selected file: " + file);
    var requestParam = "file=" + file;
    self.doPostUrlEncoded(url, requestParam);
  }
}

/**
 * Call main.
 */
$(document).ready(main);