/**
 * Kamehouse webapp tabs functions.
 */
var kameHouseWebappTabsManager;

function main() {
  kameHouseWebappTabsManager = new KameHouseWebappTabsManager();
  kameHouseWebappTabsManager.importTabs();
}

/**
 * Prototype to manage the kamehouse webapp tabs.
 */
function KameHouseWebappTabsManager() {

  this.setCookiePrefix = setCookiePrefix;
  this.loadStateFromCookies = loadStateFromCookies;
  this.openTab = openTab;
  this.importTabs = importTabs;

  let cookiePrefix = '';

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
    let currentTab = cookiesUtils.getCookie(cookiePrefix + '-current-tab');
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
    cookiesUtils.setCookie(cookiePrefix + '-current-tab', selectedTabDivId);

    // Update tab links
    const kamehouseTabLinks = document.getElementsByClassName("kh-webapp-tab-link");
    for (const kamehouseTabLink of kamehouseTabLinks) {
      domUtils.classListRemove(kamehouseTabLink, "active");
    }
    const selectedTabLink = document.getElementById(selectedTabDivId + '-link');
    domUtils.classListAdd(selectedTabLink, "active");

    // Update tab content visibility
    const kamehouseTabContent = document.getElementsByClassName("kh-webapp-tab-content");
    for (const kamehouseTabContentElement of kamehouseTabContent) {
      domUtils.setDisplay(kamehouseTabContentElement, "none");
    }
    const selectedTabDiv = document.getElementById(selectedTabDivId);
    domUtils.setDisplay(selectedTabDiv, "block");
  }

  /**
   * Import tabs.
   */
  function importTabs() {
    domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/kamehouse-webapp-tabs.css">');
    domUtils.load($("#kh-webapp-tabs-wrapper"), "/kame-house/kamehouse/html/kamehouse-webapp-tabs.html", () => {
      moduleUtils.setModuleLoaded("kameHouseWebappTabsManager");
    });
  }
}

/**
 * Call main.
 */
$(document).ready(main);