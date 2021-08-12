/** 
 * Functionality to manipulate log levels in the backend. 
 */
var backendLogLevelUtils;

window.onload = () => {
  backendLogLevelUtils = new BackendLogLevelUtils();
  moduleUtils.waitForModules(["debuggerHttpClient", "kameHouseWebappTabsManager"], () => {
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

  this.init = init;
  this.getLogLevels = getLogLevels;
  this.resetLogLevels = resetLogLevels;
  this.setKamehouseLogLevel = setKamehouseLogLevel;
  this.setKamehouseLogLevelToDebug = setKamehouseLogLevelToDebug;
  this.setKamehouseLogLevelToTrace = setKamehouseLogLevelToTrace;
  this.setRequestLoggerConfigPayload = setRequestLoggerConfigPayload;
  this.setRequestLoggerConfigHeaders = setRequestLoggerConfigHeaders;
  this.setRequestLoggerConfigQueryString = setRequestLoggerConfigQueryString;
  this.setRequestLoggerConfigClientInfo = setRequestLoggerConfigClientInfo;

  /**
   * Load templates and initial data.
   */
  function init() {
    getLogLevels('admin', false);
    getLogLevels('media', false);
    getLogLevels('tennisworld', false);
    getLogLevels('testmodule', false);
    getLogLevels('ui', false);
    getLogLevels('vlcrc', false);
  }

  /**
   * Get log-level api url for each webapp.
   */
  function getApiUrl(webapp) {
    if (webapp == "ui") {
      return '/kame-house/api/v1/commons/log-level';
    } else {
      return '/kame-house-' + webapp + '/api/v1/commons/log-level';
    }
  }

  /**
   * Get log-level request logger config api url for each webapp.
   */
  function getRequestLoggerConfigApiUrl(webapp) {
    return getApiUrl(webapp) + "/request-logger";
  }

  /** Get all current log levels */
  function getLogLevels(webapp, openModal) {
    if (openModal) {
      loadingWheelModal.open();
    }
    debuggerHttpClient.get(getApiUrl(webapp), processSuccess, processError, webapp);
  }

  /** Reset all log levels */
  function resetLogLevels(webapp) {
    loadingWheelModal.open();
    debuggerHttpClient.delete(getApiUrl(webapp), null, processSuccess, processError, webapp);
  }

  /** Set Kamehouse log level */
  function setKamehouseLogLevel(webapp) {
    const logLevel = document.getElementById("select-kamehouse-log-level-" + webapp).value;
    loadingWheelModal.open();
    debuggerHttpClient.put(getApiUrl(webapp) + logLevel, null, processSuccess, processError, webapp);
  }

  /** Set Kamehouse log levels to DEBUG */
  function setKamehouseLogLevelToDebug(webapp) {
    loadingWheelModal.open();
    debuggerHttpClient.put(getApiUrl(webapp) + "/debug", null, processSuccess, processError, webapp);
  }

  /** Set Kamehouse log levels to TRACE */
  function setKamehouseLogLevelToTrace(webapp) {
    loadingWheelModal.open();
    debuggerHttpClient.put(getApiUrl(webapp) + "/trace", null, processSuccess, processError, webapp);
  }

  /** Update the log levels table content */
  function updateLogLevelTable(logLevelsArray, webapp) {
    addLogLevelTableHeader(webapp);
    const $tableBody = $('#log-level-tbody-' + webapp);
    logLevelsArray.forEach((logLevelEntry) => {
      const logLevelEntryPair = logLevelEntry.split(":");
      const packageName = logLevelEntryPair[0];
      const logLevel = logLevelEntryPair[1];
      domUtils.append($tableBody, getLogLevelTr(packageName, logLevel));
    });
  }

  /** Add log level table header */
  function addLogLevelTableHeader(webapp) {
    const $tableBody = $('#log-level-tbody-' + webapp);
    domUtils.empty($tableBody);
    domUtils.append($tableBody, getLogLevelTh(webapp));
  }

  /** Set log level table to error */
  function updateLogLevelTableError(webapp) {
    const $tableBody = $('#log-level-tbody-' + webapp);
    domUtils.empty($tableBody);
    domUtils.append($tableBody, getErrorTr());
  }

  /** Get row for errot table */
  function getErrorTr() {
    return domUtils.getTrTd("Error retrieving log levels from the backend");
  }

  /** Get data row for log level table */
  function getLogLevelTr(packageName, logLevel) {
    const tr = domUtils.getTr(null, null);
    domUtils.append(tr, domUtils.getTd(null, packageName));
    domUtils.append(tr, domUtils.getTd(null, logLevel));
    return tr;
  }

  /** Get header row for log level table */
  function getLogLevelTh(webapp) {
    const tr = domUtils.getTr({
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
    updateLogLevelTable(responseBody, webapp);
  }

  /** Process error response */
  function processError(responseBody, responseCode, responseDescription, webapp) {
    loadingWheelModal.close();
    updateLogLevelTableError(webapp);
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
  }

  /** Set request logger config payload */
  function setRequestLoggerConfigPayload(webapp) {
    setRequestLoggerConfig(webapp, "payload", "logPayload");
  }

  /** Set request logger config headers */
  function setRequestLoggerConfigHeaders(webapp) {
    setRequestLoggerConfig(webapp, "headers", "logHeaders");
  }

  /** Set request logger config query string */
  function setRequestLoggerConfigQueryString(webapp) {
    setRequestLoggerConfig(webapp, "query-string", "logQueryString");
  }

  /** Set request logger config client info */
  function setRequestLoggerConfigClientInfo(webapp) {
    setRequestLoggerConfig(webapp, "client-info", "logClientInfo");
  }

  /** Set request logger config */
  function setRequestLoggerConfig(webapp, propertyToSet, urlParamName) {
    loadingWheelModal.open();
    const propertyValue = document.getElementById("select-kh-req-logger-cfg-" + propertyToSet + "-" + webapp).value;
    const url = getRequestLoggerConfigApiUrl(webapp) + "/" + propertyToSet + "?" + urlParamName + "=" + propertyValue;
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