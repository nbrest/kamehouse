var moduleStatusManager;

var loadModuleStatusManager = () => {
  moduleUtils.waitForModules(["logger", "debuggerHttpClient"], () => {
    moduleStatusManager = new ModuleStatusManager();
    moduleStatusManager.init();
    moduleUtils.setModuleLoaded("moduleStatusManager");
    logger.info("Initialized moduleStatusManager");
  });
};

/**
 * Manager to get the status of the tomcat modules in the current server.
 */
function ModuleStatusManager() {
  let self = this;

  /**
   * Get the data from the backend and import css.
   */
  this.init = () => {
    self.importModuleStatusCss();
    self.getAllModulesStatus();
  }

  /**
   * Get the data from the backend.
   */
  this.getAllModulesStatus = () => {
    self.getModuleStatus('admin');
    self.getModuleStatus('media');
    self.getModuleStatus('tennisworld');
    self.getModuleStatus('testmodule');
    self.getModuleStatus('ui');
    self.getModuleStatus('vlcrc');
  }

  /** Import module status css */
  this.importModuleStatusCss = () => {
    $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/module-status.css">');
  }

  /**
   * Get module status api url for each webapp.
   */
  this.getApiUrl = (webapp) => {
    if (webapp == "ui") {
      return '/kame-house/api/v1/commons/module/status';
    } else {
      return '/kame-house-' + webapp + '/api/v1/commons/module/status';
    }
  }

  /**
   * Get module status.
   */
  this.getModuleStatus = (webapp) => {
    logger.trace("getModuleStatus");
    debuggerHttpClient.get(self.getApiUrl(webapp),
      (responseBody, responseCode, responseDescription) => self.displayModuleStatus(responseBody),
      (responseBody, responseCode, responseDescription) => self.displayErrorGettingModuleStatus(webapp));
  }

  /**
   * Display module status.
   */
  this.displayModuleStatus = (moduleStatus) => {
    let webapp = moduleStatus["module"];
    $('#mst-' + webapp + '-build-version-val').text(moduleStatus["buildVersion"]);
    $('#mst-' + webapp + '-build-date-val').text(moduleStatus["buildDate"]);
  }

  /**
   * Display error getting data.
   */
  this.displayErrorGettingModuleStatus = (webapp) => {
    $("#mst-" + webapp + "-error").removeClass("hidden-kh");
    logger.error("Error retrieving module status data for " + webapp + ". Please try again later.");
  }
}

$(document).ready(loadModuleStatusManager);