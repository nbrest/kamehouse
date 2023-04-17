/**
 * Header and Footer functions.
 * 
 * Dependencies: logger, kameHouse.http.
 * 
 * @author nbrest
 */
var header;
var footer;

/**
 * Render header and footer.
 */
function renderHeaderAndFooter() {
  kameHouse.logger.info("Started initializing header and footer");
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

  kameHouse.http.get(SESSION_STATUS_URL, null,
    (responseBody, responseCode, responseDescription) => {
      kameHouse.logger.trace("Session Status: " + JSON.stringify(responseBody));
      kameHouse.session = responseBody;
      updateSessionStatus();
    },
    (responseBody, responseCode, responseDescription) => kameHouse.logger.error("Error retrieving current session information.")
  );
}

/**
 * Wait for the header and footer to be loaded and then update the session status.
 */
async function updateSessionStatus() {
  while (!header.isLoaded() && !footer.isLoaded()) {
    await kameHouse.core.sleep(1000);
  }
  header.updateLoginStatus();
  footer.updateFooterWithSessionInfo();
  kameHouse.util.banner.updateServerName();
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
    kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/kamehouse-footer.css">');
    $(document).ready(() => {
      // load the footer after the other elements are loaded, if not it randomly puts the footer in the middle
      kameHouse.logger.info("Loading footer");
      kameHouse.util.dom.append($("body"), getFooterContainerDiv());
      kameHouse.util.dom.load($("#kamehouse-footer-container"), "/kame-house/kamehouse/html/kamehouse-footer.html", () => {
        kameHouse.util.mobile.disableWebappOnlyElements();
        kameHouse.util.mobile.disableMobileOnlyElements();
        loaded = true;
        kameHouse.logger.info("Finished loading footer");
      });
    });
  }

  /** Update the server name, and build info in the footer */
  function updateFooterWithSessionInfo() {
    if (!kameHouse.core.isEmpty(kameHouse.session.server)) {
      kameHouse.util.dom.setHtml($("#footer-server-name"), kameHouse.session.server);
    }
    if (!kameHouse.core.isEmpty(kameHouse.session.buildVersion)) {
      kameHouse.util.dom.setHtml($("#footer-build-version"), kameHouse.session.buildVersion);
    }
    if (!kameHouse.core.isEmpty(kameHouse.session.buildDate)) {
      kameHouse.util.dom.setHtml($("#footer-build-date"), kameHouse.session.buildDate);
    }
  }

  function getFooterContainerDiv() {
    return kameHouse.util.dom.getDiv({
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
    kameHouse.logger.info("Loading header");
    kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/kamehouse-header.css">');
    $(document).ready(() => {
      // load the header after the other dom is ready to see if this fixes the very rare random header not loading
      kameHouse.util.dom.prepend($("body"), getHeaderContainerDiv());
      kameHouse.util.dom.load($("#kamehouse-header-container"), "/kame-house/kamehouse/html/kamehouse-header.html", () => {
        updateLoginStatus();
        kameHouse.util.mobile.disableWebappOnlyElements();
        kameHouse.util.mobile.disableMobileOnlyElements();
        updateActiveTab();
        loaded = true;
        kameHouse.logger.info("Finished loading header");
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
      kameHouse.util.dom.removeClass(navItem, "active");
      
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
      kameHouse.util.dom.addClass(navItem, "active");
    }
  }

  /** 
   * Toggle between adding and removing the "responsive" class to topnav when the user clicks on the icon. 
   */
  function toggleHeaderNav() {
    const headerMenu = document.getElementById("header-menu");
    if (headerMenu.className === "header-nav") {
      kameHouse.util.dom.classListAdd(headerMenu, "responsive");
    } else {
      kameHouse.util.dom.classListRemove(headerMenu, "responsive");
    }
  }

  /**
   * Update login status.
   */
  function updateLoginStatus() {
    const $loginStatus = $("#login-status");
    kameHouse.util.dom.empty($loginStatus);
    if (kameHouse.core.isEmpty(kameHouse.session.username) || kameHouse.session.username.trim() == "" ||
      kameHouse.session.username.trim() == "anonymousUser") {
      kameHouse.util.dom.append($loginStatus, getLoginButton());
    } else {
      kameHouse.util.dom.append($loginStatus, getUsernameHeader(kameHouse.session.username));
      kameHouse.util.dom.append($loginStatus, getLogoutButton());        
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
    if (!kameHouse.core.isEmpty(menu)) {
      kameHouse.util.dom.setDisplay(menu, "block");
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
    if (!kameHouse.core.isEmpty(menu)) {
      kameHouse.util.dom.setDisplay(menu, "none");
    }
  }

  /**
   * Get header container.
   */
  function getHeaderContainerDiv() {
    return kameHouse.util.dom.getDiv({
      id: "kamehouse-header-container"
    });
  }

  /**
   * Get login button.
   */
  function getLoginButton() {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/pc/login-left-red.png",
      className: "header-login-status-btn",
      alt: "Login KameHouse",
      onClick: () => {
        if (kameHouse.util.mobile.isMobileApp()) {
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
    if (kameHouse.util.mobile.isMobileApp()) {
      return kameHouse.util.dom.getImgBtn({
        src: "/kame-house/img/dbz/goku-dark-gray.png",
        className: "header-login-status-btn",
        alt: "KameHouse",
        onClick: () => {return;}
      });
    }
    return kameHouse.util.dom.getImgBtn({
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
    return kameHouse.util.dom.getSpan({
      class: "header-login-status-text"
    }, username);
  }
}