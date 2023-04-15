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
  grootHeader.renderGrootMenu();
}

/**
 * Functionality to manage the groot header.
 */
function GrootHeader() {

  this.toggleGrootNav = toggleGrootNav;
  this.renderGrootMenu = renderGrootMenu;

  /** Toggle expanding/collapsing the groot menu hamburguer */
  function toggleGrootNav() {
    const rootMenu = document.getElementById("groot-menu");
    if (rootMenu.className === "groot-nav") {
      domUtils.classListAdd(rootMenu, "responsive");
    } else {
      domUtils.classListRemove(rootMenu, "responsive");
    }
  }

  /** Render groot sub menu */
  function renderGrootMenu() {
    domUtils.load($("#groot-menu-wrapper"), "/kame-house-groot/html-snippets/groot-menu.html", () => {
      updateGRootMenuActiveTab();
      loadSessionStatus();
    });
  }
  
  /** Load session status */
  function loadSessionStatus(successCallback, errorCallback) {
    const SESSION_STATUS_API = '/kame-house-groot/api/v1/commons/session/status.php';
    httpClient.get(SESSION_STATUS_API, null,
      (responseBody, responseCode, responseDescription) => {
        kameHouse.groot.session = responseBody;
        updateSessionStatus();
        moduleUtils.setModuleLoaded("grootHeader");
      },
      (responseBody, responseCode, responseDescription) =>  logger.error("Error retrieving current groot session information."));
  }

  /**
   * Update groot session status.
   */
  function updateSessionStatus() {
    const $loginStatusDesktop = $("#groot-header-login-status-desktop");
    const $loginStatusMobile = $("#groot-header-login-status-mobile");
    domUtils.empty($loginStatusDesktop);
    if (isEmpty(kameHouse.groot.session.username) || kameHouse.groot.session.username.trim() == "" ||
      kameHouse.groot.session.username.trim() == "anonymousUser") {
      domUtils.append($loginStatusDesktop, getLoginButton());
      domUtils.append($loginStatusMobile, getLoginButton());
    } else {
      domUtils.append($loginStatusDesktop, getUsernameHeader(kameHouse.groot.session.username));
      domUtils.append($loginStatusDesktop, getLogoutButton());
      domUtils.append($loginStatusMobile, getUsernameHeader(kameHouse.groot.session.username));
      domUtils.append($loginStatusMobile, getLogoutButton());
    }
  }

  /**
  * Set active tab in the groot sub menu.
  */
  function updateGRootMenuActiveTab() {
    const pageUrl = window.location.pathname;
    $("#groot-menu a").toArray().forEach((navItem) => {
      domUtils.removeClass($(navItem), "active");
      if (pageUrl == "/kame-house-groot/" || pageUrl == "/kame-house-groot/index.html") {
        if ($(navItem).attr("id") == "nav-groot-home") {
          domUtils.addClass($(navItem), "active");
        }
      }
      if (pageUrl.startsWith("/kame-house-groot/admin/server-manager")) {
        if ($(navItem).attr("id") == "nav-server-manager") {
          domUtils.addClass($(navItem), "active");
        }
      }
      if (pageUrl.startsWith("/kame-house-groot/admin/kamehouse-shell")) {
        if ($(navItem).attr("id") == "nav-kamehouse-shell") {
          domUtils.addClass($(navItem), "active");
        }
      }
    });
  }
  
  function getLoginButton() {
    return domUtils.getImgBtn({
      src: "/kame-house/img/pc/login-left-gray-dark.png",
      className: "groot-header-login-status-btn kh-mobile-hidden",
      alt: "Login GRoot",
      onClick: () => window.location="/kame-house-groot/login.html"
    });
  }

  function getLogoutButton() {
    return domUtils.getImgBtn({
      src: "/kame-house/img/pc/logout-right-gray-dark.png",
      className: "groot-header-login-status-btn kh-mobile-hidden",
      alt: "Logout GRoot",
      onClick: () => window.location="/kame-house-groot/api/v1/auth/logout.php"
    });
  }

  function getUsernameHeader(username) {
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

/** 
 * @deprecated
 */
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