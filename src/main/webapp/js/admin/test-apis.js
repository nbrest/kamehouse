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
  console.debug(getTimestamp() + " : Executing GET on " + url);
  var requestTimestamp = getTimestamp();
  displayRequestPayload(requestTimestamp, url, "GET", null, null, null);
  $.get(url)
    .success(function (result) {
      displayRequestPayload(requestTimestamp, url, "GET", null, getTimestamp(), result);
    })
    .error(function (jqXHR, textStatus, errorThrown) {
      console.error(JSON.stringify(jqXHR));
      displayErrorExecutingRequest();
    });
  scrollToTop();
  setCollapsibleContent();
}

/** Execute a POST request to the specified url with the specified request url parameters. */
function doPostUrlEncoded(url, requestParam) {
  log("DEBUG", "Executing POST on " + url + " with requestParam " + JSON.stringify(requestParam));
  var requestTimestamp = getTimestamp();
  displayRequestPayload(requestTimestamp, url, "POST", requestParam, null, null);
  var requestHeaders = getUrlEncodedHeaders();
  $.ajax({
    type: "POST",
    url: url,
    data: requestParam,
    headers: requestHeaders,
    success: function (data) {
      displayRequestPayload(requestTimestamp, url, "POST", requestParam, getTimestamp(), data);
    },
    error: function (data) {
      log("ERROR", JSON.stringify(data));
      displayErrorExecutingRequest();
    }
  });
  setCollapsibleContent();
}

/** Reload VLC with the file passed as a parameter. */
function loadFileInVlc(url, file) {
  log("DEBUG", "Selected file: " + file);
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