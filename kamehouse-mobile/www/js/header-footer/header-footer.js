/**
 * Header and Footer functions for mobile.
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
  logger.info("Started initializing header and footer");
  header = new Header();
  header.renderHeader();
  footer = new Footer();
  footer.renderFooter();
}

/** Footer functionality */
function Footer() {

  this.isLoaded = isLoaded;
  this.renderFooter = renderFooter;

  let loaded = false;

  function isLoaded() { return loaded; }

  /** Renders the footer */
  function renderFooter() { 
    domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/header-footer/footer.css">');
    domUtils.append($("body"), getFooterContainerDiv());
    domUtils.load($("#footerContainer"), "/html-snippets/footer.html", () => {
      loaded = true;
    });
  }

  function getFooterContainerDiv() {
    return domUtils.getDiv({
      id: "footerContainer"
    });
  }
}

/** Header functionality */
function Header() {

  this.isLoaded = isLoaded;
  this.renderHeader = renderHeader;
  this.toggleHeaderNav = toggleHeaderNav;

  let loaded = false;

  function isLoaded() { return loaded; }
  
  /** Render the header */
  function renderHeader() {
    domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/header-footer/header.css">');
    domUtils.prepend($("body"), getHeaderContainerDiv());
    domUtils.load($("#headerContainer"), "/html-snippets/header.html", () => {
      updateActiveTab();
      loaded = true;
    });
  }

  /**
   * Set active tab in the menu.
   */
  function updateActiveTab() {
    const pageUrl = window.location.pathname;
    $("#headerContainer header .default-layout #header-menu a").toArray().forEach((navElement) => {
      const navItem = $(navElement);
      domUtils.removeClass(navItem, "active");
      
      if (pageUrl == "/") {
        setActiveNavItem(navItem, "nav-home");
      }

      const pages = {
        "/config" : "nav-config",
        "/services" : "nav-services"
      }

      for (const [urlSubstring, navId] of Object.entries(pages)) {
        setActiveNavItemForPage(pageUrl, urlSubstring, navItem, navId);
      }
    });
  }

  /**
   * Set the active nav item if the page url matches the url substring.
   */
  function setActiveNavItemForPage(pageUrl, urlSubstring, navItem, navId) {
    if (pageUrl.includes(urlSubstring)) {
      setActiveNavItem(navItem, navId);
    }
  }

  /**
   * Set active nav bar item.
   */
  function setActiveNavItem(navItem, navId) {
    if (navItem.attr("id") == navId) {
      domUtils.addClass(navItem, "active");
    }
  }

  /** 
   * Toggle between adding and removing the "responsive" class to topnav when the user clicks on the icon. 
   */
  function toggleHeaderNav() {
    const headerMenu = document.getElementById("header-menu");
    if (headerMenu.className === "header-nav") {
      domUtils.classListAdd(headerMenu, "responsive");
    } else {
      domUtils.classListRemove(headerMenu, "responsive");
    }
  }

  /**
   * Get header container.
   */
  function getHeaderContainerDiv() {
    return domUtils.getDiv({
      id: "headerContainer"
    });
  }
}