/**
 * Admin Screen Controller functionality.
 * Manager to execute the admin commands in the current server.
 * 
 * @author nbrest
 */
class ScreenController {

  static #ADMIN_API_URL = "/kame-house-admin/api/v1/admin";
  static #KEY_PRESS = '/screen/key-press';

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Started initializing screen controller");
    kameHouse.util.banner.setRandomAllBanner();
    this.#importCss();
  }

  keyPress(key, keyPresses) {
    if (kameHouse.core.isEmpty(keyPresses)) {
      kameHouse.logger.trace("keyPresses not set. Using default value of 1");
      keyPresses = 1;
    }
    const requestParam = {
      "key" : key,
      "keyPresses" : keyPresses
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, ScreenController.#ADMIN_API_URL + ScreenController.#KEY_PRESS, kameHouse.http.getUrlEncodedHeaders(), requestParam, 
    (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccess(responseBody, responseCode, responseDescription, responseHeaders)}, 
    (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processError(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** 
   * Send a up key press 
   */
  upKeyCommand() {
    this.#keyPressWithSelectedKeyPresses("ARROW_UP", "number-of-up-key-dropdown");
  }    

  /** 
   * Send a down key press
   */
  downKeyCommand() {
    this.#keyPressWithSelectedKeyPresses("ARROW_DOWN", "number-of-down-key-dropdown");
  } 
  
  /** 
   * Send a left key press
   */
  leftKeyCommand() {
    this.#keyPressWithSelectedKeyPresses("ARROW_LEFT", "number-of-left-key-dropdown");
  }    

  /** 
   * Send a right key press 
   */
  rightKeyCommand() {
    this.#keyPressWithSelectedKeyPresses("ARROW_RIGHT", "number-of-right-key-dropdown");
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
   * Send a key press with the selected key presses.
   */
  #keyPressWithSelectedKeyPresses(key, dropdownId) {
    const keyPresses = document.getElementById(dropdownId).value;
    kameHouse.logger.trace("Number of key presses: " + keyPresses);
    this.keyPress(key, keyPresses);
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
