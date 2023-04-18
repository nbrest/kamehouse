/**
 * Header functions.
 * 
 * @author nbrest
 */
function KameHouseHeader() {

  this.load = load;
  this.toggleHeaderNav = toggleHeaderNav;
  this.showGrootMenu = showGrootMenu;
  this.hideGrootMenu = hideGrootMenu;
  
  /** Load the header */
  function load() {
    kameHouse.logger.info("Loading header");
    kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/kamehouse-header.css">');
    $(document).ready(() => {
      // load the header after the other dom is ready to see if this fixes the very rare random header not loading
      kameHouse.util.dom.prepend($("body"), getHeaderContainerDiv());
      kameHouse.util.dom.load($("#kamehouse-header-container"), "/kame-house/kamehouse/html/kamehouse-header.html", () => {
        kameHouse.util.mobile.disableWebappOnlyElements();
        kameHouse.util.mobile.disableMobileOnlyElements();
        updateActiveTab();
        kameHouse.util.module.waitForModules(["session"], () => {
          updateSessionStatus();
        });
        kameHouse.logger.info("Finished loading header");
      });
    });
  }

  /**
   * Set active tab in the menu.
   */
  function updateActiveTab() {
    const pageUrl = window.location.pathname;
    $("#kamehouse-header-container header .default-layout #header-menu a").toArray().forEach((navElement) => {
      const navItem = $(navElement);
      kameHouse.util.dom.removeClass(navItem, "active");
      
      if (pageUrl == "/kame-house/" || pageUrl == "/kame-house/index.html") {
        setActiveNavItem(navItem, "nav-home");
      }

      const pages = {
        "/kame-house/about" : "nav-about",
        "/kame-house/admin" : "nav-admin",
        "/kame-house/contact-us" : "nav-contact-us",
        "/kame-house/downloads" : "nav-downloads",
        "/kame-house-groot" : "nav-groot",
        "/kame-house-mobile" : "nav-mobile",
        "/kame-house/tennisworld" : "nav-tennisworld",
        "/kame-house/test-module" : "nav-test-module",
        "/kame-house/vlc-player" : "nav-vlc-player"
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
      kameHouse.util.dom.addClass(navItem, "active");
    }
  }

  /** 
   * Toggle between adding and removing the "responsive" class to topnav when the user clicks on the icon. 
   */
  function toggleHeaderNav() {
    const headerMenu = document.getElementById("header-menu");
    if (headerMenu.className === "header-nav") {
      kameHouse.util.dom.classListAdd(headerMenu, "responsive");
    } else {
      kameHouse.util.dom.classListRemove(headerMenu, "responsive");
    }
  }

  /**
   * Update login status.
   */
  function updateLoginStatus() {
    const $loginStatus = $("#login-status");
    kameHouse.util.dom.empty($loginStatus);
    if (kameHouse.core.isEmpty(kameHouse.session.username) || kameHouse.session.username.trim() == "" ||
      kameHouse.session.username.trim() == "anonymousUser") {
      kameHouse.util.dom.append($loginStatus, getLoginButton());
    } else {
      kameHouse.util.dom.append($loginStatus, getUsernameHeader(kameHouse.session.username));
      kameHouse.util.dom.append($loginStatus, getLogoutButton());        
    }
  }

  /**
   * Checks if it's running in mobile device view.
   */
  function isMobile() {
    return window.matchMedia("only screen and (max-width: 900px)").matches;
  }

  /**
   * Show groot submenu.
   */
  function showGrootMenu() {
    if (isMobile()) {
      return;
    }
    const menu = document.getElementById("groot-menu-wrapper");
    if (!kameHouse.core.isEmpty(menu)) {
      kameHouse.util.dom.setDisplay(menu, "block");
    }
  }

  /**
   * Hide groot submenu.
   */
  function hideGrootMenu() {
    if (isMobile()) {
      return;
    }
    const menu = document.getElementById("groot-menu-wrapper");
    if (!kameHouse.core.isEmpty(menu)) {
      kameHouse.util.dom.setDisplay(menu, "none");
    }
  }

  /**
   * Get header container.
   */
  function getHeaderContainerDiv() {
    return kameHouse.util.dom.getDiv({
      id: "kamehouse-header-container"
    });
  }

  /**
   * Get login button.
   */
  function getLoginButton() {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/pc/login-left-red.png",
      className: "header-login-status-btn",
      alt: "Login KameHouse",
      onClick: () => {
        return kameHouse.util.mobile.executeOnMobile(
          () => {window.location="/kame-house-mobile/settings.html";},
          () => {window.location="/kame-house/login.html";}
        );
      }
    });
  }

  /**
   * Get logout button.
   */
  function getLogoutButton() {
    return kameHouse.util.mobile.executeOnMobile(
      () => {
        return kameHouse.util.dom.getImgBtn({
          src: "/kame-house/img/dbz/goku-dark-gray.png",
          className: "header-login-status-btn",
          alt: "KameHouse",
          onClick: () => {return;}
        });
      },
      () => {
        return kameHouse.util.dom.getImgBtn({
          src: "/kame-house/img/pc/logout-right-red.png",
          className: "header-login-status-btn",
          alt: "Logout KameHouse",
          onClick: () => window.location="/kame-house/logout"
        });
      }
    );
  }

  /**
   * Get username login status header span.
   */
  function getUsernameHeader(username) {
    return kameHouse.util.dom.getSpan({
      class: "header-login-status-text"
    }, username);
  }

  /**
   * update the session status.
   */
  function updateSessionStatus() {
    updateLoginStatus();
    kameHouse.util.banner.updateServerName();
  }  
}