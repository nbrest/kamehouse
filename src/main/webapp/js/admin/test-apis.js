/**
 * Admin Test APIs functions.
 * 
 * @author nbrest
 */
var main = function () {
  importTestApisCss();
};

/** Import test apis css */
function importTestApisCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/test-apis.css">');
}

/** Executes a get request and displays the api call output. */
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
  scrollToTop();
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

/** Reload VLC with the file passed as a parameter. */
function loadFileInVlc(url, file) {
  logger.traceFunctionCall();
  logger.debug("Selected file: " + file);
  var requestParam = "file=" + file;
  doPostUrlEncoded(url, requestParam);
}

/**
 * Scroll to the top of the screen.
 */
function scrollToTop() {
  $('html, body').animate({
    scrollTop: 0
  }, '10');
}

/**
 * Call main.
 */
$(document).ready(main);