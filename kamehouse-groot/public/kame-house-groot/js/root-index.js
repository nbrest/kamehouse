/** Update page banner with server name */
function updateBanner() {
  if (!kameHouse.core.isEmpty(kameHouse.groot.session.server)) {
    kameHouse.util.dom.setHtml($("#banner-h1"), kameHouse.groot.session.server);
  }
}

/** @deprecated Set client time and date */
function setClientTimeAndDate() {
  const clientDate = new Date();
  const clientMonth = clientDate.getMonth() + 1;
  const clientTimeAndDate = "  Client: " + clientDate.getDate() + "/" + clientMonth + "/" + clientDate.getFullYear() + " - " + clientDate.getHours() + ":" + clientDate.getMinutes() + ":" + clientDate.getSeconds();
  kameHouse.util.dom.setInnerHtml(document.getElementById("client-time-and-date"), clientTimeAndDate);
}

window.onload = () => {
  kameHouse.util.banner.setRandomTennisBanner();
  kameHouse.util.module.waitForModules(["grootHeader"], () => {
    updateBanner();
  });
};