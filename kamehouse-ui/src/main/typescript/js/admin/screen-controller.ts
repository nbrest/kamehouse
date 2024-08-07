/**
 * Admin Screen Controller functionality.
 * Manager to execute the admin commands in the current server.
 * 
 * @author nbrest
 */
class ScreenController {

  #ADMIN_API_URL = "/kame-house-admin/api/v1/admin";
  #KEY_PRESS = '/screen/key-press';
  #MOUSE_CLICK = '/screen/mouse-click';

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Started initializing screen controller", null);
    kameHouse.util.banner.setRandomAllBanner(null);
    this.#importCss();
  }

  /**
   * Send a key press to the server.
   */
  keyPress(key, keyPresses) {
    if (kameHouse.core.isEmpty(keyPresses)) {
      kameHouse.logger.trace("keyPresses not set. Using default value of 1", null);
      keyPresses = 1;
    }
    const requestParam = {
      "key" : key,
      "keyPresses" : keyPresses
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, this.#ADMIN_API_URL + this.#KEY_PRESS, kameHouse.http.getUrlEncodedHeaders(), requestParam, 
    (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccess(responseBody, responseCode, responseDescription, responseHeaders)}, 
    (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processError(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /**
   * Send a key press with the selected key presses in the specified dropdown.
   */
  keyPressWithDropdown(key, dropdownId) {
    const keyPresses = (document.getElementById(dropdownId) as HTMLSelectElement).value;
    kameHouse.logger.trace("Number of key presses: " + keyPresses, null);
    this.keyPress(key, keyPresses);
  }

  /**
   * Send a right mouse click to the server.
   */
  mouseRightClick() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const params = {
      mouseButton: "RIGHT",
      positionX: 500,
      positionY: 500,
      clickCount: 1
    };
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, this.#ADMIN_API_URL + this.#MOUSE_CLICK, kameHouse.http.getUrlEncodedHeaders(), params, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccess(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processError(responseBody, responseCode, responseDescription, responseHeaders)}
      );
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
    kameHouse.plugin.debugger.http.post(config, this.#ADMIN_API_URL + url, headers, requestBody, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccess(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processError(responseBody, responseCode, responseDescription, responseHeaders)}
      );
  }

  /**
   * Import css.
   */
  #importCss() {
    kameHouse.util.dom.append(kameHouse.util.dom.getHead(), '<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/screen-controller.css">');
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

kameHouse.ready(() => {
  kameHouse.addExtension("screenController", new ScreenController());
});
