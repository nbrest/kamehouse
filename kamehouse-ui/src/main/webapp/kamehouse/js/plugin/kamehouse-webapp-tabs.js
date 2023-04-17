/**
 * Kamehouse webapp tabs functions.
 */
/**
 * Prototype to manage the kamehouse webapp tabs.
 */
function KameHouseWebappTabsManager() {

  this.load = load;
  this.setCookiePrefix = setCookiePrefix;
  this.loadStateFromCookies = loadStateFromCookies;
  this.openTab = openTab;
  this.importTabs = importTabs;

  let cookiePrefix = '';

  function load() {
    importTabs();
  }

  /**
   * Set the cookie prefix for the tab manager.
   * For example use 'kh-admin-ehcache'.
   */
  function setCookiePrefix(cookiePrefixParam) {
    cookiePrefix = cookiePrefixParam;
  }

  /**
   * Load the current state from the cookies.
   */
  function loadStateFromCookies() {
    let currentTab = kameHouse.util.cookies.getCookie(cookiePrefix + '-current-tab');
    if (!currentTab || currentTab == '') {
      currentTab = 'tab-admin';
    }
    openTab(currentTab);
  }

  /**
   * Open the tab specified by its id.
   */
  function openTab(selectedTabDivId) {
    // Set current-tab cookie
    kameHouse.util.cookies.setCookie(cookiePrefix + '-current-tab', selectedTabDivId);

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
  function importTabs() {
    kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/plugin/kamehouse-webapp-tabs.css">');
    kameHouse.util.dom.load($("#kh-webapp-tabs-wrapper"), "/kame-house/kamehouse/html/plugin/kamehouse-webapp-tabs.html", () => {
      kameHouse.util.module.setModuleLoaded("kameHouseWebappTabsManager");
    });
  }
}

/**
 * Call main.
 */
$(document).ready(() => {kameHouse.addPlugin("kameHouseWebappTabsManager", new KameHouseWebappTabsManager());});