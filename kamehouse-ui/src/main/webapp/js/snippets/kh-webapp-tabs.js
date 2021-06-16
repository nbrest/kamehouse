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
  let self = this;

  /**
   * Open the tab specified by its id.
   */
  this.openTab = (selectedTabDivId) => {
    // Update tab links
    let kamehouseTabLinks = document.getElementsByClassName("kh-webapp-tab-link");
    for (let i = 0; i < kamehouseTabLinks.length; i++) {
      kamehouseTabLinks[i].className = kamehouseTabLinks[i].className.replace(" active", "");
    }
    let selectedTabLink = document.getElementById(selectedTabDivId + '-link');
    selectedTabLink.classList.add("active");

    // Update tab content visibility
    let kamehouseTabContent = document.getElementsByClassName("kh-webapp-tab-content");
    for (let i = 0; i < kamehouseTabContent.length; i++) {
      kamehouseTabContent[i].style.display = "none";
    }
    let selectedTabDiv = document.getElementById(selectedTabDivId);
    selectedTabDiv.style.display = "block";
  }

  /**
   * Import tabs.
   */
  this.importTabs = () => {
    $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/kh-webapp-tabs.css">');
    $("#kh-webapp-tabs-wrapper").load("/kame-house/html-snippets/kh-webapp-tabs.html", () => {
      moduleUtils.setModuleLoaded("kameHouseWebappTabsManager");
    });
  }

}

/**
 * Call main.
 */
$(document).ready(main);