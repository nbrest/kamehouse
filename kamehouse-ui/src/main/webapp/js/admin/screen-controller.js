/**
 * Admin Screen Controller functionality.
 * Manager to execute the admin commands in the current server.
 * 
 * @author nbrest
 */
class ScreenController {

  static #ADMIN_API_URL = "/kame-house-admin/api/v1/admin";
  static #ALT_TAB_URL = '/screen/alt-tab-key-press';

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Started initializing screen controller");
    kameHouse.util.banner.setRandomAllBanner();
    this.#importCss();
  }
  
  /** Send ALT+TAB key press */
  altTabCommand() {
    const numberOfTabs = document.getElementById("number-of-tabs-dropdown").value;
    kameHouse.logger.trace("Number of tab presses: " + numberOfTabs);
    const requestParam = {
      "tabs" : numberOfTabs
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, ScreenController.#ADMIN_API_URL + ScreenController.#ALT_TAB_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, 
    (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccess(responseBody, responseCode, responseDescription, responseHeaders)}, 
    (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processError(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /**
   * Execute post http request.
   */
  post(url, requestBody) {
    kameHouse.plugin.modal.loadingWheelModal.open();
    let headers = null;
    if (!kameHouse.core.isEmpty(requestBody)) {
      headers = kameHouse.http.getApplicationJsonHeaders();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, ScreenController.#ADMIN_API_URL + url, headers, requestBody, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccess(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processError(responseBody, responseCode, responseDescription, responseHeaders)}
      );
  }

  /**
   * Import css.
   */
  #importCss() {
    kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/screen-controller.css">');
  }

  /** Generic process success response */
  #processSuccess(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
  }

  /** Generic process error response */
  #processError(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
  }
}

$(document).ready(() => {
  kameHouse.addExtension("screenController", new ScreenController());
});
