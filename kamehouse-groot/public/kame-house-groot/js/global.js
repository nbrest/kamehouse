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
 * Open the tab specified by its id.
 */
 function openTab(selectedTabDivId, cookiePrefix) {
  // Set current-tab cookie
  cookiesUtils.setCookie(cookiePrefix + '-current-tab', selectedTabDivId);
  
  // Update tab links
  let tabLinks = document.getElementsByClassName("tab-groot-link");
  for (let i = 0; i < tabLinks.length; i++) {
    tabLinks[i].className = tabLinks[i].className.replace(" active", "");
  }
  let selectedTabLink = document.getElementById(selectedTabDivId + '-link');
  selectedTabLink.classList.add("active");

  // Update tab content visibility
  let kamehouseTabContent = document.getElementsByClassName("tab-groot-content");
  for (let i = 0; i < kamehouseTabContent.length; i++) {
    kamehouseTabContent[i].style.display = "none";
  }
  let selectedTabDiv = document.getElementById(selectedTabDivId);
  selectedTabDiv.style.display = "block";
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

  /** Get session status from the backend */
  this.getSessionStatus = (successCallback, errorCallback) => {
    const SESSION_STATUS_API = '/kame-house-groot/api/v1/commons/session/status.php';
    httpClient.get(SESSION_STATUS_API, null,
      (responseBody, responseCode, responseDescription) => successCallback(responseBody, responseCode, responseDescription),
      (responseBody, responseCode, responseDescription) => errorCallback(responseBody, responseCode, responseDescription));
  }
  
  /** Load session status */
  function loadSessionStatus(successCallback, errorCallback) {
    const SESSION_STATUS_API = '/kame-house-groot/api/v1/commons/session/status.php';
    httpClient.get(SESSION_STATUS_API, null,
      (responseBody, responseCode, responseDescription) => {
        global.groot = {};
        global.groot.session = responseBody;
        updateSessionStatus();
      },
      (responseBody, responseCode, responseDescription) =>  logger.error("Error retrieving current groot session information."));
  }

  /**
   * Update groot session status.
   */
  function updateSessionStatus() {
    let $loginStatus = $("#groot-header-login-status");
    $loginStatus.empty();
    if (isNullOrUndefined(global.groot.session.username) || global.groot.session.username.trim() == "" ||
      global.groot.session.username.trim() == "anonymousUser") {
      $loginStatus.append(self.getLoginButton());
    } else {
      $loginStatus.append(self.getLogoutButton());
      $loginStatus.append(self.getUsernameHeader(global.groot.session.username));
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
    img.src = "/kame-house/img/pc/login-left-red.png";
    img.className = "groot-header-login-status-btn";
    img.alt = "Login";
    img.title = "Login";
    img.onclick = () => window.location="/kame-house-groot/login.html";
    return img;
  }

  this.getLogoutButton = () => {
    let img = new Image();
    img.src = "/kame-house/img/pc/logout-right-red.png";
    img.className = "groot-header-login-status-btn";
    img.alt = "Logout";
    img.title = "Logout";
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