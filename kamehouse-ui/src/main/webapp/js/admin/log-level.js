/** 
 * Functionality to manipulate log levels in the backend. 
 */
var backendLogLevelUtils;

window.onload = () => {
  backendLogLevelUtils = new BackendLogLevelUtils();
  moduleUtils.waitForModules(["logger", "apiCallTable", "kameHouseWebappTabsManager"], () => {
    logger.info("Started initializing log-level");
    kameHouseWebappTabsManager.openTab('tab-admin');
    backendLogLevelUtils.getLogLevels('admin');
    backendLogLevelUtils.getLogLevels('media');
    backendLogLevelUtils.getLogLevels('tennisworld');
    backendLogLevelUtils.getLogLevels('testmodule');
    backendLogLevelUtils.getLogLevels('ui');
    backendLogLevelUtils.getLogLevels('vlcrc');
  });
  bannerUtils.setRandomAllBanner();
};

function BackendLogLevelUtils() {
  let self = this;

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

  /** Get all current log levels */
  this.getLogLevels = (webapp) => {
    loadingWheelModal.open();
    apiCallTable.get(self.getApiUrl(webapp), processSuccess, processError, webapp);
  }

  /** Reset all log levels */
  this.resetLogLevels = (webapp) => {
    loadingWheelModal.open();
    apiCallTable.delete(self.getApiUrl(webapp), null, processSuccess, processError, webapp);
  }

  /** Set Kamehouse log level */
  this.setKamehouseLogLevel = (webapp) => {
    let logLevel = document.getElementById("select-kamehouse-log-level-" + webapp).value;
    loadingWheelModal.open();
    apiCallTable.put(self.getApiUrl(webapp) + logLevel, null, processSuccess, processError, webapp);
  }

  /** Set Kamehouse log levels to DEBUG */
  this.setKamehouseLogLevelToDebug = (webapp) => {
    loadingWheelModal.open();
    apiCallTable.put(self.getApiUrl(webapp) + "/debug", null, processSuccess, processError, webapp);
  }

  /** Set Kamehouse log levels to TRACE */
  this.setKamehouseLogLevelToTrace = (webapp) => {
    loadingWheelModal.open();
    apiCallTable.put(self.getApiUrl(webapp) + "/trace", null, processSuccess, processError, webapp);
  }

  /** Update the log levels table content */
  this.updateLogLevelTable = (logLevelsArray, webapp) => {
    let $tableBody = $('#log-level-tbody-' + webapp);
    self.addLogLevelTableHeader(webapp);
    logLevelsArray.forEach((logLevelEntry) => {
      let logLevelEntryPair = logLevelEntry.split(":");
      let packageName = logLevelEntryPair[0];
      let logLevel = logLevelEntryPair[1];
      let tableRow = $('<tr>');
      tableRow.append($('<td>').text(packageName));
      tableRow.append($('<td>').text(logLevel));
      $tableBody.append(tableRow);
    });
  }

  /** Add log level table header */
  this.addLogLevelTableHeader = (webapp) => {
    let $tableBody = $('#log-level-tbody-' + webapp);
    $tableBody.empty();
    let tableRow = $('<tr id="log-level-thead-' + webapp + '" class="log-level-thead">');
    tableRow.append($('<td>').text("Package Name"));
    tableRow.append($('<td>').text("Log Level"));
    $tableBody.append(tableRow);
  }
  
  /** Set log level table to error */
  this.updateLogLevelTableError = (webapp) => {
    let $tableBody = $('#log-level-tbody-' + webapp);
    $tableBody.empty();
    let tableRow = $('<tr>');
    tableRow.append($('<td>').text("Error retrieving log levels from the backend"));
    $tableBody.append(tableRow);
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
}
