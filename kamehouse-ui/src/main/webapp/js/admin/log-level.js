/** 
 * Functionality to manipulate log levels in the backend. 
 */
/**
 * Manage the log level of the backend on the current server.
 */
function BackendLogLevelUtils() {

  this.load = load;
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
   * Load the extension.
   */
  function load() {
    kameHouse.logger.info("Started initializing log-level");
    kameHouse.util.banner.setRandomAllBanner();
    kameHouse.util.module.waitForModules(["webappTabsManager"], () => {
      kameHouse.plugin.webappTabsManager.setCookiePrefix('kh-admin-log-level');
      kameHouse.plugin.webappTabsManager.loadStateFromCookies();
    });
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger", "webappTabsManager"], () => {
      init();
    });
  }

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

  /** 
   * Get all current log levels 
   */
  function getLogLevels(webapp, openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, getApiUrl(webapp), null, null,
    (responseBody, responseCode, responseDescription, responseHeaders) => { processSuccess(responseBody, responseCode, responseDescription, responseHeaders, webapp); },
    (responseBody, responseCode, responseDescription, responseHeaders) => { processError(responseBody, responseCode, responseDescription, responseHeaders, webapp); });
  }

  /** Reset all log levels */
  function resetLogLevels(webapp) {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, getApiUrl(webapp), null, null,
    (responseBody, responseCode, responseDescription, responseHeaders) => { processSuccess(responseBody, responseCode, responseDescription, responseHeaders, webapp); },
    (responseBody, responseCode, responseDescription, responseHeaders) => { processError(responseBody, responseCode, responseDescription, responseHeaders, webapp); });
  }

  /** Set Kamehouse log level */
  function setKamehouseLogLevel(webapp) {
    const logLevel = document.getElementById("select-kamehouse-log-level-" + webapp).value;
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.put(config, getApiUrl(webapp) + logLevel, null, null, 
    (responseBody, responseCode, responseDescription, responseHeaders) => { processSuccess(responseBody, responseCode, responseDescription, responseHeaders, webapp); },
    (responseBody, responseCode, responseDescription, responseHeaders) => { processError(responseBody, responseCode, responseDescription, responseHeaders, webapp); });
  }

  /** Set Kamehouse log levels to DEBUG */
  function setKamehouseLogLevelToDebug(webapp) {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.put(config, getApiUrl(webapp) + "/debug", null, null, 
    (responseBody, responseCode, responseDescription, responseHeaders) => { processSuccess(responseBody, responseCode, responseDescription, responseHeaders, webapp); },
    (responseBody, responseCode, responseDescription, responseHeaders) => { processError(responseBody, responseCode, responseDescription, responseHeaders, webapp); });
  }

  /** Set Kamehouse log levels to TRACE */
  function setKamehouseLogLevelToTrace(webapp) {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.put(config, getApiUrl(webapp) + "/trace", null, null, 
    (responseBody, responseCode, responseDescription, responseHeaders) => { processSuccess(responseBody, responseCode, responseDescription, responseHeaders, webapp); },
    (responseBody, responseCode, responseDescription, responseHeaders) => { processError(responseBody, responseCode, responseDescription, responseHeaders, webapp); });
  }

  /** Update the log levels table content */
  function updateLogLevelTable(logLevelsArray, webapp) {
    addLogLevelTableHeader(webapp);
    const $tableBody = $('#log-level-tbody-' + webapp);
    logLevelsArray.forEach((logLevelEntry) => {
      const logLevelEntryPair = logLevelEntry.split(":");
      const packageName = logLevelEntryPair[0];
      const logLevel = logLevelEntryPair[1];
      kameHouse.util.dom.append($tableBody, getLogLevelTr(packageName, logLevel));
    });
  }

  /** Add log level table header */
  function addLogLevelTableHeader(webapp) {
    const $tableBody = $('#log-level-tbody-' + webapp);
    kameHouse.util.dom.empty($tableBody);
    kameHouse.util.dom.append($tableBody, getLogLevelTh(webapp));
  }

  /** Set log level table to error */
  function updateLogLevelTableError(webapp) {
    const $tableBody = $('#log-level-tbody-' + webapp);
    kameHouse.util.dom.empty($tableBody);
    kameHouse.util.dom.append($tableBody, getErrorTr());
  }

  /** Get row for errot table */
  function getErrorTr() {
    return kameHouse.util.dom.getTrTd("Error retrieving log levels from the backend");
  }

  /** Get data row for log level table */
  function getLogLevelTr(packageName, logLevel) {
    const tr = kameHouse.util.dom.getTr(null, null);
    kameHouse.util.dom.append(tr, kameHouse.util.dom.getTd(null, packageName));
    kameHouse.util.dom.append(tr, kameHouse.util.dom.getTd(null, logLevel));
    return tr;
  }

  /** Get header row for log level table */
  function getLogLevelTh(webapp) {
    const tr = kameHouse.util.dom.getTr({
      id: "log-level-thead-" + webapp,
      class: "table-kh-header"
    }, null);
    kameHouse.util.dom.append(tr, kameHouse.util.dom.getTd(null, "Package Name"));
    kameHouse.util.dom.append(tr, kameHouse.util.dom.getTd(null, "Log Level"));
    return tr;
  }

  /** Process success response */
  function processSuccess(responseBody, responseCode, responseDescription, responseHeaders, webapp) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    updateLogLevelTable(responseBody, webapp);
  }

  /** Process error response */
  function processError(responseBody, responseCode, responseDescription, responseHeaders, webapp) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    updateLogLevelTableError(webapp);
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
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
    kameHouse.plugin.modal.loadingWheelModal.open();
    const propertyValue = document.getElementById("select-kh-req-logger-cfg-" + propertyToSet + "-" + webapp).value;
    const url = getRequestLoggerConfigApiUrl(webapp) + "/" + propertyToSet;
    const params = {};
    params[urlParamName] = propertyValue;
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.put(config, url,kameHouse.http.getUrlEncodedHeaders(), params, 
    (responseBody, responseCode, responseDescription, responseHeaders) => { processSuccessRequestLoggerConfig(responseBody, responseCode, responseDescription, responseHeaders, webapp); },
    (responseBody, responseCode, responseDescription, responseHeaders) => { processErrorRequestLoggerConfig(responseBody, responseCode, responseDescription, responseHeaders); });
  }

  /** Process success response for request logger config */
  function processSuccessRequestLoggerConfig(responseBody, responseCode, responseDescription, responseHeaders, webapp) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openAutoCloseable(webapp + " : " + responseBody.message, 7000);
  }

  /** Process error response for request logger config */
  function processErrorRequestLoggerConfig(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
  }
}

$(document).ready(() => {
  kameHouse.addExtension("backendLogLevelUtils", new BackendLogLevelUtils());
});