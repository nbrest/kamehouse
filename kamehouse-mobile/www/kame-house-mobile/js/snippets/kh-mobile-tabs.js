/**
 * Kamehouse mobile tabs functions.
 */
const kameHouseMobileTabsManager = new KameHouseMobileTabsManager();

function main() {
  kameHouseMobileTabsManager.init();
}

/**
 * Prototype to manage the kamehouse mobile tabs.
 */
function KameHouseMobileTabsManager() {

  this.init = init;
  this.openTab = openTab;

  /**
   * Init module.
   */
  function init() {
    kameHouse.util.dom.load($("#kh-mobile-tabs-wrapper"), "/kame-house-mobile/html-snippets/kh-mobile-tabs.html", () => {
      kameHouse.util.module.setModuleLoaded("kameHouseMobileTabsManager");
    });
    kameHouse.util.dom.load($("#tab-home"), "/kame-house-mobile/html-snippets/tab-home.html");
    kameHouse.util.dom.load($("#tab-services"), "/kame-house-mobile/html-snippets/tab-services.html");
    kameHouse.util.module.waitForModules(["kameHouseMobile"], () => {
      kameHouse.util.dom.load($("#tab-config"), "/kame-house-mobile/html-snippets/tab-config.html", () => {
        kameHouse.mobile.configManager.refreshConfigTabView();
      });
    });
  }

  /**
   * Open the tab specified by its id.
   */
  function openTab(selectedTabDivId) {

    // Update tab links
    const kamehouseTabLinks = document.getElementsByClassName("kh-mobile-tab-link");
    for (const kamehouseTabLink of kamehouseTabLinks) {
      kameHouse.util.dom.classListRemove(kamehouseTabLink, "active");
    }
    const selectedTabLink = document.getElementById(selectedTabDivId + '-link');
    kameHouse.util.dom.classListAdd(selectedTabLink, "active");

    const kamehouseTabLinkImages = document.getElementsByClassName("kh-mobile-tab-link-img");
    for (const kamehouseTabLinkImage of kamehouseTabLinkImages) {
      kameHouse.util.dom.classListRemove(kamehouseTabLinkImage, "kh-mobile-tab-img-active");
    }
    const selectedTabLinkImage = document.getElementById(selectedTabDivId + '-link-img');
    kameHouse.util.dom.classListAdd(selectedTabLinkImage, "kh-mobile-tab-img-active");

    // Update tab content visibility
    const kamehouseTabContent = document.getElementsByClassName("kh-mobile-tab-content");
    for (const kamehouseTabContentElement of kamehouseTabContent) {
      kameHouse.util.dom.setDisplay(kamehouseTabContentElement, "none");
    }
    const selectedTabDiv = document.getElementById(selectedTabDivId);
    kameHouse.util.dom.setDisplay(selectedTabDiv, "block");

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

    if (selectedTabDivId == "tab-services") {
      bannerHeaderVal = "Services";
      bannerParagraphVal = "かめはうす"
    }

    if (selectedTabDivId == "tab-config") {
      bannerHeaderVal = "かめはうす";
      bannerParagraphVal = "Config"
    }

    const bannerHeader = document.getElementById("banner-header");
    if (bannerHeader) {
      kameHouse.util.dom.setInnerHtml(bannerHeader, bannerHeaderVal);
    }

    const bannerParagraph = document.getElementById("banner-p");
    if (bannerParagraph) {
      kameHouse.util.dom.setInnerHtml(bannerParagraph, bannerParagraphVal);
    }
  }
}

/**
 * Call main.
 */
$(document).ready(main);