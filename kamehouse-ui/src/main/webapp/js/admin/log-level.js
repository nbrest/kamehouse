/** 
 * Functionality to manipulate log levels in the backend. 
 */
var backendLogLevelUtils;

window.onload = () => {
  backendLogLevelUtils = new BackendLogLevelUtils();
  moduleUtils.waitForModules(["logger", "debuggerHttpClient", "kameHouseWebappTabsManager"], () => {
    logger.info("Started initializing log-level");
    kameHouseWebappTabsManager.setCookiePrefix('kh-admin-log-level');
    kameHouseWebappTabsManager.loadStateFromCookies();
    backendLogLevelUtils.init();
  });
  bannerUtils.setRandomAllBanner();
};

/**
 * Manage the log level of the backend on the current server.
 */
function BackendLogLevelUtils() {
  let self = this;
  this.logLevelTableTemplate;

  /**
   * Load templates and initial data.
   */
  this.init = () => {
    self.getLogLevels('admin', false);
    self.getLogLevels('media', false);
    self.getLogLevels('tennisworld', false);
    self.getLogLevels('testmodule', false);
    self.getLogLevels('ui', false);
    self.getLogLevels('vlcrc', false);
  }

  /**
   * Get log-level api url for each webapp.
   */
  this.getApiUrl = (webapp) => {
    if (webapp == "ui") {
      return '/kame-house/api/v1/commons/log-level';
    } else {
      return '/kame-house-' + webapp + '/api/v1/commons/log-level';
    }
  }

  /**
   * Get log-level request logger config api url for each webapp.
   */
  this.getRequestLoggerConfigApiUrl = (webapp) => {
    return self.getApiUrl(webapp) + "/request-logger";
  }

  /** Get all current log levels */
  this.getLogLevels = (webapp, openModal) => {
    if (openModal) {
      loadingWheelModal.open();
    }
    debuggerHttpClient.get(self.getApiUrl(webapp), processSuccess, processError, webapp);
  }

  /** Reset all log levels */
  this.resetLogLevels = (webapp) => {
    loadingWheelModal.open();
    debuggerHttpClient.delete(self.getApiUrl(webapp), null, processSuccess, processError, webapp);
  }

  /** Set Kamehouse log level */
  this.setKamehouseLogLevel = (webapp) => {
    let logLevel = document.getElementById("select-kamehouse-log-level-" + webapp).value;
    loadingWheelModal.open();
    debuggerHttpClient.put(self.getApiUrl(webapp) + logLevel, null, processSuccess, processError, webapp);
  }

  /** Set Kamehouse log levels to DEBUG */
  this.setKamehouseLogLevelToDebug = (webapp) => {
    loadingWheelModal.open();
    debuggerHttpClient.put(self.getApiUrl(webapp) + "/debug", null, processSuccess, processError, webapp);
  }

  /** Set Kamehouse log levels to TRACE */
  this.setKamehouseLogLevelToTrace = (webapp) => {
    loadingWheelModal.open();
    debuggerHttpClient.put(self.getApiUrl(webapp) + "/trace", null, processSuccess, processError, webapp);
  }

  /** Update the log levels table content */
  this.updateLogLevelTable = (logLevelsArray, webapp) => {
    self.addLogLevelTableHeader(webapp);
    let $tableBody = $('#log-level-tbody-' + webapp);
    logLevelsArray.forEach((logLevelEntry) => {
      let logLevelEntryPair = logLevelEntry.split(":");
      let packageName = logLevelEntryPair[0];
      let logLevel = logLevelEntryPair[1];
      $tableBody.append(getLogLevelTr(packageName, logLevel));
    });
  }

  /** Add log level table header */
  this.addLogLevelTableHeader = (webapp) => {
    let $tableBody = $('#log-level-tbody-' + webapp);
    $tableBody.empty();
    $tableBody.append(getLogLevelTh(webapp));
  }

  /** Set log level table to error */
  this.updateLogLevelTableError = (webapp) => {
    let $tableBody = $('#log-level-tbody-' + webapp);
    $tableBody.empty();
    $tableBody.append(getErrorTr());
  }

  /** Get row for errot table */
  function getErrorTr() {
    return domUtils.getTrTd("Error retrieving log levels from the backend");
  }

  /** Get data row for log level table */
  function getLogLevelTr(packageName, logLevel) {
    let tr = domUtils.getTr(null, null);
    domUtils.append(tr, domUtils.getTd(null, packageName));
    domUtils.append(tr, domUtils.getTd(null, logLevel));
    return tr;
  }

  /** Get header row for log level table */
  function getLogLevelTh(webapp) {
    let tr = domUtils.getTr({
      id: "log-level-thead-" + webapp,
      class: "table-kh-header"
    }, null);
    domUtils.append(tr, domUtils.getTd(null, "Package Name"));
    domUtils.append(tr, domUtils.getTd(null, "Log Level"));
    return tr;
  }

  /** Process success response */
  function processSuccess(responseBody, responseCode, responseDescription, webapp) {
    loadingWheelModal.close();
    self.updateLogLevelTable(responseBody, webapp);
  }

  /** Process error response */
  function processError(responseBody, responseCode, responseDescription, webapp) {
    loadingWheelModal.close();
    self.updateLogLevelTableError(webapp);
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
  }

  /** Set request logger config payload */
  this.setRequestLoggerConfigPayload = (webapp) => {
    self.setRequestLoggerConfig(webapp, "payload", "logPayload");
  }

  /** Set request logger config headers */
  this.setRequestLoggerConfigHeaders = (webapp) => {
    self.setRequestLoggerConfig(webapp, "headers", "logHeaders");
  }

  /** Set request logger config query string */
  this.setRequestLoggerConfigQueryString = (webapp) => {
    self.setRequestLoggerConfig(webapp, "query-string", "logQueryString");
  }

  /** Set request logger config client info */
  this.setRequestLoggerConfigClientInfo = (webapp) => {
    self.setRequestLoggerConfig(webapp, "client-info", "logClientInfo");
  }

  /** Set request logger config */
  this.setRequestLoggerConfig = (webapp, propertyToSet, urlParamName) => {
    loadingWheelModal.open();
    let propertyValue = document.getElementById("select-kh-req-logger-cfg-" + propertyToSet + "-" + webapp).value;
    let url = self.getRequestLoggerConfigApiUrl(webapp) + "/" + propertyToSet + "?" + urlParamName + "=" + propertyValue;
    debuggerHttpClient.put(url, null, processSuccessRequestLoggerConfig, processErrorRequestLoggerConfig, webapp);
  }

  /** Process success response for request logger config */
  function processSuccessRequestLoggerConfig(responseBody, responseCode, responseDescription, webapp) {
    loadingWheelModal.close();
    basicKamehouseModal.openAutoCloseable(webapp + " : " + responseBody.message, 7000);
  }

  /** Process error response for request logger config */
  function processErrorRequestLoggerConfig(responseBody, responseCode, responseDescription, webapp) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
  }
}