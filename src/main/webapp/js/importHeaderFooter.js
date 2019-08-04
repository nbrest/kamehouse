/**
 * Header and Footer functions.
 * 
 * @author nbrest
 */
var SESSION_STATUS_URL = "/kame-house/api/v1/session/status";

function main() {
  importHeaderAndFooter();
}

/**
 * Import header and footer.
 */
function importHeaderAndFooter() {
  $("#headerContainer").load("/kame-house/html/header.html", function() {
    updateActiveTab();
    getSessionStatus();
  });
  $("#footerContainer").load("/kame-house/html/footer.html");
}

/**
 * Set active tab in the menu.
 */
function updateActiveTab() {
  var pageUrl = window.location.pathname;
  console.log("Started updateActiveTab");
  console.log("pageUrl" + pageUrl);
  $("#headerContainer header .container #header-menu a").toArray().forEach(function(navItem) {
    $(navItem).removeClass("active"); 
    console.log("navItem" + navItem);
    switch (pageUrl) {
    case "/kame-house/":
      if ($(navItem).attr("id") == "nav-home") {
        $(navItem).addClass("active");
        console.log("in home");
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
        console.log("in vlc");
      } 
    }
  });
}

/* Toggle between adding and removing the "responsive" class to topnav when the user clicks on the icon */
function toggleHeaderNav() {
  var x = document.getElementById("header-menu");
  if (x.className === "header-nav") {
    x.className += " responsive";
  } else {
    x.className = "header-nav";
  }
}

/**
 * Get session status.
 */
function getSessionStatus() {
  $.get(SESSION_STATUS_URL)
  .success(function(data) {
    updateLoginStatus(data.username);
  })
  .error(function(jqXHR, textStatus, errorThrown) {
    console.error("Error retrieving current session information.");
    updateLoginStatus(null);
  });
}

/**
 * Update login status.
 */
function updateLoginStatus(name) {
  if (name == undefined || name == null || name.trim() == "" || name.trim() == "anonymousUser") {
    var $loginStatus = $("#login-status");
    var $loginButton = $("<a href='/kame-house/login' " + 
        "class='btn btn-outline-danger login-status-button'>Login</>");
    $loginStatus.append($loginButton);
  } else {
    var $loginStatus = $("#login-status");
    var $logoutButton = $("<a href='/kame-house/logout' " + 
        "class='btn btn-outline-danger'>Logout</>");
    $loginMessage = $("<h5>");
    $loginMessage.text(name);
    $loginStatus.append($logoutButton);
    $loginStatus.append($loginMessage);
  }
}

/**
 * Call main.
 */
$(document).ready(main);