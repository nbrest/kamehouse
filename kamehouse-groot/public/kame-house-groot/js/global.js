/**
 * Global js variables and functions for all pages.
 * 
 * The idea is for this to be as minimal as possible. 
 * 
 * All my main functionality should be in /kame-house project
 * 
 * @author nbrest
 */
var grootHeader;

function mainGlobalGroot() {
  grootHeader = new GrootHeader();
  moduleUtils.waitForModules(["logger", "httpClient"], () => {
    grootHeader.renderGrootMenu();
  });
}

/**
 * Functionality to manage the groot header.
 */
function GrootHeader() {
  let self = this;

  /** Toggle expanding/collapsing the groot menu hamburguer */
  this.toggleGrootNav = () => {
    let rootMenu = document.getElementById("groot-menu");
    if (rootMenu.className === "groot-nav") {
      rootMenu.className += " responsive";
    } else {
      rootMenu.className = "groot-nav";
    }
  }

  /** Render groot sub menu */
  this.renderGrootMenu = () => {
    $("#groot-menu-wrapper").load("/kame-house-groot/html-snippets/groot-menu.html", () => {
      updateGRootMenuActiveTab();
      loadSessionStatus();
    });
  }
  
  /** Load session status */
  function loadSessionStatus(successCallback, errorCallback) {
    const SESSION_STATUS_API = '/kame-house-groot/api/v1/commons/session/status.php';
    httpClient.get(SESSION_STATUS_API, null,
      (responseBody, responseCode, responseDescription) => {
        global.groot = {};
        global.groot.session = responseBody;
        updateSessionStatus();
        moduleUtils.setModuleLoaded("grootHeader");
      },
      (responseBody, responseCode, responseDescription) =>  logger.error("Error retrieving current groot session information."));
  }

  /**
   * Update groot session status.
   */
  function updateSessionStatus() {
    let $loginStatusDesktop = $("#groot-header-login-status-desktop");
    let $loginStatusMobile = $("#groot-header-login-status-mobile");
    domUtils.empty($loginStatusDesktop);
    if (isNullOrUndefined(global.groot.session.username) || global.groot.session.username.trim() == "" ||
      global.groot.session.username.trim() == "anonymousUser") {
      domUtils.append($loginStatusDesktop, self.getLoginButton());
      domUtils.append($loginStatusMobile, self.getLoginButton());
    } else {
      domUtils.append($loginStatusDesktop, self.getUsernameHeader(global.groot.session.username));
      domUtils.append($loginStatusDesktop, self.getLogoutButton());
      domUtils.append($loginStatusMobile, self.getUsernameHeader(global.groot.session.username));
      domUtils.append($loginStatusMobile, self.getLogoutButton());
    }
  }

  /**
  * Set active tab in the groot sub menu.
  */
  function updateGRootMenuActiveTab() {
    let pageUrl = window.location.pathname;
    $("#groot-menu a").toArray().forEach((navItem) => {
      domUtils.removeClass($(navItem), "active");
      if (pageUrl == "/kame-house-groot/") {
        if ($(navItem).attr("id") == "nav-groot-home") {
          domUtils.addClass($(navItem), "active");
        }
      }
      if (pageUrl.startsWith("/kame-house-groot/admin/server-manager")) {
        if ($(navItem).attr("id") == "nav-server-manager") {
          domUtils.addClass($(navItem), "active");
        }
      }
      if (pageUrl.startsWith("/kame-house-groot/admin/my-scripts")) {
        if ($(navItem).attr("id") == "nav-my-scripts") {
          domUtils.addClass($(navItem), "active");
        }
      }
    });
  }
  
  this.getLoginButton = () => {
    return domUtils.getImgBtn({
      src: "/kame-house/img/pc/login-left-gray-dark.png",
      className: "groot-header-login-status-btn",
      alt: "Login GRoot",
      onClick: () => window.location="/kame-house-groot/login.html"
    });
  }

  this.getLogoutButton = () => {
    return domUtils.getImgBtn({
      src: "/kame-house/img/pc/logout-right-gray-dark.png",
      className: "groot-header-login-status-btn",
      alt: "Logout GRoot",
      onClick: () => window.location="/kame-house-groot/api/v1/auth/logout.php"
    });
  }

  this.getUsernameHeader = (username) => {
    return domUtils.getSpan({
      class: "groot-header-login-status-text"
    }, username);
  }
}

/** 
 * @deprecated
 * Refresh the page after the specified seconds. 
 * Simulating a loop by recursively calling the same function 
 */
var countdownCounter = 60;

function refreshPageLoop() {
  if (typeof countdownCounter == 'undefined') {
    countdownCounter = 60;
  }
  if (countdownCounter > 0) {
    domUtils.setInnerHtml(document.getElementById('count'), countdownCounter--);
    setTimeout(refreshPageLoop, 1000);
  } else {
    location.href = '/';
  }
}

$(document).ready(mainGlobalGroot);