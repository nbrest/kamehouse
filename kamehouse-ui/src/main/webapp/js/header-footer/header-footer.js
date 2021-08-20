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
  logger.trace(arguments.callee.name);
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
    $("#headerContainer header .default-layout #header-menu a").toArray().forEach((navItem) => {
      domUtils.removeClass($(navItem), "active");
      switch (pageUrl) {
        case "/kame-house/":
          if ($(navItem).attr("id") == "nav-home") {
            domUtils.addClass($(navItem), "active");
          }
          break;
        case "/kame-house/about":
          if ($(navItem).attr("id") == "nav-about") {
            domUtils.addClass($(navItem), "active");
          }
          break;
        case "/kame-house/contact-us":
          if ($(navItem).attr("id") == "nav-contact-us") {
            domUtils.addClass($(navItem), "active");
          }
          break;
        case "/kame-house-groot":
          if ($(navItem).attr("id") == "nav-groot") {
            domUtils.addClass($(navItem), "active");
          }
          break;
        default:
          break;
      }
      if (pageUrl.includes("/kame-house/admin")) {
        if ($(navItem).attr("id") == "nav-admin") {
          domUtils.addClass($(navItem), "active");
        }
      }
      if (pageUrl.includes("/kame-house/tennisworld")) {
        if ($(navItem).attr("id") == "nav-tennisworld") {
          domUtils.addClass($(navItem), "active");
        }
      }
      if (pageUrl.includes("/kame-house/test-module")) {
        if ($(navItem).attr("id") == "nav-test-module") {
          domUtils.addClass($(navItem), "active");
        }
      }
      if (pageUrl.includes("/kame-house/vlc-player")) {
        if ($(navItem).attr("id") == "nav-vlc-player") {
          domUtils.addClass($(navItem), "active");
        }
      }
      if (pageUrl.startsWith("/kame-house-groot")) {
        if ($(navItem).attr("id") == "nav-groot") {
          domUtils.addClass($(navItem), "active");
        }
      }
    });
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

  function getHeaderContainerDiv() {
    return domUtils.getDiv({
      id: "headerContainer"
    });
  }

  function getLoginButton() {
    return domUtils.getImgBtn({
      src: "/kame-house/img/pc/login-left-red.png",
      className: "header-login-status-btn",
      alt: "Login KameHouse",
      onClick: () => window.location="/kame-house/login"
    });
  }

  function getLogoutButton() {
    return domUtils.getImgBtn({
      src: "/kame-house/img/pc/logout-right-red.png",
      className: "header-login-status-btn",
      alt: "Logout KameHouse",
      onClick: () => window.location="/kame-house/logout"
    });
  }

  function getUsernameHeader(username) {
    return domUtils.getSpan({
      class: "header-login-status-text"
    }, username);
  }
}