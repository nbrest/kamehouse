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
var global = {};

/** Global utils in global.js */
var bannerUtils;
var collapsibleDivUtils;
var cookiesUtils;
var coreUtils;
var cursorUtils;
var domUtils;
var fileUtils;
var fetchUtils;
var moduleUtils;
var tabUtils;
var tableUtils;
var testUtils;
var timeUtils;

/** Global modules loaded from other js files */
var httpClient;
var logger;

/** 
 * Core global functions mapped to their logic in coreUtils
 * Usage example: `if (isEmpty(val)) {...}` 
 */
var consoleLog;
var isEmpty;
var isFunction;
var isNullOrUndefined;
var scrollToBottom;
var scrollToTop;
var scrollToTopOfDiv;
var sleep;

/** 
 * ----- Global functions ------------------------------------------------------------------
 */
function main() {
  
  timeUtils = new TimeUtils();
  coreUtils = new CoreUtils();

  bannerUtils = new BannerUtils();
  collapsibleDivUtils = new CollapsibleDivUtils();
  cookiesUtils = new CookiesUtils();
  cursorUtils = new CursorUtils();
  domUtils = new DomUtils();
  fetchUtils = new FetchUtils();
  fileUtils = new FileUtils();
  tabUtils = new TabUtils();
  tableUtils = new TableUtils();
  testUtils = new TestUtils();

  moduleUtils = new ModuleUtils();
  cursorUtils.loadSpinningWheelMobile();
  moduleUtils.loadDefaultModules();
  moduleUtils.waitForModules(["logger", "httpClient"], () => {
    logger.info("Started initializing global functions");
    coreUtils.loadHeaderAndFooter();
    //testUtils.testLogLevel();
  });
  //testUtils.testSleep();
}

/**
 * BannerUtils to manipulate banners.
 */
function BannerUtils() {

  this.setRandomSanctuaryBanner = setRandomSanctuaryBanner;
  this.setRandomDragonBallBanner = setRandomDragonBallBanner;
  this.setRandomPrinceOfTennisBanner = setRandomPrinceOfTennisBanner;
  this.setRandomSaintSeiyaBanner = setRandomSaintSeiyaBanner;
  this.setRandomTennisBanner = setRandomTennisBanner;
  this.setRandomAllBanner = setRandomAllBanner;
  this.updateServerName = updateServerName;

  const DEFAULT_BANNER_ROTATE_WAIT_MS = 10000;

  const CAPTAIN_TSUBASA_BANNERS = ["banner-beni3", "banner-benji-steve", "banner-benji", "banner-benji2", "banner-benji3", "banner-benji4", "banner-niupi", "banner-niupi2", "banner-oliver-benji", "banner-oliver-benji2", "banner-oliver-steve", "banner-oliver", "banner-oliver2"];
  const DC_BANNERS = ["banner-batman-animated", "banner-batman", "banner-joker", "banner-joker2", "banner-superman-logo", "banner-superman-space", "banner-superman", "banner-superman2", "banner-superman3"];
  const DRAGONBALL_BANNERS = ["banner-gogeta", "banner-gohan-shen-long", "banner-gohan-ssj2", "banner-gohan-ssj2-2", "banner-gohan-ssj2-3", "banner-gohan-ssj2-4", "banner-goku-ssj1", "banner-goku-ssj4-earth", "banner-trunks-mountains"];
  const GAME_OF_THRONES_BANNERS = ["banner-jon-snow2", "banner-winter-is-coming"];
  const MARVEL_BANNERS = ["banner-avengers", "banner-avengers-assemble", "banner-avengers-cap", "banner-avengers-cap-mjolnir", "banner-avengers-cap-mjolnir2", "banner-avengers-cap-mjolnir3", "banner-avengers-cap-mjolnir4", "banner-avengers-cap-mjolnir5", "banner-avengers-cap-mjolnir6", "banner-avengers-cap-uniform", "banner-avengers-endgame", "banner-avengers-infinity", "banner-avengers-ironman", "banner-avengers-portals", "banner-avengers-trinity", "banner-spiderman"];
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

  let preloadedBannerImages = [];

  /** Set random saint seiya sanctuary banner */
  function setRandomSanctuaryBanner(bannerRotateWaitMs) {
    let bannerClasses = ["banner-fuego-12-casas", "banner-sanctuary"];  
    setRandomBannerWrapper(bannerClasses, true, bannerRotateWaitMs);
    preloadBannerImages('saint-seiya', bannerClasses);
  }

  /** Set random dragonball banner */
  function setRandomDragonBallBanner(bannerRotateWaitMs) {
    setRandomBannerWrapper(DRAGONBALL_BANNERS, true, bannerRotateWaitMs);
    preloadBannerImages('dragonball', DRAGONBALL_BANNERS);
  }

  /** Set random prince of tennis banner */
  function setRandomPrinceOfTennisBanner(bannerRotateWaitMs) {
    setRandomBannerWrapper(PRINCE_OF_TENNIS_BANNERS, true, bannerRotateWaitMs);
    preloadBannerImages('prince-of-tennis', PRINCE_OF_TENNIS_BANNERS);
  }

  /** Set random saint seiya banner */
  function setRandomSaintSeiyaBanner(bannerRotateWaitMs) {
    setRandomBannerWrapper(SAINT_SEIYA_BANNERS, true, bannerRotateWaitMs);
    preloadBannerImages('saint-seiya', SAINT_SEIYA_BANNERS);
  }

  /** Set random tennis banner */
  function setRandomTennisBanner(bannerRotateWaitMs) {
    setRandomBannerWrapper(TENNIS_BANNERS, true, bannerRotateWaitMs);
    preloadBannerImages('tennis', TENNIS_BANNERS);
  }

  /** Set random banner from all banners */
  function setRandomAllBanner(bannerRotateWaitMs) {
    setRandomBannerWrapper(ALL_BANNERS, true, bannerRotateWaitMs);
    preloadBannerImages('captain-tsubasa', CAPTAIN_TSUBASA_BANNERS);
    preloadBannerImages('dc', DC_BANNERS);
    preloadBannerImages('dragonball', DRAGONBALL_BANNERS);
    preloadBannerImages('game-of-thrones', GAME_OF_THRONES_BANNERS);
    preloadBannerImages('marvel', MARVEL_BANNERS);
    preloadBannerImages('matrix', MATRIX_BANNERS);
    preloadBannerImages('prince-of-tennis', PRINCE_OF_TENNIS_BANNERS);
    preloadBannerImages('saint-seiya', SAINT_SEIYA_BANNERS);
    preloadBannerImages('star-wars', STAR_WARS_BANNERS);
    preloadBannerImages('tennis', TENNIS_BANNERS);
  }

  /** Wrapper to setRandomBanner to decide if it should set it once or loop */
  function setRandomBannerWrapper(bannerClasses, shouldLoop, bannerRotateWaitMs) {
    if (shouldLoop) {
      setRandomBannerLoop(bannerClasses, bannerRotateWaitMs);
    } else {
      setRandomBanner(bannerClasses);
    }
  }

  /** Set a random image from the banner classes list */
  function setRandomBanner(bannerClasses) {
    // Get a new banner, different from the current one
    let randomBannerIndex = Math.floor(Math.random() * bannerClasses.length);
    let bannerDivClasses = $('#banner').attr('class');
    if (isNullOrUndefined(bannerDivClasses)) {
      return;
    }
    let currentClassList = bannerDivClasses.split(/\s+/);
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
      domUtils.classListRemove(element, bannerClass);
    });
    domUtils.classListAdd(element, bannerClasses[randomBannerIndex]);

    // Trigger banner animation
    var clonedElement = element.cloneNode(true);
    element.parentNode.replaceChild(clonedElement, element);
  }

  /** Set a random image banner from the classes list at the specified interval */
  function setRandomBannerLoop(bannerClass, bannerRotateWaitMs) {
    if (isNullOrUndefined(bannerRotateWaitMs)) {
      bannerRotateWaitMs = DEFAULT_BANNER_ROTATE_WAIT_MS;
    }
    setInterval(() => {
      setRandomBanner(bannerClass);
    }, bannerRotateWaitMs);
  }

  /** Update the server name in the banner */
  function updateServerName() {
    if (!isNullOrUndefined(global.session.server)) {
      domUtils.setHtml($("#banner-server-name"), global.session.server);
    }
  }
  
  /** Preload banner images */
  function preloadBannerImages(bannerPath, bannerArray) {
    bannerArray.forEach((bannerName) => {
      let img = domUtils.getImgBtn({
        src: '/kame-house/img/banners/' + bannerPath + '/' + bannerName + '.jpg'
      });
      preloadedBannerImages.push(img);
    });
  }
}

/**
 * Utility to manipulate collapsible divs.
 */
function CollapsibleDivUtils() {

  this.refreshCollapsibleDiv = refreshCollapsibleDiv;
  this.setCollapsibleContent = setCollapsibleContent;

  /**
   * Refresh to resize all the collapsible divs in the current page.
   */
  function refreshCollapsibleDiv() {
    let collapsibleElements = document.getElementsByClassName("collapsible-kh");
    let i;
    for (i = 0; i < collapsibleElements.length; i++) {
      collapsibleElements[i].click();
      collapsibleElements[i].click();
    } 
  }

  /**
   * Set collapsible content listeners.
   */
  function setCollapsibleContent() {
    let collapsibleElements = document.getElementsByClassName("collapsible-kh");
    let i;
    for (i = 0; i < collapsibleElements.length; i++) {
      collapsibleElements[i].removeEventListener("click", collapsibleContentListener);
      collapsibleElements[i].addEventListener("click", collapsibleContentListener);
    }
  }

  /**
   * Function to toggle height of the collapsible elements from null to it's scrollHeight.
   */
  function collapsibleContentListener() {
    // Can't use self here, need to use this. Also can't use an annonymous function () => {}
    domUtils.classListToggle(this, "collapsible-kh-active");
    let content = this.nextElementSibling;
    if (content.style.maxHeight) {
      domUtils.setStyle(content, "maxHeight", null);
    } else {
      domUtils.setStyle(content, "maxHeight", content.scrollHeight + "px");
    }
  }
}

/** 
 * Prototype that contains the logic for all the core global functions. 
 * Only add functions here that are truly global and I'd want them to be part of the js language.
 * If I don't want them to be native, I probably should add them to a more specific utils prototype.
 */
function CoreUtils() {

  this.loadHeaderAndFooter = loadHeaderAndFooter;

  /** Set the global variable and set the external reference to global to be used without coreUtils. prefix */
  global = {};
  global.session = {};

  /** Load header and footer. */
  function loadHeaderAndFooter() {
    fetchUtils.getScript("/kame-house/js/header-footer/header-footer.js", () => renderHeaderAndFooter());
  }

  /** Custom logger to log anything before logger module is loaded */
  consoleLog = function consoleLog(message) {
    logEntry = timeUtils.getTimestamp() + " - [INFO] - " + message;
    console.log(logEntry);
  }

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
  isEmpty = function isEmpty(val) {
    let isUndefinedOrNull = isNullOrUndefined(val);
    let isEmptyString = !isUndefinedOrNull && val === "";
    let isEmptyArray = !isUndefinedOrNull && Array.isArray(val) && val.length <= 0;
    let isEmptyObject = !isUndefinedOrNull && Object.entries(val).length === 0 && val.constructor === Object;
    return isUndefinedOrNull || isEmptyString || isEmptyArray || isEmptyObject;
  }

  /** Checks if a variable is undefined or null. */
  isNullOrUndefined = function isNullOrUndefined(val) {
    return val === undefined || val == null;
  }

  /** Returns true if the parameter variable is a fuction. */
  isFunction = function isFunction(expectedFunction) {
    return expectedFunction instanceof Function;
  } 

  /** 
   * Scroll the specified div to it's top.
   * This method doesn't scroll the entire page, it scrolls the scrollable div to it's top.
   * To scroll the page to the top of a particular div, use scrollToTop()
   */
  scrollToTopOfDiv = function scrollToTopOfDiv(divId) {
    let divToScrollToTop = '#' + divId;
    $(divToScrollToTop).animate({
      scrollTop: 0
    }, '10');
  }

  /** 
   * Scroll the window to the top of a particular div or to the top of the body if no div specified.
   */
  scrollToTop = function scrollToTop(divId) {
    let scrollPosition;
    if (isNullOrUndefined(divId)) {
      scrollPosition = 0;
    } else {
      scrollPosition = $('#' + divId).offset().top;
    }
    $('html, body').animate({
      scrollTop: scrollPosition
    }, '10');
  }

  /** 
   * Scroll the window to the bottom of a particular div or to the bottom of the body if no div specified.
   */
  scrollToBottom = function scrollToBottom(divId) {
    let scrollPosition;
    if (isNullOrUndefined(divId)) {
      scrollPosition = document.body.scrollHeight;
    } else {
      let jqDivId = '#' + divId;
      scrollPosition = $(jqDivId).offset().top + $(jqDivId).height() - window.innerHeight;
    }
    $('html, body').animate({
      scrollTop: scrollPosition
    }, '10');
  }

  /**
   * Sleep the specified milliseconds.
   * This function needs to be called in an async method, with the await prefix. 
   * Example: await sleep(1000);
   */
  sleep = function sleep(ms) { 
    return new Promise(resolve => setTimeout(resolve, ms));
  }
}

/**
 * Functionality to handle cookies.
 */
function CookiesUtils() {

  this.getCookie = getCookie;
  this.setCookie = setCookie;

  /**
   * Get a cookie.
   */
  function getCookie(cookieName) {
    let name = cookieName + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let cookiesArray = decodedCookie.split(';');
    for(let i = 0; i < cookiesArray.length; i++) {
      let cookie = cookiesArray[i];
      while (cookie.charAt(0) == ' ') {
        cookie = cookie.substring(1);
      }
      if (cookie.indexOf(name) == 0) {
        return cookie.substring(name.length, cookie.length);
      }
    }
    return "";
  }

  /**
   * Set a cookie.
   */
  function setCookie(cookieName, cookieValue, expiryDays) {
    if (expiryDays) {
      const expiriyDate = new Date();
      expiriyDate.setTime(expiriyDate.getTime() + (expiryDays * 24 * 60 * 60 * 1000));
      let expires = "expires=" + expiriyDate.toUTCString();
      document.cookie = cookieName + "=" + cookieValue + ";" + expires + "; path=/";
    } else {
      document.cookie = cookieName + "=" + cookieValue + "; path=/";
    }
  }
}

/** 
 * Functionality to manipulate the cursor. 
 */
function CursorUtils() {

  this.setCursorWait = setCursorWait;
  this.setCursorDefault = setCursorDefault;
  this.loadSpinningWheelMobile = loadSpinningWheelMobile;

  /** Set the cursor to a wait spinning wheel */
  function setCursorWait() {
    domUtils.addClass($('html'), "wait");
    domUtils.removeClass($('#spinning-wheel-mobile-wrapper'), "hidden-kh");
  }

  /** Set the cursor to default shape */
  function setCursorDefault() {
    domUtils.removeClass($('html'), "wait");
    domUtils.addClass($('#spinning-wheel-mobile-wrapper'), "hidden-kh");
  }

  /**
   * Load the spinning wheel for mobile view.
   */
  async function loadSpinningWheelMobile() {
    const spinnigWheelMobileDiv = await fetchUtils.loadHtmlSnippet("/kame-house/html-snippets/spinning-wheel-mobile.html");
    //TODO use domUtils
    document.body.insertAdjacentHTML("beforeBegin", spinnigWheelMobileDiv);
  }
}

/**
 * Functionality that manipulates dom elements.
 * 
 * Anything that manipulates the dom should go through here.
 */
function DomUtils() {

  /** ------ Manipulation through plain js --------------------------------- */  
  this.setId = setId;
  this.classListAdd = classListAdd;
  this.classListRemove = classListRemove;
  this.classListToggle = classListToggle;
  this.setInnerHtml = setInnerHtml;
  this.setStyle = setStyle;
  this.setDisplay = setDisplay;
  this.setOnClick = setOnClick;
  this.getElementFromTemplate = getElementFromTemplate;
  this.getImgBtn = getImgBtn;
  
  /** ------ Manipulation through jQuery --------------------------------- */
  this.getDomNode = getDomNode;
  this.empty = empty;
  this.append = append;
  this.appendChild = appendChild;
  this.removeChild = removeChild;
  this.setAttr = setAttr;
  this.setHtml = setHtml;
  this.setClick = setClick;
  this.setVal = setVal;
  this.addClass = addClass;
  this.removeClass = removeClass;
  this.getA = getA;
  this.getBr = getBr;
  this.getDiv = getDiv;
  this.getLi = getLi;
  this.getOption = getOption;
  this.getP = getP;
  this.getSpan = getSpan;
  this.getTbody = getTbody;
  this.getTd = getTd;
  this.getTr = getTr;
  this.getTrTd = getTrTd;
  this.getButton = getButton;

  /** ------ Manipulation through plain js --------------------------------- */
  /** Set the id of an element (non jq) */
  function setId(element, id) {
    element.id = id;
  }

  /** Add a class to the element (non jq) */
  function classListAdd(element, className) {
    element.classList.add(className);
  }

  /** Remove a class from the element (non jq) */
  function classListRemove(element, className) {
    element.classList.remove(className);
  }

  /** Toggle a class on the element (non jq) */
  function classListToggle(element, className) {
    element.classList.toggle(className);
  }

  /** Set the html to the element (non jq) */
  function setInnerHtml(element, html) {
    if (html) {
      element.innerHTML = html;
    }
  }

  /** Set the style for the element (non jq) */
  function setStyle(element, styleProperty, stylePropertyValue) {
    element.style[styleProperty] = stylePropertyValue;
  }

  /** Set the display of the element (non jq) */
  function setDisplay(element, displayValue) {
    element.style.display = displayValue;
  }  

  /** Set onclick function of the element (non jq) */
  function setOnClick(element, onclickFunction) {
    element.onclick = onclickFunction;
  }  

  /**
   * Returns a new element to attach to the dom from the specified html template loaded from an html snippet.
   */
  function getElementFromTemplate(htmlTemplate) {
    let domElementWrapper = document.createElement('div');
    domElementWrapper.innerHTML = htmlTemplate;
    return domElementWrapper.firstChild;
  }

  /**
   * Create a new image using the specified config object which should have a format: 
   * {
   *    id: "",
   *    src: "",
   *    className: "",
   *    alt: "",
   *    onClick: () => {}
   * }
   */
  function getImgBtn(config) {
    let img = new Image();
    if (config.id) {
      img.id = config.id;
    }
    img.src = config.src;
    img.className = config.className;
    img.alt = config.alt;
    img.title = config.alt;
    img.onclick = config.onClick;
    return img;
  }

  /** ------ Manipulation through jQuery --------------------------------- */
  /**
   * Get DOM node from JQuery element.
   */
  function getDomNode(jqueryElement) {
    return jqueryElement.get(0);
  }
  
  /**
   * Empty the specified div.
   */
  function empty(div) {
    div.empty();
  }

  /**
   * Append the appendObject to appendTo.
   */
  function append(appendTo, appendObject) {
    appendTo.append(appendObject);
  }

  /**
   * Append the child to parent.
   */
  function appendChild(parent, child) {
    parent.appendChild(child);
  }

  /**
   * Remove the child from parent.
   */
  function removeChild(parent, child) {
    parent.removeChild(child);
  }

  /**
   * Set an attribute in an element.
   */
  function setAttr(element, attrKey, attrValue) {
    element.attr(attrKey, attrValue);
  }

  /** Set the html to the element */
  function setHtml(element, html) {
    if (html) {
      element.html(html);
    }
  }

  /**
   * Set click function in an element.
   */
  function setClick(element, clickData, clickFunction) {
    element.click(clickData, clickFunction);
  }

  /**
   * Set the value in an element. Usually used for input fields with a value property.
   */
  function setVal(element, value) {
    element.val(value);
  }

  /**
   * Add a class to an element.
   */
  function addClass(element, className) {
    element.addClass(className);
  }

  /**
   * Remove a class from an element.
   */
  function removeClass(element, className) {
    element.removeClass(className);
  }

  function getA(attr, html) {
    return getElement('a', attr, html);
  }

  function getBr() {
    return getElement('br', null, null);
  }

  function getDiv(attr, html) {
    return getElement('div', attr, html);
  }

  function getLi(attr, html) {
    return getElement('li', attr, html);
  }
  
  function getOption(attr, html) {
    return getElement('option', attr, html);
  }

  function getP(attr, html) {
    return getElement('p', attr, html);
  }

  function getSpan(attr, html) {
    return getElement('span', attr, html);
  }

  function getTbody(attr, html) {
    return getElement('tbody', attr, html);
  }

  function getTd(attr, html) {
    return getElement('td', attr, html);
  }

  /**
   * Returns a <tr> with the specified attributes and html content. 
   * Pass the attribute object such as:
   * domUtils.getTr({
   *   id: "my-id",
   *   class: "class1 class2"
   * }, htmlContent);
   */
  function getTr(attr, html) {
    return getElement('tr', attr, html);
  }

  /** Shorthand used in several places to create dynamic table rows */
  function getTrTd(html) {
    return getTr(null, getTd(null, html));
  }

  /**
   * Create a new button using the specified config object which should have a format: 
   * {
   *    attr: {
   *      id: "",
   *      class: ""
   *    },
   *    html: htmlObject,
   *    clickData: {},
   *    click: () => {}
   * }
   */
  function getButton(config) {
    let btn = getElement('button', config.attr, config.html);
    setClick(btn, config.clickData, config.click);
    return btn;
  }

  /** Create an element with the specified tag, attributes and html */
  function getElement(tagType, attr, html) {
    let element = $('<' + tagType + '>');
    setAttributes(element, attr);
    setHtml(element, html);
    return element;
  }

  /** Set the attributes to the element */
  function setAttributes(element, attr) {
    if (attr) {
      for (const [key, value] of Object.entries(attr)) {
        element.attr(`${key}`, `${value}`);
      }
    }
  }
}

/** 
 * Functionality to retrieve files from the server.
 */
 function FetchUtils() {

  this.loadHtmlSnippet = loadHtmlSnippet;
  this.getScript = getScript;

  /**
   * Load an html snippet to insert to the dom or use as a template.
   * 
   * Declare the caller function as async
   * and call this with await fetchUtils.loadHtmlSnippet(...);
   */
  async function loadHtmlSnippet(htmlSnippetPath) {
    const htmlSnippetResponse = await fetch(htmlSnippetPath);
    const htmlSnippet = await htmlSnippetResponse.text();
    return htmlSnippet;
  }

  /** Get a js script from the server. */
  function getScript(scriptPath, successCallback) { 
    $.getScript(scriptPath)
    .done((script, textStatus) => {
      consoleLog("Loaded successfully script: " + scriptPath);
      if (isFunction(successCallback)) {
        successCallback();
      }
    })
    .fail((jqxhr, settings, exception) => {
      consoleLog("Error loading script: " + scriptPath);
      consoleLog("jqxhr.readyState: " + jqxhr.readyState);
      consoleLog("jqxhr.status: " + jqxhr.status);
      consoleLog("jqxhr.statusText: " + jqxhr.statusText);
      //consoleLog("jqxhr.responseText: " + jqxhr.responseText);
      consoleLog("settings: " + settings);
      consoleLog("exception:");
      console.error(exception);
    });
  }
}

/** 
 * Functionality related to file and filename manipulation. 
 */
function FileUtils() {

  this.getShortFilename = getShortFilename;

  /** Get the last part of the absolute filename */
  // Split the filename into an array based on the path separators '/' and '\'
  function getShortFilename(filename) { return filename.split(/[\\/]+/).pop(); }
}

/** 
 * Functionality to load different modules and control the dependencies between them.
 */
function ModuleUtils() {

  this.setModuleLoaded = setModuleLoaded;
  this.waitForModules = waitForModules;
  this.loadDefaultModules = loadDefaultModules;
  this.loadWebSocketKameHouse = loadWebSocketKameHouse; 
  
  /** 
   * Object that determines which module is loaded. 
   * For example, when logger gets loaded, set modules.logger = true;
   * I use it in waitForModules() to check if a module is loaded or not.
   */
  let modules = {};

  /** Marks the specified module as loaded */
  function setModuleLoaded(moduleName) {
    consoleLog("setModuleLoaded: " + moduleName);
    modules[moduleName] = true;
  }

  /** Load default modules. */
  function loadDefaultModules() {
    loadLogger();
    loadHttpClient();
  }

  /** Load logger object. */
  function loadLogger() {
    fetchUtils.getScript("/kame-house/js/utils/logger.js", () => {
      logger = new Logger();
      setModuleLoaded("logger");
    });
  }

  /** Load httpClient. */
  function loadHttpClient() {
    fetchUtils.getScript("/kame-house/js/utils/http-client.js", () => {
      waitForModules(["logger"], () => {
        httpClient = new HttpClient();
        setModuleLoaded("httpClient");
      });
    });
  }

  /**
   * Load kamehouse websockets module.
   */
  function loadWebSocketKameHouse() {
    fetchUtils.getScript("/kame-house/js/utils/websocket-kamehouse.js", () => {
      waitForModules(["logger"], () => setModuleLoaded("webSocketKameHouse"));
    });
  }

  /** 
   * Waits until all specified modules in the moduleNames array are loaded, 
   * then executes the specified init function.
   * Use this function in the main() of each page that requires modules like logger and httpClient
   * to be loaded before the main code is executed.
   */
  async function waitForModules(moduleNames, initFunction) {
    //consoleLog("init: " + initFunction.name + ". Start waitForModules " + JSON.stringify(moduleNames) + ". modules status: " + JSON.stringify(modules));
    let areAllModulesLoaded = false;
    while (!areAllModulesLoaded) {
      //consoleLog("init: " + initFunction.name + ". Waiting waitForModules " + JSON.stringify(moduleNames) + ". modules status: " + JSON.stringify(modules));
      let isAnyModuleStillLoading = false;
      moduleNames.forEach((moduleName) => {
        if (!modules[moduleName]) {
          isAnyModuleStillLoading = true;
        }
      });
      if (!isAnyModuleStillLoading) {
        areAllModulesLoaded = true;
      }
      // SLEEP IS IN MS!!
      await sleep(15);
    }
    //consoleLog("init: " + initFunction.name + ". *** Finished *** waitForModules " + JSON.stringify(moduleNames) + ". modules status: " + JSON.stringify(modules));
    if (isFunction(initFunction)) {
      //consoleLog("Executing " + initFunction.name);
      initFunction();
    }
  }
}

/**
 * Manage generic kamehouse tabs (used for example in groot server manager).
 */
 function TabUtils() {

  this.openTab = openTab;
  this.openTabFromCookies = openTabFromCookies;

  /**
   * Open the tab specified by its id.
   */
  function openTab(selectedTabDivId, cookiePrefix) {
    // Set current-tab cookie
    cookiesUtils.setCookie(cookiePrefix + '-current-tab', selectedTabDivId);
    
    // Update tab links
    let tabLinks = document.getElementsByClassName("tab-kh-link");
    for (let i = 0; i < tabLinks.length; i++) {
      domUtils.classListRemove(tabLinks[i], "active");
    }
    let selectedTabLink = document.getElementById(selectedTabDivId + '-link');
    domUtils.classListAdd(selectedTabLink, "active");

    // Update tab content visibility
    let kamehouseTabContent = document.getElementsByClassName("tab-content-kh");
    for (let i = 0; i < kamehouseTabContent.length; i++) {
      domUtils.setDisplay(kamehouseTabContent[i], "none");
    }
    let selectedTabDiv = document.getElementById(selectedTabDivId);
    domUtils.setDisplay(selectedTabDiv, "block");
  }

  /**
   * Open the tab from cookies or the default tab if not set in the cookies.
   */
  function openTabFromCookies(cookiePrefix, defaultTab) {
    let currentTab = cookiesUtils.getCookie(cookiePrefix + '-current-tab');
    if (!currentTab || currentTab == '') {
      currentTab = defaultTab;
    }
    openTab(currentTab, cookiePrefix);
  }
}

/** 
 * Functionality to manipulate tables. 
 */
function TableUtils() {

  this.filterTableRows = filterTableRows;

  /** Filter table rows based on the specified filter string. Shouldn't filter the header row. */
  function filterTableRows(filterString, tableBodyId) {
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

  this.testLogLevel = testLogLevel;
  this.testSleep = testSleep;

  /** Test the different log levels. */
  function testLogLevel() {
    consoleLog("logger.logLevel " + logger.logLevel);
    logger.error("This is an ERROR message");
    logger.warn("This is a WARN message");
    logger.info("This is an INFO message");
    logger.debug("This is a DEBUG message");
    logger.trace("This is a TRACE message");
  }

  async function testSleep() {
    consoleLog("TEST SLEEP ------------- BEFORE " + new Date());
    await sleep(3000);
    consoleLog("TEST SLEEP ------------- AFTER  " + new Date());
  }
}

/**
 * TimeUtils utility object for manipulating time and dates.
 */
function TimeUtils() {

  this.getTimestamp = getTimestamp;
  this.convertSecondsToHsMsSs = convertSecondsToHsMsSs;

  /** Get current timestamp with client timezone. */
  function getTimestamp() {
    let newDate = new Date();
    let offsetTime = newDate.getTimezoneOffset() * -1 * 60 * 1000;
    let currentDateTime = newDate.getTime();
    return new Date(currentDateTime + offsetTime).toISOString().replace("T", " ").slice(0, 19);
  }

  /** Convert input in seconds to hh:mm:ss output. */
  function convertSecondsToHsMsSs(seconds) { return new Date(seconds * 1000).toISOString().substr(11, 8); }
}

/** Call main. */
$(document).ready(main);