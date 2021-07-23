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
    if (webapp == "ui") {
      return '/kame-house/api/v1/commons/log-level/request-logger';
    } else {
      return '/kame-house-' + webapp + '/api/v1/commons/log-level/request-logger';
    }
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
      $tableBody.append(self.getLogLevelTableRow(packageName, logLevel));
    });
  }

  /** Add log level table header */
  this.addLogLevelTableHeader = (webapp) => {
    let $tableBody = $('#log-level-tbody-' + webapp);
    $tableBody.empty();
    $tableBody.append(self.getLogLevelTableHeader(webapp));
  }
  
  /** Set log level table to error */
  this.updateLogLevelTableError = (webapp) => {
    let $tableBody = $('#log-level-tbody-' + webapp);
    $tableBody.empty();
    $tableBody.append(self.getErrorTableRow());
  }

  /** Set request logger config payload */
  this.setRequestLoggerConfigPayload = (webapp) => {
    loadingWheelModal.open();
    let logPayload = document.getElementById("select-kh-req-logger-cfg-payload-" + webapp).value;
    let url = self.getRequestLoggerConfigApiUrl(webapp) + "/payload?logPayload=" + logPayload;
    debuggerHttpClient.put(url, null, processSuccessRequestLoggerConfig, processErrorRequestLoggerConfig, webapp);
  }

  /** Set request logger config headers */
  this.setRequestLoggerConfigHeaders = (webapp) => {
    loadingWheelModal.open();
    let logHeaders = document.getElementById("select-kh-req-logger-cfg-headers-" + webapp).value;
    let url = self.getRequestLoggerConfigApiUrl(webapp) + "/headers?logHeaders=" + logHeaders;
    debuggerHttpClient.put(url, null, processSuccessRequestLoggerConfig, processErrorRequestLoggerConfig, webapp);
  }

  /** Set request logger config query string */
  this.setRequestLoggerConfigQueryString = (webapp) => {
    loadingWheelModal.open();
    let logQueryString = document.getElementById("select-kh-req-logger-cfg-query-string-" + webapp).value;
    let url = self.getRequestLoggerConfigApiUrl(webapp) + "/query-string?logQueryString=" + logQueryString;
    debuggerHttpClient.put(url, null, processSuccessRequestLoggerConfig, processErrorRequestLoggerConfig, webapp);
  }

  /** Set request logger config client info */
  this.setRequestLoggerConfigClientInfo = (webapp) => {
    loadingWheelModal.open();
    let logClientInfo = document.getElementById("select-kh-req-logger-cfg-client-info-" + webapp).value;
    let url = self.getRequestLoggerConfigApiUrl(webapp) + "/client-info?logClientInfo=" + logClientInfo;
    debuggerHttpClient.put(url, null, processSuccessRequestLoggerConfig, processErrorRequestLoggerConfig, webapp);
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

  /** Process success response for request logger config */
  function processSuccessRequestLoggerConfig(responseBody, responseCode, responseDescription, webapp) {
    loadingWheelModal.close();
    basicKamehouseModal.openAutoCloseable(responseBody.message, 7000);
  }  

  /** Process error response for request logger config */
  function processErrorRequestLoggerConfig(responseBody, responseCode, responseDescription, webapp) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
  }

  /** Dynamic DOM element generation ------------------------------------------ */
  this.getErrorTableRow = () => {
    let tableRow = $('<tr>');
    let tableRowData = $('<td>');
    tableRowData.text("Error retrieving log levels from the backend");
    tableRow.append(tableRowData);
    return tableRow;
  }

  this.getLogLevelTableRow = (packageName, logLevel) => {
    let tableRow = $('<tr>');

    let packageNameTableRowData = $('<td>')
    packageNameTableRowData.text(packageName)
    tableRow.append(packageNameTableRowData);

    let logLevelTableRowData = $('<td>')
    logLevelTableRowData.text(logLevel)
    tableRow.append(logLevelTableRowData);

    return tableRow;
  }

  this.getLogLevelTableHeader = (webapp) => {
    let tableRow = $('<tr>');
    tableRow.attr("id", "log-level-thead-" + webapp);
    tableRow.attr("class", "log-level-thead");

    let packageNameTableRowData = $('<td>')
    packageNameTableRowData.text("Package Name")
    tableRow.append(packageNameTableRowData);

    let logLevelTableRowData = $('<td>')
    logLevelTableRowData.text("Log Level")
    tableRow.append(logLevelTableRowData);

    return tableRow;
  }
}
