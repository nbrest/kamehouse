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
const global = {};

/** Global utils in global.js */
const bannerUtils = new BannerUtils();
const collapsibleDivUtils = new CollapsibleDivUtils();
const cookiesUtils = new CookiesUtils();
const coreUtils = new CoreUtils();
const cursorUtils = new CursorUtils();
const domUtils = new DomUtils();
const fetchUtils = new FetchUtils();
const fileUtils = new FileUtils();
const moduleUtils = new ModuleUtils();
const tabUtils = new TabUtils();
const tableUtils = new TableUtils();
const testUtils = new TestUtils();
const timeUtils = new TimeUtils();

/** Global modules */
const httpClient = new HttpClient();
const logger = new Logger();

/** 
 * Core global functions mapped to their logic in coreUtils
 * Usage example: `if (isEmpty(val)) {...}` 
 */
const isEmpty = coreUtils.isEmpty;
const isFunction = coreUtils.isFunction;
const scrollToBottom = coreUtils.scrollToBottom;
const scrollToTop = coreUtils.scrollToTop;
const scrollToTopOfDiv = coreUtils.scrollToTopOfDiv;
const sleep = coreUtils.sleep;

/** 
 * ----- Global functions ------------------------------------------------------------------
 */
function mainGlobal() {
  coreUtils.loadHeaderAndFooter();
  cursorUtils.loadSpinningWheelMobile();
  logger.info("Initialized global functions");
  //testUtils.testLogLevel();
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

  const ALL_BANNERS = [];
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

  const preloadedBannerImages = [];

  /** Set random saint seiya sanctuary banner */
  function setRandomSanctuaryBanner(bannerRotateWaitMs) {
    const bannerClasses = ["banner-fuego-12-casas", "banner-sanctuary"];  
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
    const bannerDivClasses = $('#banner').attr('class');
    if (isEmpty(bannerDivClasses)) {
      return;
    }
    const currentClassList = bannerDivClasses.split(/\s+/);
    let currentBannerClass = "";
    currentClassList.forEach((currentClass) => {
      if (currentClass.startsWith("banner-")) {
        currentBannerClass = currentClass;
      }
    });
    const indexOfCurrentBannerClass = bannerClasses.indexOf(currentBannerClass);
    while (randomBannerIndex == indexOfCurrentBannerClass) {
      randomBannerIndex = Math.floor(Math.random() * bannerClasses.length);
    }
    // Update banner
    const element = document.getElementById("banner");
    bannerClasses.forEach((bannerClass) => {
      domUtils.classListRemove(element, bannerClass);
    });
    domUtils.classListAdd(element, bannerClasses[randomBannerIndex]);

    // Trigger banner animation
    const clonedElement = domUtils.cloneNode(element, true);
    domUtils.replaceChild(element.parentNode, clonedElement, element);
  }

  /** Set a random image banner from the classes list at the specified interval */
  function setRandomBannerLoop(bannerClass, bannerRotateWaitMs) {
    if (isEmpty(bannerRotateWaitMs)) {
      bannerRotateWaitMs = DEFAULT_BANNER_ROTATE_WAIT_MS;
    }
    setInterval(() => {
      setRandomBanner(bannerClass);
    }, bannerRotateWaitMs);
  }

  /** Update the server name in the banner */
  function updateServerName() {
    if (!isEmpty(global.session.server)) {
      domUtils.setHtml($("#banner-server-name"), global.session.server);
    }
  }
  
  /** Preload banner images */
  function preloadBannerImages(bannerPath, bannerArray) {
    bannerArray.forEach((bannerName) => {
      const img = domUtils.getImgBtn({
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
    const collapsibleElements = document.getElementsByClassName("collapsible-kh");
    for (const collapsibleElement of collapsibleElements) {
      collapsibleElement.click();
      collapsibleElement.click();
    }
  }

  /**
   * Set collapsible content listeners.
   */
  function setCollapsibleContent() {
    const collapsibleElements = document.getElementsByClassName("collapsible-kh");
    for (const collapsibleElement of collapsibleElements) {
      collapsibleElement.removeEventListener("click", collapsibleContentListener);
      collapsibleElement.addEventListener("click", collapsibleContentListener);
    }
  }

  /**
   * Function to toggle height of the collapsible elements from null to it's scrollHeight.
   */
  function collapsibleContentListener() {
    // Can't use self here, need to use this. Also can't use an annonymous function () => {}
    domUtils.classListToggle(this, "collapsible-kh-active");
    const content = this.nextElementSibling;
    if (content.style.maxHeight != 0) {
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

  this.isEmpty = isEmpty;
  this.isFunction = isFunction;
  this.loadHeaderAndFooter = loadHeaderAndFooter;
  this.scrollToTopOfDiv = scrollToTopOfDiv;
  this.scrollToTop = scrollToTop;
  this.scrollToBottom = scrollToBottom;
  this.sleep = sleep;

  /** Set the global variable and set the external reference to global to be used without coreUtils. prefix */
  global.session = {};

  /** Load header and footer. */
  function loadHeaderAndFooter() {
    fetchUtils.getScript("/kame-house/js/header-footer/header-footer.js", () => renderHeaderAndFooter());
  }

  /** 
   * @deprecated(use isEmpty())
   * 
   * Checks if a variable is undefined or null, an empty array [] or an empty object {}. 
   * 
   * --- IMPORTANT --- 
   * DEPRECATED: This method performs poorly with large objects. For large playlists (3000 elements) this comparison
   * takes more than 1 seconds causing a lag in the entire view. Use it for objects that I don't expect
   * to be large and be aware of performance issues that can be caused from using it.
   * 
   * For better performance, use isEmpty() when that check is enough.
   * 
   * Keeping the definition so I don't attempt to do the same later down the track.
   */
   function isEmptyDeprecated(val) {
    const isUndefinedOrNull = isEmpty(val);
    const isEmptyString = !isUndefinedOrNull && val === "";
    const isEmptyArray = !isUndefinedOrNull && Array.isArray(val) && val.length <= 0;
    const isEmptyObject = !isUndefinedOrNull && Object.entries(val).length === 0 && val.constructor === Object;
    return isUndefinedOrNull || isEmptyString || isEmptyArray || isEmptyObject;
  }

  /** Checks if a variable is undefined or null. */
  function isEmpty(val) {
    return val === undefined || val == null;
  }

  /** Returns true if the parameter variable is a fuction. */
  function isFunction(expectedFunction) {
    return expectedFunction instanceof Function;
  } 

  /** 
   * Scroll the specified div to it's top.
   * This method doesn't scroll the entire page, it scrolls the scrollable div to it's top.
   * To scroll the page to the top of a particular div, use scrollToTop()
   */
  function scrollToTopOfDiv(divId) {
    const divToScrollToTop = '#' + divId;
    $(divToScrollToTop).animate({
      scrollTop: 0
    }, '10');
  }

  /** 
   * Scroll the window to the top of a particular div or to the top of the body if no div specified.
   */
  function scrollToTop(divId) {
    let scrollPosition;
    if (isEmpty(divId)) {
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
  function scrollToBottom(divId) {
    let scrollPosition;
    if (isEmpty(divId)) {
      scrollPosition = document.body.scrollHeight;
    } else {
      const jqDivId = '#' + divId;
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
  function sleep(ms) { 
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
    const name = cookieName + "=";
    const decodedCookie = decodeURIComponent(document.cookie);
    const cookiesArray = decodedCookie.split(';');
    for (const cookieElement of cookiesArray) {
      let cookie = cookieElement;
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
    if (!isEmpty(expiryDays)) {
      const expiriyDate = new Date();
      expiriyDate.setTime(expiriyDate.getTime() + (expiryDays * 24 * 60 * 60 * 1000));
      const expires = "expires=" + expiriyDate.toUTCString();
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
    domUtils.insertBeforeBegin(spinnigWheelMobileDiv);
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
  this.setAttribute = setAttribute;
  this.setValue = setValue;
  this.classListAdd = classListAdd;
  this.classListRemove = classListRemove;
  this.classListToggle = classListToggle;
  this.setInnerHtml = setInnerHtml;
  this.setStyle = setStyle;
  this.setDisplay = setDisplay;
  this.setOnClick = setOnClick;
  this.getElementFromTemplate = getElementFromTemplate;
  this.getImgBtn = getImgBtn;
  this.insertBeforeBegin = insertBeforeBegin;
  this.replaceChild = replaceChild;
  this.appendChild = appendChild;
  this.removeChild = removeChild;
  this.insertBefore = insertBefore;
  this.cloneNode = cloneNode;

  /** ------ Manipulation through jQuery --------------------------------- */
  this.getDomNode = getDomNode;
  this.empty = empty;
  this.load = load;
  this.detach = detach;
  this.prepend = prepend;
  this.append = append;
  this.replaceWith = replaceWith;
  this.setAttr = setAttr;
  this.setHtml = setHtml;
  this.setText = setText;
  this.setClick = setClick;
  this.setVal = setVal;
  this.addClass = addClass;
  this.removeClass = removeClass;
  this.toggle = toggle;
  this.getA = getA;
  this.getBr = getBr;
  this.getDiv = getDiv;
  this.getInput = getInput;
  this.getLabel = getLabel;
  this.getLi = getLi;
  this.getOption = getOption;
  this.getP = getP;
  this.getSelect = getSelect;
  this.getSpan = getSpan;
  this.getTbody = getTbody;
  this.getTextArea = getTextArea;
  this.getTd = getTd;
  this.getTr = getTr;
  this.getTrTd = getTrTd;
  this.getButton = getButton;

  /** ------ Manipulation through plain js --------------------------------- */
  /** Set the id of an element (non jq) */
  function setId(element, id) {
    element.id = id;
  }

  /** Set an attribute of an element (non jq) */
  function setAttribute(element, attrKey, attrVal) {
    element.setAttribute(attrKey, attrVal);
  }

  /** Set the value of an element (non jq) */
  function setValue(element, val) {
    element.value = val;
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
    if (!isEmpty(html)) {
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
    const domElementWrapper = document.createElement('div');
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
    const img = new Image();
    if (!isEmpty(config.id)) {
      img.id = config.id;
    }
    img.src = config.src;
    img.className = config.className;
    img.alt = config.alt;
    img.title = config.alt;
    img.onclick = config.onClick;
    return img;
  }

  /** Insert the html before the body */
  function insertBeforeBegin(html) {
    document.body.insertAdjacentHTML("beforeBegin", html);
  }

  /** Replace the old child with the new one in the parent */
  function replaceChild(parentNode, newChild, oldChild) {
    parentNode.replaceChild(newChild, oldChild);
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
   * Insert the new node under the parent.
   */
  function insertBefore(parent, newNode, nextSibling) {
    parent.insertBefore(newNode, nextSibling);
  }

  /**
   * Clone a node.
   */
   function cloneNode(nodeToClone, deep) {
    if (isEmpty(deep)) {
      deep = false;
    }
    return nodeToClone.cloneNode(deep);
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
   * Load the specified htmlPath into the div.
   */
  function load(divToLoadTo, htmlPath, successCallback) {
    if (isFunction(successCallback)) {
      divToLoadTo.load(htmlPath, successCallback);
    } else {
      divToLoadTo.load(htmlPath);
    }
  }

  /**
   * Detach the specified element from the dom.
   */
   function detach(elementToDetach) {
    elementToDetach.detach();
  }

  /**
   * Prepend the prependObject to prependTo.
   */
   function prepend(prependTo, prependObject) {
    prependTo.prepend(prependObject);
  }

  /**
   * Append the appendObject to appendTo.
   */
  function append(appendTo, appendObject) {
    appendTo.append(appendObject);
  }

  /**
   * Replaces the specified dom element with the 
   */
  function replaceWith(elementToReplace, replacement) {
    elementToReplace.replaceWith(replacement);
  }

  /**
   * Set an attribute in an element.
   */
  function setAttr(element, attrKey, attrValue) {
    element.attr(attrKey, attrValue);
  }

  /** Set the html to the element */
  function setHtml(element, html) {
    if (!isEmpty(html)) {
      element.html(html);
    }
  }

  /** Set the text to the element */
  function setText(element, text) {
    if (!isEmpty(text)) {
      element.text(text);
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

  /** Toggle the visibility of all element that have the specified className */
  function toggle(className) {
    $('.' + className).toggle();
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

  function getInput(attr, html) {
    return getElement('input', attr, html);
  }

  function getLabel(attr, html) {
    return getElement('label', attr, html);
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

  function getSelect(attr, html) {
    return getElement('select', attr, html);
  }

  function getSpan(attr, html) {
    return getElement('span', attr, html);
  }

  function getTbody(attr, html) {
    return getElement('tbody', attr, html);
  }

  function getTextArea(attr, html) {
    return getElement('textarea', attr, html);
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
    const btn = getElement('button', config.attr, config.html);
    setClick(btn, config.clickData, config.click);
    return btn;
  }

  /** Create an element with the specified tag, attributes and html */
  function getElement(tagType, attr, html) {
    const element = $('<' + tagType + '>');
    setAttributes(element, attr);
    setHtml(element, html);
    return element;
  }

  /** Set the attributes to the element */
  function setAttributes(element, attr) {
    if (!isEmpty(attr)) {
      for (const [key, value] of Object.entries(attr)) {
        element.attr(key, value);
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
    return htmlSnippetResponse.text();
  }

  /** Get a js script from the server. */
  function getScript(scriptPath, successCallback) { 
    $.getScript(scriptPath)
    .done((script, textStatus) => {
      logger.debug("Loaded successfully script: " + scriptPath);
      if (isFunction(successCallback)) {
        successCallback();
      }
    })
    .fail((jqxhr, settings, exception) => {
      logger.info("Error loading script: " + scriptPath);
      logger.info("jqxhr.readyState: " + jqxhr.readyState);
      logger.info("jqxhr.status: " + jqxhr.status);
      logger.info("jqxhr.statusText: " + jqxhr.statusText);
      //logger.info("jqxhr.responseText: " + jqxhr.responseText);
      logger.info("settings: " + settings);
      logger.info("exception:");
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
  this.loadWebSocketKameHouse = loadWebSocketKameHouse; 
  
  /** 
   * Object that determines which module is loaded. 
   * For example, when logger gets loaded, set modules.logger = true;
   * I use it in waitForModules() to check if a module is loaded or not.
   */
  const modules = {};

  /** Marks the specified module as loaded */
  function setModuleLoaded(moduleName) {
    logger.debug("setModuleLoaded: " + moduleName);
    modules[moduleName] = true;
  }

  /**
   * Load kamehouse websockets module.
   */
  function loadWebSocketKameHouse() {
    fetchUtils.getScript("/kame-house/js/utils/websocket-kamehouse.js", () => {
      setModuleLoaded("webSocketKameHouse");
    });
  }

  /** 
   * Waits until all specified modules in the moduleNames array are loaded, 
   * then executes the specified init function.
   * Use this function in the main() of each page that requires modules like logger and httpClient
   * to be loaded before the main code is executed.
   */
  async function waitForModules(moduleNames, initFunction) {
    //logger.debug("init: " + initFunction.name + ". Start waitForModules " + JSON.stringify(moduleNames) + ". modules status: " + JSON.stringify(modules));
    let areAllModulesLoaded = false;
    while (!areAllModulesLoaded) {
      //logger.debug("init: " + initFunction.name + ". Waiting waitForModules " + JSON.stringify(moduleNames) + ". modules status: " + JSON.stringify(modules));
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
    //logger.debug("init: " + initFunction.name + ". *** Finished *** waitForModules " + JSON.stringify(moduleNames) + ". modules status: " + JSON.stringify(modules));
    if (isFunction(initFunction)) {
      //logger.debug("Executing " + initFunction.name);
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
    const tabLinks = document.getElementsByClassName("tab-kh-link");
    for (const tabLink of tabLinks) {
      domUtils.classListRemove(tabLink, "active");
    }
    const selectedTabLink = document.getElementById(selectedTabDivId + '-link');
    domUtils.classListAdd(selectedTabLink, "active");

    // Update tab content visibility
    const kamehouseTabContent = document.getElementsByClassName("tab-content-kh");
    for (const kamehouseTabContentItem of kamehouseTabContent) {
      domUtils.setDisplay(kamehouseTabContentItem, "none");
    }
    const selectedTabDiv = document.getElementById(selectedTabDivId);
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
  this.filterTableRowsByColumn = filterTableRowsByColumn;
  this.sortTable = sortTable;
  this.limitRows = limitRows;

  /** 
   * Filter table rows based on the specified filter string. Shouldn't filter the header row. 
   * Toggles a maximum of maxRows.
   * Set skipHiddenRows to process only currently visible rows.   
   */
  function filterTableRows(filterString, tableBodyId, maxRows, skipHiddenRows) {
    const table = document.getElementById(tableBodyId);
    const rows = table.rows;
    if (isEmpty(maxRows) || maxRows == "" || maxRows == "all") {
      maxRows = rows.length;
    }

    const regex = getRegex(filterString);
    const tableRows = $("#" + tableBodyId + " tr");
    tableRows.filter(function () {
      const tr = this;
      const classList = tr.classList.value;
      if (isEmpty(classList) || !classList.includes("table-kh-header")) {
        // Filter if it's not the header row
        const trText = $(tr).text().toLowerCase();
        let shouldDisplayRow = regex.test(trText);
        if (skipHiddenRows && isHiddenRow(tr)) {
          shouldDisplayRow = false;
        }
        if (maxRows > 0) {
          $(tr).toggle(shouldDisplayRow);
          maxRows--;
        }
      }
    });
  }

  /** 
   * Filter table rows by a specific column based on the specified filter string. Shouldn't filter the header row. 
   * Toggles a maximum of maxRows.
   * Set skipHiddenRows to process only currently visible rows.
   */
  function filterTableRowsByColumn(filterString, tableBodyId, columnIndex, maxRows, skipHiddenRows) {
    const table = document.getElementById(tableBodyId);
    const rows = table.rows;
    if (isEmpty(columnIndex)) {
      logger.trace("columnIndex not set. Using 0");
      columnIndex = 0;
    }
    if (isEmpty(maxRows) || maxRows == "" || maxRows == "all") {
      maxRows = rows.length;
    }
    const regex = getRegex(filterString);
    const tableRows = $("#" + tableBodyId + " tr");
    tableRows.filter(function () {
      const tr = this;
      const classList = tr.classList.value;
      if (isEmpty(classList) || !classList.includes("table-kh-header")) {
        // Filter if it's not the header row
        const td = tr.getElementsByTagName("td")[columnIndex];
        const tdText = $(td).text().toLowerCase();
        let shouldDisplayRow = regex.test(tdText);
        if (skipHiddenRows && isHiddenRow(tr)) {
          shouldDisplayRow = false;
        }
        if (maxRows > 0) {
          $(tr).toggle(shouldDisplayRow);
          maxRows--;
        }
      }
    });
  }

  /**
   * Get the regex to filter the rows.
   */
  function getRegex(filterString) {
    filterString = filterString.toLowerCase();
    try {
      if (isQuotedString(filterString)) {
        filterString = removeQuotes(filterString);
      } else {
        filterString = addAsterisksBetweenAllCharsToRegex(filterString);
      }
      return new RegExp(filterString);
    } catch (error) {
      logger.error("Error creating regex from filter string " + filterString);
      return /""/;
    }
  }

  /**
   * Check for double quotes at the beginning and end of the string.
   */
   function isQuotedString(string) {
    return string.startsWith("\"") && string.endsWith("\"");
  }

  /**
   * Remove the double quotes from the string.
   */
  function removeQuotes(string) {
    return string.substring(1, string.length-1);
  }

  /**
   * Adds .* between each character to expand the matching criteria of the regex.
   */
  function addAsterisksBetweenAllCharsToRegex(string) {
    return string.split('').join('.*').replace(/\s/g, '');
  }

  /**
   * Check if it's a hidden row.
   */
  function isHiddenRow(tr) {
    return tr.style.display == "none";
  }

  /**
   * Sort the table by the specified column number.
   * 
   * getComparatorFunction() determines which type of sorting will be used depending on the dataType.
   * The dataType is the same type used in crud-manager.js to determine the column types.
   * For some columns, such as select, there's a separate sortType property set to determine the sorting type.
   * The default sorting type if not specified will be lexicographically, 
   * which also works for dates formatted as yyyy-mm-dd and timestamps formatted as yyyy-mm-dd hh:mm:ss 
   * 
   * sortDirection can either be "asc" or "desc"
   */
  function sortTable(tableId, columnNumber, dataType, initialSortDirection) {
    logger.trace("tableId " + tableId);
    logger.trace("columnNumber " + columnNumber);
    logger.trace("dataType " + dataType);
    logger.trace("initialSortDirection " + initialSortDirection);

    const table = document.getElementById(tableId);
    const rows = table.rows;
    const MAX_SORTING_CYCLES = 100000;
    const compareFunction = getComparatorFunction(dataType);

    let numSortingCycles = 0;
    let sorting = true;
    let swapRows = false;
    let swapCount = 0;
    let currentRowIndex;
    let currentRow = null;
    let nextRow = null;
    let sortDirection = "asc";

    initialSortDirection = initSortDirection(initialSortDirection);

    while (sorting) {
      sorting = false;

      for (currentRowIndex = 2; currentRowIndex < (rows.length - 1); currentRowIndex++) {
        swapRows = false;
        currentRow = rows[currentRowIndex].getElementsByTagName("td")[columnNumber];
        nextRow = rows[currentRowIndex + 1].getElementsByTagName("td")[columnNumber];        
        if (shouldSwapRows(currentRow, nextRow, sortDirection, compareFunction)) {
          swapRows = true;
          break;
        }
      }

      if (swapRows) {
        domUtils.insertBefore(rows[currentRowIndex].parentNode, rows[currentRowIndex + 1], rows[currentRowIndex]);
        sorting = true;
        swapCount++;
      } else {
        if (shouldSwapDirection(swapCount, sortDirection, initialSortDirection)) {
          // if no sorting was done, swap sort direction, and sort reversely.
          sortDirection = "desc";
          sorting = true;
        }
      }
      if (numSortingCycles > MAX_SORTING_CYCLES) {
        sorting = false;
        logger.error("Ending sorting after " + MAX_SORTING_CYCLES + " sorting cycles. Something is VERY likely off with the sorting function. Breaking either infinite loop or a very inefficient sorting");
      }
      numSortingCycles++;
    }
    logger.trace("numSortingCycles " + numSortingCycles);
  }

  /**
   * Check if it should swap the sorting direction.
   */
  function shouldSwapDirection(swapCount, sortDirection, initialSortDirection) {
    return swapCount == 0 && sortDirection == "asc" && initialSortDirection != "asc";
  }

  /**
   * Set initial direction.
   */
  function initSortDirection(initialSortDirection) {
    if (initialSortDirection != "asc" && initialSortDirection != "desc") {
      return null;
    }
    return initialSortDirection;
  }

  /**
   * Returns true if the current and next rows need to be swapped.
   */
  function shouldSwapRows(currentRow, nextRow, sortDirection, compareFunction) {
    return compareFunction(currentRow, nextRow, sortDirection);
  }

  /**
   * Get the sorting function depending on the data type.
   */
  function getComparatorFunction(dataType) {
    if (dataType == "number" || dataType == "id") {
      logger.trace("Using compareNumerically");
      return compareNumerically;
    }

    logger.trace("Using compareLexicographically");
    return compareLexicographically;
  }

  /**
   * Returns true if first parameter is higher than second lexicographically.
   */
  function compareLexicographically(currentRow, nextRow, sortDirection) {
    const currentRowVal = currentRow.innerHTML.toLowerCase();
    const nextRowVal = nextRow.innerHTML.toLowerCase();
    if (sortDirection == "asc") {
      return currentRowVal > nextRowVal;
    } else {
      return currentRowVal < nextRowVal;
    }
  }

  /**
   * Returns true if first parameter is higher than second numerically.
   */
  function compareNumerically(currentRow, nextRow, sortDirection) {
    const currentRowVal = parseInt(currentRow.innerHTML);
    const nextRowVal = parseInt(nextRow.innerHTML);
    if (sortDirection == "asc") {
      return currentRowVal > nextRowVal;
    } else {
      return currentRowVal < nextRowVal;
    }
  }

  /**
   * Limit the number of rows displayed on the table. 
   * Set skipHiddenRows to process only currently visible rows.
   */
  function limitRows(tableId, maxRows, skipHiddenRows) {
    const table = document.getElementById(tableId);
    const rows = table.rows;
    if (isEmpty(maxRows) || maxRows == "" || maxRows == "all") {
      maxRows = rows.length;
    }
    let shownRows = 0;
    for (let i = 1; i < rows.length; i++) { 
      if (skipHiddenRows && isHiddenRow(rows[i])) {
        continue;
      }
      if (shownRows <= maxRows) {
        domUtils.setDisplay(rows[i], "table-row");
        shownRows++;
      } else {
        domUtils.setDisplay(rows[i], "none");
      }
    }
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
    console.log("logger.getLogLevel(): " + logger.getLogLevel());
    logger.error("This is an ERROR message");
    logger.warn("This is a WARN message");
    logger.info("This is an INFO message");
    logger.debug("This is a DEBUG message");
    logger.trace("This is a TRACE message");
  }

  async function testSleep() {
    logger.info("TEST SLEEP ------------- BEFORE " + new Date());
    await sleep(3000);
    logger.info("TEST SLEEP ------------- AFTER  " + new Date());
  }
}

/**
 * TimeUtils utility object for manipulating time and dates.
 */
function TimeUtils() {

  this.getTimestamp = getTimestamp;
  this.convertSecondsToHsMsSs = convertSecondsToHsMsSs;
  this.getDateWithTimezoneOffset = getDateWithTimezoneOffset;
  this.getDateFromEpoch = getDateFromEpoch;
  this.isValidDate = isValidDate;

  /** Get timestamp with client timezone for the specified date or current date if null. */
  function getTimestamp(date) {
    if (isEmpty(date)) {
      date = new Date();
    }
    const offsetTime = date.getTimezoneOffset() * -1 * 60 * 1000;
    const currentDateTime = date.getTime();
    return new Date(currentDateTime + offsetTime).toISOString().replace("T", " ").slice(0, 19);
  }
  
  /** Get current timestamp with client timezone. */
  function getDateWithTimezoneOffset(date) {
    const offsetTime = date.getTimezoneOffset() * -1 * 60 * 1000;
    const currentDateTime = date.getTime();
    return new Date(currentDateTime + offsetTime);
  }

  /** Convert input in seconds to hh:mm:ss output. */
  function convertSecondsToHsMsSs(seconds) { 
    return new Date(seconds * 1000).toISOString().substr(11, 8); 
  }

  /**
   * Get the date from an epoch string.
   */
  function getDateFromEpoch(epoch) {
    return new Date(parseInt(epoch));
  }

  /**
   * Checks if it's a valid date.
   */
  function isValidDate(date) {
    return date instanceof Date && !isNaN(date);
  }
}

/**
 * Log object to perform logging to the console on the frontend side.
 * 
 * Dependencies: timeUtils.
 * 
 * @author nbrest
 */
 function Logger() {

  this.setLogLevel = setLogLevel;
  this.getLogLevel = getLogLevel;
  this.error = error;
  this.warn = warn;
  this.info = info;
  this.debug = debug;
  this.trace = trace;
  this.logApiError = logApiError;

  /**
   * Log levels:
   * 
   * 0: ERROR
   * 1: WARN
   * 2: INFO
   * 3: DEBUG
   * 4: TRACE
   * 
   * Default log level: INFO (2)
   */
  let logLevelNumber = 2;

  /**
   * Set the log level for the console in numeric value, based on the mapping shown above.
   */
  function setLogLevel(levelNumber) {
    logLevelNumber = levelNumber;
  }

  /**
   * Get the log level for the console in numeric value, based on the mapping shown above.
   */
  function getLogLevel() {
    return logLevelNumber;
  }

  /** Log a specified message with the specified logging level. */
  function log(logLevel, message) {
    if (isEmpty(logLevel)) {
      console.error("Invalid use of log(logLevel, message) function. LogLevel is missing.");
      return;
    }
    if (!message) {
      console.error("Invalid use of log(logLevel, message) function. Message is empty");
      return;
    }
    const logLevelUpperCase = logLevel.toUpperCase();
    let logEntry = "";
    logEntry = timeUtils.getTimestamp() + " - [" + logLevelUpperCase + "] - " + message;
    if (logLevelUpperCase == "ERROR") {
      console.error(logEntry);
      logToDebugMode(logEntry);
    }
    if (logLevelUpperCase == "WARN" && logLevelNumber >= 1) {
      console.warn(logEntry);
      logToDebugMode(logEntry);
    }
    if (logLevelUpperCase == "INFO" && logLevelNumber >= 2) {
      console.info(logEntry);
      logToDebugMode(logEntry);
    }
    if (logLevelUpperCase == "DEBUG" && logLevelNumber >= 3) {
      // Use debug to log behavior, such as executing x method, selected x playlist, etc.
      console.debug(logEntry);
      logToDebugMode(logEntry);
    }
    if (logLevelUpperCase == "TRACE" && logLevelNumber >= 4) {
      // Use trace to log content such as responses from api calls. But use debug or info logger. trace prints a useless stack trace in the console that doesn't help.
      console.info(logEntry);
      logToDebugMode(logEntry);
    }
  }

  /** Log an error message */
  function error(message) { log("ERROR", message); }

  /** Log a warn message */
  function warn(message) { log("WARN", message); }

  /** Log an info message */
  function info(message) { log("INFO", message); }

  /** Log a debug message */
  function debug(message) { log("DEBUG", message); }

  /** Log a trace message */
  function trace(message) { log("TRACE", message); }

  /**
   * Log the entry into the debug mode console log table.
   */
  function logToDebugMode(logEntry) {
    const DEBUG_MODE_LOG_SIZE = 20;
    const debugModeConsoleLog = document.getElementById("debug-mode-console-log-entries");
    if (!isEmpty(debugModeConsoleLog)) {
      // Remove first log N entries
      let logEntriesSize = debugModeConsoleLog.childElementCount;
      while (logEntriesSize > DEBUG_MODE_LOG_SIZE) {
        domUtils.removeChild(debugModeConsoleLog, debugModeConsoleLog.firstChild);
        logEntriesSize = debugModeConsoleLog.childElementCount;
      }
      // Add new log entry
      domUtils.append($("#debug-mode-console-log-entries"), getLogEntryListItem(logEntry));
      // Scroll down log div
      debugModeLogScroll();
    }
  }

  /**
   * Scroll to the last entries of the console log.
   */
  function debugModeLogScroll() {
    const height = $("#debug-mode-console-log-entries").get(0).scrollHeight;
    $("#debug-mode-console-log-entries").animate({
      scrollTop: height
    }, 100);
  }
  
  function getLogEntryListItem(logEntry) {
    const li = domUtils.getLi({}, null);
    domUtils.setText(li, logEntry);
    return li;
  }

  /**
   * Log an api call error to the console.
   */
  function logApiError(responseBody, responseCode, responseDescription, message) {
    if (isEmpty(message) || message == "") {
      message = "Error executing api call";
    }
    const errorMessage = message + ": responseBody=" + responseBody + "; responseCode=" + responseCode + "; responseDescription=" + responseDescription + ";";
    error(errorMessage);
  }
}

/**
 * HttpClient object to perform http calls.
 * 
 * Dependencies: logger.
 * 
 * @author nbrest
 */
 function HttpClient() {

  this.get = get;
  this.put = put;
  this.post = post;
  this.delete = deleteHttp;
  this.getUrlEncodedHeaders = getUrlEncodedHeaders;
  this.getApplicationJsonHeaders = getApplicationJsonHeaders;

  /** Execute an http GET request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription) 
   * and errorCallback(responseBody, responseCode, responseDescription) */
  function get(url, requestHeaders, successCallback, errorCallback, data) {
    httpRequest("GET", url, requestHeaders, null, successCallback, errorCallback, data)
  }

  /** Execute an http PUT request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription) 
   * and errorCallback(responseBody, responseCode, responseDescription) */
  function put(url, requestHeaders, requestBody, successCallback, errorCallback, data) {
    httpRequest("PUT", url, requestHeaders, requestBody, successCallback, errorCallback, data)
  }

  /** Execute an http POST request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription) 
   * and errorCallback(responseBody, responseCode, responseDescription) */
  function post(url, requestHeaders, requestBody, successCallback, errorCallback, data) {
    httpRequest("POST", url, requestHeaders, requestBody, successCallback, errorCallback, data)
  }

  /** Execute an http DELETE request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription) 
   * and errorCallback(responseBody, responseCode, responseDescription) */
  function deleteHttp(url, requestHeaders, requestBody, successCallback, errorCallback, data) {
    httpRequest("DELETE", url, requestHeaders, requestBody, successCallback, errorCallback, data)
  }

  /** Execute an http request with the specified http method. 
   * Implement and pass successCallback(responseBody, responseCode, responseDescription) 
   * and errorCallback(responseBody, responseCode, responseDescription)
   * Don't call this method directly, instead call the wrapper get(), post(), put(), delete() */
  function httpRequest(httpMethod, url, requestHeaders, requestBody, successCallback, errorCallback, customData) {
    if (isEmpty(requestBody)) {
      $.ajax({
        type: httpMethod,
        url: url,
        headers: requestHeaders,
        success: (data, status, xhr) => processSuccess(data, status, xhr, successCallback, customData),
        error: (jqXhr, textStatus, errorMessage) => processError(jqXhr, textStatus, errorMessage, errorCallback, customData, url)
      });
    } else {
      $.ajax({
        type: httpMethod,
        url: url,
        data: JSON.stringify(requestBody),
        headers: requestHeaders,
        success: (data, status, xhr) => processSuccess(data, status, xhr, successCallback, customData),
        error: (jqXhr, textStatus, errorMessage) => processError(jqXhr, textStatus, errorMessage, errorCallback, customData, url)
      });
    }
  }

  /** Process a successful response from the api call */
  function processSuccess(data, status, xhr, successCallback, customData) {
    /**
     * data: response body
     * status: success/error
     * xhr: {
     *    readyState: 4
     *    responseText: response body as text
     *    responseJson: response body as json
     *    status: numeric status code
     *    statusText: status code as text (success/error)
     * }
     */
    const responseBody = data;
    const responseCode = xhr.status;
    const responseDescription = xhr.statusText;
    successCallback(responseBody, responseCode, responseDescription, customData);
  }

  /** Process an error response from the api call */
  function processError(jqXhr, textStatus, errorMessage, errorCallback, data, url) {
     /**
      * jqXhr: {
      *    readyState: 4
      *    responseText: response body as text
      *    status: numeric status code
      *    statusText: status code as text (success/error)
      * }
      * textStatus: response body
      * errorMessage: (so far came empty, might have the response body)
      */
     const responseBody = jqXhr.responseText;
     const responseCode = jqXhr.status;
     const responseDescription = jqXhr.statusText;
     logger.error("API call error. Url: " + url + " jqXhr: " + JSON.stringify(jqXhr));
     errorCallback(responseBody, responseCode, responseDescription, data);
  }

  /** Get request headers object with Url Encoded content type. */
  function getUrlEncodedHeaders() {
    const requestHeaders = {};
    requestHeaders.Accept = '*/*';
    requestHeaders['Content-Type'] = "application/x-www-form-urlencoded";
    logger.trace("request headers: " + JSON.stringify(requestHeaders));
    return requestHeaders;
  }

  /** Get request headers object with application json content type. */
  function getApplicationJsonHeaders() {
    const requestHeaders = {};
    requestHeaders.Accept = '*/*';
    requestHeaders['Content-Type'] = 'application/json';
    logger.trace("request headers: " + JSON.stringify(requestHeaders));
    return requestHeaders;
  }
}

/** Call main. */
$(document).ready(mainGlobal);