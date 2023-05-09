
function GrootHome() {

  this.load = load;

  function load() {
    kameHouse.util.banner.setRandomTennisBanner();
    kameHouse.util.module.waitForModules(["kameHouseGrootSession"], () => {
      updateBanner();
    });
  }

  /** Update page banner with server name */
  function updateBanner() {
    if (!kameHouse.core.isEmpty(kameHouse.extension.groot.session.server)) {
      kameHouse.util.dom.setHtml($("#banner-p"), kameHouse.extension.groot.session.server);
    }
  }

  /** @deprecated Set client time and date */
  function setClientTimeAndDate() {
    const clientDate = new Date();
    const clientMonth = clientDate.getMonth() + 1;
    const clientTimeAndDate = "  Client: " + clientDate.getDate() + "/" + clientMonth + "/" + clientDate.getFullYear() + " - " + clientDate.getHours() + ":" + clientDate.getMinutes() + ":" + clientDate.getSeconds();
    kameHouse.util.dom.setInnerHtml(document.getElementById("client-time-and-date"), clientTimeAndDate);
  }
}

$(document).ready(() => {
  kameHouse.addExtension("grootHome", new GrootHome());
});