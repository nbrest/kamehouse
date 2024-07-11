/**
 * Manager to get the status of the tomcat modules in the current server.
 */
class TomcatModuleStatusManager {

  /**
   * Load the extension.
   */
  load() {
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger"], () => {
      this.#init();
      kameHouse.util.module.setModuleLoaded("moduleStatusManager");
      kameHouse.logger.info("Initialized moduleStatusManager", null);
    });
  }

  /**
   * Get the data from the backend.
   */
  getAllModulesStatus() {
    this.#getModuleStatus('admin');
    this.#getModuleStatus('media');
    this.#getModuleStatus('tennisworld');
    this.#getModuleStatus('testmodule');
    this.#getModuleStatus('ui');
    this.#getModuleStatus('vlcrc');
  }

  /**
   * Get the data from the backend and import css.
   */
  #init() {
    this.getAllModulesStatus();
  }

  /**
   * Get module status api url for each webapp.
   */
  #getApiUrl(webapp) {
    if (webapp == "ui") {
      return '/kame-house/api/v1/commons/module/status';
    } else {
      return '/kame-house-' + webapp + '/api/v1/commons/module/status';
    }
  }

  /**
   * Get module status.
   */
  #getModuleStatus(webapp) {
    kameHouse.logger.trace("getModuleStatus", null);
    const config = kameHouse.http.getConfig();
    config.timeout = 15;
    kameHouse.plugin.debugger.http.get(config, this.#getApiUrl(webapp), null, null,
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#displayModuleStatus(responseBody),
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#displayErrorGettingModuleStatus(webapp));
  }

  /**
   * Display module status.
   */
  #displayModuleStatus(moduleStatus) {
    const webapp = moduleStatus["module"];
    kameHouse.util.dom.setHtmlById('mst-' + webapp + '-build-version-val', moduleStatus["buildVersion"]);
    kameHouse.util.dom.setHtmlById('mst-' + webapp + '-build-date-val', moduleStatus["buildDate"]);
  }

  /**
   * Display error getting data.
   */
  #displayErrorGettingModuleStatus(webapp) {
    kameHouse.util.dom.setHtmlById('mst-' + webapp + '-build-version-val', "Error getting data");
    kameHouse.util.dom.setHtmlById('mst-' + webapp + '-build-date-val', "Error getting data");
    const message = "Error retrieving module status data for " + webapp + ". Please try again later.";
    kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("tomcatModuleStatusManager", new TomcatModuleStatusManager());
});