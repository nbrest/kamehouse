/** 
 * Functionality to manipulate log levels in the backend. 
 *
 * Manage the log level of the backend on the current server.
 * 
 * @author nbrest
 */
class BackendLogLevelUtils {

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Started initializing log-level");
    kameHouse.util.banner.setRandomAllBanner();
    kameHouse.util.module.waitForModules(["webappTabsManager"], () => {
      kameHouse.plugin.webappTabsManager.cookiePrefix('kh-admin-log-level');
      kameHouse.plugin.webappTabsManager.loadStateFromCookies();
    });
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger", "webappTabsManager"], () => {
      this.#init();
    });
  }

  /** 
   * Get all current log levels 
   */
  getLogLevels(webapp, openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, this.#getApiUrl(webapp), null, null,
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processSuccess(responseBody, responseCode, responseDescription, responseHeaders, webapp); },
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processError(responseBody, responseCode, responseDescription, responseHeaders, webapp); });
  }

  /** Reset all log levels */
  resetLogLevels(webapp) {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, this.#getApiUrl(webapp), null, null,
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processSuccess(responseBody, responseCode, responseDescription, responseHeaders, webapp); },
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processError(responseBody, responseCode, responseDescription, responseHeaders, webapp); });
  }

  /** Set Kamehouse log level */
  setKamehouseLogLevel(webapp) {
    const logLevel = document.getElementById("select-kamehouse-log-level-" + webapp).value;
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.put(config, this.#getApiUrl(webapp) + logLevel, null, null, 
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processSuccess(responseBody, responseCode, responseDescription, responseHeaders, webapp); },
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processError(responseBody, responseCode, responseDescription, responseHeaders, webapp); });
  }

  /** Set Kamehouse log levels to DEBUG */
  setKamehouseLogLevelToDebug(webapp) {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.put(config, this.#getApiUrl(webapp) + "/debug", null, null, 
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processSuccess(responseBody, responseCode, responseDescription, responseHeaders, webapp); },
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processError(responseBody, responseCode, responseDescription, responseHeaders, webapp); });
  }

  /** Set Kamehouse log levels to TRACE */
  setKamehouseLogLevelToTrace(webapp) {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.put(config, this.#getApiUrl(webapp) + "/trace", null, null, 
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processSuccess(responseBody, responseCode, responseDescription, responseHeaders, webapp); },
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processError(responseBody, responseCode, responseDescription, responseHeaders, webapp); });
  }

  /** Set request logger config payload */
  setRequestLoggerConfigPayload(webapp) {
    this.#setRequestLoggerConfig(webapp, "payload", "logPayload");
  }

  /** Set request logger config headers */
  setRequestLoggerConfigHeaders(webapp) {
    this.#setRequestLoggerConfig(webapp, "headers", "logHeaders");
  }

  /** Set request logger config query string */
  setRequestLoggerConfigQueryString(webapp) {
    this.#setRequestLoggerConfig(webapp, "query-string", "logQueryString");
  }

  /** Set request logger config client info */
  setRequestLoggerConfigClientInfo(webapp) {
    this.#setRequestLoggerConfig(webapp, "client-info", "logClientInfo");
  }

  /**
   * Load templates and initial data.
   */
  #init() {
    this.getLogLevels('admin', false);
    this.getLogLevels('media', false);
    this.getLogLevels('tennisworld', false);
    this.getLogLevels('testmodule', false);
    this.getLogLevels('ui', false);
    this.getLogLevels('vlcrc', false);
  }

  /**
   * Get log-level api url for each webapp.
   */
  #getApiUrl(webapp) {
    if (webapp == "ui") {
      return '/kame-house/api/v1/commons/log-level';
    } else {
      return '/kame-house-' + webapp + '/api/v1/commons/log-level';
    }
  }

  /**
   * Get log-level request logger config api url for each webapp.
   */
  #getRequestLoggerConfigApiUrl(webapp) {
    return this.#getApiUrl(webapp) + "/request-logger";
  }

  /** Update the log levels table content */
  #updateLogLevelTable(logLevelsArray, webapp) {
    this.#addLogLevelTableHeader(webapp);
    const tableBody = document.getElementById('log-level-tbody-' + webapp);
    logLevelsArray.forEach((logLevelEntry) => {
      const logLevelEntryPair = logLevelEntry.split(":");
      const packageName = logLevelEntryPair[0];
      const logLevel = logLevelEntryPair[1];
      kameHouse.util.dom.append(tableBody, this.#getLogLevelTr(packageName, logLevel));
    });
  }

  /** Add log level table header */
  #addLogLevelTableHeader(webapp) {
    const tableBody = document.getElementById('log-level-tbody-' + webapp);
    kameHouse.util.dom.empty(tableBody);
    kameHouse.util.dom.append(tableBody, this.#getLogLevelTh(webapp));
  }

  /** Set log level table to error */
  #updateLogLevelTableError(webapp) {
    const tableBody = document.getElementById('log-level-tbody-' + webapp);
    kameHouse.util.dom.empty(tableBody);
    kameHouse.util.dom.append(tableBody, this.#getErrorTr());
  }

  /** Get row for errot table */
  #getErrorTr() {
    return kameHouse.util.dom.getTrTd("Error retrieving log levels from the backend");
  }

  /** Get data row for log level table */
  #getLogLevelTr(packageName, logLevel) {
    const tr = kameHouse.util.dom.getTr(null, null);
    kameHouse.util.dom.append(tr, kameHouse.util.dom.getTd(null, packageName));
    kameHouse.util.dom.append(tr, kameHouse.util.dom.getTd(null, logLevel));
    return tr;
  }

  /** Get header row for log level table */
  #getLogLevelTh(webapp) {
    const tr = kameHouse.util.dom.getTr({
      id: "log-level-thead-" + webapp,
      class: "table-kh-header"
    }, null);
    kameHouse.util.dom.append(tr, kameHouse.util.dom.getTd(null, "Package Name"));
    kameHouse.util.dom.append(tr, kameHouse.util.dom.getTd(null, "Log Level"));
    return tr;
  }

  /** Process success response */
  #processSuccess(responseBody, responseCode, responseDescription, responseHeaders, webapp) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    this.#updateLogLevelTable(responseBody, webapp);
  }

  /** Process error response */
  #processError(responseBody, responseCode, responseDescription, responseHeaders, webapp) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    this.#updateLogLevelTableError(webapp);
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
  }

  /** Set request logger config */
  #setRequestLoggerConfig(webapp, propertyToSet, urlParamName) {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const propertyValue = document.getElementById("select-kh-req-logger-cfg-" + propertyToSet + "-" + webapp).value;
    const url = this.#getRequestLoggerConfigApiUrl(webapp) + "/" + propertyToSet;
    const params = {};
    params[urlParamName] = propertyValue;
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.put(config, url,kameHouse.http.getUrlEncodedHeaders(), params, 
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processSuccessRequestLoggerConfig(responseBody, responseCode, responseDescription, responseHeaders, webapp); },
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processErrorRequestLoggerConfig(responseBody, responseCode, responseDescription, responseHeaders); });
  }

  /** Process success response for request logger config */
  #processSuccessRequestLoggerConfig(responseBody, responseCode, responseDescription, responseHeaders, webapp) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openAutoCloseable(webapp + " : " + responseBody.message, 7000);
  }

  /** Process error response for request logger config */
  #processErrorRequestLoggerConfig(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("backendLogLevelUtils", new BackendLogLevelUtils());
});