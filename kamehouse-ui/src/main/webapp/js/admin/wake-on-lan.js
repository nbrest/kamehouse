/**
 * wake on lan functions.
 * 
 * @author nbrest
 */
/**
 * Manager to execute a wake on lan command to the media server.
 */
function WakeOnLanManager() {

  this.load = load;
  this.execWakeOnLan = execWakeOnLan;
  const WOL_API_URL = "/kame-house-admin/api/v1/admin/power-management/wol";

  function load() {
    kameHouse.logger.info("Started initializing wake on lan");
    kameHouse.util.banner.setRandomAllBanner();
    importWolCss();
    kameHouse.util.module.waitForModules(["kameHouseDebugger"], () => {
      execWakeOnLan();
    });
  }

  function importWolCss() {
    kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/wake-on-lan.css">');
  }

  /**
   * WakeOnLan functions
   */
  function execWakeOnLan() {
    const requestParam =  {
      "server" : "media.server"
    };
    kameHouse.plugin.modal.loadingWheelModal.open("Sending WOL packet to media server");
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, WOL_API_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, processSuccess, processError);
  }

  function processSuccess(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.util.dom.setHtml($("#wol-status"), kameHouse.util.time.getTimestamp() + " - " + responseBody.message);
  }

  function processError(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    kameHouse.util.dom.setHtml($("#wol-status"), kameHouse.util.time.getTimestamp() + " - Error sending WOL packet. Please try again");
  }
}

$(document).ready(() => {
  kameHouse.addExtension("wakeOnLanManager", new WakeOnLanManager());
});
