/**
 * wake on lan functions.
 * 
 * Dependencies: logger, debuggerHttpClient.
 * 
 * @author nbrest
 */
var wakeOnLanManager;

function mainWakeOnLan() {
  bannerUtils.setRandomAllBanner();
  importWolCss();
  moduleUtils.waitForModules(["debuggerHttpClient"], () => {
    logger.info("Started initializing wake on lan");
    wakeOnLanManager = new WakeOnLanManager();
    wakeOnLanManager.execWakeOnLan();
  });
}

function importWolCss() {
  domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/wake-on-lan.css">');
}

/**
 * Manager to execute a wake on lan command to the media server.
 */
function WakeOnLanManager() {

  this.execWakeOnLan = execWakeOnLan;
  const WOL_API_URL = "/kame-house-admin/api/v1/admin/power-management/wol";

  /**
   * WakeOnLan functions
   */
  function execWakeOnLan() {
    const requestParam = "server=media.server";
    loadingWheelModal.open("Sending WOL packet to media server");
    debuggerHttpClient.postUrlEncoded(WOL_API_URL, requestParam, processSuccess, processError);
  }

  function processSuccess(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    domUtils.setHtml($("#wol-status"), timeUtils.getTimestamp() + " - " + responseBody.message);
  }

  function processError(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
    domUtils.setHtml($("#wol-status"), timeUtils.getTimestamp() + " - Error sending WOL packet. Please try again");
  }
}

/**
 * Call main.
 */
$(document).ready(mainWakeOnLan);
