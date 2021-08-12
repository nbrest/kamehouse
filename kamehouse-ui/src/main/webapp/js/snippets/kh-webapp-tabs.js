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
    for (let i = 0; i < kamehouseTabLinks.length; i++) {
      domUtils.classListRemove(kamehouseTabLinks[i], "active");
    }
    const selectedTabLink = document.getElementById(selectedTabDivId + '-link');
    domUtils.classListAdd(selectedTabLink, "active");

    // Update tab content visibility
    const kamehouseTabContent = document.getElementsByClassName("kh-webapp-tab-content");
    for (let i = 0; i < kamehouseTabContent.length; i++) {
      domUtils.setDisplay(kamehouseTabContent[i], "none");
    }
    const selectedTabDiv = document.getElementById(selectedTabDivId);
    domUtils.setDisplay(selectedTabDiv, "block");
  }

  /**
   * Import tabs.
   */
  function importTabs() {
    domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/kh-webapp-tabs.css">');
    domUtils.load($("#kh-webapp-tabs-wrapper"), "/kame-house/html-snippets/kh-webapp-tabs.html", () => {
      moduleUtils.setModuleLoaded("kameHouseWebappTabsManager");
    });
  }
}

/**
 * Call main.
 */
$(document).ready(main);