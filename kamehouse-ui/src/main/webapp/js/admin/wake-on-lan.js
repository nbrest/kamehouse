/**
 * wake on lan functions.
 * 
 * Dependencies: logger, kameHouse.plugin.debugger.http.
 * 
 * @author nbrest
 */
var wakeOnLanManager;

function mainWakeOnLan() {
  kameHouse.util.banner.setRandomAllBanner();
  importWolCss();
  kameHouse.util.module.waitForModules(["kameHouseDebugger"], () => {
    kameHouse.logger.info("Started initializing wake on lan");
    wakeOnLanManager = new WakeOnLanManager();
    wakeOnLanManager.execWakeOnLan();
  });
}

function importWolCss() {
  kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/wake-on-lan.css">');
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
    const requestParam =  {
      "server" : "media.server"
    };
    kameHouse.plugin.modal.loadingWheelModal.open("Sending WOL packet to media server");
    kameHouse.plugin.debugger.http.postUrlEncoded(WOL_API_URL, requestParam, processSuccess, processError);
  }

  function processSuccess(responseBody, responseCode, responseDescription) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.util.dom.setHtml($("#wol-status"), kameHouse.util.time.getTimestamp() + " - " + responseBody.message);
  }

  function processError(responseBody, responseCode, responseDescription) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription);
    kameHouse.util.dom.setHtml($("#wol-status"), kameHouse.util.time.getTimestamp() + " - Error sending WOL packet. Please try again");
  }
}

/**
 * Call main.
 */
$(document).ready(mainWakeOnLan);
