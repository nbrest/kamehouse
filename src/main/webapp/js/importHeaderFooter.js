function importHeaderAndFooter(path) {
  if (path == undefined || path == null) {
    path = "";
  }
  $("#headerContainer").load(path + "header.html", function() {
    updateActiveTab();
    getSessionInformation();
  });
  $("#footerContainer").load(path + "footer.html");
}

function updateActiveTab() {
  var pageUrl = window.location.pathname; 
  $("#headerContainer header .container #header-menu nav ul li").toArray().forEach(function(navItem) {
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
    if (pageUrl.includes("/kame-house/jsp/")) {
      if ($(navItem).attr("id") == "nav-jsp") {
        $(navItem).addClass("active");
      } 
    }
    if (pageUrl.includes("/kame-house/app/")) {
      if ($(navItem).attr("id") == "nav-app") {
        $(navItem).addClass("active");
      } 
    }
    if (pageUrl.includes("/kame-house/admin/")) {
      if ($(navItem).attr("id") == "nav-admin") {
        $(navItem).addClass("active");
      } 
    }
  });
}

function getSessionInformation() {
  SESSION_INFO_URL = "/kame-house/api/v1/session/status";
  $.get(SESSION_INFO_URL)
  .success(function(data) {
    updateLoginStatus(data.username);
  })
  .error(function(jqXHR, textStatus, errorThrown) {
    console.error("Error retrieving current session information.");
    updateLoginStatus(null);
  });
}

function updateLoginStatus(username) {
  //console.log("updateLoginStatus - username: " + username);
  if (username == undefined || username == null || username.trim() == "" || username.trim() == "anonymousUser") {
    var $loginStatus = $("#login-status");
    var $loginButton = $("<a href='/kame-house/login' class='btn btn-outline-info login-status-button'>Login</>");
    $loginStatus.append($loginButton);
  } else {
    var $loginStatus = $("#login-status");
    var $logoutButton = $("<a href='/kame-house/logout' class='btn btn-outline-secondary login-status-button'>Logout</>");
    $loginMessage = $("<h5>");
    $loginMessage.text("Welcome " + username + "!");
    $loginStatus.append($logoutButton);
    $loginStatus.append($loginMessage);
  }
}