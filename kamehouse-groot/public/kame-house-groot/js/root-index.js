/**
 * Groot Home manager.
 * 
 * @author nbrest
 */
class GrootHome {

  /**
   * Load the extension.
   */
  load() {
    kameHouse.util.banner.setRandomAllBanner();
  }

  /** @deprecated Set client time and date */
  #setClientTimeAndDate() {
    const clientDate = new Date();
    const clientMonth = clientDate.getMonth() + 1;
    const clientTimeAndDate = "  Client: " + clientDate.getDate() + "/" + clientMonth + "/" + clientDate.getFullYear() + " - " + clientDate.getHours() + ":" + clientDate.getMinutes() + ":" + clientDate.getSeconds();
    kameHouse.util.dom.setHtml(document.getElementById("client-time-and-date"), clientTimeAndDate);
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("grootHome", new GrootHome());
});