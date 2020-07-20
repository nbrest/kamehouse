/**
 * Global js variables and functions for all pages.
 * 
 * Dependencies: logger, httpClient.
 * 
 * @author nbrest
 */
/** 
 * ----- Global variables ------------------------------------------------------------------ 
 */
var global;
// TODO: remove this when I move to moduleUtils. in all js files
var modules;

/** Global modules */
var cursorUtils;
var globalUtils;
var httpClient;
var logger;
var moduleUtils;
var testUtils;
var timeUtils;
var tableUtils;

/** 
 * Global functions mapped to logic in global utils in globalUtils.setGlobalFunctions().
 * Usage example: `if (isEmpty(val)) {...}` 
 */
var isEmpty;
var isFunction;
var scrollToTop;
var sleep;
// TODO: remove this when I move to moduleUtils. in all js files
var waitForModules;

/** 
 * ----- Global functions ------------------------------------------------------------------
 */
function main() {
  globalUtils = new GlobalUtils();
  globalUtils.setGlobalFunctions();
  moduleUtils = new ModuleUtils();
  moduleUtils.loadDefaultModules();
  // TODO: once I move everything to moduleUtils. in other js files, remove these and those global vars
  modules = moduleUtils.modules;
  waitForModules = moduleUtils.waitForModules;
  moduleUtils.waitForModules(["logger", "httpClient"], initGlobal);
}

/** Init function to call after all global dependencies are loaded */
function initGlobal() {
  logger.info("Started initializing global functions");
  globalUtils.loadHeaderAndFooter();
  cursorUtils.loadSpinningWheelMobile();
  //testUtils.testLogLevel();
}

/** 
 * Prototype that contains the logic for all the global functions. 
 * Only add functions here that are truly global and I'd want them to be part of the js language itself.
 * If I don't want them to be native, I probably should add them to a more specific utils prototype.
 */
function GlobalUtils() {
  let self = this;
  
  /** Set the global variable and set the external reference to global to be used without globalUtils. prefix */
  this.global = {};
  this.global.session = {};
  global = this.global;

  /** Checks if a variable is undefined or null, an empty array [] or an empty object {}. */
  this.isEmpty = (val) => {
    let isNullOrUndefined = val === undefined || val == null;
    let isEmptyString = !isNullOrUndefined && val === "";
    let isEmptyArray = !isNullOrUndefined && Array.isArray(val) && val.length <= 0;
    let isEmptyObject = !isNullOrUndefined && Object.entries(val).length === 0 && val.constructor === Object;
    return isNullOrUndefined || isEmptyString || isEmptyArray || isEmptyObject;
  }

  /** Returns true if the parameter variable is a fuction. */
  this.isFunction = (expectedFunction) => expectedFunction instanceof Function;

  /** Load header and footer. */
  this.loadHeaderAndFooter = () => {
    $.getScript("/kame-house/js/header-footer/header-footer.js", (data, textStatus, jqxhr) => renderHeaderAndFooter());
  }

  /** Scroll to the top of the specified div or top of the window if no div specified. */
  this.scrollToTop = (divId) => {
    let divToScrollToTop;
    if (isEmpty(divId)) {
      divToScrollToTop = 'html, body';
    } else {
      divToScrollToTop = '#' + divId;
    }
    $(divToScrollToTop).animate({
      scrollTop: 0
    }, '10');
  }

  /** Set the aliases for the global functions to be used everywhere without the prefix globalUtils. */
  this.setGlobalFunctions = () => {
    isEmpty = self.isEmpty;
    isFunction = self.isFunction;
    scrollToTop = self.scrollToTop;
    sleep = self.sleep;
  }

  /**
   * Sleep the specified milliseconds.
   * This function needs to be called in an async method, with the await prefix. 
   * Example: await sleep(1000);
   */
  this.sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));
}

/** 
 * Functionality to load different modules and control the dependencies between them.
 */
function ModuleUtils() {
  let self = this;

  /** 
   * Object that determines which module is loaded. 
   * For example, when timeUtils gets loaded, set modules.timeUtils = true;
   * I use it in waitForModules() to check if a module is loaded or not.
   */
  this.modules = {};

  /** Load default modules. */
  this.loadDefaultModules = () => {
    self.loadTimeUtils();
    self.loadLogger();
    self.loadHttpClient();
    cursorUtils = new CursorUtils();
    tableUtils = new TableUtils();
    testUtils = new TestUtils();
  }

  /** Load time utils. */
  this.loadTimeUtils = () => {
    timeUtils = new TimeUtils();
    self.modules.timeUtils = true;
  }

  /** Load logger object. */
  this.loadLogger = () => {
    $.getScript("/kame-house/js/utils/logger.js", (data, textStatus, jqxhr) => {
      self.waitForModules(["timeUtils"], () => {
        logger = new Logger();
        self.modules.logger = true;
      });
    });
  }

  /** Load httpClient. */
  this.loadHttpClient = () => {
    $.getScript("/kame-house/js/utils/http-client.js", (data, textStatus, jqxhr) => {
      self.waitForModules(["logger"], () => {
        httpClient = new HttpClient();
        self.modules.httpClient = true;
      });
    });
  }
    
  /** 
   * Waits until all specified modules in the moduleNames array are loaded, 
   * then executes the specified init function.
   * Use this function in the main() of each page that requires modules like logger and httpClient
   * to be loaded before the main code is executed.
   */
  this.waitForModules = async function waitForModules(moduleNames, initFunction) {
    //console.log("init: " + initFunction.name + ". Start waitForModules " + JSON.stringify(moduleNames) + ". modules status: " + JSON.stringify(modules));
    let areAllModulesLoaded = false;
    while (!areAllModulesLoaded) {
      //console.log("init: " + initFunction.name + ". Waiting waitForModules " + JSON.stringify(moduleNames) + ". modules status: " + JSON.stringify(modules));
      let isAnyModuleStillLoading = false;
      moduleNames.forEach((moduleName) => {
        if (!self.modules[moduleName]) {
          isAnyModuleStillLoading = true;
        }
      });
      if (!isAnyModuleStillLoading) {
        areAllModulesLoaded = true;
      }
      await sleep(3);
    }
    //console.log("init: " + initFunction.name + ". *** Finished *** waitForModules " + JSON.stringify(moduleNames) + ". modules status: " + JSON.stringify(modules));
    if (isFunction(initFunction)) {
      //console.log("Executing " + initFunction.name);
      initFunction();
    }
  }
}

/** 
 * Functionality to manipulate the cursor. 
 */
function CursorUtils() {

  /** Set the cursor to a wait spinning wheel */
  this.setCursorWait = () => {
    $('html').addClass("wait");
    $('#spinning-wheel-mobile-wrapper').removeClass("hidden-kh");
  }

  /** Set the cursor to default shape */
  this.setCursorDefault = () => {
    $('html').removeClass("wait");
    $('#spinning-wheel-mobile-wrapper').addClass("hidden-kh");
  }

  this.loadSpinningWheelMobile = () => {
    document.body.insertAdjacentHTML("beforeBegin", "<div id='spinning-wheel-mobile-wrapper' class='hidden-kh'>");
    $("#spinning-wheel-mobile-wrapper").append("<div class='spinning-wheel-mobile-container'>");
    $(".spinning-wheel-mobile-container").append("<div class='spinning-wheel-mobile'>");
  }
}

/** 
 * Functionality to manipulate tables. 
 */
function TableUtils() {

  /** Filter table rows based on the specified filter string. Shouldn't filter the header row. */
  this.filterTableRows = function filterTableRows(filterString, tableBodyId) {
    filterString = filterString.toLowerCase();
    let playlistBodyRows = $("#" + tableBodyId + " tr");
    let regex;
    try {
      filterString = filterString.split('').join('.*').replace(/\s/g, '');
      regex = RegExp(filterString);
    } catch (error) {
      logger.error("Error creating regex from filter string " + filterString);
      regex = RegExp("");
    }
    playlistBodyRows.filter(function () {
      $(this).toggle(regex.test($(this).text().toLowerCase()))
    });
  }
}

/**
 * TimeUtils utility object for manipulating time and dates.
 */
function TimeUtils() {

  /** Get current timestamp with client timezone. */
  this.getTimestamp = () => {
    let newDate = new Date();
    let offsetTime = newDate.getTimezoneOffset() * -1 * 60 * 1000;
    let currentDateTime = newDate.getTime();
    return new Date(currentDateTime + offsetTime).toISOString().replace("T", " ").slice(0, 19);
  }

  /** Convert input in seconds to hh:mm:ss output. */
  this.convertSecondsToHsMsSs = (seconds) => new Date(seconds * 1000).toISOString().substr(11, 8);
}

/** 
 * Prototype for test functionality. 
 */
function TestUtils() {

  /** Test the different log levels. */
  this.testLogLevel = () => {
    console.log("logger.logLevel " + logger.logLevel);
    logger.error("This is an ERROR message");
    logger.warn("This is a WARN message");
    logger.info("This is an INFO message");
    logger.debug("This is a DEBUG message");
    logger.trace("This is a TRACE message");
  }
}

/** Call main. */
$(document).ready(main);