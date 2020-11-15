/** 
 * Functionality to manipulate log levels in the backend. 
 */
var backendLogLevelUtils;
var kamehouseDebugger;

window.onload = () => {
  backendLogLevelUtils = new BackendLogLevelUtils();
  kamehouseDebugger = new KamehouseDebugger();
  bannerUtils.setRandomAllBanner();
};

function BackendLogLevelUtils() {
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

  /** Set Kamehouse log levels to TRACE */
  this.setKamehouseLogLevelToTrace = () => {
    loadingWheelModal.open();
    apiCallTable.post(LOG_LEVEL_API_URL + "/trace", null, processSuccess, processError);
  }

  /** Process success response */
  function processSuccess(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
  }

  /** Process error response */
  function processError(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
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
