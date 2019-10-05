/**
 * Global js variables and functions for all pages.
 * 
 * @author nbrest
 */
/** ----- Global variables ------------------------------------------------------------------ */
var global = {};
//Defaults logging level to INFO 
global.logLevel = 2;

global.session = {};

/** ----- Global functions ------------------------------------------------------------------ */
function main() {
    //testLogLevel();
	loadHeaderAndFooter(); 
}

/** Load header and footer. */
function loadHeaderAndFooter() {
	$.getScript( "/kame-house/js/header-footer/headerFooter.js", function(data, textStatus, jqxhr) {
		renderHeaderAndFooter();
	});
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
    console.error("Invalid use of log(logLevel, message) function. LogLevel is missing.");
    return;
  }
  var logLevelUpperCase = logLevel.toUpperCase();
  var logEntry = getTimestamp() + " - [" + logLevelUpperCase + "] - " + message;
  if (logLevelUpperCase == "ERROR") {
    console.error(logEntry)
  }
  if (logLevelUpperCase == "WARN" && global.logLevel >= 1) {
    console.warn(logEntry);
  }
  if (logLevelUpperCase == "INFO" && global.logLevel >= 2) {
    console.info(logEntry);
  }
  if (logLevelUpperCase == "DEBUG" && global.logLevel >= 3) {
    // Use debug to log behavior, such as executing x method, selected x playlist, etc.
    console.debug(logEntry);
  }
  if (logLevelUpperCase == "TRACE" && global.logLevel >= 4) {
    // Use trace to log content such as responses from api calls.
    console.trace(logEntry);
  } 
}

function testLogLevel() {
	console.log("global.logLevel " + global.logLevel);
	log("ERROR", "This is an ERROR message");
	log("WARN", "This is a WARN message");
	log("INFO", "This is an INFO message");
	log("DEBUG", "This is a DEBUG message");
	log("TRACE", "This is a TRACE message");
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
  log("TRACE", JSON.stringify(requestHeaders));
  return requestHeaders;
}

/** Get request headers object with Url Encoded content type. */
function getUrlEncodedHeaders() { 
  var requestHeaders = {}; 
  requestHeaders.Accept = '*/*';
  requestHeaders['Content-Type'] = "application/x-www-form-urlencoded"; 
  log("TRACE", JSON.stringify(requestHeaders));
  return requestHeaders;
}

/** Get request headers object with application json content type. */
function getApplicationJsonHeaders() {
  var requestHeaders = {};
  requestHeaders.Accept = '*/*';
  requestHeaders['Content-Type'] = 'application/json';
  log("TRACE", JSON.stringify(requestHeaders));
  return requestHeaders;
}

/** Checks if a variable is undefined or null, an empty array [] or an empty object {} */
function isEmpty(val) {
  return (val === undefined || val == null || val.length <= 0 
		  || (Object.entries(val).length === 0 && val.constructor === Object));
}

/** Call main. */
$(document).ready(main);