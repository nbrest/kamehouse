/**
 * Header and Footer functions.
 * 
 * @author nbrest
 */

/**
 * Render header and footer.
 */
function renderHeaderAndFooter() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/header-footer/header.css">');
  $("#headerContainer").load("/kame-house/html-snippets/header.html", function() {
    updateActiveTab();
    updateHeaderLoginStatus();
  });
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/header-footer/footer.css">');
  $("#footerContainer").load("/kame-house/html-snippets/footer.html");
}

/**
 * Set active tab in the menu.
 */
function updateActiveTab() {
  var pageUrl = window.location.pathname; 
  $("#headerContainer header .default-layout #header-menu a").toArray().forEach(function(navItem) {
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
 * Update header login status.
 */
function updateHeaderLoginStatus() {
  if (isEmpty(global.session.username) || global.session.username.trim() == "" 
	  || global.session.username.trim() == "anonymousUser") {
    var $loginStatus = $("#login-status");
    var $loginButton = $("<a href='/kame-house/login' " + 
        "class='btn btn-outline-danger login-status-button'>Login</>");
    $loginStatus.append($loginButton);
  } else {
    var $loginStatus = $("#login-status");
    var $logoutButton = $("<a href='/kame-house/logout' " + 
        "class='btn btn-outline-danger'>Logout</>");
    $loginMessage = $("<h5>");
    $loginMessage.text(global.session.username);
    $loginStatus.append($logoutButton);
    $loginStatus.append($loginMessage);
  }
}