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
  logger.traceFunctionCall();
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
  let SESSION_STATUS_URL = "/kame-house/api/v1/session/status";

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
  footer.updateServerName();
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
    $("body").append('<div id="footerContainer"></div>');
    $("#footerContainer").load("/kame-house/html-snippets/footer.html", () => {
      self.loaded = true;
    });
  }

  /** Update the server name in the footer */
  this.updateServerName = () => {
    if (!isNullOrUndefined(global.session.server)) {
      $("#footer-server-name").text(global.session.server);
    }
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
    $("body").prepend('<div id="headerContainer"></div>');
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
      $(navItem).removeClass("active");
      switch (pageUrl) {
        case "/kame-house/":
          if ($(navItem).attr("id") == "nav-home") {
            $(navItem).addClass("active");
          }
          break;
        case "/kame-house/about":
          if ($(navItem).attr("id") == "nav-about") {
            $(navItem).addClass("active");
          }
          break;
        case "/kame-house/contact-us":
          if ($(navItem).attr("id") == "nav-contact-us") {
            $(navItem).addClass("active");
          }
          break;
        case "/":
          if ($(navItem).attr("id") == "nav-root") {
            $(navItem).addClass("active");
          }
          break;
        default:
          break;
      }
      if (pageUrl.includes("/kame-house/admin")) {
        if ($(navItem).attr("id") == "nav-admin") {
          $(navItem).addClass("active");
        }
      }
      if (pageUrl.includes("/kame-house/test-module")) {
        if ($(navItem).attr("id") == "nav-test-module") {
          $(navItem).addClass("active");
        }
      }
      if (pageUrl.includes("/kame-house/vlc-player")) {
        if ($(navItem).attr("id") == "nav-vlc-player") {
          $(navItem).addClass("active");
        }
      }
      if (pageUrl.startsWith("/admin/my.scripts")) {
        if ($(navItem).attr("id") == "nav-root") {
          $(navItem).addClass("active");
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
    $loginStatus.empty();
    if (isNullOrUndefined(global.session.username) || global.session.username.trim() == "" ||
      global.session.username.trim() == "anonymousUser") {
      let $loginButton = $("<a href='/kame-house/login' " +
        "class='btn btn-outline-danger login-status-button'>Login</>");
      $loginStatus.append($loginButton);
    } else {
      let $logoutButton = $("<a href='/kame-house/logout' " +
        "class='btn btn-outline-danger'>Logout</>");
      let $loginMessage = $("<h5>");
      $loginMessage.text(global.session.username);
      $loginStatus.append($logoutButton);
      $loginStatus.append($loginMessage);
    }
  }
}