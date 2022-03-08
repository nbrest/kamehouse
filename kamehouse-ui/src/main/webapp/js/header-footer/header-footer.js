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
      global.session = responseBody;
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
    domUtils.append($("body"), getFooterContainerDiv());
    domUtils.load($("#footerContainer"), "/kame-house/html-snippets/footer.html", () => {
      loaded = true;
    });
  }

  /** Update the server name, and build info in the footer */
  function updateFooterWithSessionInfo() {
    if (!isEmpty(global.session.server)) {
      domUtils.setHtml($("#footer-server-name"), global.session.server);
    }
    if (!isEmpty(global.session.buildVersion)) {
      domUtils.setHtml($("#footer-build-version"), global.session.buildVersion);
    }
    if (!isEmpty(global.session.buildDate)) {
      domUtils.setHtml($("#footer-build-date"), global.session.buildDate);
    }
  }

  function getFooterContainerDiv() {
    return domUtils.getDiv({
      id: "footerContainer"
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
    domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/header-footer/header.css">');
    domUtils.prepend($("body"), getHeaderContainerDiv());
    domUtils.load($("#headerContainer"), "/kame-house/html-snippets/header.html", () => {
      updateLoginStatus();
      updateActiveTab();
      loaded = true;
    });
  }

  /**
   * Set active tab in the menu.
   */
  function updateActiveTab() {
    const pageUrl = window.location.pathname;
    $("#headerContainer header .default-layout #header-menu a").toArray().forEach((navElement) => {
      const navItem = $(navElement);
      domUtils.removeClass(navItem, "active");
      
      if (pageUrl == "/kame-house/") {
        setActiveNavItem(navItem, "nav-home");
      }

      const pages = {
        "/kame-house/about" : "nav-about",
        "/kame-house/admin" : "nav-admin",
        "/kame-house/contact-us" : "nav-contact-us",
        "/kame-house-groot" : "nav-groot",
        "/kame-house/mobile" : "nav-mobile",
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
    if (isEmpty(global.session.username) || global.session.username.trim() == "" ||
      global.session.username.trim() == "anonymousUser") {
      domUtils.append($loginStatus, getLoginButton());
    } else {
      domUtils.append($loginStatus, getUsernameHeader(global.session.username));
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
      id: "headerContainer"
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
      onClick: () => window.location="/kame-house/login"
    });
  }

  /**
   * Get logout button.
   */
  function getLogoutButton() {
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