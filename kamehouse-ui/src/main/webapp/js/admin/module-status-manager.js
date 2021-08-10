var moduleStatusManager;

function loadModuleStatusManager() {
  moduleUtils.waitForModules(["logger", "debuggerHttpClient"], () => {
    moduleStatusManager = new ModuleStatusManager();
    moduleStatusManager.init();
    moduleUtils.setModuleLoaded("moduleStatusManager");
    logger.info("Initialized moduleStatusManager");
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
    logger.trace("getModuleStatus");
    debuggerHttpClient.get(getApiUrl(webapp),
      (responseBody, responseCode, responseDescription) => displayModuleStatus(responseBody),
      (responseBody, responseCode, responseDescription) => displayErrorGettingModuleStatus(webapp));
  }

  /**
   * Display module status.
   */
  function displayModuleStatus(moduleStatus) {
    let webapp = moduleStatus["module"];
    domUtils.setHtml($('#mst-' + webapp + '-build-version-val'), moduleStatus["buildVersion"]);
    domUtils.setHtml($('#mst-' + webapp + '-build-date-val'), moduleStatus["buildDate"]);
  }

  /**
   * Display error getting data.
   */
  function displayErrorGettingModuleStatus(webapp) {
    domUtils.removeClass($("#mst-" + webapp + "-error"), "hidden-kh");
    logger.error("Error retrieving module status data for " + webapp + ". Please try again later.");
  }
}

$(document).ready(loadModuleStatusManager);