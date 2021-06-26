/**
 * Module Status main function.
 * 
 * Dependencies: timeUtils, logger, debuggerHttpClient.
 * 
 * @author nbrest
 */
var moduleStatusManager;

var main = () => {
  bannerUtils.setRandomPrinceOfTennisBanner();
  importModuleStatusCss();
  moduleUtils.waitForModules(["logger", "debuggerHttpClient"], () => {
    logger.info("Started initializing module status");
    moduleStatusManager = new ModuleStatusManager();
    moduleStatusManager.init();
  });
};

/** Import module status css */
function importModuleStatusCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/module-status.css">');
}

function ModuleStatusManager() {
  let self = this;

  /**
   * Get the data from the backend.
   */
  this.init = () => {
    self.getModuleStatus('admin');
    self.getModuleStatus('media');
    self.getModuleStatus('tennisworld');
    self.getModuleStatus('testmodule');
    self.getModuleStatus('ui');
    self.getModuleStatus('vlcrc');
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
    $('#mst-' + webapp + '-header-val').text(moduleStatus["server"] + " - " + webapp);
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

/**
 * Call main.
 */
$(document).ready(main);