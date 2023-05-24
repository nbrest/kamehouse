
function GrootHome() {

  this.load = load;

  function load() {
    kameHouse.util.banner.setRandomAllBanner();
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