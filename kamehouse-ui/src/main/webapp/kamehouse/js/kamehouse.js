/**
 * KameHouse js framework.
 * 
 * To pass data to the KameHouse object call the script with id="kamehouse-data" tag and set the
 * custom data- attributes: 
 * 
 *  data-skip-header=[true|false]
 *  data-skip-footer=[true|false]
 * 
 * Dependencies: jquery
 * 
 * @author nbrest
 */
const kameHouse = new KameHouse();

/**
 * Global KameHouse js object containing most of the functionality.
 */
function KameHouse() {
  this.init = init;
  this.addExtension = addExtension;
  this.addPlugin = addPlugin;

  /**
   * Init kamehouse.js
   */
  function init() {
    this.session = {};
    this.plugin = {};
    this.extension = {};

    /** core modules */
    this.core = new KameHouseCoreFunctions();
    this.logger = new KameHouseLogger();
    this.http = new KameHouseHttpClient();

    /** utils */
    this.util = {};
    this.util.banner = new KameHouseBannerUtils();
    this.util.collapsibleDiv = new KameHouseCollapsibleDivUtils();
    this.util.cookies = new KameHouseCookiesUtils();
    this.util.cursor = new KameHouseCursorUtils();
    this.util.dom = new KameHouseDomUtils();
    this.util.fetch = new KameHouseFetchUtils();
    this.util.file = new KameHouseFileUtils();
    this.util.mobile = new KameHouseMobileUtils();
    this.util.module = new KameHouseModuleUtils();
    this.util.tab = new KameHouseTabUtils();
    this.util.table = new KameHouseTableUtils();
    this.util.test = new KameHouseTestUtils();
    this.util.time = new KameHouseTimeUtils();

    /**
     * Init core modules and utils
     */
    this.logger.init();
    this.logger.info("Started initializing kamehouse.js")
    this.util.mobile.init();
    this.core.loadSession();
    this.core.loadHeader();
    this.core.loadFooter();
    this.util.cursor.loadSpinningWheelMobile();
    //kameHouse.util.test.testLogLevel();
    //kameHouse.util.test.testSleep();
    this.logger.info("Finished initializing kamehouse.js");
  }

  /**
   * Extensions live externally to the kamehouse js/css/html bundle. Examples: kamehouse-mobile.js, kamehouse-groot.js, newsletter.js. They need to implement a load() function that initializes the extension.
   */
  function addExtension(extensionName, extension) {
    kameHouse.logger.info("Adding extension " + extensionName);
    this.extension[extensionName] = extension;
    extension.load();
  }

  /**
   * Plugins live in the kamehouse js/css/html bundle, but are not loaded by default. Examples kamehouse-debugger.js, kamehouse-modal.js. They need to implement a load() function that initializes the plugin.
   */
  function addPlugin(pluginName, plugin) {
    kameHouse.logger.info("Adding plugin " + pluginName);
    this.plugin[pluginName] = plugin;
    plugin.load();
  }
}

/**
 * BannerUtils to manipulate banners.
 */
function KameHouseBannerUtils() {

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
    if (kameHouse.core.isEmpty(bannerDivClasses)) {
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
      kameHouse.util.dom.classListRemove(element, bannerClass);
    });
    kameHouse.util.dom.classListAdd(element, bannerClasses[randomBannerIndex]);

    // Trigger banner animation
    const clonedElement = kameHouse.util.dom.cloneNode(element, true);
    kameHouse.util.dom.replaceChild(element.parentNode, clonedElement, element);
  }

  /** Set a random image banner from the classes list at the specified interval */
  function setRandomBannerLoop(bannerClass, bannerRotateWaitMs) {
    if (kameHouse.core.isEmpty(bannerRotateWaitMs)) {
      bannerRotateWaitMs = DEFAULT_BANNER_ROTATE_WAIT_MS;
    }
    setInterval(() => {
      setRandomBanner(bannerClass);
    }, bannerRotateWaitMs);
  }

  /** Update the server name in the banner */
  function updateServerName() {
    if (!kameHouse.core.isEmpty(kameHouse.session.server)) {
      kameHouse.util.dom.setHtml($("#banner-server-name"), kameHouse.session.server);
    }
  }
  
  /** Preload banner images */
  function preloadBannerImages(bannerPath, bannerArray) {
    bannerArray.forEach((bannerName) => {
      const img = kameHouse.util.dom.getImgBtn({
        src: '/kame-house/img/banners/' + bannerPath + '/' + bannerName + '.jpg'
      });
      preloadedBannerImages.push(img);
    });
  }
}

/**
 * Utility to manipulate collapsible divs.
 */
function KameHouseCollapsibleDivUtils() {

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
    kameHouse.util.dom.classListToggle(this, "collapsible-kh-active");
    const content = this.nextElementSibling;
    if (content.style.maxHeight != 0) {
      kameHouse.util.dom.setStyle(content, "maxHeight", null);
    } else {
      kameHouse.util.dom.setStyle(content, "maxHeight", content.scrollHeight + "px");
    }
  }
}

/**
 * Functionality to handle cookies.
 */
function KameHouseCookiesUtils() {

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
    if (!kameHouse.core.isEmpty(expiryDays)) {
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
function KameHouseCursorUtils() {

  this.setCursorWait = setCursorWait;
  this.setCursorDefault = setCursorDefault;
  this.loadSpinningWheelMobile = loadSpinningWheelMobile;

  /** Set the cursor to a wait spinning wheel */
  function setCursorWait() {
    kameHouse.util.dom.addClass($('html'), "wait");
    kameHouse.util.dom.removeClass($('#spinning-wheel-mobile-wrapper'), "hidden-kh");
  }

  /** Set the cursor to default shape */
  function setCursorDefault() {
    kameHouse.util.dom.removeClass($('html'), "wait");
    kameHouse.util.dom.addClass($('#spinning-wheel-mobile-wrapper'), "hidden-kh");
  }

  /**
   * Load the spinning wheel for mobile view.
   */
  async function loadSpinningWheelMobile() {
    const spinnigWheelMobileDiv = await kameHouse.util.fetch.loadHtmlSnippet("/kame-house/html-snippets/spinning-wheel-mobile.html");
    kameHouse.util.dom.insertBeforeBegin(spinnigWheelMobileDiv);
  }
}

/**
 * Functionality that manipulates dom elements.
 * 
 * Anything that manipulates the dom should go through here.
 */
function KameHouseDomUtils() {

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
  this.after = after;
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
    if (!kameHouse.core.isEmpty(html)) {
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
    if (!kameHouse.core.isEmpty(config.id)) {
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
   * Insert the new node after the selected node.
   */
    function after(sibling, newNode) {
      sibling.after(newNode);
    }

  /**
   * Clone a node.
   */
   function cloneNode(nodeToClone, deep) {
    if (kameHouse.core.isEmpty(deep)) {
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
    if (kameHouse.core.isFunction(successCallback)) {
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
    if (!kameHouse.core.isEmpty(html)) {
      element.html(html);
    }
  }

  /** Set the text to the element */
  function setText(element, text) {
    if (!kameHouse.core.isEmpty(text)) {
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
   * kameHouse.util.dom.getTr({
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
    if (!kameHouse.core.isEmpty(attr)) {
      for (const [key, value] of Object.entries(attr)) {
        element.attr(key, value);
      }
    }
  }
}

/** 
 * Functionality to retrieve files from the server.
 */
 function KameHouseFetchUtils() {

  this.loadHtmlSnippet = loadHtmlSnippet;
  this.loadJsonConfig = loadJsonConfig;
  this.getScript = getScript;

  /**
   * Load an html snippet to insert to the dom or use as a template.
   * 
   * Declare the caller function as async
   * and call this with await kameHouse.util.fetch.loadHtmlSnippet(...);
   */
  async function loadHtmlSnippet(htmlSnippetPath) {
    const htmlSnippetResponse = await fetch(htmlSnippetPath);
    return htmlSnippetResponse.text();
  }

  /**
   * Load a json config object.
   * 
   * Declare the caller function as async
   * and call this with await kameHouse.util.fetch.loadJsonConfig(...);
   */
   async function loadJsonConfig(jsonConfigPath) {
    const jsonConfigResponse = await fetch(jsonConfigPath);
    return jsonConfigResponse.text();
  }

  /** Get a js script from the server. */
  function getScript(scriptPath, successCallback) { 
    $.getScript(scriptPath)
    .done((script, textStatus) => {
      kameHouse.logger.debug("Loaded successfully script: " + scriptPath);
      if (kameHouse.core.isFunction(successCallback)) {
        successCallback();
      }
    })
    .fail((jqxhr, settings, exception) => {
      kameHouse.logger.info("Error loading script: " + scriptPath);
      kameHouse.logger.info("jqxhr.readyState: " + jqxhr.readyState);
      kameHouse.logger.info("jqxhr.status: " + jqxhr.status);
      kameHouse.logger.info("jqxhr.statusText: " + jqxhr.statusText);
      kameHouse.logger.trace("jqxhr.responseText: " + jqxhr.responseText);
      kameHouse.logger.info("settings: " + settings);
      kameHouse.logger.info("exception:");
      console.error(exception);
    });
  }
}

/** 
 * Functionality related to file and filename manipulation. 
 */
function KameHouseFileUtils() {

  this.getShortFilename = getShortFilename;

  /** Get the last part of the absolute filename */
  // Split the filename into an array based on the path separators '/' and '\'
  function getShortFilename(filename) { return filename.split(/[\\/]+/).pop(); }
}

/**
 * Functionality for the native mobile app.
 */
function KameHouseMobileUtils() {

  this.init = init;
  this.disableMobileOnlyElements = disableMobileOnlyElements;
  this.disableWebappOnlyElements = disableWebappOnlyElements;
  this.windowLocation = windowLocation;
  this.windowOpen = windowOpen;
  this.exec = exec;
  this.generateAndroidQrCode = generateAndroidQrCode;

  let isMobileAppStatus = false;
  
  function init() {
    loadCordovaModule();
    disableMobileOnlyElements();
    loadGlobalMobile();
  }

  function isMobileApp() {
    return isMobileAppStatus;
  }

  function loadCordovaModule() {
    const urlParams = new URLSearchParams(window.location.search);
    const mockCordova = urlParams.get('mockCordova');
    if (mockCordova) {
      cordova = {};
    }
    try {
      if (cordova) { 
        kameHouse.logger.info("cordova object is present. Running as a mobile app");
        isMobileAppStatus = true;
      }
    } catch (error) {
      kameHouse.logger.info("cordova object is not present. Running as a webapp");
      isMobileAppStatus = false;
    }
  }

  /**
   * Set the window location differently depending if running on web or mobile app.
   */
  function windowLocation(webLocation, mobileLocation) {
    return exec(
      () => {window.location=webLocation},
      () => {window.location=mobileLocation}
    )
  }

  function windowOpen(url, targetWeb) {
    return exec(
      () => {window.open(url, targetWeb)},
      () => {window.open(kameHouse.extension.mobile.core.getBackendServer() + url)}
    )
  }

  function disableMobileOnlyElements() {
    $(document).ready(() => {
      exec(
        () => { 
          kameHouse.logger.debug("Disabling mobile only elements in webapp view");
          const mobileOnlyElements = document.getElementsByClassName("kh-mobile-only");
          for (const mobileOnlyElement of mobileOnlyElements) {
            kameHouse.util.dom.classListAdd(mobileOnlyElement, "hidden-kh");
            kameHouse.util.dom.classListRemove(mobileOnlyElement, "kh-mobile-only");
          }
        }, 
        null
      );
    });
  }

  function disableWebappOnlyElements() {
    exec(
      null,
      () => {
        kameHouse.util.module.waitForModules(["kameHouseMobile"], () => {
          kameHouse.extension.mobile.core.disableWebappOnlyElements();
        });
      }
    );
  }

  function loadGlobalMobile() {
    exec(
      null,
      () => {
        kameHouse.util.fetch.getScript("/kame-house-mobile/kamehouse-mobile/js/kamehouse-mobile.js", () => {
          kameHouse.logger.info("Loaded kamehouse-mobile.js");
        }); 
      }
    );
  }

  /**
   * Executes the first parmeter function only when running on the web app.
   * Executes the second parameter function only when running as a mobile app.
   */
  function exec(webFunction, mobileFunction) {
    if (!isMobileApp()) {
      if (kameHouse.core.isFunction(webFunction)) {
        return webFunction(); 
      }
    } else {
      try {
        if (kameHouse.core.isFunction(mobileFunction)) {
          return mobileFunction(); 
        }
      } catch (error) {
        kameHouse.logger.error("Unexpected error executing mobile function. Error: " + error);
        alert("Unexpected error executing mobile function. Error: " + error)
        return null;
      }
    }
  }

  function generateAndroidQrCode(qrCodeDivId) {
    $("#" + qrCodeDivId).qrcode({
      text: 'https://kame.nicobrest.com/kame-house-mobile/kamehouse.apk',
      ecLevel: 'L',
      size: 256
    });
  }
}

/** 
 * Functionality to load different modules and control the dependencies between them.
 */
function KameHouseModuleUtils() {

  this.isModuleLoaded = isModuleLoaded;
  this.setModuleLoaded = setModuleLoaded;
  this.waitForModules = waitForModules;
  this.loadKameHouseWebSocket = loadKameHouseWebSocket; 
  
  /** 
   * Object that determines which module is loaded. 
   * For example, when logger gets loaded, set modules.logger = true;
   * I use it in waitForModules() to check if a module is loaded or not.
   */
  const modules = {};

  /** Marks the specified module as loaded */
  function setModuleLoaded(moduleName) {
    kameHouse.logger.debug("setModuleLoaded: " + moduleName);
    modules[moduleName] = true;
  }

  /** Checks if the specified module is loaded */
  function isModuleLoaded(moduleName) {
    if (kameHouse.core.isEmpty(modules[moduleName])) {
      return false;
    }
    return modules[moduleName];
  }

  /**
   * Load kamehouse websockets module.
   */
  function loadKameHouseWebSocket() {
    kameHouse.util.fetch.getScript("/kame-house/kamehouse/js/kamehouse-websocket.js", () => {
      setModuleLoaded("kameHouseWebSocket");
    });
  }

  /** 
   * Waits until all specified modules in the moduleNames array are loaded, 
   * then executes the specified init function.
   * Use this function in the main() of each page that requires modules like logger and httpClient
   * to be loaded before the main code is executed.
   */
  async function waitForModules(moduleNames, initFunction) {
    let message;
    message = "Start waitForModules " + JSON.stringify(moduleNames) + ". modules status: " + JSON.stringify(modules);
    kameHouse.logger.trace(message);

    let areAllModulesLoaded = false;
    while (!areAllModulesLoaded) {
      message = "Waiting waitForModules " + JSON.stringify(moduleNames) + ". modules status: " + JSON.stringify(modules);
      kameHouse.logger.trace(message);

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
      await kameHouse.core.sleep(15);
    }
    message = "*** Finished  waitForModules " + JSON.stringify(moduleNames) + " ***. modules status: " + JSON.stringify(modules);
    kameHouse.logger.trace(message);
    if (kameHouse.core.isFunction(initFunction)) {
      initFunction();
    }
  }
}

/**
 * Manage generic kamehouse tabs (used for example in groot server manager).
 */
 function KameHouseTabUtils() {

  this.openTab = openTab;
  this.openTabFromCookies = openTabFromCookies;

  /**
   * Open the tab specified by its id.
   */
  function openTab(selectedTabDivId, cookiePrefix) {
    // Set current-tab cookie
    kameHouse.util.cookies.setCookie(cookiePrefix + '-current-tab', selectedTabDivId);
    
    // Update tab links
    const tabLinks = document.getElementsByClassName("tab-kh-link");
    for (const tabLink of tabLinks) {
      kameHouse.util.dom.classListRemove(tabLink, "active");
    }
    const selectedTabLink = document.getElementById(selectedTabDivId + '-link');
    kameHouse.util.dom.classListAdd(selectedTabLink, "active");

    // Update tab content visibility
    const kamehouseTabContent = document.getElementsByClassName("tab-content-kh");
    for (const kamehouseTabContentItem of kamehouseTabContent) {
      kameHouse.util.dom.setDisplay(kamehouseTabContentItem, "none");
    }
    const selectedTabDiv = document.getElementById(selectedTabDivId);
    kameHouse.util.dom.setDisplay(selectedTabDiv, "block");
  }

  /**
   * Open the tab from cookies or the default tab if not set in the cookies.
   */
  function openTabFromCookies(cookiePrefix, defaultTab) {
    let currentTab = kameHouse.util.cookies.getCookie(cookiePrefix + '-current-tab');
    if (!currentTab || currentTab == '') {
      currentTab = defaultTab;
    }
    openTab(currentTab, cookiePrefix);
  }
}

/** 
 * Functionality to manipulate tables. 
 */
function KameHouseTableUtils() {

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
    if (kameHouse.core.isEmpty(maxRows) || maxRows == "all") {
      maxRows = rows.length;
    }

    const regex = getRegex(filterString);
    const tableRows = $("#" + tableBodyId + " tr");
    tableRows.filter(function () {
      const tr = this;
      const classList = tr.classList.value;
      if (kameHouse.core.isEmpty(classList) || !classList.includes("table-kh-header")) {
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
    if (kameHouse.core.isEmpty(columnIndex)) {
      kameHouse.logger.trace("columnIndex not set. Using 0");
      columnIndex = 0;
    }
    if (kameHouse.core.isEmpty(maxRows) || maxRows == "all") {
      maxRows = rows.length;
    }
    const regex = getRegex(filterString);
    const tableRows = $("#" + tableBodyId + " tr");
    tableRows.filter(function () {
      const tr = this;
      const classList = tr.classList.value;
      if (kameHouse.core.isEmpty(classList) || !classList.includes("table-kh-header")) {
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
      kameHouse.logger.error("Error creating regex from filter string " + filterString);
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
    kameHouse.logger.trace("tableId " + tableId);
    kameHouse.logger.trace("columnNumber " + columnNumber);
    kameHouse.logger.trace("dataType " + dataType);
    kameHouse.logger.trace("initialSortDirection " + initialSortDirection);

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
        kameHouse.util.dom.insertBefore(rows[currentRowIndex].parentNode, rows[currentRowIndex + 1], rows[currentRowIndex]);
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
        kameHouse.logger.error("Ending sorting after " + MAX_SORTING_CYCLES + " sorting cycles. Something is VERY likely off with the sorting function. Breaking either infinite loop or a very inefficient sorting");
      }
      numSortingCycles++;
    }
    kameHouse.logger.trace("numSortingCycles " + numSortingCycles);
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
      kameHouse.logger.trace("Using compareNumerically");
      return compareNumerically;
    }

    kameHouse.logger.trace("Using compareLexicographically");
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
    if (kameHouse.core.isEmpty(maxRows) || maxRows == "all") {
      maxRows = rows.length;
    }
    let shownRows = 0;
    for (let i = 1; i < rows.length; i++) { 
      if (skipHiddenRows && isHiddenRow(rows[i])) {
        continue;
      }
      if (shownRows <= maxRows) {
        kameHouse.util.dom.setDisplay(rows[i], "table-row");
        shownRows++;
      } else {
        kameHouse.util.dom.setDisplay(rows[i], "none");
      }
    }
  }
}

/** 
 * Prototype for test functionality. 
 */
function KameHouseTestUtils() {

  this.testLogLevel = testLogLevel;
  this.testSleep = testSleep;

  /** Test the different log levels. */
  function testLogLevel() {
    console.log("kameHouse.logger.getLogLevel(): " + kameHouse.logger.getLogLevel());
    kameHouse.logger.error("This is an ERROR message");
    kameHouse.logger.warn("This is a WARN message");
    kameHouse.logger.info("This is an INFO message");
    kameHouse.logger.debug("This is a DEBUG message");
    kameHouse.logger.trace("This is a TRACE message");
  }

  async function testSleep() {
    kameHouse.logger.info("TEST SLEEP ------------- BEFORE " + new Date());
    await kameHouse.core.sleep(3000);
    kameHouse.logger.info("TEST SLEEP ------------- AFTER  " + new Date());
  }
}

/**
 * TimeUtils utility object for manipulating time and dates.
 */
function KameHouseTimeUtils() {

  this.getTimestamp = getTimestamp;
  this.convertSecondsToHsMsSs = convertSecondsToHsMsSs;
  this.getDateWithTimezoneOffset = getDateWithTimezoneOffset;
  this.getDateFromEpoch = getDateFromEpoch;
  this.isValidDate = isValidDate;

  /** Get timestamp with client timezone for the specified date or current date if null. */
  function getTimestamp(date) {
    if (kameHouse.core.isEmpty(date)) {
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
 * Prototype that contains the logic for all the core global functions. 
 * Only add functions here that are truly global and I'd want them to be part of the js language.
 * If I don't want them to be native, I probably should add them to a more specific utils prototype.
 */
function KameHouseCoreFunctions() {

  this.isEmpty = isEmpty;
  this.isFunction = isFunction;
  this.loadSession = loadSession;
  this.loadHeader= loadHeader;
  this.loadFooter= loadFooter;
  this.scrollToTopOfDiv = scrollToTopOfDiv;
  this.scrollToTop = scrollToTop;
  this.scrollToBottom = scrollToBottom;
  this.sleep = sleep;
  this.getBooleanKameHouseData = getBooleanKameHouseData;

  /** 
   * Get session from the backend. 
   */
  function loadSession() {
    const SESSION_STATUS_URL = "/kame-house/api/v1/ui/session/status";

    kameHouse.http.get(SESSION_STATUS_URL, null, null,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.trace("Session Status: " + JSON.stringify(responseBody));
        kameHouse.session = responseBody;
        kameHouse.util.module.setModuleLoaded("session");
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.error("Error retrieving current session information.");
        kameHouse.util.module.setModuleLoaded("session");
      }
    );
  }

  /** 
   * Load header.
   */
  function loadHeader() {
    const skipHeader = getBooleanKameHouseData("skip-header");
    if (!skipHeader) {
      kameHouse.util.fetch.getScript("/kame-house/kamehouse/js/kamehouse-header.js", () => {
        kameHouse.header = new KameHouseHeader();
        kameHouse.header.load();
      });
    } else {
      kameHouse.logger.info("Skip header kamehouse data set to true");
    }
  }

  /** 
   * Load footer.
   */
  function loadFooter() {
    const skipFooter = getBooleanKameHouseData("skip-footer");
    if (!skipFooter) {
      kameHouse.util.fetch.getScript("/kame-house/kamehouse/js/kamehouse-footer.js", () => {
        kameHouse.footer = new KameHouseFooter();
        kameHouse.footer.load();
      });
    } else {
      kameHouse.logger.info("Skip footer kamehouse data set to true");
    }
  }  
  
  /**
   * Returns the boolean value of data-xx attributes defined in the script tag of kamehouse.js
   * The script tag id must be set to id="kamehouse-data"
   */
  function getBooleanKameHouseData(dataAttributeName) {
    const kameHouseData = document.getElementById('kamehouse-data');
    if (!isEmpty(kameHouseData)) {
      let data = kameHouseData.getAttribute("data-" + dataAttributeName);
      if (!isEmpty(data)) {
        return data.toLowerCase() === "true";
      }
    }
    return false;
  }

  /** 
   * @deprecated(use kameHouse.core.isEmpty())
   * 
   * Checks if a variable is undefined or null, an empty array [] or an empty object {}. 
   * 
   * --- IMPORTANT --- 
   * DEPRECATED: This method performs poorly with large objects. For large playlists (3000 elements) this comparison
   * takes more than 1 seconds causing a lag in the entire view. Use it for objects that I don't expect
   * to be large and be aware of performance issues that can be caused from using it.
   * 
   * For better performance, use kameHouse.core.isEmpty() when that check is enough.
   * 
   * Keeping the definition so I don't attempt to do the same later down the track.
   */
   function isEmptyDeprecated(val) {
    const isUndefinedOrNull = kameHouse.core.isEmpty(val);
    const isEmptyString = !isUndefinedOrNull && val === "";
    const isEmptyArray = !isUndefinedOrNull && Array.isArray(val) && val.length <= 0;
    const isEmptyObject = !isUndefinedOrNull && Object.entries(val).length === 0 && val.constructor === Object;
    return isUndefinedOrNull || isEmptyString || isEmptyArray || isEmptyObject;
  }

  /** Checks if a variable is undefined or null or an empty string "". */
  function isEmpty(val) {
    return val === undefined || val == null || val == "";
  }

  /** Returns true if the parameter variable is a fuction. */
  function isFunction(expectedFunction) {
    return expectedFunction instanceof Function;
  } 

  /** 
   * Scroll the specified div to it's top.
   * This method doesn't scroll the entire page, it scrolls the scrollable div to it's top.
   * To scroll the page to the top of a particular div, use kameHouse.core.scrollToTop()
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
   * Example: await kameHouse.core.sleep(1000);
   */
  function sleep(ms) { 
    return new Promise(resolve => setTimeout(resolve, ms));
  }
}

/**
 * Log object to perform logging to the console on the frontend side.
 * 
 * Dependencies: kameHouse.util.time.
 * 
 * @author nbrest
 */
 function KameHouseLogger() {

  this.init = init;
  this.setLogLevel = setLogLevel;
  this.getLogLevel = getLogLevel;
  this.error = error;
  this.warn = warn;
  this.info = info;
  this.debug = debug;
  this.trace = trace;
  this.logApiError = logApiError;
  this.logHttpRequest = logHttpRequest;
  this.logHttpResponse = logHttpResponse;

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
   * Override the default log level from url parameters.
   */
  function init() {
    info("Initializing logger");
    const urlParams = new URLSearchParams(window.location.search);
    const logLevel = urlParams.get('logLevel');
    if (!kameHouse.core.isEmpty(logLevel)) {
      const logLevelNumberParam = getLogLevelNumber(logLevel);
      info("Overriding logLevel with url parameter logLevel: " + logLevel + " mapped to logLevelNumber: " + logLevelNumberParam);
      setLogLevel(logLevelNumberParam);
    }
  }

  /**
   * Get the log level number mapped to the specified log level string.
   */
  function getLogLevelNumber(logLevel) {
    const logLevelUpperCase = logLevel.toUpperCase();
    if (logLevelUpperCase == "ERROR") {
      return 0;
    }
    if (logLevelUpperCase == "WARN") {
      return 1;
    }
    if (logLevelUpperCase == "INFO") {
      return 2;
    }
    if (logLevelUpperCase == "DEBUG") {
      return 3;
    }
    if (logLevelUpperCase == "TRACE") {
      return 4;
    }
    // default INFO
    return 2;
  }

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
    if (kameHouse.core.isEmpty(logLevel)) {
      console.error("Invalid use of log(logLevel, message) function. LogLevel is missing.");
      return;
    }
    if (!message) {
      console.error("Invalid use of log(logLevel, message) function. Message is empty");
      return;
    }
    const logLevelUpperCase = logLevel.toUpperCase();
    const logEntry = kameHouse.util.time.getTimestamp() + " - [" + logLevelUpperCase + "] - " + message;
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
      // Use trace to log content such as responses from api calls. But use debug or info kameHouse.logger. trace prints a useless stack trace in the console that doesn't help.
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
    const DEBUG_MODE_LOG_SIZE = 40;
    const debugModeConsoleLog = document.getElementById("debug-mode-console-log-entries");
    if (!kameHouse.core.isEmpty(debugModeConsoleLog)) {
      // Remove first log N entries
      let logEntriesSize = debugModeConsoleLog.childElementCount;
      while (logEntriesSize > DEBUG_MODE_LOG_SIZE) {
        kameHouse.util.dom.removeChild(debugModeConsoleLog, debugModeConsoleLog.firstChild);
        logEntriesSize = debugModeConsoleLog.childElementCount;
      }
      // Add new log entry
      kameHouse.util.dom.append($("#debug-mode-console-log-entries"), getLogEntryListItem(logEntry));
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
    const li = kameHouse.util.dom.getLi({}, null);
    kameHouse.util.dom.setText(li, logEntry);
    return li;
  }

  /**
   * Log an api call error to the console.
   */
  function logApiError(responseBody, responseCode, responseDescription, responseHeaders, message) {
    if (kameHouse.core.isEmpty(message)) {
      message = "Error executing api call";
    }
    const errorMessage = message + ": responseBody=" + responseBody + "; responseCode=" + responseCode + "; responseDescription=" + responseDescription + "; responseHeaders=" + JSON.stringify(responseHeaders);
    error(errorMessage);
  }

  /**
   * Log an http request.
   */
  function logHttpRequest(httpMethod, url, requestHeaders, requestBody) {
    debug("http request: [ " 
    + "'method' : '" + httpMethod + "', "
    + "'url' : '" + url + "', "
    + "'headers' : '" + JSON.stringify(requestHeaders) + "', "
    + "'body' : '" + JSON.stringify(requestBody) + "' ]");
  }
  
  /**
   * Log an http response.
   */
  function logHttpResponse(responseBody, responseCode, responseDescription, responseHeaders) {
    debug("http response: [ " 
    + "'responseCode' : '" + responseCode + "', "
    + "'responseDescription' : '" + responseDescription + "', "
    + "'responseHeaders' : '" + JSON.stringify(responseHeaders) + "', "
    + "'responseBody' : '" + JSON.stringify(responseBody) + "' ]");   
  }
}

/**
 * HttpClient object to perform http calls.
 * 
 * Dependencies: kameHouse.logger.
 * 
 * @author nbrest
 */
 function KameHouseHttpClient() {

  this.get = get;
  this.put = put;
  this.post = post;
  this.delete = deleteHttp;
  this.getUrlEncodedHeaders = getUrlEncodedHeaders;
  this.getApplicationJsonHeaders = getApplicationJsonHeaders;
  this.urlEncodeParams = urlEncodeParams;
  this.urlEncode = urlEncode;
  this.isUrlEncodedRequest = isUrlEncodedRequest;

  const GET = "GET";
  const POST = "POST";
  const PUT = "PUT";
  const DELETE = "DELETE";

  /** Execute an http GET request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription, responseHeaders) 
   * and errorCallback(responseBody, responseCode, responseDescription, responseHeaders) */
  function get(url, requestHeaders, requestBody, successCallback, errorCallback) {
    httpRequest(GET, url, requestHeaders, requestBody, successCallback, errorCallback);
  }

  /** Execute an http PUT request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription, responseHeaders) 
   * and errorCallback(responseBody, responseCode, responseDescription, responseHeaders) */
  function put(url, requestHeaders, requestBody, successCallback, errorCallback) {
    httpRequest(PUT, url, requestHeaders, requestBody, successCallback, errorCallback);
  }

  /** Execute an http POST request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription, responseHeaders) 
   * and errorCallback(responseBody, responseCode, responseDescription, responseHeaders) */
  function post(url, requestHeaders, requestBody, successCallback, errorCallback) {
    httpRequest(POST, url, requestHeaders, requestBody, successCallback, errorCallback);
  }

  /** Execute an http DELETE request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription, responseHeaders) 
   * and errorCallback(responseBody, responseCode, responseDescription, responseHeaders) */
  function deleteHttp(url, requestHeaders, requestBody, successCallback, errorCallback) {
    httpRequest(DELETE, url, requestHeaders, requestBody, successCallback, errorCallback);
  }

  /** Execute an http request with the specified http method. 
   * Implement and pass successCallback(responseBody, responseCode, responseDescription, responseHeaders) 
   * and errorCallback(responseBody, responseCode, responseDescription, responseHeaders)
   * Don't call this method directly, instead call the wrapper get(), post(), put(), delete() */
  function httpRequest(httpMethod, url, requestHeaders, requestBody, successCallback, errorCallback) {
    kameHouse.logger.logHttpRequest(httpMethod, url, requestHeaders, requestBody);
    kameHouse.util.mobile.exec(
      () => {
        if (kameHouse.core.isEmpty(requestBody)) {
          $.ajax({
            type: httpMethod,
            url: url,
            headers: requestHeaders,
            success: (data, status, xhr) => processSuccess(data, status, xhr, successCallback),
            error: (jqXhr, textStatus, errorMessage) => processError(jqXhr, textStatus, errorMessage, errorCallback)
          });
          return;
        }
        if (isUrlEncodedRequest(requestHeaders)) {
          const urlEncoded = url + "?" + urlEncodeParams(requestBody);
          $.ajax({
            type: httpMethod,
            url: urlEncoded,
            headers: requestHeaders,
            success: (data, status, xhr) => processSuccess(data, status, xhr, successCallback),
            error: (jqXhr, textStatus, errorMessage) => processError(jqXhr, textStatus, errorMessage, errorCallback)
          });
          return;
        }
        $.ajax({
          type: httpMethod,
          url: url,
          data: JSON.stringify(requestBody),
          headers: requestHeaders,
          success: (data, status, xhr) => processSuccess(data, status, xhr, successCallback),
          error: (jqXhr, textStatus, errorMessage) => processError(jqXhr, textStatus, errorMessage, errorCallback)
        });        
      },
      () => {
        kameHouse.util.module.waitForModules(["kameHouseMobile"], () => {
          kameHouse.extension.mobile.core.mobileHttpRequst(httpMethod, url, requestHeaders, requestBody, successCallback, errorCallback);
        });
      },
    );
  }

  function getResponseHeaders(xhr) {
    return xhr.getAllResponseHeaders()
      .trim()
      .split('\r\n')
      .reduce((acc, current) => {
          const [x,v] = current.split(': ');
          return Object.assign(acc, { [x] : v });
      }, 
      {}
    );
  }

  function urlEncodeParams(params) {
    let urlEncodeParams = [];
    for (const key in params)
      if (params.hasOwnProperty(key)) {
        urlEncodeParams.push(encodeURIComponent(key) + "=" + encodeURIComponent(params[key]));
      }
    return urlEncodeParams.join("&");
  }

  function urlEncode(param) {
    return encodeURIComponent(param);
  }

  function isUrlEncodedRequest(headers) {
    if (kameHouse.core.isEmpty(headers)) {
      return false;
    }
    let isUrlEncoded = false;
    for (const [key, value] of Object.entries(headers)) {
      if (!kameHouse.core.isEmpty(key) && key.toLowerCase() == "content-type" && !kameHouse.core.isEmpty(value)) {
        if (value.toLowerCase() == "application/x-www-form-urlencoded") {
          isUrlEncoded = true;
        }
      }
    }
    return isUrlEncoded;
  }

  /** Process a successful response from the api call */
  function processSuccess(data, status, xhr, successCallback) {
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
    const responseHeaders = getResponseHeaders(xhr);
    kameHouse.logger.logHttpResponse(responseBody, responseCode, responseDescription, responseHeaders);
    successCallback(responseBody, responseCode, responseDescription, responseHeaders);
  }

  /** Process an error response from the api call */
  function processError(jqXhr, textStatus, errorMessage, errorCallback) {
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
     const responseHeaders = getResponseHeaders(jqXhr);
     kameHouse.logger.logApiError(responseBody, responseBody, responseDescription, responseHeaders, null);
     errorCallback(responseBody, responseCode, responseDescription, responseHeaders);
  }

  /** Get request headers object with Url Encoded content type. */
  function getUrlEncodedHeaders() {
    const requestHeaders = {};
    requestHeaders.Accept = '*/*';
    requestHeaders['Content-Type'] = "application/x-www-form-urlencoded";
    return requestHeaders;
  }

  /** Get request headers object with application json content type. */
  function getApplicationJsonHeaders() {
    const requestHeaders = {};
    requestHeaders.Accept = '*/*';
    requestHeaders['Content-Type'] = 'application/json';
    return requestHeaders;
  }
}

$(document).ready(() => {kameHouse.init();});