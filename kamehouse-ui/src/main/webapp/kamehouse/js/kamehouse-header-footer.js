/**
 * Header and Footer functions.
 * 
 * Dependencies: logger, httpClient.
 * 
 * @author nbrest
 */
var header;
var footer;

/**
 * Render header and footer.
 */
function renderHeaderAndFooter() {
  logger.info("Started initializing header and footer");
  header = new Header();
  header.renderHeader();
  footer = new Footer();
  footer.renderFooter();
  loadSessionStatus();
}

/** 
 * Get session status and update the header and footer status. 
 */
function loadSessionStatus() {
  const SESSION_STATUS_URL = "/kame-house/api/v1/ui/session/status";

  httpClient.get(SESSION_STATUS_URL, null,
    (responseBody, responseCode, responseDescription) => {
      logger.trace("Session Status: " + JSON.stringify(responseBody));
      kameHouse.session = responseBody;
      updateSessionStatus();
    },
    (responseBody, responseCode, responseDescription) => logger.error("Error retrieving current session information.")
  );
}

/**
 * Wait for the header and footer to be loaded and then update the session status.
 */
async function updateSessionStatus() {
  while (!header.isLoaded() && !footer.isLoaded()) {
    await sleep(1000);
  }
  header.updateLoginStatus();
  footer.updateFooterWithSessionInfo();
  bannerUtils.updateServerName();
}

/** Footer functionality */
function Footer() {

  this.isLoaded = isLoaded;
  this.renderFooter = renderFooter;
  this.updateFooterWithSessionInfo = updateFooterWithSessionInfo;

  let loaded = false;

  function isLoaded() { return loaded; }

  /** Renders the footer */
  function renderFooter() { 
    domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/header-footer/footer.css">');
    $(document).ready(() => {
      // load the footer after the other elements are loaded, if not it randomly puts the footer in the middle
      logger.info("Loading footer");
      domUtils.append($("body"), getFooterContainerDiv());
      domUtils.load($("#kamehouse-footer-container"), "/kame-house/kamehouse/html/kamehouse-footer.html", () => {
        mobileAppUtils.updateMobileElements();
        loaded = true;
        logger.info("Finished loading footer");
      });
    });
  }

  /** Update the server name, and build info in the footer */
  function updateFooterWithSessionInfo() {
    if (!isEmpty(kameHouse.session.server)) {
      domUtils.setHtml($("#footer-server-name"), kameHouse.session.server);
    }
    if (!isEmpty(kameHouse.session.buildVersion)) {
      domUtils.setHtml($("#footer-build-version"), kameHouse.session.buildVersion);
    }
    if (!isEmpty(kameHouse.session.buildDate)) {
      domUtils.setHtml($("#footer-build-date"), kameHouse.session.buildDate);
    }
  }

  function getFooterContainerDiv() {
    return domUtils.getDiv({
      id: "kamehouse-footer-container"
    });
  }
}

/** Header functionality */
function Header() {

  this.isLoaded = isLoaded;
  this.renderHeader = renderHeader;
  this.toggleHeaderNav = toggleHeaderNav;
  this.updateLoginStatus = updateLoginStatus;
  this.showGrootMenu = showGrootMenu;
  this.hideGrootMenu = hideGrootMenu;

  let loaded = false;

  function isLoaded() { return loaded; }
  
  /** Render the header */
  function renderHeader() {
    logger.info("Loading header");
    domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/header-footer/header.css">');
    $(document).ready(() => {
      // load the header after the other dom is ready to see if this fixes the very rare random header not loading
      domUtils.prepend($("body"), getHeaderContainerDiv());
      domUtils.load($("#kamehouse-header-container"), "/kame-house/kamehouse/html/kamehouse-header.html", () => {
        updateLoginStatus();
        mobileAppUtils.updateMobileElements();
        updateActiveTab();
        loaded = true;
        logger.info("Finished loading header");
      });
    });
  }

  /**
   * Set active tab in the menu.
   */
  function updateActiveTab() {
    const pageUrl = window.location.pathname;
    $("#kamehouse-header-container header .default-layout #header-menu a").toArray().forEach((navElement) => {
      const navItem = $(navElement);
      domUtils.removeClass(navItem, "active");
      
      if (pageUrl == "/kame-house/" || pageUrl == "/kame-house/index.html") {
        setActiveNavItem(navItem, "nav-home");
      }

      const pages = {
        "/kame-house/about" : "nav-about",
        "/kame-house/admin" : "nav-admin",
        "/kame-house/contact-us" : "nav-contact-us",
        "/kame-house/downloads" : "nav-downloads",
        "/kame-house-groot" : "nav-groot",
        "/kame-house-mobile" : "nav-mobile",
        "/kame-house/tennisworld" : "nav-tennisworld",
        "/kame-house/test-module" : "nav-test-module",
        "/kame-house/vlc-player" : "nav-vlc-player"
      }

      for (const [urlSubstring, navId] of Object.entries(pages)) {
        setActiveNavItemForPage(pageUrl, urlSubstring, navItem, navId);
      }
    });
  }

  /**
   * Set the active nav item if the page url matches the url substring.
   */
  function setActiveNavItemForPage(pageUrl, urlSubstring, navItem, navId) {
    if (pageUrl.includes(urlSubstring)) {
      setActiveNavItem(navItem, navId);
    }
  }

  /**
   * Set active nav bar item.
   */
  function setActiveNavItem(navItem, navId) {
    if (navItem.attr("id") == navId) {
      domUtils.addClass(navItem, "active");
    }
  }

  /** 
   * Toggle between adding and removing the "responsive" class to topnav when the user clicks on the icon. 
   */
  function toggleHeaderNav() {
    const headerMenu = document.getElementById("header-menu");
    if (headerMenu.className === "header-nav") {
      domUtils.classListAdd(headerMenu, "responsive");
    } else {
      domUtils.classListRemove(headerMenu, "responsive");
    }
  }

  /**
   * Update login status.
   */
  function updateLoginStatus() {
    const $loginStatus = $("#login-status");
    domUtils.empty($loginStatus);
    if (isEmpty(kameHouse.session.username) || kameHouse.session.username.trim() == "" ||
      kameHouse.session.username.trim() == "anonymousUser") {
      domUtils.append($loginStatus, getLoginButton());
    } else {
      domUtils.append($loginStatus, getUsernameHeader(kameHouse.session.username));
      domUtils.append($loginStatus, getLogoutButton());        
    }
  }

  /**
   * Checks if it's running in mobile device view.
   */
  function isMobile() {
    return window.matchMedia("only screen and (max-width: 900px)").matches;
  }

  /**
   * Show groot submenu.
   */
  function showGrootMenu() {
    if (isMobile()) {
      return;
    }
    const menu = document.getElementById("groot-menu-wrapper");
    if (!isEmpty(menu)) {
      domUtils.setDisplay(menu, "block");
    }
  }

  /**
   * Hide groot submenu.
   */
  function hideGrootMenu() {
    if (isMobile()) {
      return;
    }
    const menu = document.getElementById("groot-menu-wrapper");
    if (!isEmpty(menu)) {
      domUtils.setDisplay(menu, "none");
    }
  }

  /**
   * Get header container.
   */
  function getHeaderContainerDiv() {
    return domUtils.getDiv({
      id: "kamehouse-header-container"
    });
  }

  /**
   * Get login button.
   */
  function getLoginButton() {
    return domUtils.getImgBtn({
      src: "/kame-house/img/pc/login-left-red.png",
      className: "header-login-status-btn",
      alt: "Login KameHouse",
      onClick: () => {
        if (mobileAppUtils.isMobileApp()) {
          window.location="/kame-house-mobile/index.html";
          return;
        }
        window.location="/kame-house/login.html";
      }
    });
  }

  /**
   * Get logout button.
   */
  function getLogoutButton() {
    if (mobileAppUtils.isMobileApp()) {
      return domUtils.getImgBtn({
        src: "/kame-house/img/dbz/goku-dark-gray.png",
        className: "header-login-status-btn",
        alt: "KameHouse",
        onClick: () => {return;}
      });
    }
    return domUtils.getImgBtn({
      src: "/kame-house/img/pc/logout-right-red.png",
      className: "header-login-status-btn",
      alt: "Logout KameHouse",
      onClick: () => window.location="/kame-house/logout"
    });
  }

  /**
   * Get username login status header span.
   */
  function getUsernameHeader(username) {
    return domUtils.getSpan({
      class: "header-login-status-text"
    }, username);
  }
}