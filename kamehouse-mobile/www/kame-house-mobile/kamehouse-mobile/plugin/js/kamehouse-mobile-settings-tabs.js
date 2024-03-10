/**
 * Kamehouse mobile tabs functions.
 * Prototype to manage the kamehouse mobile tabs.
 * 
 * @author nbrest
 */
class KameHouseMobileTabsManager {

  /**
   * load mobile tabs manager plugin.
   */
  load() {
    kameHouse.util.dom.loadById("kh-mobile-tabs-wrapper", "/kame-house-mobile/kamehouse-mobile/plugin/html/kamehouse-mobile-settings-tabs.html", () => {
      kameHouse.util.module.setModuleLoaded("mobileTabsManager");
    });
    kameHouse.util.dom.loadById("tab-backend", "/kame-house-mobile/kamehouse-mobile/plugin/html/", () => {
      kameHouse.util.module.waitForModules(["kameHouseMobile"], () => {
        kameHouse.extension.mobile.configManager.refreshBackendServerViewFromConfig();
      });
    });
  }

  /**
   * Open the tab specified by its id.
   */
  openTab(selectedTabDivId) {

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
  }
}

kameHouse.ready(() => {
  kameHouse.addPlugin("mobileTabsManager", new KameHouseMobileTabsManager());
});