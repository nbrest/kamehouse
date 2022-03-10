/**
 * Kamehouse mobile tabs functions.
 */
var kameHouseMobileTabsManager;

function main() {
  kameHouseMobileTabsManager = new KameHouseMobileTabsManager();
  kameHouseMobileTabsManager.importTabs();
}

/**
 * Prototype to manage the kamehouse mobile tabs.
 */
function KameHouseMobileTabsManager() {

  this.openTab = openTab;
  this.importTabs = importTabs;

  /**
   * Open the tab specified by its id.
   */
  function openTab(selectedTabDivId) {

    // Update tab links
    const kamehouseTabLinks = document.getElementsByClassName("kh-mobile-tab-link");
    for (const kamehouseTabLink of kamehouseTabLinks) {
      domUtils.classListRemove(kamehouseTabLink, "active");
    }
    const selectedTabLink = document.getElementById(selectedTabDivId + '-link');
    domUtils.classListAdd(selectedTabLink, "active");

    // Update tab content visibility
    const kamehouseTabContent = document.getElementsByClassName("kh-mobile-tab-content");
    for (const kamehouseTabContentElement of kamehouseTabContent) {
      domUtils.setDisplay(kamehouseTabContentElement, "none");
    }
    const selectedTabDiv = document.getElementById(selectedTabDivId);
    domUtils.setDisplay(selectedTabDiv, "block");

    setBannerHeader(selectedTabDivId);
  }

  /**
   * Update the banner text based on the tab open.
   */
  function setBannerHeader(selectedTabDivId) {
    let bannerHeaderVal = null;
    let bannerParagraphVal = null;

    if (selectedTabDivId == "tab-home") {
      bannerHeaderVal = "カメハウス";
      bannerParagraphVal = "KameHouse Mobile"
    }

    if (selectedTabDivId == "tab-config") {
      bannerHeaderVal = "Config";
      bannerParagraphVal = ""
    }

    if (selectedTabDivId == "tab-services") {
      bannerHeaderVal = "Services";
      bannerParagraphVal = ""
    }

    const bannerHeader = document.getElementById("banner-header");
    domUtils.setInnerHtml(bannerHeader, bannerHeaderVal);

    const bannerParagraph = document.getElementById("banner-p");
    domUtils.setInnerHtml(bannerParagraph, bannerParagraphVal);
  }

  /**
   * Import tabs.
   */
  function importTabs() {
    domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/css/snippets/kh-mobile-tabs.css">');
    moduleUtils.setModuleLoaded("kameHouseMobileTabsManager");
  }
}

/**
 * Call main.
 */
$(document).ready(main);