var moduleStatusManager;

function loadModuleStatusManager() {
  kameHouse.util.module.waitForModules(["kameHouseDebugger"], () => {
    moduleStatusManager = new ModuleStatusManager();
    moduleStatusManager.init();
    kameHouse.util.module.setModuleLoaded("moduleStatusManager");
    kameHouse.logger.info("Initialized moduleStatusManager");
  });
}

/**
 * Manager to get the status of the tomcat modules in the current server.
 */
function ModuleStatusManager() {

  this.init = init;
  this.getAllModulesStatus = getAllModulesStatus;
  
  /**
   * Get the data from the backend and import css.
   */
  function init() {
    getAllModulesStatus();
  }

  /**
   * Get the data from the backend.
   */
  function getAllModulesStatus() {
    getModuleStatus('admin');
    getModuleStatus('media');
    getModuleStatus('tennisworld');
    getModuleStatus('testmodule');
    getModuleStatus('ui');
    getModuleStatus('vlcrc');
  }

  /**
   * Get module status api url for each webapp.
   */
  function getApiUrl(webapp) {
    if (webapp == "ui") {
      return '/kame-house/api/v1/commons/module/status';
    } else {
      return '/kame-house-' + webapp + '/api/v1/commons/module/status';
    }
  }

  /**
   * Get module status.
   */
  function getModuleStatus(webapp) {
    kameHouse.logger.trace("getModuleStatus");
    kameHouse.plugin.debugger.http.get(getApiUrl(webapp),
      (responseBody, responseCode, responseDescription) => displayModuleStatus(responseBody),
      (responseBody, responseCode, responseDescription) => displayErrorGettingModuleStatus(webapp));
  }

  /**
   * Display module status.
   */
  function displayModuleStatus(moduleStatus) {
    const webapp = moduleStatus["module"];
    kameHouse.util.dom.setHtml($('#mst-' + webapp + '-build-version-val'), moduleStatus["buildVersion"]);
    kameHouse.util.dom.setHtml($('#mst-' + webapp + '-build-date-val'), moduleStatus["buildDate"]);
  }

  /**
   * Display error getting data.
   */
  function displayErrorGettingModuleStatus(webapp) {
    kameHouse.util.dom.removeClass($("#mst-" + webapp + "-error"), "hidden-kh");
    kameHouse.logger.error("Error retrieving module status data for " + webapp + ". Please try again later.");
  }
}

$(document).ready(loadModuleStatusManager);