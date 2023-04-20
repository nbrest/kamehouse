/**
 * Global js variables and functions for all pages.
 * 
 * The idea is for this to be as minimal as possible. 
 * 
 * All my main functionality should be in /kame-house project
 * 
 * @author nbrest
 */
function KameHouseGroot() {
  this.load = load;
  this.windowLocation = windowLocation;

  function load() {
    kameHouse.extension.groot.header = new GrootHeader();
    kameHouse.extension.groot.header.renderGrootMenu();
  }

  /** Set the location to php on web and html on mobile. pass the location without extension */
  function windowLocation(location, args) {
    if (kameHouse.core.isEmpty(args)) {
      return kameHouse.util.mobile.windowLocation(location + ".php", location + ".html");
    }
    return kameHouse.util.mobile.windowLocation(location + ".php" + args, location + ".html" + args);
  }

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
      kameHouse.util.dom.classListAdd(rootMenu, "responsive");
    } else {
      kameHouse.util.dom.classListRemove(rootMenu, "responsive");
    }
  }

  /** Render groot sub menu */
  function renderGrootMenu() {
    kameHouse.util.dom.load($("#groot-menu-wrapper"), "/kame-house-groot/kamehouse-groot/html/kamehouse-groot-menu.html", () => {
      updateGRootMenuActiveTab();
      loadSessionStatus();
    });
  }
  
  /** Load session status */
  function loadSessionStatus() {
    const SESSION_STATUS_API = '/kame-house-groot/api/v1/commons/session/status.php';
    kameHouse.http.get(SESSION_STATUS_API, null, null,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.extension.groot.session = responseBody;
        updateSessionStatus();
        kameHouse.util.module.setModuleLoaded("kameHouseGroot");
      },
      (responseBody, responseCode, responseDescription, responseHeaders) =>  kameHouse.logger.error("Error retrieving current groot session information."));
  }

  /**
   * Update groot session status.
   */
  function updateSessionStatus() {
    const $loginStatusDesktop = $("#groot-header-login-status-desktop");
    const $loginStatusMobile = $("#groot-header-login-status-mobile");
    kameHouse.util.dom.empty($loginStatusDesktop);
    if (kameHouse.core.isEmpty(kameHouse.extension.groot.session.username) || kameHouse.extension.groot.session.username.trim() == "" ||
    kameHouse.extension.groot.session.username.trim() == "anonymousUser") {
      kameHouse.util.dom.append($loginStatusDesktop, getLoginButton());
      kameHouse.util.dom.append($loginStatusMobile, getLoginButton());
    } else {
      kameHouse.util.dom.append($loginStatusDesktop, getUsernameHeader(kameHouse.extension.groot.session.username));
      kameHouse.util.dom.append($loginStatusDesktop, getLogoutButton());
      kameHouse.util.dom.append($loginStatusMobile, getUsernameHeader(kameHouse.extension.groot.session.username));
      kameHouse.util.dom.append($loginStatusMobile, getLogoutButton());
    }
  }

  /**
  * Set active tab in the groot sub menu.
  */
  function updateGRootMenuActiveTab() {
    const pageUrl = window.location.pathname;
    $("#groot-menu a").toArray().forEach((navItem) => {
      kameHouse.util.dom.removeClass($(navItem), "active");
      if (pageUrl == "/kame-house-groot/" || pageUrl == "/kame-house-groot/index.html") {
        if ($(navItem).attr("id") == "nav-groot-home") {
          kameHouse.util.dom.addClass($(navItem), "active");
        }
      }
      if (pageUrl.startsWith("/kame-house-groot/admin/server-manager")) {
        if ($(navItem).attr("id") == "nav-server-manager") {
          kameHouse.util.dom.addClass($(navItem), "active");
        }
      }
      if (pageUrl.startsWith("/kame-house-groot/admin/kamehouse-shell")) {
        if ($(navItem).attr("id") == "nav-kamehouse-shell") {
          kameHouse.util.dom.addClass($(navItem), "active");
        }
      }
    });
  }
  
  function getLoginButton() {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/pc/login-left-gray-dark.png",
      className: "groot-header-login-status-btn",
      alt: "Login GRoot",
      onClick: () => {
        kameHouse.util.mobile.windowLocation("/kame-house-groot/login.html", "/kame-house-mobile/settings.html");
      }
    });
  }

  function getLogoutButton() {
    return kameHouse.util.mobile.exec(
      () => {
        return kameHouse.util.dom.getImgBtn({
          src: "/kame-house/img/pc/logout-right-gray-dark.png",
          className: "groot-header-login-status-btn",
          alt: "Logout GRoot",
          onClick: () => window.location="/kame-house-groot/api/v1/auth/logout.php"
        });
      },
      () => {
        return kameHouse.util.dom.getImgBtn({
          src: "/kame-house/img/dbz/goku-red-very-dark.png",
          className: "header-login-status-btn",
          alt: "KameHouse Groot",
          onClick: () => {return;}
        });
      }
    );
  }

  function getUsernameHeader(username) {
    return kameHouse.util.dom.getSpan({
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
    kameHouse.util.dom.setInnerHtml(document.getElementById('count'), countdownCounter--);
    setTimeout(refreshPageLoop, 1000);
  } else {
    location.href = '/';
  }
}

$(document).ready(() => {
  kameHouse.addExtension("groot", new KameHouseGroot());
});