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
    $loginStatusDesktop.empty();
    if (isNullOrUndefined(global.groot.session.username) || global.groot.session.username.trim() == "" ||
      global.groot.session.username.trim() == "anonymousUser") {
      $loginStatusDesktop.append(self.getLoginButton());
      $loginStatusMobile.append(self.getLoginButton());
    } else {
      $loginStatusDesktop.append(self.getUsernameHeader(global.groot.session.username));
      $loginStatusDesktop.append(self.getLogoutButton());
      $loginStatusMobile.append(self.getUsernameHeader(global.groot.session.username));
      $loginStatusMobile.append(self.getLogoutButton());
    }
  }

  /**
  * Set active tab in the groot sub menu.
  */
  function updateGRootMenuActiveTab() {
    let pageUrl = window.location.pathname;
    $("#groot-menu a").toArray().forEach((navItem) => {
      $(navItem).removeClass("active");
      if (pageUrl == "/kame-house-groot/") {
        if ($(navItem).attr("id") == "nav-groot-home") {
          $(navItem).addClass("active");
        }
      }
      if (pageUrl.startsWith("/kame-house-groot/admin/server-manager")) {
        if ($(navItem).attr("id") == "nav-server-manager") {
          $(navItem).addClass("active");
        }
      }
      if (pageUrl.startsWith("/kame-house-groot/admin/my-scripts")) {
        if ($(navItem).attr("id") == "nav-my-scripts") {
          $(navItem).addClass("active");
        }
      }
    });
  }
  
  /** Dynamic DOM element generation ------------------------------------------ */
  this.getLoginButton = () => {
    let img = new Image();
    img.src = "/kame-house/img/pc/login-left-gray-dark.png";
    img.className = "groot-header-login-status-btn";
    img.alt = "Login GRoot";
    img.title = "Login GRoot";
    img.onclick = () => window.location="/kame-house-groot/login.html";
    return img;
  }

  this.getLogoutButton = () => {
    let img = new Image();
    img.src = "/kame-house/img/pc/logout-right-gray-dark.png";
    img.className = "groot-header-login-status-btn";
    img.alt = "Logout GRoot";
    img.title = "Logout GRoot";
    img.onclick = () => window.location="/kame-house-groot/api/v1/auth/logout.php";
    return img;
  }

  this.getUsernameHeader = (username) => {
    let usernameHeader = $('<span>');
    usernameHeader.attr("class", "groot-header-login-status-text");
    usernameHeader.text(username);
    return usernameHeader;
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
    document.getElementById('count').innerHTML = countdownCounter--;
    setTimeout(refreshPageLoop, 1000);
  } else {
    location.href = '/';
  }
}

$(document).ready(mainGlobalGroot);