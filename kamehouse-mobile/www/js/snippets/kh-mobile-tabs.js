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
    domUtils.load($("#tab-home"), "/html-snippets/tab-home.html", () => {
      moduleUtils.setModuleLoaded("kameHouseMobileTabsManager");
    });
    domUtils.load($("#tab-services"), "/html-snippets/tab-services.html");
    moduleUtils.waitForModules(["mobileConfigManager"], () => {
      domUtils.load($("#tab-config"), "/html-snippets/tab-config.html", () => {
        initConfigTab();
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
   * Init config tab values.
   */
  function initConfigTab() {
    logger.info("Initializing config tab");
    const vlcServer = mobileConfigManager.getServers().find(server => server.name === "vlc");
    const vlcServerInput = document.getElementById("vlc-server-input");
    domUtils.setValue(vlcServerInput, vlcServer.url);
  }
}

/**
 * Call main.
 */
$(document).ready(main);