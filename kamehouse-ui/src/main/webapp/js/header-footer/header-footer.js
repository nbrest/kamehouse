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
  moduleUtils.waitForModules(["logger", "httpClient"], () => {
    logger.info("Started initializing header and footer");
    header = new Header();
    header.renderHeader();
    footer = new Footer();
    footer.renderFooter();
    loadSessionStatus();
  });
}

/** 
 * Get session status and update the header and footer status. 
 */
function loadSessionStatus() {
  let SESSION_STATUS_URL = "/kame-house/api/v1/ui/session/status";

  httpClient.get(SESSION_STATUS_URL, null,
    (responseBody, responseCode, responseDescription) => {
      logger.trace("Sessin Status: " + JSON.stringify(responseBody));
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
  self = this;
  this.loaded = false;

  this.isLoaded = () => self.loaded;

  /** Renders the footer */
  this.renderFooter = () => { 
    $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/header-footer/footer.css">');
    $("body").append(self.getFooterContainerDiv());
    $("#footerContainer").load("/kame-house/html-snippets/footer.html", () => {
      self.loaded = true;
    });
  }

  /** Update the server name, and build info in the footer */
  this.updateFooterWithSessionInfo = () => {
    if (!isNullOrUndefined(global.session.server)) {
      domUtils.setHtml($("#footer-server-name"), global.session.server);
    }
    if (!isNullOrUndefined(global.session.buildVersion)) {
      domUtils.setHtml($("#footer-build-version"), global.session.buildVersion);
    }
    if (!isNullOrUndefined(global.session.buildDate)) {
      domUtils.setHtml($("#footer-build-date"), global.session.buildDate);
    }
  }

  this.getFooterContainerDiv = () => {
    return domUtils.getDiv({
      id: "footerContainer"
    });
  }
}

/** Header functionality */
function Header() {
  let self = this;
  this.loaded = false;

  this.isLoaded = () => self.loaded;
  
  /** Render the header */
  this.renderHeader = () => {
    $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/header-footer/header.css">');
    $("body").prepend(self.getHeaderContainerDiv());
    $("#headerContainer").load("/kame-house/html-snippets/header.html", () => {
      self.updateLoginStatus();
      self.updateActiveTab();
      self.loaded = true;
    });
  }

  /**
   * Set active tab in the menu.
   */
  this.updateActiveTab = () => {
    let pageUrl = window.location.pathname;
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
  this.toggleHeaderNav = () => {
    let headerMenu = document.getElementById("header-menu");
    if (headerMenu.className === "header-nav") {
      headerMenu.className += " responsive";
    } else {
      headerMenu.className = "header-nav";
    }
  }

  /**
   * Update login status.
   */
  this.updateLoginStatus = () => {
    let $loginStatus = $("#login-status");
    domUtils.empty($loginStatus);
    if (isNullOrUndefined(global.session.username) || global.session.username.trim() == "" ||
      global.session.username.trim() == "anonymousUser") {
      $loginStatus.append(self.getLoginButton());
    } else {
      $loginStatus.append(self.getUsernameHeader(global.session.username));
      $loginStatus.append(self.getLogoutButton());
    }
  }

  this.getHeaderContainerDiv = () => {
    return domUtils.getDiv({
      id: "headerContainer"
    });
  }

  this.getLoginButton = () => {
    return domUtils.getImgBtn({
      src: "/kame-house/img/pc/login-left-red.png",
      className: "header-login-status-btn",
      alt: "Login KameHouse",
      onClick: () => window.location="/kame-house/login"
    });
  }

  this.getLogoutButton = () => {
    return domUtils.getImgBtn({
      src: "/kame-house/img/pc/logout-right-red.png",
      className: "header-login-status-btn",
      alt: "Logout KameHouse",
      onClick: () => window.location="/kame-house/logout"
    });
  }

  this.getUsernameHeader = (username) => {
    return domUtils.getSpan({
      class: "header-login-status-text"
    }, username);
  }
}