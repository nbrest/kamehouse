/**
 * Kamehouse Mobile Apk Status page. 
 * 
 * @author nbrest
 */
class ApkStatus {

  #isApkStatusRendered = false;

  /**
   * Load the downloads manager extension.
   */
  load() {
    this.#setApkStatusMessageListener();
    this.#sendApkStatusLoadedMessageToParent();
  }

  /**
   * Send apk-status-loaded message to parent window.
   */
  #sendApkStatusLoadedMessageToParent() {
    if (this.#isApkStatusRendered) {
      kameHouse.logger.info("Apk status is already rendered in the parent", null);
      return;
    }
    kameHouse.logger.info("Sending apk-status-loaded message to parent", null);
    window.parent.postMessage("apk-status-loaded","*");

    setTimeout(() => {this.#sendApkStatusLoadedMessageToParent()}, 3000);
  }

  /**
   * Set a message listener to check for apk status iframe was rendered in the parent.
   */
  #setApkStatusMessageListener() {
    kameHouse.logger.info("Setting message listener for apk status being rendered in the parent", null);
    window.addEventListener(
      "message",
      (event) => {
        const data = event.data;
        kameHouse.logger.trace("message event data: " + kameHouse.json.stringify(data, null, 2), null);
        if (data != "apk-status-loaded-ack") {
          return;
        }
        kameHouse.logger.info("APK status iframe rendered in parent", null);
        this.#isApkStatusRendered = true;
      },
      false,
    );
  }
}

kameHouse.ready(() => {  
  kameHouse.addExtension("apkStatus", new ApkStatus());
});