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

/** Global utils in global.js */
var bannerUtils;
var coreUtils;
var cursorUtils;
var fileUtils;
var moduleUtils;
var tableUtils;
var testUtils;
var timeUtils;

/** Global modules loaded from other js files */
var httpClient;
var logger;

/** 
 * Core global functions mapped to their logic in coreUtils.setGlobalFunctions().
 * Usage example: `if (isEmpty(val)) {...}` 
 */
var isEmpty;
var isFunction;
var isNullOrUndefined;
var scrollToTop;
var sleep;

/** 
 * ----- Global functions ------------------------------------------------------------------
 */
function main() {
  bannerUtils = new BannerUtils();
  coreUtils = new CoreUtils();
  coreUtils.setGlobalFunctions();
  cursorUtils = new CursorUtils();
  cursorUtils.loadSpinningWheelMobile();
  fileUtils = new FileUtils();
  tableUtils = new TableUtils();
  testUtils = new TestUtils();
  timeUtils = new TimeUtils();

  moduleUtils = new ModuleUtils();
  moduleUtils.loadDefaultModules();
  moduleUtils.waitForModules(["logger", "httpClient"], () => {
    logger.info("Started initializing global functions");
    coreUtils.loadHeaderAndFooter();
    //testUtils.testLogLevel();
  });
}

/**
 * BannerUtils to manipulate banners.
 */
function BannerUtils() {
  let self = this;
  const DEFAULT_BANNER_ROTATE_WAIT_MS = 10000;

  const CAPTAIN_TSUBASA_BANNERS = ["banner-beni3", "banner-benji-steve", "banner-benji", "banner-benji2", "banner-benji3", "banner-benji4", "banner-niupi", "banner-niupi2", "banner-oliver-benji", "banner-oliver-benji2", "banner-oliver-steve", "banner-oliver", "banner-oliver2"];
  const DC_BANNERS = ["banner-batman-animated", "banner-batman", "banner-joker", "banner-joker2", "banner-superman-logo", "banner-superman-space", "banner-superman", "banner-superman2", "banner-superman3"];
  const DRAGONBALL_BANNERS = ["banner-gogeta", "banner-gohan-shen-long", "banner-gohan-ssj2", "banner-gohan-ssj2-2", "banner-gohan-ssj2-3", "banner-gohan-ssj2-4", "banner-goku-ssj1", "banner-goku-ssj4-earth", "banner-trunks-mountains"];
  const GAME_OF_THRONES_BANNERS = ["banner-jon-snow2", "banner-winter-is-coming"];
  const MARVEL_BANNERS = ["banner-avengers-cap-uniform", "banner-avengers-cap", "banner-avengers-endgame", "banner-avengers-infinity", "banner-avengers-ironman", "banner-avengers-portals", "banner-avengers-trinity", "banner-avengers", "banner-spiderman"];
  const MATRIX_BANNERS = ["banner-matrix"];
  const PRINCE_OF_TENNIS_BANNERS = ["banner-fuji", "banner-pot-pijamas", "banner-rikkaidai", "banner-ryoma-chibi", "banner-ryoma-chibi2", "banner-ryoma-drive", "banner-ryoma-ss", "banner-seigaku", "banner-tezuka", "banner-yukimura", "banner-yukimura2", "banner-yukimura-sanada"];
  const SAINT_SEIYA_BANNERS = ["banner-ancient-era-warriors", "banner-aries-knights", "banner-athena", "banner-athena-saints", "banner-camus", "banner-dohko", "banner-fuego-12-casas", "banner-hades", "banner-hyoga", "banner-ikki", "banner-ikki2", "banner-pegasus-ryu-sei-ken", "banner-sanctuary", "banner-seiya", "banner-shaka", "banner-shion", "banner-shiryu", "banner-shun"];
  const STAR_WARS_BANNERS = ["banner-anakin", "banner-anakin2", "banner-anakin3", "banner-anakin4", "banner-anakin5", "banner-luke-vader", "banner-luke-vader2", "banner-luke-vader3", "banner-star-wars-ep3", "banner-star-wars-poster", "banner-star-wars-trilogy", "banner-vader", "banner-vader2", "banner-yoda", "banner-yoda2"];
  const TENNIS_BANNERS = ["banner-australian-open", "banner-roland-garros", "banner-wimbledon"];

  let ALL_BANNERS = [];
  // When adding new arrays here, also add them to preloadedBannerImages in setRandomAllBanner().
  ALL_BANNERS.push.apply(ALL_BANNERS, CAPTAIN_TSUBASA_BANNERS);
  ALL_BANNERS.push.apply(ALL_BANNERS, DC_BANNERS);
  ALL_BANNERS.push.apply(ALL_BANNERS, DRAGONBALL_BANNERS);
  ALL_BANNERS.push.apply(ALL_BANNERS, GAME_OF_THRONES_BANNERS);
  ALL_BANNERS.push.apply(ALL_BANNERS, MARVEL_BANNERS);
  ALL_BANNERS.push.apply(ALL_BANNERS, MATRIX_BANNERS);
  ALL_BANNERS.push.apply(ALL_BANNERS, PRINCE_OF_TENNIS_BANNERS);
  ALL_BANNERS.push.apply(ALL_BANNERS, SAINT_SEIYA_BANNERS);
  ALL_BANNERS.push.apply(ALL_BANNERS, STAR_WARS_BANNERS);
  ALL_BANNERS.push.apply(ALL_BANNERS, TENNIS_BANNERS);

  this.preloadedBannerImages = [];

  /** Set random saint seiya sanctuary banner */
  this.setRandomSanctuaryBanner = (bannerRotateWaitMs) => {
    let bannerClasses = ["banner-fuego-12-casas", "banner-sanctuary"];  
    self.setRandomBannerWrapper(bannerClasses, true, bannerRotateWaitMs);
    self.preloadBannerImages('saint-seiya', bannerClasses);
  }

  /** Set random dragonball banner */
  this.setRandomDragonBallBanner = (bannerRotateWaitMs) => {
    self.setRandomBannerWrapper(DRAGONBALL_BANNERS, true, bannerRotateWaitMs);
    self.preloadBannerImages('dragonball', DRAGONBALL_BANNERS);
  }

  /** Set random prince of tennis banner */
  this.setRandomPrinceOfTennisBanner = (bannerRotateWaitMs) => {
    self.setRandomBannerWrapper(PRINCE_OF_TENNIS_BANNERS, true, bannerRotateWaitMs);
    self.preloadBannerImages('prince-of-tennis', PRINCE_OF_TENNIS_BANNERS);
  }

  /** Set random saint seiya banner */
  this.setRandomSaintSeiyaBanner = (bannerRotateWaitMs) => {
    self.setRandomBannerWrapper(SAINT_SEIYA_BANNERS, true, bannerRotateWaitMs);
    self.preloadBannerImages('saint-seiya', SAINT_SEIYA_BANNERS);
  }

  /** Set random tennis banner */
  this.setRandomTennisBanner = (bannerRotateWaitMs) => {
    self.setRandomBannerWrapper(TENNIS_BANNERS, true, bannerRotateWaitMs);
    self.preloadBannerImages('tennis', TENNIS_BANNERS);
  }

  /** Set random banner from all banners */
  this.setRandomAllBanner = (bannerRotateWaitMs) => {
    self.setRandomBannerWrapper(ALL_BANNERS, true, bannerRotateWaitMs);
    self.preloadBannerImages('captain-tsubasa', CAPTAIN_TSUBASA_BANNERS);
    self.preloadBannerImages('dc', DC_BANNERS);
    self.preloadBannerImages('dragonball', DRAGONBALL_BANNERS);
    self.preloadBannerImages('game-of-thrones', GAME_OF_THRONES_BANNERS);
    self.preloadBannerImages('marvel', MARVEL_BANNERS);
    self.preloadBannerImages('matrix', MATRIX_BANNERS);
    self.preloadBannerImages('prince-of-tennis', PRINCE_OF_TENNIS_BANNERS);
    self.preloadBannerImages('saint-seiya', SAINT_SEIYA_BANNERS);
    self.preloadBannerImages('star-wars', STAR_WARS_BANNERS);
    self.preloadBannerImages('tennis', TENNIS_BANNERS);
  }

  /** Wrapper to setRandomBanner to decide if it should set it once or loop */
  this.setRandomBannerWrapper = (bannerClasses, shouldLoop, bannerRotateWaitMs) => {
    if (shouldLoop) {
      self.setRandomBannerLoop(bannerClasses, bannerRotateWaitMs);
    } else {
      self.setRandomBanner(bannerClasses);
    }
  }

  /** Set a random image from the banner classes list */
  this.setRandomBanner = (bannerClasses) => {
    // Get a new banner, different from the current one
    let randomBannerIndex = Math.floor(Math.random() * bannerClasses.length);
    let currentClassList = $('#banner').attr('class').split(/\s+/);
    let currentBannerClass = "";
    currentClassList.forEach((currentClass) => {
      if (currentClass.startsWith("banner-")) {
        currentBannerClass = currentClass;
      }
    });
    let indexOfCurrentBannerClass = bannerClasses.indexOf(currentBannerClass);
    while (randomBannerIndex == indexOfCurrentBannerClass) {
      randomBannerIndex = Math.floor(Math.random() * bannerClasses.length);
    }
    // Update banner
    let element = document.getElementById("banner");
    bannerClasses.forEach((bannerClass) => {
      element.classList.remove(bannerClass);
    });
    element.classList.add(bannerClasses[randomBannerIndex]);
    element
    // Trigger banner annimation
    var clonedElement = element.cloneNode(true);
    element.parentNode.replaceChild(clonedElement, element);
  }

  /** Set a random image banner from the classes list at the specified interval */
  this.setRandomBannerLoop = (bannerClass, bannerRotateWaitMs) => {
    if (isNullOrUndefined(bannerRotateWaitMs)) {
      bannerRotateWaitMs = DEFAULT_BANNER_ROTATE_WAIT_MS;
    }
    setInterval(() => {
      self.setRandomBanner(bannerClass);
    }, bannerRotateWaitMs);
  }

  /** Update the server name in the banner */
  this.updateServerName = () => {
    if (!isNullOrUndefined(global.session.server)) {
      $("#banner-server-name").text(global.session.server);
    }
  }
  
  /** Preload banner images */
  this.preloadBannerImages = (bannerPath, bannerArray) => {
    bannerArray.forEach((bannerName) => {
      let img = new Image();
      img.src = '/kame-house/img/banners/' + bannerPath + '/' + bannerName + '.jpg';
      self.preloadedBannerImages.push(img);
    });
  }
}

/** 
 * Prototype that contains the logic for all the core global functions. 
 * Only add functions here that are truly global and I'd want them to be part of the js language itself.
 * If I don't want them to be native, I probably should add them to a more specific utils prototype.
 */
function CoreUtils() {
  let self = this;

  /** Set the global variable and set the external reference to global to be used without coreUtils. prefix */
  this.global = {};
  this.global.session = {};
  global = this.global;

  /** 
   * @deprecated(use isNullOrUndefined())
   * 
   * Checks if a variable is undefined or null, an empty array [] or an empty object {}. 
   * 
   * --- IMPORTANT --- 
   * DEPRECATED: This method performs poorly with large objects. For large playlists (3000 elements) this comparison
   * takes more than 1 seconds causing a lag in the entire view. Use it for objects that I don't expect
   * to be large and be aware of performance issues that can be caused from using it.
   * 
   * For better performance, use isNullOrUndefined() when that check is enough.
   * 
   * Keeping the definition so I don't attempt to do the same later down the track.
   */
  this.isEmpty = (val) => {
    let isUndefinedOrNull = self.isNullOrUndefined(val);
    let isEmptyString = !isUndefinedOrNull && val === "";
    let isEmptyArray = !isUndefinedOrNull && Array.isArray(val) && val.length <= 0;
    let isEmptyObject = !isUndefinedOrNull && Object.entries(val).length === 0 && val.constructor === Object;
    return isUndefinedOrNull || isEmptyString || isEmptyArray || isEmptyObject;
  }

  /** Checks if a variable is undefined or null. */
  this.isNullOrUndefined = (val) => {
    return val === undefined || val == null;
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
    if (isNullOrUndefined(divId)) {
      divToScrollToTop = 'html, body';
    } else {
      divToScrollToTop = '#' + divId;
    }
    $(divToScrollToTop).animate({
      scrollTop: 0
    }, '10');
  }

  /** Set the aliases for the global functions to be used everywhere without the prefix coreUtils. */
  this.setGlobalFunctions = () => {
    isEmpty = self.isEmpty;
    isFunction = self.isFunction;
    isNullOrUndefined = self.isNullOrUndefined;
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
 * Functionality related to file and filename manipulation. 
 */
function FileUtils() {

  /** Get the last part of the absolute filename */
  // Split the filename into an array based on the path separators '/' and '\'
  this.getShortFilename = (filename) => filename.split(/[\\/]+/).pop();
}

/** 
 * Functionality to load different modules and control the dependencies between them.
 */
function ModuleUtils() {
  let self = this;

  /** 
   * Object that determines which module is loaded. 
   * For example, when logger gets loaded, set modules.logger = true;
   * I use it in waitForModules() to check if a module is loaded or not.
   */
  this.modules = {};

  /** Marks the specified module as loaded */
  this.setModuleLoaded = (moduleName) => self.modules[moduleName] = true;

  /** Load default modules. */
  this.loadDefaultModules = () => {
    self.loadLogger();
    self.loadHttpClient();
  }

  /** Load logger object. */
  this.loadLogger = () => {
    $.getScript("/kame-house/js/utils/logger.js", (data, textStatus, jqxhr) => {
      logger = new Logger();
      self.setModuleLoaded("logger");
    });
  }

  /** Load httpClient. */
  this.loadHttpClient = () => {
    $.getScript("/kame-house/js/utils/http-client.js", (data, textStatus, jqxhr) => {
      self.waitForModules(["logger"], () => {
        httpClient = new HttpClient();
        self.setModuleLoaded("httpClient");
      });
    });
  }

  this.loadWebSocketKameHouse = () => {
    $.getScript("/kame-house/js/utils/websocket-kamehouse.js", (data, textStatus, jqxhr) =>
      self.waitForModules(["logger"], () => self.setModuleLoaded("webSocketKameHouse")));
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

/** Call main. */
$(document).ready(main);