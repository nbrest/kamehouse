/**
 * Global js functions for all pages.
 * 
 * @author nbrest
 */
function main() {}

/**
 * Site under construction message.
 */
function siteUnderCostructionAlert() {
  alert('The site is still under construction and this functionality has not been implemented yet.');
}

/**
 * Get timestamp.
 */
function getTimestamp() {
  return new Date().toISOString().replace("T", " ").slice(0, 19);
}

/**
 * Convert input in seconds to hh:mm:ss output. 
 */
function convertSecondsToHsMsSs(seconds) {
  return new Date(seconds * 1000).toISOString().substr(11, 8);
}

/**
 * Sleep the specified milliseconds.
 * This function needs to be called in an async method, with the await prefix. 
 * Example: await sleep(1000);
 */
function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
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
 * Get CSRF standard requestHeaders object.
 */
function getCsrfRequestHeadersObject() {
  var csrfHeader = getCsrfHeader();
  var csrfToken = getCsrfToken();
  var requestHeaders = {};
  requestHeaders.Accept = 'application/json';
  requestHeaders['Content-Type'] = 'application/json';
  requestHeaders[csrfHeader] = csrfToken;
  //console.log(JSON.stringify(requestHeaders));
  return requestHeaders;
}

/**
 * Call main.
 */
$(document).ready(main);