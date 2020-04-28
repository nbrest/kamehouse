/**
 * Global js variables and functions for all pages.
 * 
 * @author nbrest
 */
/** 
 * ----- Global variables ------------------------------------------------------------------ 
 */
var global = {};
global.session = {};

var timeUtils;
var httpClient;
var logger;

/** 
 * ----- Global functions ------------------------------------------------------------------
 */
function main() {
  loadTimeUtils();
  loadLogger();
  loadHttpClient();
  initKameHouse(initGlobal);
}

/** Waits until all global dependencies are loaded, then executes the specified init function.
 * Use this function in the main() of each page that requires global dependencies like logger and httpClient
 * to be loaded before the main code is executed.
 */
async function initKameHouse(initFunction) {
  //console.log("Start initKameHouse");
  while (isEmpty(httpClient) && isEmpty(logger) && isEmpty(timeUtils)) {
    //console.log("Waiting initKameHouse");
    await sleep(100);
  }
  //console.log("Finish initKameHouse");
  initFunction();
}

/** Init function to call after all global dependencies are loaded */
function initGlobal() {
  loadHeaderAndFooter();
  //testLogLevel();
}

/** Load time utils. */
function loadTimeUtils() {
  $.getScript("/kame-house/js/utils/time-utils.js", function (data, textStatus, jqxhr) {
    timeUtils = new TimeUtils();
  });
}

/** Load logger object. */
function loadLogger() {
  $.getScript("/kame-house/js/utils/logger.js", function (data, textStatus, jqxhr) {
    logger = new Logger();
  });
}

/** Load httpClient. */
function loadHttpClient() {
  $.getScript("/kame-house/js/utils/http-client.js", function (data, textStatus, jqxhr) {
    httpClient = new HttpClient();
  });
}

/** Load header and footer. */
function loadHeaderAndFooter() {
  $.getScript("/kame-house/js/header-footer/header-footer.js", function (data, textStatus, jqxhr) {
    renderHeaderAndFooter();
  });
}

/** Test the different log levels. */
function testLogLevel() {
  console.log("logger.logLevel " + logger.logLevel);
  logger.error("This is an ERROR message");
  logger.warn("This is a WARN message");
  logger.info("This is an INFO message");
  logger.debug("This is a DEBUG message");
  logger.trace("This is a TRACE message");
}

/**
 * Sleep the specified milliseconds.
 * This function needs to be called in an async method, with the await prefix. 
 * Example: await sleep(1000);
 */
function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

/** Checks if a variable is undefined or null, an empty array [] or an empty object {} */
function isEmpty(val) {
  return (val === undefined || val == null || val.length <= 0 ||
    (Object.entries(val).length === 0 && val.constructor === Object));
}

/** Call main. */
$(document).ready(main);