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
class KameHouse {

  core: KameHouseCore;
  http: KameHouseHttpClient;
  json: KameHouseJson;
  logger: KameHouseLogger;
  util: KameHouseUtil;
  footer: KameHouseFooter;
  header: KameHouseHeader;
  session = {} as SessionStatus;
  extension = {
    apkStatus: null,
    batcave: null,
    batcaveShell: null,
    batcaveServerManager: null,
    batcaveTailLogManager: null,
    downloadsManager: null,
    dragonBallUserServiceJsp: null,
    groot: null,
    kameHouseUserCrudManager: null,
    kameHouseShell: null,
    mobile: null,
    serverManager: null,
    tailLogManager: null,
    tomcatModuleStatusManager: null,
    vlcPlayer: null
  };
  plugin = {
    crudManager: null,
    debugger: null,
    modal: null,
    mobileTabsManager: null,
    slideshow: null,
    kameHouseCommandManager: null,
    webappTabsManager: null
  };

  cordova = null;
  jq = null;

  /**
   * Build the initial kamehouse object.
   */
  constructor() {
    this.#setJquery();
  }

  /**
   * Init kamehouse framework.
   */
  init() {    
    /** root kamehouse elements */
    this.core = new KameHouseCore();
    this.json = new KameHouseJson();
    this.logger = new KameHouseLogger();
    this.http = new KameHouseHttpClient();

    /** utils */
    this.util = new KameHouseUtil();

    /**
     * Init root elements and utils
     */
    this.logger.init();
    this.logger.info("Started initializing kamehouse.js", null);
    this.core.setGlobalErrorHandler();
    this.core.initAuthorizeUser();
    this.util.mobile.init();
    this.core.configDynamicHtml();
    this.core.loadSession();
    this.core.loadHeader();
    this.core.loadFooter();
    this.core.loadStickyBackToTop();
    this.core.loadKameHouseModal();
    this.core.loadKameHouseDebugger();
    this.util.cursor.loadSpinningWheelCursorWait();
    this.logger.info("Finished initializing kamehouse.js", null);
  }

  /**
   * Extensions live externally to the kamehouse js/css/html bundle. Examples: kamehouse-mobile.js, kamehouse-groot.js, newsletter.js. They need to implement a load() function that initializes the extension.
   */
  addExtension(extensionName, extension) {
    if (this.#skipLoading(extensionName)) {
      kameHouse.logger.info("Extension '" + extensionName + "' marked as skipped. Not loading", null);
      return;
    }
    kameHouse.logger.info("Adding extension " + extensionName, null);
    this.extension[extensionName] = extension;
    extension.load();
  }

  /**
   * Plugins live in the kamehouse js/css/html bundle, but are not loaded by default. Examples kamehouse-debugger.js, kamehouse-modal.js. They need to implement a load() function that initializes the plugin.
   */
  addPlugin(pluginName, plugin) {
    if (this.#skipLoading(pluginName)) {
      kameHouse.logger.info("Plugin '" + pluginName + "' marked as skipped. Not loading", null);
      return;
    }
    kameHouse.logger.info("Adding plugin " + pluginName, null);
    this.plugin[pluginName] = plugin;
    plugin.load();
  }

  /**
   * Execute the ready function after the document is ready.
   */
  ready(readyFunction) {
    return this.jq(document).ready(() => {readyFunction()});
  }

  /**
   * Returns true if it should skip loading the kamehouse extension/plugin. False otherwise.
   */
  #skipLoading(elementToLoad) {
    const skipLoading = kameHouse.core.getArrayKameHouseData("skip-loading");
    return skipLoading.includes(elementToLoad);
  }
  
  /**
   * Set jQuery.
   */
  #setJquery() {
    try {
      this.jq = $;
    } catch (error) {
      console.log("Error setting jquery in kamehouse");
      this.jq = {};
    }
  }

} // KameHouse root object

/**
 * BannerUtils to manipulate banners.
 * 
 * @author nbrest
 */
class KameHouseBannerUtils {

  #DEFAULT_BANNER_ROTATE_WAIT_MS = 10000;
  #PRELOAD_BANNERS_WAIT_MS = 25000;
  #BANNERS_LIST = [
    {
      category: "batcave",
      banners: ["banner-batman-animated", "banner-batman-1989-batcave-bw", "banner-batman-animated-batcave-01", "banner-batman-forever-batcave-02", "banner-batman-1989-batcave-01", "banner-batman-1989-batmobile-01", "banner-batman-animated-batcave-02", "banner-batman-forever-batcave-03", "banner-batman-1989-batcave-02", "banner-batman-1989-batmobile-02", "banner-batman-animated-batcave-03", "banner-batman-n-robin-batcave-01", "banner-batman-1989-batcave-03", "banner-batman-1989-batsignal-01", "banner-batman-animated-batcave-04", "banner-batman-1989-batcave-04", "banner-batman-1989-batsignal-02", "banner-batman-forever-batcave-01"]
    },    
    {
      category: "captain-tsubasa",
      banners: ["banner-benji1", "banner-benji-steve", "banner-benji", "banner-benji2", "banner-benji3", "banner-benji4", "banner-niupi", "banner-niupi2", "banner-oliver-benji", "banner-oliver-benji2", "banner-oliver-steve", "banner-oliver", "banner-oliver2", "banner-youth-arc-opening-3-messi"]
    },
    {
      category: "dc",
      banners: ["banner-batman-animated", "banner-batman", "banner-joker", "banner-joker2", "banner-superman-logo", "banner-superman-space", "banner-superman", "banner-superman2", "banner-superman3"]
    },
    {
      category: "dragonball",
      banners: ["banner-gogeta", "banner-gohan-shen-long", "banner-gohan-ssj2", "banner-gohan-ssj2-1", "banner-gohan-ssj2-2", "banner-gohan-ssj2-3", "banner-gohan-ssj2-4", "banner-goku-ssj1", "banner-goku-ssj4-earth", "banner-kamehouse", "banner-trunks-mountains", "banner-trunks-vs-goku-sword-finger", "banner-z-senshi"]
    },
    {
      category: "game-of-thrones",
      banners: ["banner-jon-snow2", "banner-winter-is-coming"]
    },
    {
      category: "marvel",
      banners: ["banner-avengers", "banner-avengers-assemble", "banner-avengers-cap", "banner-avengers-cap-mjolnir", "banner-avengers-cap-mjolnir2", "banner-avengers-cap-mjolnir3", "banner-avengers-cap-mjolnir4", "banner-avengers-cap-mjolnir5", "banner-avengers-cap-mjolnir6", "banner-avengers-cap-uniform", "banner-avengers-endgame", "banner-avengers-infinity", "banner-avengers-ironman", "banner-avengers-portals", "banner-avengers-trinity", "banner-spiderman"]
    },
    {
      category: "prince-of-tennis",
      banners: ["banner-fuji", "banner-pot-pijamas", "banner-rikkaidai", "banner-ryoma-chibi", "banner-ryoma-chibi2", "banner-ryoma-drive", "banner-ryoma-ss", "banner-seigaku", "banner-tezuka", "banner-yukimura", "banner-yukimura2", "banner-yukimura-sanada"]
    },
    {
      category: "saint-seiya",
      banners: ["banner-ancient-era-warriors", "banner-aries-knights", "banner-athena", "banner-athena-saints", "banner-camus", "banner-dohko", "banner-fuego-12-casas", "banner-hades", "banner-hyoga", "banner-ikki", "banner-ikki2", "banner-pegasus-ryu-sei-ken", "banner-sanctuary", "banner-seiya", "banner-shaka", "banner-shion", "banner-shiryu", "banner-shun"]
    },
    {
      category: "star-wars",
      banners: ["banner-anakin", "banner-anakin2", "banner-anakin3", "banner-anakin4", "banner-anakin5", "banner-luke-vader", "banner-luke-vader2", "banner-luke-vader3", "banner-star-wars-ep3", "banner-star-wars-poster", "banner-star-wars-trilogy", "banner-vader", "banner-vader2", "banner-yoda", "banner-yoda2"]
    },
    {
      category: "tennis",
      banners: ["banner-australian-open", "banner-roland-garros", "banner-wimbledon"]
    },
    {
      category: "world-cup-2022",
      banners: [ "banner-arabia-flags", "banner-australia-messi-gol", "banner-dbz-messi-maradona", "banner-francia-flags", "banner-francia-messi-festejando-mbappe", "banner-holanda-messi-corriendo", "banner-messi-campeones-arrodillado", "banner-mexico-flags", "banner-mexico-messi-gol", "banner-mexico-messi-gol2", "banner-world-cup-champions"]
    }
  ];

  /** Set random saint seiya sanctuary banner */
  setRandomSanctuaryBanner(bannerRotateWaitMs) {
    kameHouse.logger.info("Set random sanctuary banners", null);
    const bannerClasses = ["banner-fuego-12-casas", "banner-sanctuary"];  
    this.#setRandomBannerWrapper(bannerClasses, true, bannerRotateWaitMs);
    this.#preloadBannerImages('saint-seiya', bannerClasses);
  }

  /** Set random batcave banner */
  setRandomBatcaveBanner(bannerRotateWaitMs) {
    this.#setRandomBannerFromCategory('batcave', bannerRotateWaitMs);
  }

  /** Set random captain tsubasa banner */
  setRandomCaptainTsubasaBanner(bannerRotateWaitMs) {
    this.#setRandomBannerFromCategory('captain-tsubasa', bannerRotateWaitMs);
  }

  /** Set random dragonball banner */
  setRandomDragonBallBanner(bannerRotateWaitMs) {
    this.#setRandomBannerFromCategory('dragonball', bannerRotateWaitMs);
  }

  /** Set random prince of tennis banner */
  setRandomPrinceOfTennisBanner(bannerRotateWaitMs) {
    this.#setRandomBannerFromCategory('prince-of-tennis', bannerRotateWaitMs);
  }

  /** Set random saint seiya banner */
  setRandomSaintSeiyaBanner(bannerRotateWaitMs) {
    this.#setRandomBannerFromCategory('saint-seiya', bannerRotateWaitMs);
  }

  /** Set random tennis banner */
  setRandomTennisBanner(bannerRotateWaitMs) {
    this.#setRandomBannerFromCategory('tennis', bannerRotateWaitMs);
  }

  /** Set random world cup 2022 ARGENTINA CAMPEON!!! banner */
  setRandomWorldCupBanner(bannerRotateWaitMs) {
    this.#setRandomBannerFromCategory('world-cup-2022', bannerRotateWaitMs);
  }  

  /** Set random banner from all banners */
  setRandomAllBanner(bannerRotateWaitMs) {
    kameHouse.logger.info("Set random all banners", null);
    this.#setRandomBannerWrapper(this.#getAllBanners(), true, bannerRotateWaitMs);
    this.getBannerCategories().forEach((bannerCategory) => {
      this.#preloadBannerImages(bannerCategory, this.getBanners(bannerCategory));
    });
  }

  /**
   * Get banner categories.
   */
  getBannerCategories() {
    const allCategories = [];
    this.#BANNERS_LIST.forEach((banner) => {
      allCategories.push(banner.category);
    });
    return allCategories;
  }

  /**
   * Get banners for a category.
   */
  getBanners(bannerCategory) {
    let selectedBanners = [];
    this.#BANNERS_LIST.forEach((banner) => {
      if (bannerCategory === banner.category) {
        selectedBanners = banner.banners;
      }
    });
    return selectedBanners;
  }

  /**
   * Set the banner.
   */
  setBanner(bannerName) {
    // Update banner
    const element = document.getElementById("banner");
    kameHouse.util.dom.classListRemoveAll(element, "banner-");
    kameHouse.util.dom.classListAdd(element, bannerName);

    // Trigger banner animation
    const clonedElement = kameHouse.util.dom.cloneNode(element, true);
    kameHouse.util.dom.replaceChild(element.parentNode, clonedElement, element);
  }

  /**
   * Get all baners.
   */
  #getAllBanners() {
    const allBanners = [];
    this.#BANNERS_LIST.forEach((banner) => {
      allBanners.push(...banner.banners);
    });
    return allBanners;
  }

  #setRandomBannerFromCategory(bannerCategory, bannerRotateWaitMs) {
    kameHouse.logger.info("Set random " + bannerCategory + " banners", null);
    this.#setRandomBannerWrapper(this.getBanners(bannerCategory), true, bannerRotateWaitMs);
    this.#preloadBannerImages(bannerCategory, this.getBanners(bannerCategory));
  }

  /** Wrapper to setRandomBanner to decide if it should set it once or loop */
  #setRandomBannerWrapper(bannerClasses, shouldLoop, bannerRotateWaitMs) {
    if (shouldLoop) {
      this.#setRandomBannerLoop(bannerClasses, bannerRotateWaitMs);
    } else {
      this.#setRandomBanner(bannerClasses);
    }
  }

  /** Set a random image from the banner classes list */
  #setRandomBanner(bannerClasses) {
    // Get a new banner, different from the current one
    let randomBannerIndex = Math.floor(Math.random() * bannerClasses.length);
    const bannerDivClasses = document.getElementById('banner').getAttribute('class');
    if (kameHouse.core.isEmpty(bannerDivClasses)) {
      kameHouse.logger.trace("No banner classes to update. Returning...", null);
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
    this.setBanner(bannerClasses[randomBannerIndex]);
  }

  /** Set a random image banner from the classes list at the specified interval */
  #setRandomBannerLoop(bannerClass, bannerRotateWaitMs) {
    if (kameHouse.core.isEmpty(bannerRotateWaitMs)) {
      bannerRotateWaitMs = this.#DEFAULT_BANNER_ROTATE_WAIT_MS;
    }
    setInterval(() => {
      this.#setRandomBanner(bannerClass);
    }, bannerRotateWaitMs);
  }
  
  /** Preload banner images */
  #preloadBannerImages(banerCategory, bannerArray) {
    setTimeout(() => {
      const preloadedBannerImages = [];
      const message = "Preloading " + banerCategory + " banners";
      const coloredMessage = "Preloading " + kameHouse.logger.getCyanText(banerCategory) + " banners";
      kameHouse.logger.debug(message, coloredMessage);
      bannerArray.forEach((bannerName) => {
        const img = kameHouse.util.dom.getImg({
          src: '/kame-house/img/banners/' + banerCategory + '/' + bannerName + '.jpg'
        });
        preloadedBannerImages.push(img);
      });
      kameHouse.logger.trace("Preloaded " + preloadedBannerImages.length + " banners", null);
    }, this.#PRELOAD_BANNERS_WAIT_MS);
  }
  
} // KameHouseBannerUtils

/**
 * Utility to manipulate collapsible divs.
 * 
 * @author nbrest
 */
class KameHouseCollapsibleDivUtils {

  /**
   * Resize the specified collapsible div id.
   */
  resize(elementId) {
    const element = document.getElementById(elementId);
    // need to trigger the 2 clicks to refresh
    this.toggle(element);
    this.toggle(element);
  }

  /**
   * Function to toggle the view and update the height of a collapsible div.
   */
  toggle(collapsibleElement) {
    if (kameHouse.core.isEmpty(collapsibleElement)) {
      return;
    }
    const content = collapsibleElement.nextElementSibling;
    if (kameHouse.core.isEmpty(content)) {
      return;
    }
    kameHouse.util.dom.toggleClassOnElement(collapsibleElement, "collapsible-kh-expanded");
    if (content.style.maxHeight != 0) {
      kameHouse.util.dom.setStyle(content, "maxHeight", null);
    } else {
      kameHouse.util.dom.setStyle(content, "maxHeight", content.scrollHeight + "px");
    }
  }

} // KameHouseCollapsibleDivUtils

/**
 * Functionality to handle cookies.
 * 
 * @author nbrest
 */
class KameHouseCookiesUtils {

  /**
   * Get a cookie.
   */
  getCookie(cookieName) {
    kameHouse.logger.trace("Getting cookie " + cookieName, null);
    const name = cookieName + "=";
    const decodedCookie = decodeURIComponent(document.cookie);
    const cookiesArray = decodedCookie.split(';');
    for (const cookieElement of cookiesArray) {
      let cookie = cookieElement;
      while (cookie.startsWith(' ')) {
        cookie = cookie.substring(1);
      }
      if (cookie.startsWith(name)) {
        return cookie.substring(name.length, cookie.length);
      }
    }
    return "";
  }

  /**
   * Set a cookie.
   */
  setCookie(cookieName: String, cookieValue: String, expiryDays: number) {
    kameHouse.logger.trace("Setting cookie " + cookieName + " to " + cookieValue, null);
    if (!kameHouse.core.isEmpty(expiryDays)) {
      const expiriyDate = new Date();
      expiriyDate.setTime(expiriyDate.getTime() + (expiryDays * 24 * 60 * 60 * 1000));
      const expires = "expires=" + expiriyDate.toUTCString();
      document.cookie = cookieName + "=" + cookieValue + ";" + expires + "; path=/";
    } else {
      document.cookie = cookieName + "=" + cookieValue + "; path=/";
    }
  }

} // KameHouseCookiesUtils

/** 
 * Functionality to manipulate the cursor. 
 * 
 * @author nbrest
 */
class KameHouseCursorUtils {

  /** Set the cursor to a wait spinning wheel */
  setCursorWait() {
    kameHouse.util.dom.classListAdd(kameHouse.util.dom.getHtml(), "wait");
    kameHouse.util.dom.classListRemoveById('spinning-wheel-cursor-wait-wrapper', "hidden-kh");
  }

  /** Set the cursor to default shape */
  setCursorDefault() {
    kameHouse.util.dom.classListRemove(kameHouse.util.dom.getHtml(), "wait");
    kameHouse.util.dom.classListAddById('spinning-wheel-cursor-wait-wrapper', "hidden-kh");
  }

  /**
   * Load the spinning wheel for cursor wait.
   */
  async loadSpinningWheelCursorWait() {
    const spinnigWheelCursorWaitDiv = await kameHouse.util.fetch.loadHtmlSnippet("/kame-house/html-snippets/spinning-wheel-cursor-wait.html");
    kameHouse.util.dom.append(kameHouse.util.dom.getBody(), spinnigWheelCursorWaitDiv);
  }

} // KameHouseCursorUtils

/**
 * Functionality that manipulates dom elements.
 * 
 * Anything that manipulates the dom should go through here.
 * 
 * @author nbrest
 */
class KameHouseDomUtils {

  /**
   * Insert the new node after the selected node.
   */
  after(siblingElement, newNodeElement) {
    if (siblingElement) {
      siblingElement.after(newNodeElement);
    }
  }

  /**
   * Insert the new node after the selected node.
   */
  afterById(siblingElementId, newNodeElement) {
    const element = document.getElementById(siblingElementId);
    return this.after(element, newNodeElement);
  }

  /**
   * Append the appendElement to appendToElement.
   */
  append(appendToElement, appendElement) {
    if (appendToElement) {
      kameHouse.jq(appendToElement).append(appendElement);
    }
  }

  /**
   * Append the appendElement to appendToElement.
   */
  appendById(appendToElementId, appendElement) {
    const element = document.getElementById(appendToElementId);
    return this.append(element, appendElement);
  }

  /**
   * Append the child to parent.
   */
  appendChild(parentElement, childElement) {
    if (parentElement) {
      parentElement.appendChild(childElement);
    }
  }

  /**
   * Append the child to parent.
   */
  appendChildById(parentElementId, childElement) {
    const element = document.getElementById(parentElementId);
    return this.appendChild(element, childElement);
  }  

  /** Add a class to the element */
  classListAdd(element, className) {
    if (element) {
      element.classList.add(className);
    }
  }

  /** Add a class to the element */
  classListAddById(elementId, className) {
    const element = document.getElementById(elementId);
    return this.classListAdd(element, className);
  }  

  /** Remove a class from the element */
  classListRemove(element, className) {
    if (element) {
      element.classList.remove(className);
    }
  }

  /** Remove a class from the element */
  classListRemoveById(elementId, className) {
    const element = document.getElementById(elementId);
    return this.classListRemove(element, className);
  }

  /**
   * Remove all classes starting with the specified prefix from the element.
   */
  classListRemoveAll(element, classPrefix) {
    if (element) {
      const classes = element.className.split(" ").filter(className => !className.startsWith(classPrefix));
      element.className = classes.join(" ").trim();
    }
  }

  /**
   * Remove all classes starting with the specified prefix from the element.
   */
  classListRemoveAllById(elementId, classPrefix) {
    const element = document.getElementById(elementId);
    return this.classListRemoveAll(element, classPrefix);
  }

  /**
   * Clone an element.
   */
  cloneNode(element, deep) {
    if (kameHouse.core.isEmpty(deep)) {
      deep = false;
    }
    if (element) {
      return element.cloneNode(deep);
    }
    return null;
  }

  /**
   * Clone an element.
   */
  cloneNodeById(elementId, deep) {
    const element = document.getElementById(elementId);
    return this.cloneNode(element, deep);
  }  

  /**
   * Create a new element of the specified tag.
   */
  createElement(tag) {
    return document.createElement(tag);
  }

  /**
   * Detach the specified element from the dom.
   */
  detach(element) {
    if (element) {
      kameHouse.jq(element).detach();
    }
  }

  /**
   * Detach the specified element from the dom.
   */
  detachById(elementId) {
    const element = document.getElementById(elementId);
    return this.detach(element);
  }

  /**
   * Empty the specified element.
   */
  empty(element) {
    if (element) {
      kameHouse.jq(element).empty();
    }
  }  

  /**
   * Empty the specified element.
   */
  emptyById(elementId) {
    const element = document.getElementById(elementId);
    return this.empty(element);
  }    

  /**
   * Returns a new element to attach to the dom from the specified html template loaded from an html snippet.
   */
  getElementFromTemplate(htmlTemplate) {
    const domElementWrapper = document.createElement('div');
    domElementWrapper.innerHTML = htmlTemplate;
    return domElementWrapper.firstChild;
  }

  /**
   * Get body element.
   */
  getBody() {
    return document.getElementsByTagName("body")[0];
  }
  
  /**
   * Get head element.
   */
  getHead() {
    return document.getElementsByTagName("head")[0];
  }

  /**
   * Get html element.
   */
  getHtml() {
    return document.getElementsByTagName("html")[0];
  }

  /** Insert the html element before the body */
  insertBeforeBegin(element) {
    if (element) {
      this.getBody().insertAdjacentHTML("beforebegin", element.innerHTML);
    }
  }

  /** Insert the html element before the body */
  insertBeforeBeginById(elementId) {
    const element = document.getElementById(elementId);
    return this.insertBeforeBegin(element);
  }

  /**
   * Insert the new node under the parent.
   */
  insertBefore(parentElement, newNodeElement, nextSiblingElement) {
    if (parentElement) {
      parentElement.insertBefore(newNodeElement, nextSiblingElement);
    }
  }

  /**
   * Insert the new node under the parent.
   */
  insertBeforeById(parentElementId, newNodeElement, nextSiblingElement) {
    const element = document.getElementById(parentElementId);
    return this.insertBefore(element, newNodeElement, nextSiblingElement);
  }

  /**
   * Load the specified htmlPath into the element.
   */
  load(element, htmlPath, completeCallback) {
    if (element) {
      if (kameHouse.core.isFunction(completeCallback)) {
        kameHouse.jq(element).load(htmlPath, completeCallback);
      } else {
        kameHouse.jq(element).load(htmlPath);
      }
    }
  }

  /**
   * Load the specified htmlPath into the element.
   */
  loadById(elementId, htmlPath, completeCallback) {
    const element = document.getElementById(elementId);
    return this.load(element, htmlPath, completeCallback);
  }

  /**
   * Prepend the prependElement to prependToElement.
   */
  prepend(prependToElement, prependElement) {
    if (prependToElement) {
      kameHouse.jq(prependToElement).prepend(prependElement);
    }
  }

  /**
   * Prepend the prependElement to prependToElement.
   */
  prependById(prependToElementId, prependElement) {
    const element = document.getElementById(prependToElementId);
    return this.prepend(element, prependElement);
  }  

  /**
   * Remove element from dom.
   */
  remove(element) {
    if (element) {
      element.remove();
    }
  }

  /**
   * Remove element from dom.
   */
  removeById(elementId) {
    const element = document.getElementById(elementId);
    return this.remove(element);
  }  

  /**
   * Remove the child from parent.
   */
  removeChild(parentElement, childElement) {
    if (parentElement) {
      parentElement.removeChild(childElement);
    }
  }

  /**
   * Remove the child from parent.
   */
  removeChildById(parentElementId, childElement) {
    const element = document.getElementById(parentElementId);
    return this.removeChild(element, childElement);
  }  

  /** Replace the old child with the new one in the parent */
  replaceChild(parentElement, newChildElement, oldChildElement) {
    if (parentElement) {
      parentElement.replaceChild(newChildElement, oldChildElement);
    }
  }

  /** Replace the old child with the new one in the parent */
  replaceChildById(parentElementId, newChildElement, oldChildElement) {
    const element = document.getElementById(parentElementId);
    return this.replaceChild(element, newChildElement, oldChildElement);
  }  

  /**
   * Replaces the specified dom element with the new element.
   */
  replaceWith(elementToReplace, newElement) {
    if (elementToReplace) {
      kameHouse.jq(elementToReplace).replaceWith(newElement);
    }
  }

  /**
   * Replaces the specified dom element with the new element.
   */
  replaceWithById(elementToReplaceId, newElement) {
    const element = document.getElementById(elementToReplaceId);
    return this.replaceWith(element, newElement);
  }  

  /** Set an attribute of an element */
  setBackgroundImage(element, imgUrl) {
    if (element) {
      this.setStyle(element, "background-image", "url('" + imgUrl + "')");
    }
  }

  /** Set an attribute of an element */
  setAttribute(element, attrKey, attrVal) {
    if (element) {
      element.setAttribute(attrKey, attrVal);
    }
  }

  /** Set an attribute of an element */
  setAttributeById(elementId, attrKey, attrVal) {
    const element = document.getElementById(elementId);
    return this.setAttribute(element, attrKey, attrVal);
  }

  /**
   * Set click function in an element.
   */
  setClick(element, data, clickFunction) {
    if (element) {
      element.addEventListener("click", (event) => {
        clickFunction(event, data);
      });
    }
  }

  /**
   * Set click function in an element.
   */
  setClickById(elementId, data, clickFunction) {
    const element = document.getElementById(elementId);
    return this.setClick(element, data, clickFunction);
  }  

  /** Set the display of the element */
  setDisplay(element, displayValue) {
    if (element) {
      element.style.display = displayValue;
    }
  }  

  /** Set the display of the element */
  setDisplayById(elementId, displayValue) {
    const element = document.getElementById(elementId);
    return this.setDisplay(element, displayValue);
  }    

  /** Set the html to the element */
  setHtml(element, html) {
    if (!kameHouse.core.isEmpty(html) && element) {
      kameHouse.jq(element).html(html);
    }
  }

  /** Set the html to the element */
  setHtmlById(elementId, html) {
    const element = document.getElementById(elementId);
    return this.setHtml(element, html);
  }

  /** Set the id of an element */
  setId(element, id) {
    if (element) {
      element.id = id;
    }
  }

  /** Set the style for the element */
  setStyle(element, styleProperty, stylePropertyValue) {
    if (element) {
      element.style[styleProperty] = stylePropertyValue;
    }
  }

  /** Set the style for the element */
  setStyleById(elementId, styleProperty, stylePropertyValue) {
    const element = document.getElementById(elementId);
    return this.setStyle(element, styleProperty, stylePropertyValue);
  }  

  /** Set the text to the element */
  setText(element, text) {
    if (!kameHouse.core.isEmpty(text) && element) {
      element.textContent = text;
    }
  }

  /** Set the text to the element */
  setTextById(elementId, text) {
    const element = document.getElementById(elementId);
    return this.setText(element, text);
  }

  /** Set the value of an element */
  setValue(element, val) {
    if (element) {
      element.value = val;
    }
  }

  /** Set the value of an element */
  setValueById(elementId, val) {
    const element = document.getElementById(elementId);
    return this.setValue(element, val);
  }  

  /** Toggle the visibility of all elements of classname */
  toggleClass(className) {
    kameHouse.jq('.' + className).toggle();
  }

  /** Toggle a class on the element */
  toggleClassOnElement(element, className) {
    if (element) {
      element.classList.toggle(className);
    }
  }

  /** Toggle a class on the element */
  toggleClassOnElementById(elementId, className) {
    const element = document.getElementById(elementId);
    return this.toggleClassOnElement(element, className);
  }  

  /**
   * Toggle visibility of an element.
   */
  toggleElement(element, visible) {
    if (element) {
      kameHouse.jq(element).toggle(visible);
    }
  }

  /**
   * Toggle visibility of an element.
   */
  toggleElementById(elementId, visible) {
    const element = document.getElementById(elementId);
    return this.toggleElement(element, visible);
  }

  /** Get DOM elements ************************** */
  /**
   * Get 'a' html element.
   */
  getA(attr, html) {
    return this.#getElement('a', attr, html);
  }

  /**
   * Get 'br' html element.
   */
  getBr() {
    return this.#getElement('br', null, null);
  }

  /**
   * Create a new button using the specified config object which should have a format: 
   * {
   *    attr: {
   *      id: "",
   *      class: ""
   *    },
   *    mobileClass: "",
   *    html: htmlObject,
   *    backgroundImg: "/kame-house/img/url.png",
   *    data: {},
   *    click: () => {}
   * }
   */
  getButton(config) {
    const btn = this.#getElement('button', config.attr, config.html);
    if (!kameHouse.core.isEmpty(config.mobileClass)) {
      kameHouse.util.mobile.exec(
        () => {},
        () => {kameHouse.util.dom.classListAdd(btn, config.mobileClass)}
      );
    }
    this.setClick(btn, config.data, config.click);
    if (config.backgroundImg) {
      this.setBackgroundImage(btn, config.backgroundImg);
    }
    return btn;
  }

  /**
   * Get 'div' html element.
   */
  getDiv(attr, html) {
    return this.#getElement('div', attr, html);
  }

  /**
   * Create a new image using the specified config object which should have a format: 
   * {
   *    id: "",
   *    src: "",
   *    className: "",
   *    alt: ""
   * }
   */
  getImg(config) {
    const img = new Image();
    if (!kameHouse.core.isEmpty(config.id)) {
      img.id = config.id;
    }
    img.src = config.src;
    img.className = config.className;
    img.alt = config.alt;
    img.title = config.alt;
    return img;
  }

  /**
   * Get 'input' html element.
   */
  getInput(attr, html) {
    return this.#getElement('input', attr, html);
  }

  /**
   * Get 'label' html element.
   */
  getLabel(attr, html) {
    return this.#getElement('label', attr, html);
  }

  /**
   * Get 'li' html element.
   */
  getLi(attr, html) {
    return this.#getElement('li', attr, html);
  }
  
  /**
   * Get 'option' html element.
   */
  getOption(attr, html) {
    return this.#getElement('option', attr, html);
  }

  /**
   * Get 'p' html element.
   */
  getP(attr, html) {
    return this.#getElement('p', attr, html);
  }

  /**
   * Get 'select' html element.
   */
  getSelect(attr, html) {
    return this.#getElement('select', attr, html);
  }

  /**
   * Get 'span' html element.
   */
  getSpan(attr, html) {
    return this.#getElement('span', attr, html);
  }

  /**
   * Get 'tbody' html element.
   */
  getTbody(attr, html) {
    return this.#getElement('tbody', attr, html);
  }

  /**
   * Get 'textarea' html element.
   */
  getTextArea(attr, html) {
    return this.#getElement('textarea', attr, html);
  }

  /**
   * Get 'td' html element.
   */
  getTd(attr, html) {
    return this.#getElement('td', attr, html);
  }

  /**
   * Returns a <tr> with the specified attributes and html content. 
   * Pass the attribute object such as:
   * kameHouse.util.dom.getTr({
   *   id: "my-id",
   *   class: "class1 class2"
   * }, htmlContent);
   */
  getTr(attr, html) {
    return this.#getElement('tr', attr, html);
  }

  /** Shorthand used in several places to create dynamic table rows */
  getTrTd(html) {
    return this.getTr(null, this.getTd(null, html));
  }

  /** Create an element with the specified tag, attributes and html */
  #getElement(tagType, attr, html) {
    const element = document.createElement(tagType);
    this.#setAttributes(element, attr);
    this.setHtml(element, html);
    return element;
  }

  /** Set the attributes to the element */
  #setAttributes(element, attr) {
    if (!kameHouse.core.isEmpty(attr)) {
      for (const [key, value] of Object.entries(attr)) {
        element.setAttribute(key, value);
      }
    }
  }

} // KameHouseDomUtils

/** 
 * Functionality to retrieve files from the server.
 * 
 * @author nbrest
 */
 class KameHouseFetchUtils {

  /**
   * Load an html snippet to insert to the dom or use as a template.
   * 
   * Declare the caller function as async
   * and call this with await kameHouse.util.fetch.loadHtmlSnippet(...);
   */
  async loadHtmlSnippet(htmlSnippetPath) {
    kameHouse.logger.info("Fetching html snippet: " + htmlSnippetPath, null);
    const htmlSnippetResponse = await fetch(htmlSnippetPath);
    return htmlSnippetResponse.text();
  }

  /**
   * Load a file as a text string.
   * 
   * Declare the caller function as async
   * and call this with await kameHouse.util.fetch.loadFile(...);
   */
  async loadFile(filePath) {
    kameHouse.logger.info("Fetching file: " + filePath, null);
    const file = await fetch(filePath);
    return file.text();
  }

  /**
   * Loads a file with a configurable timeout.
   */
  async loadFileWithTimeout(filePath, timeout) {
    const DEFAULT_TIMEOUT = 60000;
    const options = {
      timeout: null
    };
    if (kameHouse.core.isEmpty(timeout)) {
      options.timeout = DEFAULT_TIMEOUT;
    } else {
      options.timeout = timeout;
    }
    const controller = new AbortController();
    const id = setTimeout(() => controller.abort(), options.timeout);
    try {
      kameHouse.logger.info("Fetching file: " + filePath, null);
      const response = await fetch(filePath, {
        ...options,
        signal: controller.signal  
      });
      clearTimeout(id);
      return response.text();
    } catch (error) {
      kameHouse.logger.error("Error executing fetch: " + error, null);
      return '{"code": 404, "message": "Error executing fetch to ' + filePath + '"}';
    }
  }

  /** Get a js script from the server. */
  getScript(scriptPath, successCallback) { 
    kameHouse.logger.info("Loading script: " + scriptPath, null);
    kameHouse.jq.getScript(scriptPath)
    .done((script, textStatus) => {
      kameHouse.logger.debug("Loaded successfully script: " + scriptPath, null);
      if (kameHouse.core.isFunction(successCallback)) {
        successCallback();
      }
    })
    .fail((jqxhr, settings, exception) => {
      kameHouse.logger.info("Error loading script: " + scriptPath, null);
      kameHouse.logger.info("jqxhr.readyState: " + jqxhr.readyState, null);
      kameHouse.logger.info("jqxhr.status: " + jqxhr.status, null);
      kameHouse.logger.info("jqxhr.statusText: " + jqxhr.statusText, null);
      kameHouse.logger.trace("jqxhr.responseText: " + jqxhr.responseText, null);
      kameHouse.logger.info("settings: " + settings, null);
      kameHouse.logger.info("exception:", null);
      console.error(exception);
    });
  }

} // KameHouseFetchUtils

/** 
 * Functionality related to file and filename manipulation. 
 * 
 * @author nbrest
 */
class KameHouseFileUtils {

  /** 
   * Get the last part of the absolute filename 
   * Split the filename into an array based on the path separators '/' and '\'
   */
  getShortFilename(filename) { return filename.split(/[\\/]+/).pop(); }

} // KameHouseFileUtils

/**
 * Functionality for the native mobile app.
 * 
 * @author nbrest
 */
class KameHouseMobileUtils {

  #isMobileAppStatus = false;

  /**
   * Init kamehouse mobile utils.
   */
  init() {
    this.#loadCordovaModule();
    this.configureApp();
    this.#loadGlobalMobile();
    this.setMobileEventListeners(
      () => {
        const message = "KameHouse sent to background";
        kameHouse.logger.debug(message, kameHouse.logger.getGreenText(message));
      }, 
      () => {
        const message = "KameHouse sent to foreground";
        kameHouse.logger.debug(message, kameHouse.logger.getCyanText(message));
      }
    );
  }

  /**
   * Set mobile event listeners.
   */
  setMobileEventListeners(onPauseFunction, onResumeFunction) {
    this.exec(
      null,
      () => {
        kameHouse.logger.info("Setting mobile app event handlers", null);
        document.addEventListener("pause", () => {this.#onPause(onPauseFunction)} , false);
        document.addEventListener("resume", () => {this.#onResume(onResumeFunction)}, false);   
      }
    )
  }

  /**
   * Configure kamehouse for running either as a webapp or mobile native app.
   */
  configureApp() {
    kameHouse.ready(() => {
      this.exec(
        () => {this.#configureWebAppOnlyElements();}, 
        () => {this.#configureMobileOnlyElements();}
      );
    });
  }

  /**
   * Set the window location differently depending if running on web or mobile app.
   */
  windowLocation(webLocation, mobileLocation) {
    return this.exec(
      () => {window.location=webLocation},
      () => {window.location=mobileLocation}
    )
  }

  /**
   * Open url link.
   */
  windowOpen(url, targetWeb) {
    return this.exec(
      () => {window.open(url, targetWeb)},
      () => {window.open(kameHouse.extension.mobile.core.getSelectedBackendServerUrl() + url)}
    )
  }

  /**
   * Executes the first parmeter function only when running on the web app.
   * Executes the second parameter function only when running as a mobile app.
   */
  exec(webFunction, mobileFunction) {
    if (!this.#isMobileApp()) {
      if (kameHouse.core.isFunction(webFunction)) {
        return webFunction(); 
      }
    } else {
      try {
        if (kameHouse.core.isFunction(mobileFunction)) {
          return mobileFunction(); 
        }
      } catch (error) {
        kameHouse.logger.error("Unexpected error executing mobile function. Error: " + error, null);
        alert("Unexpected error executing mobile function. Error: " + error)
        return null;
      }
    }
  }

  /**
   * Configure elements that only need to be rendered on the web app app.
   */
  #configureWebAppOnlyElements() {
    kameHouse.logger.debug("Configuring webapp only elements", null);
    this.#disableMobileOnlyElements();
  }

  /**
   * Configure elements that only need to be rendered on the mobile app.
   */
  #configureMobileOnlyElements() {
    kameHouse.logger.debug("Configuring mobile app only elements", null);
    kameHouse.util.module.waitForModules(["kameHouseMobile"], () => {
      this.#disableWebAppOnlyElements();
      this.#addMobileClassOnElements();
    });
  }

  /**
   * Disable mobile only elements in webapp view.
   */
  #disableMobileOnlyElements() {
    kameHouse.logger.debug("Disabling mobile only elements in webapp view", null);
    const mobileOnlyElements = document.getElementsByClassName("kh-mobile-only");
    for (const mobileOnlyElement of mobileOnlyElements) {
      kameHouse.util.dom.classListAdd(mobileOnlyElement, "hidden-kh");
      kameHouse.util.dom.classListRemove(mobileOnlyElement, "kh-mobile-only");
    }
  }

  /**
   * Disable webapp only elements in mobile app.
   */
  #disableWebAppOnlyElements() {
    kameHouse.logger.debug("Disabling webapp only elements in mobile app view", null);
    const mobileOnlyElements = document.getElementsByClassName("kh-mobile-hidden");
    for (const mobileOnlyElement of mobileOnlyElements) {
      kameHouse.util.dom.classListAdd(mobileOnlyElement, "hidden-kh");
      kameHouse.util.dom.classListRemove(mobileOnlyElement, "kh-mobile-hidden");
    }
  }

  /**
   * Disable hover animations on image buttons on mobile.
   */
  #addMobileClassOnElements() {
    kameHouse.logger.debug("Adding mobile class on elements", null);
    this.#addMobileClass("img-btn-kh");
    this.#addMobileClass("link-image-img");
    this.#addMobileClass("collapsible-kh");
    this.#addMobileClass("debug-mode-client-table-header-btn");
    const vlcPlayerButtons = document.getElementsByClassName("vlc-player-btn");
    for (const vlcPlayerButton of vlcPlayerButtons) {
      kameHouse.util.dom.classListAdd(vlcPlayerButton, "img-btn-kh-mobile");
    } 
  }

  /**
   * Add mobile class names.
   */
  #addMobileClass(className) {
    const elements = document.getElementsByClassName(className);
    for (const element of elements) {
      kameHouse.util.dom.classListAdd(element, className + "-mobile");
    }   
  }

  /**
   * Check if it's running on mobile app.
   */
  #isMobileApp() {
    return this.#isMobileAppStatus;
  }
  
  /**
   * Load cordova module.
   */
  #loadCordovaModule() {
    try {
      if (cordova) { 
        kameHouse.cordova = cordova;
        kameHouse.logger.info("cordova object is present. Running as a mobile app", null);
        this.#isMobileAppStatus = true;
        this.#addCordovaErrorHandler();
      } else {
        this.#setMockMobileAppStatus();
      }
    } catch (error) {
      this.#setMockMobileAppStatus();
    }
  }

  /**
   * Set mock movbile app status.
   */
  #setMockMobileAppStatus() {
    const urlParams = new URLSearchParams(window.location.search);
    const mockCordova = urlParams.get('mockCordova');
    if (mockCordova) {
      kameHouse.logger.info("mockCordova is set. Running as a mobile app", null);
      this.#isMobileAppStatus = true;
    } else {
      kameHouse.logger.info("cordova object is not present. Running as a webapp", null);
      this.#isMobileAppStatus = false;
    }
  }

  /**
   * Add cordova error handler.
   */
  #addCordovaErrorHandler() {
    kameHouse.logger.info("Adding cordova error handler", null);
    window.addEventListener("cordovacallbackerror", (event) => {
      const message = "Unexpected cordova error: " + kameHouse.json.stringify(event, null, null);
      kameHouse.logger.error(message, null);
      kameHouse.plugin.modal.basicModal.setHtml(message);
      kameHouse.plugin.modal.basicModal.open();
    });  
  }

  /**
   * Run onPause function.
   */
  #onPause(onPauseFunction) {
    if (kameHouse.core.isFunction(onPauseFunction)) {
      onPauseFunction();
    }
  }

  /**
   * Run onResume function.
   */
  #onResume(onResumeFunction) {
    if (kameHouse.core.isFunction(onResumeFunction)) {
      onResumeFunction();
    }
  }

  /**
   * Load global mobile.
   */
  #loadGlobalMobile() {
    this.exec(
      null,
      () => {
        kameHouse.util.fetch.getScript("/kame-house/lib/js/crypto-js.min.js", () => {
          kameHouse.util.fetch.getScript("/kame-house-mobile/kamehouse-mobile/js/kamehouse-mobile.js", () => {
            kameHouse.logger.info("Loaded kamehouse-mobile.js", null);
          }); 
        }); 
      }
    );
  }

} // KameHouseMobileUtils

/** 
 * Functionality to load different modules and control the dependencies between them.
 * KameHouse modules are sections of code that perform functionality that is usually required by
 * other separate parts of the code. Elements like kamehouse and groot sessions are modules.
 * Most (or all) other modules are registered as plugins or extensions.
 * For example the code to update the view on the header and footer needs to wait for the module
 * kamehouse session so that the session data is available to update the view.
 * A lot of code execution depends on waiting for the kamehouse debugger or kamehouse modal code
 * to be loaded. The modules framework synchronizes those dependencies.
 * 
 * @author nbrest
 */
class KameHouseModuleUtils {
  
  /** 
   * Object that determines which module is loaded. 
   * For example, when logger gets loaded, set modules.logger = true;
   * I use it in waitForModules() to check if a module is loaded or not.
   */
  #modules = {};

  /** Checks if the specified module is loaded */
  isModuleLoaded(moduleName) {
    if (kameHouse.core.isEmpty(this.#modules[moduleName])) {
      return false;
    }
    return this.#modules[moduleName];
  }

  /** Marks the specified module as loaded */
  setModuleLoaded(moduleName) {
    const message = "Module loaded: " + moduleName;
    const messageColored = "Module loaded: " + kameHouse.logger.getGreenText(moduleName);
    kameHouse.logger.info(message, messageColored);
    this.#modules[moduleName] = true;
  }

  /** 
   * Waits until all specified modules in the moduleNames array are loaded, 
   * then executes the specified init function.
   * Use this function in the main() of each page that requires modules like logger and httpClient
   * to be loaded before the main code is executed.
   */
  async waitForModules(moduleNames, initFunction) {
    let message;
    message = "Start waitForModules " + kameHouse.json.stringify(moduleNames, null, null) + ". modules status: " + kameHouse.json.stringify(this.#modules, null, null);
    kameHouse.logger.trace(message, null);

    const WAIT_FOR_MODULES_MS = 20;
    let areAllModulesLoaded = false;
    let loopCount = 0;
    while (!areAllModulesLoaded) {
      if (loopCount >= 150) {
        message = "Waiting waitForModules " + kameHouse.json.stringify(moduleNames, null, null) + ". modules status: " + kameHouse.json.stringify(this.#modules, null, null);
        kameHouse.logger.trace(message, null);
        loopCount = 0;
      }
      
      let isAnyModuleStillLoading = false;
      moduleNames.forEach((moduleName) => {
        if (!this.#modules[moduleName]) {
          isAnyModuleStillLoading = true;
        }
      });
      if (!isAnyModuleStillLoading) {
        areAllModulesLoaded = true;
      }
      loopCount++;
      // SLEEP IS IN MS!!
      await kameHouse.core.sleep(WAIT_FOR_MODULES_MS);
    }
    message = "*** Finished  waitForModules " + kameHouse.json.stringify(moduleNames, null, null) + " ***. modules status: " + kameHouse.json.stringify(this.#modules, null, null);
    kameHouse.logger.trace(message, null);
    if (kameHouse.core.isFunction(initFunction)) {
      initFunction();
    }
  }

} // KameHouseModuleUtils

/**
 * Manage generic kamehouse tabs (used for example in groot server manager).
 * 
 * @author nbrest
 */
 class KameHouseTabUtils {

  /**
   * Open the tab specified by its id.
   */
  openTab(selectedTabDivId, cookiePrefix) {
    // Set current-tab cookie
    kameHouse.util.cookies.setCookie(cookiePrefix + '-current-tab', selectedTabDivId, null);
    
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
  openTabFromCookies(cookiePrefix, defaultTab) {
    let currentTab = kameHouse.util.cookies.getCookie(cookiePrefix + '-current-tab');
    if (!currentTab || currentTab == '') {
      currentTab = defaultTab;
    }
    this.openTab(currentTab, cookiePrefix);
  }

} // KameHouseTabUtils

/** 
 * Functionality to manipulate tables. 
 * 
 * @author nbrest
 */
class KameHouseTableUtils {

  /** 
   * Filter table rows based on the specified filter string. Shouldn't filter the header row. 
   * Toggles a maximum of maxRows.
   * Set skipHiddenRows to process only currently visible rows.   
   */
  filterTableRows(filterString, tableBodyId, maxRows, skipHiddenRows) {
    const table = document.getElementById(tableBodyId) as HTMLTableElement;
    if (kameHouse.core.isEmpty(table)) {
      return;
    }
    const rows = table.rows;
    if (kameHouse.core.isEmpty(maxRows) || maxRows == "all") {
      maxRows = rows.length;
    }

    const regex = this.#getRegex(filterString);
    kameHouse.logger.trace("Filtering table " + tableBodyId, null);
    kameHouse.logger.trace("Total number of rows to filter " + rows.length, null);
    kameHouse.core.filter(rows, (index, element) => {
      const tr = element;
      const classList = tr.classList.value;
      if (kameHouse.core.isEmpty(classList) || !classList.includes("table-kh-header")) {
        // Filter if it's not the header row
        const trText = tr.textContent.toLowerCase();
        let shouldDisplayRow = regex.test(trText);
        if (skipHiddenRows && this.#isHiddenRow(tr)) {
          shouldDisplayRow = false;
        }
        if (maxRows > 0) {
          kameHouse.util.dom.toggleElement(tr, shouldDisplayRow);
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
  filterTableRowsByColumn(filterString, tableBodyId, columnIndex, maxRows, skipHiddenRows) {
    const table = document.getElementById(tableBodyId) as HTMLTableElement;
    const rows = table.rows;
    if (kameHouse.core.isEmpty(columnIndex)) {
      kameHouse.logger.trace("columnIndex not set. Using 0", null);
      columnIndex = 0;
    }
    if (kameHouse.core.isEmpty(maxRows) || maxRows == "all") {
      maxRows = rows.length;
    }
    const regex = this.#getRegex(filterString);
    kameHouse.logger.trace("Filtering table " + tableBodyId + " by column number " + columnIndex, null);
    kameHouse.logger.trace("Total number of rows to filter " + rows.length, null);
    kameHouse.core.filter(rows, (index, element) => {  
      const tr = element;
      const classList = tr.classList.value;
      if (kameHouse.core.isEmpty(classList) || !classList.includes("table-kh-header")) {
        // Filter if it's not the header row
        const td = tr.getElementsByTagName("td")[columnIndex];
        const tdText = td.textContent.toLowerCase();
        let shouldDisplayRow = regex.test(tdText);
        if (skipHiddenRows && this.#isHiddenRow(tr)) {
          shouldDisplayRow = false;
        }
        if (maxRows > 0) {
          kameHouse.util.dom.toggleElement(tr, shouldDisplayRow);
          maxRows--;
        }
      }
    });
  }

  /**
   * Sort the table by the specified column number. COLUMN NUMBER STARTS WITH 0!
   * 
   * getComparatorFunction() determines which type of sorting will be used depending on the dataType.
   * The dataType is the same type used in crud-manager.js to determine the column types.
   * For some columns, such as select, there's a separate sortType property set to determine the sorting type.
   * The default sorting type if not specified will be lexicographically, 
   * which also works for dates formatted as yyyy-mm-dd and timestamps formatted as yyyy-mm-dd hh:mm:ss 
   * 
   * sortDirection can either be "asc" or "desc"
   */
  sortTable(tableId, columnNumber, dataType, initialSortDirection, callback) {
    kameHouse.plugin.modal.loadingWheelModal.open("Sorting data...");
    const sortConfig = {
      numSortingCycles : 0,
      sorting : true,
      swapRows : false,
      swapCount : 0,
      currentRowIndex : null,
      currentRow : null,
      nextRow : null,
      sortDirection : this.#initSortDirection(initialSortDirection),
      directionSwitchCount : 0
    };
    setTimeout(() => {
      const table = document.getElementById(tableId) as HTMLTableElement;
      const rows = table.rows;
      const compareFunction = this.#getComparatorFunction(dataType);
      kameHouse.logger.trace("Sort table: [ tableId: " + tableId + ", columnNumber: " + columnNumber + ", dataType: " + dataType + ", initialSortDirection: " + initialSortDirection + ", sortDirection: " + sortConfig.sortDirection, null);  
      kameHouse.logger.trace("Started sorting process", null);
      while (sortConfig.sorting) {
        sortConfig.sorting = false;
        this.#processSortDirection(sortConfig, rows, columnNumber, compareFunction);
        this.#processSwapRows(sortConfig, rows, initialSortDirection);
        this.#processNumSortingCycles(sortConfig);
      }
      kameHouse.plugin.modal.loadingWheelModal.close();
      kameHouse.logger.trace("Finished sorting process. Sorting cycles: " + sortConfig.numSortingCycles + "; Swap count: " + sortConfig.swapCount, null);
      if (kameHouse.core.isFunction(callback)) {
        callback();
      }
    }, 50);
  }

  /**
   * Limit the number of rows displayed on the table. 
   * Set skipHiddenRows to process only currently visible rows.
   */
  limitRows(tableId, maxRows, skipHiddenRows) {
    const table = document.getElementById(tableId) as HTMLTableElement;
    const rows = table.rows;
    if (kameHouse.core.isEmpty(maxRows) || maxRows == "all") {
      maxRows = rows.length;
    }
    let shownRows = 0;
    for (let i = 1; i < rows.length; i++) { 
      if (skipHiddenRows && this.#isHiddenRow(rows[i])) {
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

  /**
   * Get the regex to filter the rows.
   */
  #getRegex(filterString) {
    filterString = filterString.toLowerCase();
    try {
      if (this.#isQuotedString(filterString)) {
        filterString = this.#removeQuotes(filterString);
      } else {
        filterString = this.#addAsterisksBetweenAllCharsToRegex(filterString);
      }
      return new RegExp(filterString);
    } catch (error) {
      kameHouse.logger.error("Error creating regex from filter string " + filterString, null);
      return /""/;
    }
  }

  /**
   * Check for double quotes at the beginning and end of the string.
   */
   #isQuotedString(string) {
    return string.startsWith("\"") && string.endsWith("\"");
  }

  /**
   * Remove the double quotes from the string.
   */
  #removeQuotes(string) {
    return string.substring(1, string.length-1);
  }

  /**
   * Adds .* between each character to expand the matching criteria of the regex.
   */
  #addAsterisksBetweenAllCharsToRegex(string) {
    return string.split('').join('.*').replace(/\s/g, '');
  }

  /**
   * Check if it's a hidden row.
   */
  #isHiddenRow(tr) {
    return tr.style.display == "none";
  }

  /**
   * Process sort direction in sort table.
   */
  #processSortDirection(sortConfig, rows, columnNumber, compareFunction) {
    if (sortConfig.sortDirection != null) {
      for (sortConfig.currentRowIndex = 2; sortConfig.currentRowIndex < (rows.length - 1); sortConfig.currentRowIndex++) {
        sortConfig.swapRows = false;
        sortConfig.currentRow = rows[sortConfig.currentRowIndex].getElementsByTagName("td")[columnNumber];
        sortConfig.nextRow = rows[sortConfig.currentRowIndex + 1].getElementsByTagName("td")[columnNumber];
        if (this.#shouldSwapRows(sortConfig.currentRow, sortConfig.nextRow, sortConfig.sortDirection, compareFunction)) {
          sortConfig.swapRows = true;
          break;
        }
      }
    }
  }

  /**
   * Process swap rows in sort table.
   */
  #processSwapRows(sortConfig, rows, initialSortDirection) {
    if (sortConfig.swapRows) {
      kameHouse.util.dom.insertBefore(rows[sortConfig.currentRowIndex].parentNode, rows[sortConfig.currentRowIndex + 1], rows[sortConfig.currentRowIndex]);
      sortConfig.sorting = true;
      sortConfig.swapCount++;
    } else if (sortConfig.directionSwitchCount < 2) {
      if (this.#shouldSwapDirection(sortConfig.swapCount, sortConfig.sortDirection, initialSortDirection)) {
        // if no sorting was done, swap sort direction, and sort reversely.
        if (sortConfig.sortDirection == "asc") {
          sortConfig.sortDirection = "desc";
        } else {
          sortConfig.sortDirection = "asc";
        }
        kameHouse.logger.trace("No sorting was done, swap sort direction, and sort reversely. sortDirection is now " + sortConfig.sortDirection, null);
        sortConfig.directionSwitchCount++;
        sortConfig.sorting = true;
      }
    }
  }

  /**
   * Process number of sorting cycles in sort table.
   */
  #processNumSortingCycles(sortConfig) {
    const MAX_SORTING_CYCLES = 100000;
    if (sortConfig.numSortingCycles > MAX_SORTING_CYCLES) {
      sortConfig.sorting = false;
      const message = "Ending sorting after " + MAX_SORTING_CYCLES + " sorting cycles. Something is VERY likely off with the sorting function. Breaking either infinite loop or a very inefficient sorting";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
    }
    sortConfig.numSortingCycles++;
  }

  /**
   * Check if it should swap the sorting direction.
   */
  #shouldSwapDirection(swapCount, sortDirection, initialSortDirection) {
    return swapCount == 0 && ((sortDirection == "asc" && initialSortDirection != "asc") 
      || (sortDirection == "desc" && initialSortDirection != "desc")
      || (sortDirection == null));
  }

  /**
   * Set initial direction.
   */
  #initSortDirection(initialSortDirection) {
    if (initialSortDirection != "asc" && initialSortDirection != "desc") {
      return null;
    }
    return initialSortDirection;
  }

  /**
   * Returns true if the current and next rows need to be swapped.
   */
  #shouldSwapRows(currentRow, nextRow, sortDirection, compareFunction) {
    return compareFunction(currentRow, nextRow, sortDirection);
  }

  /**
   * Get the sorting function depending on the data type.
   */
  #getComparatorFunction(dataType) {
    if (dataType == "number" || dataType == "id") {
      kameHouse.logger.trace("Using compareNumerically", null);
      return this.#compareNumerically;
    }

    kameHouse.logger.trace("Using compareLexicographically", null);
    return this.#compareLexicographically;
  }

  /**
   * Returns true if first parameter is higher than second lexicographically.
   */
  #compareLexicographically(currentRow, nextRow, sortDirection) {
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
  #compareNumerically(currentRow, nextRow, sortDirection) {
    const currentRowVal = parseInt(currentRow.innerHTML);
    const nextRowVal = parseInt(nextRow.innerHTML);
    if (sortDirection == "asc") {
      return currentRowVal > nextRowVal;
    } else {
      return currentRowVal < nextRowVal;
    }
  }

} // KameHouseTableUtils

/** 
 * Prototype for test functionality. 
 * 
 * @author nbrest
 */
class KameHouseTestUtils {

  /** Test the different log levels. */
  testLogLevel() {
    console.log("kameHouse.logger.getLogLevel(): " + kameHouse.logger.getLogLevel());
    kameHouse.logger.error("This is an ERROR message", kameHouse.logger.getRedText("This is an ERROR message"));
    kameHouse.logger.warn("This is a WARN message", kameHouse.logger.getYellowText("This is a WARN message"));
    kameHouse.logger.info("This is an INFO message", kameHouse.logger.getBlueText("This is an INFO message"));
    kameHouse.logger.debug("This is a DEBUG message", kameHouse.logger.getGreenText("This is a DEBUG message"));
    kameHouse.logger.trace("This is a TRACE message", kameHouse.logger.getCyanText("This is a TRACE message"));
  }

  /**
   * Test sleep function.
   */
  async testSleep() {
    kameHouse.logger.info("TEST SLEEP ------------- BEFORE " + new Date(), null);
    await kameHouse.core.sleep(3000);
    kameHouse.logger.info("TEST SLEEP ------------- AFTER  " + new Date(), null);
  }

} // KameHouseTestUtils

/**
 * TimeUtils utility object for manipulating time and dates.
 * 
 * @author nbrest
 */
class KameHouseTimeUtils {

  /** Get timestamp with client timezone for the specified date or current date if null. */
  getTimestamp(date) {
    if (kameHouse.core.isEmpty(date)) {
      date = new Date();
    }
    const offsetTime = date.getTimezoneOffset() * -1 * 60 * 1000;
    const currentDateTime = date.getTime();
    return new Date(currentDateTime + offsetTime).toISOString().replace("T", " ").slice(0, 19);
  }

  /** Convert input in seconds to hh:mm:ss output. */
  convertSecondsToHsMsSs(seconds) { 
    return new Date(seconds * 1000).toISOString().substring(11, 19); 
  }

  /** Get current timestamp with client timezone. */
  getDateWithTimezoneOffset(date) {
    const offsetTime = date.getTimezoneOffset() * -1 * 60 * 1000;
    const currentDateTime = date.getTime();
    return new Date(currentDateTime + offsetTime);
  }

  /**
   * Get the date from an epoch string.
   */
  getDateFromEpoch(epoch) {
    return new Date(parseInt(epoch));
  }

  /**
   * Checks if it's a valid date.
   */
  isValidDate(date) {
    return date instanceof Date;
  }

} // KameHouseTimeUtils

/** 
 * Methods to handle json objects.
 */
class KameHouseJson {
  
  /**
   * Parse string into json object.
   */
  parse(string) {
    try {
      return JSON.parse(string);
    } catch (error) {
      kameHouse.logger.warn("Error on JSON.parse(). Returning initial value. " + error, null);
    }
    return string;
  }

  /**
   * Convert json object into string.
   */
  stringify(object, replacer, identation) {
    try {
      return JSON.stringify(object, replacer, identation);
    } catch (error) {
      kameHouse.logger.warn("Error on JSON.stringify(). Returning initial value. " + error, null);
    }
    return object;
  }

} // KameHouseJson


/** 
 * Class that contains the logic for all the core global methods. 
 * Only add methods here that are truly global and I'd want them to be part of the js language.
 * If I don't want them to be native, I probably should add them to a more specific utils prototype.
 * 
 * @author nbrest
 */
class KameHouseCore {

  /** Checks if a variable is undefined or null or an empty string "". */
  isEmpty(val) {
    return val === undefined || val == null || val == "";
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
  isEmptyDeprecated(val) {
    const isUndefinedOrNull = this.isEmpty(val);
    const isEmptyString = !isUndefinedOrNull && val === "";
    const isEmptyArray = !isUndefinedOrNull && Array.isArray(val) && val.length <= 0;
    const isEmptyObject = !isUndefinedOrNull && Object.entries(val).length === 0 && val.constructor === Object;
    return isUndefinedOrNull || isEmptyString || isEmptyArray || isEmptyObject;
  }

  /** Returns true if the parameter variable is a fuction. */
  isFunction(expectedFunction) {
    return expectedFunction instanceof Function;
  } 

  /**
   * Check if the page requires authorization and sets a splashscreen when it does.
   */
  initAuthorizeUser() {
    if (!this.pageRequiresAuthorization()) {
      kameHouse.logger.debug("Page doesn't require authorization", null);
      return;
    }
    const authorizedRoles = this.getStringKameHouseData("authorized-roles");
    kameHouse.logger.debug("Page requires roles: " + authorizedRoles, null);
    this.#openKameHouseSplashScreen();
  }

  /**
   * Update dynamic html properties. Run this every time I insert a dynamic html element like loading an html snippet.
   */
  configDynamicHtml() {
    kameHouse.logger.debug("Configuring dynamic html", null);
    this.#setButtonBackgrounds();
    this.#disablePageRefreshOnForms();
    kameHouse.util.mobile.configureApp();
  }

  /**
   * Get session from the backend.
   */
  loadSession() {
    kameHouse.logger.info("Loading kamehouse session", null);
    const SESSION_STATUS_URL = "/kame-house/api/v1/ui/session/status";
    const config = kameHouse.http.getConfig();
    config.timeout = 30;
    kameHouse.http.get(config, SESSION_STATUS_URL, null, null,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.info("KameHouse session: " + kameHouse.json.stringify(responseBody, null, null), null);
        kameHouse.session = responseBody;
        kameHouse.util.module.setModuleLoaded("kameHouseSession");
        if (!this.isGRootPage()) {
          this.completeAuthorizeUser(responseCode, responseBody);
        }
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        const message = "Error retrieving current session information.";
        kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
        kameHouse.session = new SessionStatus();
        kameHouse.util.module.setModuleLoaded("kameHouseSession");
        if (!this.isGRootPage()) {
          this.completeAuthorizeUser(responseCode, responseBody);
        }
      }
    );
  }

  /** 
   * Load header.
   */
  loadHeader() {
    const skipHeader = this.getBooleanKameHouseData("skip-header");
    if (!skipHeader) {
      kameHouse.util.fetch.getScript("/kame-house/kamehouse/js/kamehouse-header.js", () => {
        kameHouse.header = new KameHouseHeader();
        kameHouse.header.load();
      });
    } else {
      kameHouse.logger.info("Skip header kamehouse data set to true", null);
    }
  }

  /** 
   * Load footer.
   */
  loadFooter() {
    const skipFooter = this.getBooleanKameHouseData("skip-footer");
    if (!skipFooter) {
      kameHouse.util.fetch.getScript("/kame-house/kamehouse/js/kamehouse-footer.js", () => {
        kameHouse.footer = new KameHouseFooter();
        kameHouse.footer.load();
      });
    } else {
      kameHouse.logger.info("Skip footer kamehouse data set to true", null);
    }
  }  

  /**
   * Load sticky back to top.
   */
  loadStickyBackToTop() {
    const skipStickyBackToTop = this.getBooleanKameHouseData("skip-sticky-back-to-top");
    if (!skipStickyBackToTop) {
      kameHouse.util.fetch.getScript("/kame-house/kamehouse/js/plugin/kamehouse-sticky-back-to-top.js", () => {
        kameHouse.logger.info("Loaded sticky-back-to-top.js", null);
      });
    } else {
      kameHouse.logger.info("Skip sticky-back-to-top data set to true", null);
    }
  }

  /**
   * Load kamehouse modal.
   */
  loadKameHouseModal() {
    kameHouse.util.fetch.getScript("/kame-house/kamehouse/js/plugin/kamehouse-modal.js", () => {
      kameHouse.logger.info("Loaded kamehouse-modal.js", null);
    });
  }

  /**
   * Load kamehouse debugger.
   */
  loadKameHouseDebugger() {
    kameHouse.util.fetch.getScript("/kame-house/kamehouse/js/plugin/kamehouse-debugger.js", () => {
      kameHouse.logger.info("Loaded kamehouse-debugger.js", null);
    });
  }

  /**
   * Load kamehouse websockets module.
   */
  loadKameHouseWebSocket() {
    kameHouse.util.fetch.getScript("/kame-house/kamehouse/js/kamehouse-websocket.js", () => {
      kameHouse.logger.info("Loaded kamehouse-websocket.js", null);
      kameHouse.util.module.setModuleLoaded("kameHouseWebSocket");
    });
  }
  
  /**
   * Scroll back to the top of the page.
   */
  backToTop() {
    const currentHeight = document.documentElement.scrollTop || kameHouse.util.dom.getBody().scrollTop;
    if (currentHeight > 0) {
      window.scrollTo({
        top: 0,
        left: 0,
        behavior: 'smooth'
      });
    }
  }

  /** 
   * Scroll the specified div to it's top.
   * This method doesn't scroll the entire page, it scrolls the scrollable div to it's top.
   * To scroll the page to the top of a particular div, use kameHouse.core.scrollToTopById()
   */
  scrollToTopOfDivById(divId) {
    const divToScrollToTop = document.getElementById(divId);
    this.animate(divToScrollToTop, {
      scrollTop: 0
    }, '10');
  }

  /** 
   * Scroll the window to the top of a particular div or to the top of the body if no div specified.
   */
  scrollToTopById(divId) {
    let scrollPosition;
    if (this.isEmpty(divId)) {
      scrollPosition = 0;
    } else {
      scrollPosition = this.offsetById(divId).top;
    }
    this.animate(kameHouse.util.dom.getHtml(), {
      scrollTop: scrollPosition
    }, '10');
  }

  /**
   * Scroll top element.
   */
  scrollTop(element, val) {
    return kameHouse.jq(element).scrollTop(val);
  }  
    
  /**
   * Scroll top element by id.
   */
  scrollTopById(divId, val) {
    const element = document.getElementById(divId);
    return kameHouse.jq(element).scrollTop(val);
  }  

  /** 
   * Scroll the window to the bottom of a particular div or to the bottom of the body if no div specified.
   */
  scrollToBottomById(divId) {
    let scrollPosition;
    if (this.isEmpty(divId)) {
      scrollPosition = kameHouse.util.dom.getBody().scrollHeight;
    } else {
      scrollPosition = this.offsetById(divId).top + this.heightById(divId) - window.innerHeight;
    }
    this.animate(kameHouse.util.dom.getHtml(), {
      scrollTop: scrollPosition
    }, '10');
  }

  /**
   * Sleep the specified milliseconds.
   * This function needs to be called in an async method, with the await prefix. 
   * Example: await kameHouse.core.sleep(1000);
   */
  async sleep(ms) { 
    try {
      return await this.#sleepPromise(ms);
    } catch (error) {
      kameHouse.logger.error("Error during sleep(). Error: " + kameHouse.json.stringify(error, null, null), null);
    }
  }

  /**
   * Returns the boolean value of data-xx attributes defined in the script tag of kamehouse.js
   * The script tag id must be set to id="kamehouse-data"
   */
  getBooleanKameHouseData(dataAttributeName) {
    const kameHouseData = document.getElementById('kamehouse-data');
    if (!this.isEmpty(kameHouseData)) {
      let data = kameHouseData.getAttribute("data-" + dataAttributeName);
      if (!this.isEmpty(data)) {
        return data.toLowerCase() === "true";
      }
    }
    return false;
  }  
    
  /**
   * Returns the string value of data-xx attributes defined in the script tag of kamehouse.js
   * The script tag id must be set to id="kamehouse-data"
   */
  getStringKameHouseData(dataAttributeName) {
    const kameHouseData = document.getElementById('kamehouse-data');
    if (!this.isEmpty(kameHouseData)) {
      return kameHouseData.getAttribute("data-" + dataAttributeName);
    }
    return null;
  }

  /**
   * Returns the string array value of csv data-xx attributes defined in the script tag of kamehouse.js
   * The script tag id must be set to id="kamehouse-data"
   */
  getArrayKameHouseData(dataAttributeName) {
    const kameHouseData = document.getElementById('kamehouse-data');
    if (!this.isEmpty(kameHouseData)) {
      const dataString = kameHouseData.getAttribute("data-" + dataAttributeName);
      if (!this.isEmpty(dataString)) {
        return dataString.split(",");
      }
    }
    return [];
  }

  /** 
   * Replaces bash colors in the input string for the equivalent css styled color.
   * @deprecated moved logic to convert bash to html to the backend.
   */
  convertBashColorsToHtml(bashOutput) {
    const colorMappings = {
      '\\[0;30m' : '<span style="color:black">',  
      '\\[1;30m' : '<span style="color:black">',
      '\\[0;31m' : '<span style="color:red">', 
      '\\[1;31m' : '<span style="color:red">', 
      '\\[0;32m' : '<span style="color:green">', 
      '\\[00;32m' : '<span style="color:green">',   
      '\\[1;32m' : '<span style="color:green">',
      '\\[0;1;32m' : '', // remove these in-the-middle-of green span symbols on build-kamehouse
      '\\[0;33m' : '<span style="color:yellow">',
      '\\[1;33m' : '<span style="color:yellow">',
      '\\[0;1;33m' : '', // remove these in-the-middle-of yellow span symbols on build-kamehouse
      '\\[0;34m' : '<span style="color:#3996ff">',
      '\\[1;34m' : '<span style="color:#3996ff">',
      '\\[0;35m' : '<span style="color:purple">',
      '\\[1;35m' : '<span style="color:purple">',
      '\\[0;36m' : '<span style="color:cyan">',
      '\\[1;36m' : '<span style="color:cyan">',
      '\\[36m' : '<span style="color:cyan">',
      '\\[0;37m' : '<span style="color:white">',
      '\\[1;37m' : '<span style="color:white">',
      '\\[0;39m' : '<span style="color:gray">',
      '\\[1;39m' : '<span style="color:gray">',
      '\\[1;32;49m' : '<span style="color:lightgreen">',  
      '\\[0m' : '</span>',
      '\\[00m' : '</span>',
      '\\[1m' : '</span>',
      '\\[0;1m' : '</span>',
      '\\[m'   : '</span>'
    };
    let htmlOutput = bashOutput;
    
    Object.entries(colorMappings).forEach(([bashColor, htmlColor]) => {
      const regex = new RegExp(bashColor,"g");
      htmlOutput = htmlOutput.replace(regex, htmlColor)
    });
    // Remove the special character added in my bash color mappings
    htmlOutput = htmlOutput.replace(/""/g, "");
    htmlOutput = htmlOutput.replace(/\x1B/g, "");
    htmlOutput = htmlOutput.replace(/\x1b/g, "");
    htmlOutput = htmlOutput.replace(/"<\/span>ain\]"/g, "[main]");

    return htmlOutput;
  }  

  /**
   * Returns true if the page requires authorization.
   */
  pageRequiresAuthorization() {
    const authorizedRoles = this.getStringKameHouseData("authorized-roles");
    return !this.isEmpty(authorizedRoles);
  }  

  /**
   * Add a global error handler for uncaught exceptions, specially useful to see them in debug mode in mobile app.
   */
  setGlobalErrorHandler() {
    kameHouse.logger.info("Setting global kamehouse error handler", null);
    window.addEventListener("error", (errorEvent) => { this.#handleErrorEvent(errorEvent); });
    window.addEventListener("unhandledrejection", (rejectionEvent) => { this.#handleRejectionEvent(rejectionEvent); });
  }

  /**
   * After the session is loaded, checks if the user is authorized and closes splashscreen or redirects to login.
   * Call this function after the session is loaded.
   */
  completeAuthorizeUser(responseCode, responseBody) {
    if (!this.pageRequiresAuthorization()) {
      kameHouse.logger.trace("Page doesn't require authorization. Exiting complete authorize user", null);
      return;
    }
    let loginUrl = "/kame-house/login.html?unauthorizedPageAccess=true";
    let roles = kameHouse.session.roles;
    if (this.isGRootPage()) {
      loginUrl = "/kame-house/groot/login.html?unauthorizedPageAccess=true";
      roles = kameHouse.extension.groot.session.roles;
    }

    let mobileSettingsUrl = "/kame-house-mobile/settings.html?unauthorizedPageAccess=true&responseCode=" + responseCode;
    if (this.#isUnableToConnectToBackend(responseCode, responseBody)) {
      mobileSettingsUrl = mobileSettingsUrl + "&requestTimeout=true";
    }
    if (responseCode == "-2") {
      mobileSettingsUrl = mobileSettingsUrl + "&sslError=true";
    }
    if (this.isEmpty(roles)) {
      kameHouse.util.mobile.windowLocation(loginUrl, mobileSettingsUrl);
      return;
    }
    const authorizedRoles = this.getStringKameHouseData("authorized-roles");
    let isAuthorized = false;
    roles.forEach((userRole) => {
      if (authorizedRoles.includes(userRole)) {
        isAuthorized = true;
      }
    });

    if (isAuthorized) {
      kameHouse.logger.debug("User is authorized to access this page", null);
      kameHouse.util.dom.classListRemove(kameHouse.util.dom.getBody(), "hidden-kh");
      kameHouse.util.dom.removeById('kamehouse-splashscreen');  
    } else {
      kameHouse.util.mobile.windowLocation(loginUrl, mobileSettingsUrl);
    }
  }

  /**
   * Redirect the user to the url.
   */
  windowLocation(url) {
    window.location=url;
  }

  /**
   * Redirect the user to the url.
   */
  windowLocationHref(url) {
    window.location.href=url;
  }

  /**
   * Filter elements.
   */
  filter(element, filterFunction) {
    if (element) {
      kameHouse.jq(element).filter((index, element) => {filterFunction(index, element)});
    }
  }

  /**
   * Filter elements by id.
   */
  filterById(elementId, filterFunction) {
    const element = document.getElementById(elementId);
    this.filter(element, filterFunction);
  }

  /**
   * Animate element.
   */
  animate(element, config, duration) {
    if (element) {
      kameHouse.jq(element).animate(config, duration);
    }
  }

  /**
   * Animate element by id.
   */
  animateById(elementId, config, duration) {
    const element = document.getElementById(elementId);
    this.animate(element, config, duration);
  }

  /**
   * Get element offset.
   */
  offset(element) {
    if (element) {
      return kameHouse.jq(element).offset();
    }
    return null;
  }

  /**
   * Get element offset by id.
   */
  offsetById(elementId) {
    const element = document.getElementById(elementId);
    return this.offset(element);
  }

  /**
   * Get element height.
   */
  height(element) {
    if (element) {
      return kameHouse.jq(element).height();
    }
    return null;
  }

  /**
   * Get element height by id.
   */
  heightById(elementId) {
    const element = document.getElementById(elementId);
    return this.height(element);
  }

  /**
   * Returns true when processing a groot page.
   */
  isGRootPage() {
    return window.location.href.includes("/kame-house/groot/") || window.location.href.includes("/kame-house-batcave/");
  }

  /**
   * Set the background of link-image elements.
   */
  #setButtonBackgrounds() {
    kameHouse.logger.debug("Setting button backgrounds", null);
    kameHouse.logger.debug("Setting link-image backgrounds", null);
    const linkImages = document.getElementsByClassName("link-image-img") as HTMLCollectionOf<HTMLElement>;
    for (const linkImage of linkImages) {
      const backgroundImg = linkImage.dataset['backgroundImg'];
      if (backgroundImg) {
        kameHouse.util.dom.setBackgroundImage(linkImage, backgroundImg);
      }
    }
    kameHouse.logger.debug("Setting img-btn-kh backgrounds", null);
    const imgBtns = document.getElementsByClassName("img-btn-kh") as HTMLCollectionOf<HTMLElement>;
    for (const imgBtn of imgBtns) {
      const backgroundImg = imgBtn.dataset['backgroundImg'];
      if (backgroundImg) {
        kameHouse.util.dom.setBackgroundImage(imgBtn, backgroundImg);
      }
    }
  }

  /**
   * Disable default page refresh on form submit.
   */
  #disablePageRefreshOnForms() {
    kameHouse.logger.debug("Disabling page refresh on in-page forms", null);
    const forms = document.getElementsByClassName("form-in-page-kh");
    for (const form of forms) {
      form.addEventListener('submit', (event) => {
        event.preventDefault();
      });
    }
  }

  /**
   * Returns a promise that simulates sleep.
   */
  #sleepPromise(ms) {
    const sleepPromise = new Promise((resolve, reject) => {setTimeout(resolve, ms)})
      .then(() => {})
      .catch((error) => {
        kameHouse.logger.error("Error during sleep(). Error: " + kameHouse.json.stringify(error, null, null), null);
      });
    
    return sleepPromise;
  }
  
  /**
   * Error event handler.
   */
  #handleErrorEvent(errorEvent) {
    const errorObject = {
      message: errorEvent.message,
      filename: errorEvent.filename,
      lineNumber: errorEvent.lineno,
      columnNumber: errorEvent.colno,
      error: errorEvent.error
    };
    const errorMessage = "Uncaught KameHouse error: " + kameHouse.json.stringify(errorObject, null, 2);
    console.log(errorMessage);
    kameHouse.util.module.waitForModules(["kameHouseModal"], () => {
      kameHouse.plugin.modal.basicModal.setHtml(errorMessage);
      kameHouse.plugin.modal.basicModal.open();
    });
    kameHouse.util.module.waitForModules(["kameHouseDebugger"], () => {
      kameHouse.logger.error(errorMessage, null);
    });
  }

  /**
   * Rejection event handler.
   */
  #handleRejectionEvent(rejectionEvent) {
    const errorMessage = "Uncaught KameHouse rejection: " + kameHouse.json.stringify(rejectionEvent, null, 2);
    console.log(errorMessage);
    kameHouse.util.module.waitForModules(["kameHouseModal"], () => {
      kameHouse.plugin.modal.basicModal.setHtml(errorMessage);
      kameHouse.plugin.modal.basicModal.open();
    });
    kameHouse.util.module.waitForModules(["kameHouseDebugger"], () => {
      kameHouse.logger.error(errorMessage, null);
    });
  }

  /**
   * Returns true if it's unable to connect to the backend.
   */
  #isUnableToConnectToBackend(responseCode, responseBody) {
    return (responseCode == "-3") || (responseCode == "-4") || (responseCode == "-5")
      || (responseCode == "-6") || (responseCode == "-7") || (responseCode == "-8")   
      || (responseCode == "-1" && responseBody.includes("Failed to connect to"));
  }

  /**
   * Open kamehouse splash screen.
   */
  #openKameHouseSplashScreen() {
    kameHouse.util.dom.classListAdd(kameHouse.util.dom.getBody(), "hidden-kh");
    const kameHouseSplashScreen = this.#getKameHouseSplashScreen();
    kameHouse.util.dom.insertBeforeBegin(kameHouseSplashScreen);
  }

  /**
   * Get kamehouse splash screen.
   */
  #getKameHouseSplashScreen() {
    const kameHouseSplashScreen = kameHouse.util.dom.getDiv({
      id: "kamehouse-splashscreen",
      class: "splashscreen-kh"
    }, null); 
    
    const img = kameHouse.util.dom.getImg({
      id: "kamehouse-splashscreen-img",
      src: "/kame-house/img/dbz/kamesenin-logo.png",
      className: "splashscreen-img-kh rotate-4",
      alt: "Loading KameHouse"
    });
    kameHouse.util.dom.append(kameHouseSplashScreen, img);

    const splashScreenWrapper = kameHouse.util.dom.getDiv({
      id: "kamehouse-splashscreen-wrapper"
    }, null); 
    kameHouse.util.dom.append(splashScreenWrapper, kameHouseSplashScreen);
    return splashScreenWrapper;
  }

} // KameHouseCore

/**
 * Log object to perform logging to the console on the frontend side.
 * 
 * Dependencies: kameHouse.util.time.
 * 
 * @author nbrest
 */
 class KameHouseLogger {

  /**
   * Log levels:
   * 
   * 0: DISABLED
   * 1: ERROR
   * 2: WARN
   * 3: INFO
   * 4: DEBUG
   * 5: TRACE
   * 
   * Default log level: INFO (3)
   */
  #logLevelNumber = 3;

  /**
   * Override the default log level from url parameters.
   */
  init() {
    this.info("Initializing logger", null);
    const urlParams = new URLSearchParams(window.location.search);
    const logLevelCookie = kameHouse.util.cookies.getCookie('kh-log-level');
    if (!kameHouse.core.isEmpty(logLevelCookie)) {
      this.info("Overriding logLevel with cookie logLevel: " + logLevelCookie, null);
      this.setLogLevel(logLevelCookie);
    }
    const logLevel = urlParams.get('logLevel');
    if (!kameHouse.core.isEmpty(logLevel)) {
      const logLevelNumberParam = this.#getLogLevelNumber(logLevel);
      this.info("Overriding logLevel with url parameter logLevel: " + logLevel + " mapped to logLevelNumber: " + logLevelNumberParam, null);
      this.setLogLevel(logLevelNumberParam);
    }
  }

  /**
   * Set the log level for the console in numeric value, based on the mapping shown above.
   */
  setLogLevel(levelNumber) {
    this.#logLevelNumber = levelNumber;
  }

  /**
   * Get the log level for the console in numeric value, based on the mapping shown above.
   */
  getLogLevel() {
    return this.#logLevelNumber;
  }
  
  /**
   * Get red text.
   */
  getRedText(text: String) {
    return this.#getSpanRed() + text + this.#getSpanEnd();
  }

  /**
   * Get yellow text.
   */
  getYellowText(text: String) {
    return this.#getSpanYellow() + text + this.#getSpanEnd();
  }

  /**
   * Get blue text.
   */
  getBlueText(text: String) {
    return this.#getSpanBlue() + text + this.#getSpanEnd();
  }

  /**
   * Get green text.
   */
  getGreenText(text: String) {
    return this.#getSpanGreen() + text + this.#getSpanEnd();
  }

  /**
   * Get cyan text.
   */
  getCyanText(text: String) {
    return this.#getSpanCyan() + text + this.#getSpanEnd();
  }

  /** Log an error message */
  error(message: String, coloredMessage: String) { this.#log("ERROR", message, coloredMessage); }

  /** Log a warn message */
  warn(message: String, coloredMessage: String) { this.#log("WARN", message, coloredMessage); }

  /** Log an info message */
  info(message: String, coloredMessage: String) { this.#log("INFO", message, coloredMessage); }

  /** Log a debug message */
  debug(message: String, coloredMessage: String) { this.#log("DEBUG", message, coloredMessage); }

  /** Log a trace message */
  trace(message: String, coloredMessage: String) { this.#log("TRACE", message, coloredMessage); }

  /**
   * Log an api call error to the console.
   */
  logApiError(config, url, responseBody, responseCode, responseDescription, responseHeaders) {
    const errorMessage = "http response: [ "
      + "'id' : '" + config.requestId + "', "
      + "'url' : '" + url + "', " 
      + "'responseCode' : '" + responseCode + "', "
      + "'responseDescription' : '" + responseDescription + "', "
      + "'responseHeaders' : '" + kameHouse.json.stringify(responseHeaders, null, null) + "', "
      + "'responseBody' : '" + kameHouse.json.stringify(responseBody, null, null) 
      + "' ]";
    this.error(errorMessage, null);
  }

  /**
   * Log an http request.
   */
  logHttpRequest(httpMethod, config, url, requestHeaders, requestBody) {
    this.debug("Http request: [ " 
    + "'id' : '" + config.requestId + "', "
    + "'url' : '" + url + "', "
    + "'method' : '" + httpMethod + "', "
    + "'config' : '" + kameHouse.json.stringify(config, null, null) + "', "
    + "'headers' : '" + this.maskSensitiveData(kameHouse.json.stringify(requestHeaders, null, null)) + "', "
    + "'body' : '" + this.maskSensitiveData(kameHouse.json.stringify(requestBody, null, null)) + "' ]", null);
  }
  
  /**
   * Log an http response.
   */
  logHttpResponse(config, url, responseBody, responseCode, responseDescription, responseHeaders) {
    this.debug("Http response: [ " 
    + "'id' : '" + config.requestId + "', "
    + "'url' : '" + url + "', "
    + "'responseCode' : '" + responseCode + "', "
    + "'responseDescription' : '" + responseDescription + "', "
    + "'responseHeaders' : '" + kameHouse.json.stringify(responseHeaders, null, null) + "', "
    + "'responseBody' : '" + kameHouse.json.stringify(responseBody, null, null) + "' ]", null);   
  }

  /**
   * Mask passwords, card details and any sensitive data in the message.
   */
  maskSensitiveData(message) {
    if (kameHouse.core.isEmpty(message)) {
      return;
    }
    const emptyOrWhiteSpaces = "(\\s)*";
    const anythingButDoubleQuote = '[^"]*';
    const passwordsRegex = new RegExp('"[p|P]assword"' + emptyOrWhiteSpaces + ':' + emptyOrWhiteSpaces + '"' + anythingButDoubleQuote + '"',"g");
    const basicAuthRegex = new RegExp('"[a|A]uthorization"' + emptyOrWhiteSpaces + ':' + emptyOrWhiteSpaces + '"Basic ' + anythingButDoubleQuote + '"',"g");
    const cvvRegex = new RegExp('"[c|C][v|V][v|V]"' + emptyOrWhiteSpaces + ':' + emptyOrWhiteSpaces + '"\\d{1,3}"',"g");
    return message.replace(passwordsRegex, '"password": "****"')
                  .replace(basicAuthRegex, '"Authorization": "Basic ****"')
                  .replace(cvvRegex, '"cvv": "***"');
  }

  /**
   * Log the entry into the debug mode console log table.
   */
  #logToDebugMode(logEntry) {
    const DEBUG_MODE_LOG_SIZE = 200;
    const debugModeConsoleLog = document.getElementById("debug-mode-console-log-entries");
    if (!kameHouse.core.isEmpty(debugModeConsoleLog)) {
      // Remove first log N entries
      let logEntriesSize = debugModeConsoleLog.childElementCount;
      while (logEntriesSize > DEBUG_MODE_LOG_SIZE) {
        kameHouse.util.dom.removeChild(debugModeConsoleLog, debugModeConsoleLog.firstChild);
        logEntriesSize = debugModeConsoleLog.childElementCount;
      }
      // Add new log entry
      kameHouse.util.dom.appendById("debug-mode-console-log-entries", this.#getLogEntryListItem(logEntry));
      // Scroll down log div
      this.#debugModeLogScroll();
    }
  }

  /**
   * Scroll to the last entries of the console log.
   */
  #debugModeLogScroll() {
    const scrollLogCheckbox = document.getElementById("debug-mode-scroll-log-checkbox") as HTMLInputElement;
    const scrollLog = scrollLogCheckbox.checked;
    if (!scrollLog) {
      return;
    }
    const logEntries = document.getElementById("debug-mode-console-log-entries");
    const height = logEntries.scrollHeight;
    kameHouse.core.animate(logEntries, {
      scrollTop: height
    }, 100);
  }
  
  /**
   * Get log entry list item.
   */
  #getLogEntryListItem(logEntry) {
    const li = kameHouse.util.dom.getLi({}, null);
    kameHouse.util.dom.setHtml(li, logEntry);
    return li;
  }

  /**
   * Get span end.
   */
  #getSpanEnd() {
    return "</span>";
  }

  /**
   * Get red span.
   */
  #getSpanRed() {
    return "<span style='color:red'>";
  }

  /**
   * Get yellow span.
   */
  #getSpanYellow() {
    return "<span style='color:yellow'>";
  }

  /**
   * Get blue span.
   */
  #getSpanBlue() {
    return "<span style='color:#3996ff'>";
  }

  /**
   * Get green span.
   */
  #getSpanGreen() {
    return "<span style='color:green'>";
  }

  /**
   * Get cyan span.
   */
  #getSpanCyan() {
    return "<span style='color:#00b2b2'>";
  }

  /**
   * Escape html content.
   */
  #escapeHtml(html) {
    return html.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;').replaceAll("'", '&#039;');
  }

  /** Log a specified message with the specified logging level. */
  #log(logLevel: String, message: String, coloredMessage: String) {
    if (kameHouse.core.isEmpty(logLevel)) {
      console.error("Invalid use of log(logLevel, message) function. LogLevel is missing.");
      return;
    }
    if (!message) {
      console.error("Invalid use of log(logLevel, message) function. Message is empty");
      return;
    }
    if (message.length > 1000) {
      message = message.slice(0, 1000) + "... [trimmed]";
    }
    const logLevelUpperCase = logLevel.toUpperCase();
    const timestamp = kameHouse.util.time.getTimestamp(null);
    const logEntry = timestamp + " - [" + logLevelUpperCase + "] - " + message;
    const logEntryForDebugMode = this.#buildLogEntryForDebug(timestamp, logLevelUpperCase, message, coloredMessage);
    if (logLevelUpperCase == "ERROR" && this.#logLevelNumber >= 1) {
      console.error(logEntry);
      this.#logToDebugMode(logEntryForDebugMode);
      return;
    }
    if (logLevelUpperCase == "WARN" && this.#logLevelNumber >= 2) {
      console.warn(logEntry);
      this.#logToDebugMode(logEntryForDebugMode);
      return;
    }
    if (logLevelUpperCase == "INFO" && this.#logLevelNumber >= 3) {
      console.info(logEntry);
      this.#logToDebugMode(logEntryForDebugMode);
      return;
    }
    if (logLevelUpperCase == "DEBUG" && this.#logLevelNumber >= 4) {
      // Use debug to log behavior, such as executing x method, selected x playlist, etc.
      console.debug(logEntry);
      this.#logToDebugMode(logEntryForDebugMode);
      return;
    }
    if (logLevelUpperCase == "TRACE" && this.#logLevelNumber >= 5) {
      // Use trace to log content such as responses from api calls. But use debug or info kameHouse.logger. trace prints a useless stack trace in the console that doesn't help.
      console.info(logEntry);
      this.#logToDebugMode(logEntryForDebugMode);
      return;
    }
  }

  /**
   * Build log entry for kamehouse debugger.
   */
  #buildLogEntryForDebug(timestamp, logLevel, message, coloredMessage) {
    const logLineTemplate = this.getCyanText(timestamp) + this.getBlueText(" - ") + "[" + this.#getLogLevelColored(logLevel) + "] " + this.getBlueText(" - ");
    if (!kameHouse.core.isEmpty(coloredMessage)) {
      return logLineTemplate + coloredMessage;
    }
    return logLineTemplate + this.#escapeHtml(message);
  }

  /**
   * Get log level colored.
   */
  #getLogLevelColored(logLevel) {
    if (logLevel == "ERROR") {
      return this.getRedText(logLevel);
    }
    if (logLevel == "WARN") {
      return this.getYellowText(logLevel);
    }
    if (logLevel == "INFO") {
      return this.getBlueText(logLevel);
    }
    if (logLevel == "DEBUG") {
      return this.getGreenText(logLevel);
    }
    if (logLevel == "TRACE") {
      return this.getCyanText(logLevel);
    }
    return logLevel;
  }

  /**
   * Get the log level number mapped to the specified log level string.
   */
  #getLogLevelNumber(logLevel) {
    const logLevelUpperCase = logLevel.toUpperCase();
    if (logLevelUpperCase == "DISABLED") {
      return 0;
    }
    if (logLevelUpperCase == "ERROR") {
      return 1;
    }
    if (logLevelUpperCase == "WARN") {
      return 2;
    }
    if (logLevelUpperCase == "INFO") {
      return 3;
    }
    if (logLevelUpperCase == "DEBUG") {
      return 4;
    }
    if (logLevelUpperCase == "TRACE") {
      return 5;
    }
    // default INFO
    return 3;
  }  

} // KameHouseLogger

/**
 * HttpClient object to perform http calls.
 * 
 * Dependencies: kameHouse.logger.
 * 
 * @author nbrest
 */
 class KameHouseHttpClient {

  #GET = "GET";
  #POST = "POST";
  #PUT = "PUT";
  #DELETE = "DELETE";
  #DEFAULT_TIMEOUT_MS = 60000;

  /** Execute an http GET request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription, responseHeaders) 
   * and errorCallback(responseBody, responseCode, responseDescription, responseHeaders) */
  get(config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    this.#httpRequest(this.#GET, config, url, requestHeaders, requestBody, successCallback, errorCallback);
  }

  /** Execute an http PUT request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription, responseHeaders) 
   * and errorCallback(responseBody, responseCode, responseDescription, responseHeaders) */
  put(config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    this.#httpRequest(this.#PUT, config, url, requestHeaders, requestBody, successCallback, errorCallback);
  }

  /** Execute an http POST request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription, responseHeaders) 
   * and errorCallback(responseBody, responseCode, responseDescription, responseHeaders) */
  post(config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    this.#httpRequest(this.#POST, config, url, requestHeaders, requestBody, successCallback, errorCallback);
  }

  /** Execute an http DELETE request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription, responseHeaders) 
   * and errorCallback(responseBody, responseCode, responseDescription, responseHeaders) */
  delete(config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    this.#httpRequest(this.#DELETE, config, url, requestHeaders, requestBody, successCallback, errorCallback);
  }

  /** Get request headers object with Url Encoded content type. */
  getUrlEncodedHeaders() {
    const requestHeaders = {
      Accept: "*/*"
    };
    requestHeaders['Content-Type'] = "application/x-www-form-urlencoded";
    return requestHeaders;
  }

  /** Get request headers object with application json content type. */
  getApplicationJsonHeaders() {
    const requestHeaders = {
      Accept: "*/*"
    };
    requestHeaders['Content-Type'] = 'application/json';
    return requestHeaders;
  }

  /**
   * Get encoded url parameters.
   */
  urlEncodeParams(params) {
    let urlEncodeParams = [];
    for (const key in params)
      if (params.hasOwnProperty(key)) {
        urlEncodeParams.push(encodeURIComponent(key) + "=" + encodeURIComponent(params[key]));
      }
    return urlEncodeParams.join("&");
  }

  /**
   * Encode url parameters.
   */
  urlEncode(param) {
    return encodeURIComponent(param);
  }

  /**
   * Check if it's url encoded request.
   */
  isUrlEncodedRequest(headers) {
    if (kameHouse.core.isEmpty(headers)) {
      return false;
    }
    let isUrlEncoded = false;
    for (const [key, value] of Object.entries<String>(headers)) {
      if (!kameHouse.core.isEmpty(key) && key.toLowerCase() == "content-type" && !kameHouse.core.isEmpty(value)) {
        if (value.toLowerCase() == "application/x-www-form-urlencoded") {
          isUrlEncoded = true;
        }
      }
    }
    return isUrlEncoded;
  }

  /**
   * Get the config object to pass to http request methods.
   */
  getConfig() {
    return {
      timeout: null
    }
  }

  /** Execute an http request with the specified http method. 
   * Implement and pass successCallback(responseBody, responseCode, responseDescription, responseHeaders) 
   * and errorCallback(responseBody, responseCode, responseDescription, responseHeaders)
   * Don't call this method directly, instead call the wrapper get(), post(), put(), delete() */
  #httpRequest(httpMethod, config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    const requestId = this.#generateRequestId();
    config.requestId = requestId;
    kameHouse.util.mobile.exec(
      () => {
        this.#webHttpRequest(httpMethod, config, url, requestHeaders, requestBody, successCallback, errorCallback);
      },
      () => {
        this.#mobileHttpRequest(httpMethod, config, url, requestHeaders, requestBody, successCallback, errorCallback);
      },
    );
  }

  /**
   * Execute http request on web.
   */
  #webHttpRequest(httpMethod, config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    kameHouse.logger.logHttpRequest(httpMethod, config, url, requestHeaders, requestBody);
    let requestTimeout = this.#DEFAULT_TIMEOUT_MS;
    if (!kameHouse.core.isEmpty(config.timeout)) {
      requestTimeout = config.timeout * 1000;
      kameHouse.logger.trace("Setting timeout for web request to " + requestTimeout, null);
    }
    if (kameHouse.core.isEmpty(requestBody)) {
      kameHouse.jq.ajax({
        type: httpMethod,
        url: url,
        headers: requestHeaders,
        timeout: requestTimeout,
        success: (data, status, xhr) => this.#processSuccess(config, url, data, status, xhr, successCallback),
        error: (jqXhr, textStatus, errorMessage) => this.#processError(config, url, jqXhr, textStatus, errorMessage, errorCallback)
      });
      return;
    }
    if (this.isUrlEncodedRequest(requestHeaders)) {
      const urlEncoded = url + "?" + this.urlEncodeParams(requestBody);
      kameHouse.jq.ajax({
        type: httpMethod,
        url: urlEncoded,
        headers: requestHeaders,
        timeout: requestTimeout,
        success: (data, status, xhr) => this.#processSuccess(config, urlEncoded, data, status, xhr, successCallback),
        error: (jqXhr, textStatus, errorMessage) => this.#processError(config, urlEncoded, jqXhr, textStatus, errorMessage, errorCallback)
      });
      return;
    }
    kameHouse.jq.ajax({
      type: httpMethod,
      url: url,
      data: kameHouse.json.stringify(requestBody, null, null),
      headers: requestHeaders,
      timeout: requestTimeout,
      success: (data, status, xhr) => this.#processSuccess(config, url, data, status, xhr, successCallback),
      error: (jqXhr, textStatus, errorMessage) => this.#processError(config, url, jqXhr, textStatus, errorMessage, errorCallback)
    });
  }
  
  /**
   * Execute http request on mobile.
   */
  #mobileHttpRequest(httpMethod, config, url, requestHeaders, requestBody, successCallback, errorCallback) {
    kameHouse.util.module.waitForModules(["kameHouseMobile"], () => {
      kameHouse.extension.mobile.core.mobileHttpRequst(httpMethod, config, url, requestHeaders, requestBody, successCallback, errorCallback);
    });
  }

  /**
   * Generate a random id for the http request. 
   */
  #generateRequestId() {
    return (Math.random() + 1).toString(36).slice(-8).toUpperCase();
  }

  /**
   * Get response headers.
   */
  #getResponseHeaders(xhr) {
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

  /** Process a successful response from the api call */
  #processSuccess(config, url, data, status, xhr, successCallback) {
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
    const responseHeaders = this.#getResponseHeaders(xhr);
    kameHouse.logger.logHttpResponse(config, url, responseBody, responseCode, responseDescription, responseHeaders);
    successCallback(responseBody, responseCode, responseDescription, responseHeaders);
  }

  /** Process an error response from the api call */
  #processError(config, url, jqXhr, textStatus, errorMessage, errorCallback) {
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
     const responseHeaders = this.#getResponseHeaders(jqXhr);
     kameHouse.logger.logApiError(config, url, responseBody, responseCode, responseDescription, responseHeaders);
     errorCallback(responseBody, responseCode, responseDescription, responseHeaders);
  }

} // KameHouseHttpClient

/**
 * Utility classes in KameHouse js framework.
 */
class KameHouseUtil {
  
  cookies = new KameHouseCookiesUtils();
  banner = new KameHouseBannerUtils();
  collapsibleDiv = new KameHouseCollapsibleDivUtils();
  cursor = new KameHouseCursorUtils();
  dom = new KameHouseDomUtils();
  fetch = new KameHouseFetchUtils();
  file = new KameHouseFileUtils();
  mobile = new KameHouseMobileUtils();
  module = new KameHouseModuleUtils();
  tab = new KameHouseTabUtils();
  table = new KameHouseTableUtils();
  test = new KameHouseTestUtils();
  time = new KameHouseTimeUtils();

} // KameHouseUtil

/**
 * KameHouse session status.
 */
class SessionStatus {
  username: string;
  firstName: string;
  lastName: string;
  server: string;
  sessionId: string;
  roles: string[];
} // SessionStatus

const kameHouse = new KameHouse();
kameHouse.ready(() => {kameHouse.init();});
