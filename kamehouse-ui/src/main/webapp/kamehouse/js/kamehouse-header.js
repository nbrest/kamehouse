/**
 * Header functions.
 * 
 * @author nbrest
 */
class KameHouseHeader {
  
  /** Load the header */
  load() {
    kameHouse.logger.info("Loading header");
    kameHouse.util.dom.append(document.head, '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/kamehouse-header.css">');
    kameHouse.ready(() => {
      // load the header after the other dom is ready to see if this fixes the very rare random header not loading
      kameHouse.util.dom.prepend(document.body, this.#getHeaderContainerDiv());
      kameHouse.util.dom.load("kamehouse-header-container", "/kame-house/kamehouse/html/kamehouse-header.html", () => {
        kameHouse.util.mobile.configureApp();
        this.#updateActiveTab();
        kameHouse.util.module.waitForModules(["kameHouseSession"], () => {
          this.#updateSessionStatus();
          kameHouse.util.module.setModuleLoaded("kameHouseHeader");
          kameHouse.logger.info("Finished loading header");
        });
        
      });
    });
  }

  /** 
   * Toggle between adding and removing the "responsive" class to topnav when the user clicks on the icon. 
   */
  toggleHeaderNav() {
    const headerMenu = document.getElementById("header-menu");
    if (headerMenu.className === "header-nav") {
      kameHouse.util.dom.classListAdd(headerMenu, "responsive");
    } else {
      kameHouse.util.dom.classListRemove(headerMenu, "responsive");
    }
  }  

  /**
   * Toggle debug mode.
   */
  toggleDebugMode() {
    const debugModeDiv = document.getElementById("debug-mode");
    if (debugModeDiv) {
      kameHouse.plugin.debugger.toggleDebugMode();
    } else {
      kameHouse.plugin.modal.basicModal.openAutoCloseable("No debug mode in this page", 1000);
      kameHouse.logger.info("No debug mode in this page");
    }
  }

  /**
   * Set active tab in the menu.
   */
  #updateActiveTab() {
    const pageUrl = window.location.pathname;
    document.querySelectorAll("#kamehouse-header-container header .default-layout #header-menu a").forEach((navItem) => {
      kameHouse.util.dom.removeClass(navItem, "active");
      
      if (pageUrl == "/kame-house/" || pageUrl == "/kame-house/index.html") {
        this.#setActiveNavItem(navItem, "nav-home");
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
        "/kame-house/jsp/test-module" : "nav-test-module",
        "/kame-house/vlc-player" : "nav-vlc-player"
      }

      for (const [urlSubstring, navId] of Object.entries(pages)) {
        this.#setActiveNavItemForPage(pageUrl, urlSubstring, navItem, navId);
      }
    });
  }

  /**
   * Set the active nav item if the page url matches the url substring.
   */
  #setActiveNavItemForPage(pageUrl, urlSubstring, navItem, navId) {
    if (pageUrl.includes(urlSubstring)) {
      this.#setActiveNavItem(navItem, navId);
    }
  }

  /**
   * Set active nav bar item.
   */
  #setActiveNavItem(navItem, navId) {
    if (navItem.getAttribute("id") == navId) {
      kameHouse.util.dom.addClass(navItem, "active");
    }
  }

  /**
   * Update login status.
   */
  #updateLoginStatus() {
    const loginStatus = document.getElementById("login-status");
    kameHouse.util.dom.empty(loginStatus);
    if (kameHouse.core.isEmpty(kameHouse.session.username) || kameHouse.session.username.trim() == "" ||
      kameHouse.session.username.trim() == "anonymousUser") {
      kameHouse.util.dom.append(loginStatus, this.#getLoginButton());
    } else {
      kameHouse.util.dom.append(loginStatus, this.#getUsernameHeader(kameHouse.session.username));
      kameHouse.util.dom.append(loginStatus, this.#getLogoutButton());        
    }
  }

  /**
   * Get header container.
   */
  #getHeaderContainerDiv() {
    return kameHouse.util.dom.getDiv({
      id: "kamehouse-header-container"
    });
  }

  /**
   * Get login button.
   */
  #getLoginButton() {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/pc/login-red-dark.png",
      className: "header-login-status-btn",
      alt: "Login KameHouse",
      onClick: () => {
        return kameHouse.util.mobile.windowLocation("/kame-house/login.html", "/kame-house-mobile/settings.html");
      }
    });
  }

  /**
   * Get logout button.
   */
  #getLogoutButton() {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/dbz/goku.png",
      className: "header-login-status-btn img-user-icon",
      alt: "Logout KameHouse",
      onClick: () => {
        return kameHouse.util.mobile.windowLocation("/kame-house/logout", "/kame-house-mobile/settings.html");
      }
    });
  }

  /**
   * Get username login status header span.
   */
  #getUsernameHeader(username) {
    return kameHouse.util.dom.getSpan({
      class: "header-login-status-text"
    }, username);
  }

  /**
   * update the session status.
   */
  #updateSessionStatus() {
    kameHouse.logger.info("Updating header with session status");
    this.#updateLoginStatus();
    kameHouse.util.banner.updateServerName();
  }  
}