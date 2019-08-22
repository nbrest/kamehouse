/**
 * Global js variables and functions for all pages.
 * 
 * @author nbrest
 */
function main() {}

/** Site under construction message. */
function siteUnderCostructionAlert() {
  alert('The site is still under construction and this functionality has not been implemented yet.');
}

/** Get current timestamp with client timezone. */
function getTimestamp() {
  var newDate = new Date();
  var offsetTime = newDate.getTimezoneOffset() * -1 * 60 * 1000;
  var currentDateTime = newDate.getTime();
  return new Date(currentDateTime + offsetTime).toISOString().replace("T", " ").slice(0, 19);
}

/** Log a specified message with the specified logging level. */
function log(logLevel, message) {
  if (isEmpty(logLevel)) {
    return;
  }
  var logLevelUpperCase = logLevel.toUpperCase();
  var logEntry = getTimestamp() + " - [" + logLevelUpperCase + "] - " + message;
  if (logLevelUpperCase == "ERROR") {
    console.error(logEntry)
  }
  if (logLevelUpperCase == "WARN") {
    console.warn(logEntry);
  }
  if (logLevelUpperCase == "INFO") {
    console.info(logEntry);
  }
  if (logLevelUpperCase == "DEBUG") {
    console.debug(logEntry);
  }
  if (logLevelUpperCase == "TRACE") {
    console.trace(logEntry);
  } 
}

/** Convert input in seconds to hh:mm:ss output. */
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

/** Get CSRF token. */
function getCsrfToken() {
  var token = $("meta[name='_csrf']").attr("content");
  return token;
}

/** Get CSRF header. */
function getCsrfHeader() {
  var header = $("meta[name='_csrf_header']").attr("content");
  return header;
}

/** Get CSRF standard requestHeaders object. */
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

/** Checks if a variable is undefined or null. */
function isEmpty(val) {
  return (val === undefined || val == null);
}

/** Checks if an array is empty. */
function isEmptyArray(val) {
  return (isEmpty(val) || val.length <= 0);
}

/** Call main. */
$(document).ready(main);