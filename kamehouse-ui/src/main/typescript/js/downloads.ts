/**
 * Downloads manager. 
 * 
 * @author nbrest
 */
class DownloadsManager {

  /**
   * Load the downloads manager extension.
   */
  load() {
    kameHouse.util.banner.setRandomAllBanner(null);
    kameHouse.util.module.waitForModules(["slideshow"], () => {
      kameHouse.plugin.slideshow.setDotSlide(1);
    });
    this.#setApkStatusMessageListener();
  }

  /**
   * Set a message listener to check for apk status iframe being loaded.
   */
  #setApkStatusMessageListener() {
    kameHouse.logger.info("Setting message listener for apk status iframe", null);
    window.addEventListener(
      "message",
      (event) => {
        if (event.origin == "https://kame.nicobrest.com") {
          const data = event.data;
          kameHouse.logger.trace("message event data: " + kameHouse.json.stringify(data, null, 2), null);
          if (data == "apk-status-loaded") {
            kameHouse.logger.info("APK Status iframe loaded", null);
            const apkStatusIframe = document.getElementById("apk-status-iframe") as HTMLIFrameElement;
            kameHouse.util.dom.classListRemove(apkStatusIframe, "hidden-kh");
            const apkStatusNotAvailableDiv = document.getElementById("apk-status-not-available");
            kameHouse.util.dom.classListAdd(apkStatusNotAvailableDiv, "hidden-kh");
          }
        } else {
          kameHouse.logger.trace("Ignoring message from unexpected origin", null);
        }
      },
      false,
    );
  }
}

kameHouse.ready(() => {  
  kameHouse.addExtension("downloadsManager", new DownloadsManager());
});