/**
 * Header functions.
 * 
 * @author nbrest
 */
class KameHouseHeader {
  
  /** Load the header */
  load() {
    kameHouse.logger.info("Loading header", null);
    kameHouse.util.dom.append(kameHouse.util.dom.getHead(), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/kamehouse-header.css">');
    kameHouse.ready(() => {
      // load the header after the other dom is ready to see if this fixes the very rare random header not loading
      kameHouse.util.dom.prepend(kameHouse.util.dom.getBody(), this.#getHeaderContainerDiv());
      kameHouse.util.dom.loadById("kamehouse-header-container", "/kame-house/kamehouse/html/kamehouse-header.html", () => {
        kameHouse.core.configDynamicHtml();
        this.#updateActiveTab();
        kameHouse.util.module.waitForModules(["kameHouseSession"], () => {
          this.#updateHeaderWithSessionInfo();
          kameHouse.util.module.setModuleLoaded("kameHouseHeader");
          kameHouse.logger.info("Finished loading header", null);
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
      kameHouse.logger.info("No debug mode in this page", null);
    }
  }

  /**
   * Set active tab in the menu.
   */
  #updateActiveTab() {
    const pageUrl = window.location.pathname;
    document.querySelectorAll("#kamehouse-header-container header .default-layout #header-menu a").forEach((navItem) => {
      kameHouse.util.dom.classListRemove(navItem, "active");
      
      if (pageUrl == "/kame-house/" || pageUrl == "/kame-house/index.html") {
        this.#setActiveNavItem(navItem, "nav-home");
      }

      const pages = {
        "/kame-house/about" : "nav-about",
        "/kame-house/admin" : "nav-admin",
        "/kame-house/contact-us" : "nav-contact-us",
        "/kame-house/downloads" : "nav-downloads",
        "/kame-house/groot" : "nav-groot",
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
      kameHouse.util.dom.classListAdd(navItem, "active");
    }
  }

  /**
   * Update kamehouse header with kamehouse session info.
   */
  #updateHeaderWithSessionInfo() {
    kameHouse.logger.info("Updating kamehouse header with kamehouse session info", null);
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
    }, null);
  }

  /**
   * Get login button.
   */
  #getLoginButton() {
    return kameHouse.util.dom.getButton({
      attr: {
        class: "header-login-status-btn va-m-kh img-btn-kh",
      },
      mobileClass: null,
      backgroundImg: "/kame-house/img/pc/login-red-dark.png",
      html: null,
      data: null,
      click: (event, data) => kameHouse.util.mobile.windowLocation("/kame-house/login.html", "/kame-house-mobile/settings.html")
    });
  }

  /**
   * Get logout button.
   */
  #getLogoutButton() {
    return kameHouse.util.dom.getButton({
      attr: {
        class: "header-login-status-btn va-m-kh img-user-icon",
      },
      mobileClass: null,
      backgroundImg: "/kame-house/img/dbz/goku.png",
      html: null,
      data: null,
      click: (event, data) => kameHouse.util.mobile.windowLocation("/kame-house/logout", "/kame-house-mobile/settings.html")
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
}