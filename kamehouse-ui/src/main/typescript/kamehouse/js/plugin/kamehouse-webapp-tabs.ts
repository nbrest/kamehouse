/**
 * Kamehouse webapp tabs functions.
 * Prototype to manage the kamehouse webapp tabs.
 * 
 * @author nbrest
 */
class KameHouseWebappTabsManager {

  #cookiePrefix = '';

  /**
   * Load kamehouse webapps tab manager plugin.
   */
  load() {
    this.importTabs();
  }

  /**
   * Set the cookie prefix for the tab manager.
   * For example use 'kh-admin-ehcache'.
   */
  cookiePrefix(cookiePrefixParam) {
    this.#cookiePrefix = cookiePrefixParam;
  }

  /**
   * Load the current state from the cookies.
   */
  loadStateFromCookies() {
    let currentTab = kameHouse.util.cookies.getCookie(this.#cookiePrefix + '-current-tab');
    if (!currentTab || currentTab == '') {
      currentTab = 'tab-admin';
    }
    this.openTab(currentTab);
  }

  /**
   * Open the tab specified by its id.
   */
  openTab(selectedTabDivId) {
    // Set current-tab cookie
    kameHouse.util.cookies.setCookie(this.#cookiePrefix + '-current-tab', selectedTabDivId, null);

    // Update tab links
    const kamehouseTabLinks = document.getElementsByClassName("kh-webapp-tab-link");
    for (const kamehouseTabLink of kamehouseTabLinks) {
      kameHouse.util.dom.classListRemove(kamehouseTabLink, "active");
    }
    const selectedTabLink = document.getElementById(selectedTabDivId + '-link');
    kameHouse.util.dom.classListAdd(selectedTabLink, "active");

    // Update tab content visibility
    const kamehouseTabContent = document.getElementsByClassName("kh-webapp-tab-content");
    for (const kamehouseTabContentElement of kamehouseTabContent) {
      kameHouse.util.dom.setDisplay(kamehouseTabContentElement, "none");
    }
    const selectedTabDiv = document.getElementById(selectedTabDivId);
    kameHouse.util.dom.setDisplay(selectedTabDiv, "block");
  }

  /**
   * Import tabs.
   */
  importTabs() {
    kameHouse.util.dom.append(kameHouse.util.dom.getHead(), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/plugin/kamehouse-webapp-tabs.css">');
    kameHouse.util.dom.loadById("kh-webapp-tabs-wrapper", "/kame-house/kamehouse/html/plugin/kamehouse-webapp-tabs.html", () => {
      kameHouse.util.module.setModuleLoaded("webappTabsManager");
    });
  }
}

kameHouse.ready(() => {kameHouse.addPlugin("webappTabsManager", new KameHouseWebappTabsManager());});