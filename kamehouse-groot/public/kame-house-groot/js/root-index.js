/** @deprecated Set client time and date */
function setClientTimeAndDate() {
  const clientDate = new Date();
  const clientMonth = clientDate.getMonth() + 1;
  const clientTimeAndDate = "  Client: " + clientDate.getDate() + "/" + clientMonth + "/" + clientDate.getFullYear() + " - " + clientDate.getHours() + ":" + clientDate.getMinutes() + ":" + clientDate.getSeconds();
  domUtils.setInnerHtml(document.getElementById("client-time-and-date"), clientTimeAndDate);
}

/** Update page banner with server name */
function updateBanner() {
  if (!isEmpty(global.groot.session.server)) {
    domUtils.setHtml($("#banner-h1"), global.groot.session.server);
  }
}

window.onload = () => {
  bannerUtils.setRandomTennisBanner();
  moduleUtils.waitForModules(["grootHeader"], () => {
    updateBanner();
  });
};