function updateActiveTab() {
  var pageUrl = window.location.pathname; 
  $("#headerContainer header .container nav ul li").toArray().forEach(function(navItem) {
    $(navItem).removeClass("active");   
    switch (pageUrl) {
    case "/kame-house/":
      if ($(navItem).attr("id") == "nav-home") {
        $(navItem).addClass("active");
      } 
      break;
    case "/kame-house/html/about.html":
      if ($(navItem).attr("id") == "nav-about") {
        $(navItem).addClass("active");
      } 
      break;
    case "/kame-house/html/services.html":
      if ($(navItem).attr("id") == "nav-services") {
        $(navItem).addClass("active");
      } 
      break;
    default:
      break;
    }
    if (pageUrl.includes("/kame-house/jsp")) {
      if ($(navItem).attr("id") == "nav-jsp") {
        $(navItem).addClass("active");
      } 
    }
    if (pageUrl.includes("/kame-house/app")) {
      if ($(navItem).attr("id") == "nav-app") {
        $(navItem).addClass("active");
      } 
    }
  });
}

function importHeaderAndFooter(path) {
  if (path == undefined || path == null) {
    path = "";
  }
  console.log("Loading header and footer from path: " + path);

  $("#headerContainer").load(path + "header.html", function() {
    updateActiveTab();
    $("#footerContainer").load(path + "footer.html");
  });
}