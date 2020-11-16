/** 
 * Functionality to manipulate log levels in the backend. 
 */
var backendLogLevelUtils;
var kamehouseDebugger;

window.onload = () => {
  backendLogLevelUtils = new BackendLogLevelUtils();
  moduleUtils.waitForModules(["logger", "httpClient"], () => {
    logger.info("Started initializing log-level");
    backendLogLevelUtils.getLogLevels();
  });
  kamehouseDebugger = new KamehouseDebugger();
  bannerUtils.setRandomAllBanner();
};

function BackendLogLevelUtils() {
  let self = this;
  const LOG_LEVEL_API_URL = "/kame-house/api/v1/admin/log-level";

  /** Get all current log levels */
  this.getLogLevels = () => {
    loadingWheelModal.open();
    apiCallTable.get(LOG_LEVEL_API_URL, processSuccess, processError);
  }

  /** Reset all log levels */
  this.resetLogLevels = () => {
    loadingWheelModal.open();
    apiCallTable.delete(LOG_LEVEL_API_URL, null, processSuccess, processError);
  }

  /** Set Kamehouse log levels to DEBUG */
  this.setKamehouseLogLevelToDebug = () => {
    loadingWheelModal.open();
    apiCallTable.put(LOG_LEVEL_API_URL + "/debug", null, processSuccess, processError);
  }

  /** Set Kamehouse log levels to TRACE */
  this.setKamehouseLogLevelToTrace = () => {
    loadingWheelModal.open();
    apiCallTable.put(LOG_LEVEL_API_URL + "/trace", null, processSuccess, processError);
  }

  /** Update the log levels table content */
  this.updateLogLevelTable = (logLevelsArray) => {
    let $tableBody = $('#log-level-tbody');
    self.addLogLevelTableHeader();
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
  this.addLogLevelTableHeader = () => {
    let $tableBody = $('#log-level-tbody');
    $tableBody.empty();
    let tableRow = $('<tr id="log-level-thead">');
    tableRow.append($('<td>').text("Package Name"));
    tableRow.append($('<td>').text("Log Level"));
    $tableBody.append(tableRow);
  }
  
  /** Set log level table to error */
  this.updateLogLevelTableError = () => {
    let $tableBody = $('#log-level-tbody');
    $tableBody.empty();
    let tableRow = $('<tr>');
    tableRow.append($('<td>').text("Error retrieving log levels from the backend"));
    $tableBody.append(tableRow);
  }

  /** Process success response */
  function processSuccess(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    self.updateLogLevelTable(responseBody);
  }

  /** Process error response */
  function processError(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    self.updateLogLevelTableError();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
  }
}

/** 
 * Handles the debugger functionality.
 * 
 * @author nbrest
 */
function KamehouseDebugger() {

  /** Toggle debug mode. */
  this.toggleDebugMode = () => {
    logger.debug("Toggled debug mode")
    let debugModeDiv = document.getElementById("debug-mode");
    debugModeDiv.classList.toggle("hidden-kh");
  }
}
