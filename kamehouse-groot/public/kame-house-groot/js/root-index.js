/** @deprecated Set client time and date */
function setClientTimeAndDate() {
  let clientDate = new Date();
  let clientMonth = clientDate.getMonth() + 1;
  let clientTimeAndDate = "  Client: " + clientDate.getDate() + "/" + clientMonth + "/" + clientDate.getFullYear() + " - " + clientDate.getHours() + ":" + clientDate.getMinutes() + ":" + clientDate.getSeconds();
  document.getElementById("client-time-and-date").innerHTML = clientTimeAndDate;
}

/** Update page banner with server name */
function updateBanner(sessionStatus) {
  if (!isNullOrUndefined(sessionStatus.server)) {
    $("#banner-h1").text(sessionStatus.server);
  }
}

window.onload = () => {
  bannerUtils.setRandomTennisBanner();
  moduleUtils.waitForModules(["logger", "httpClient"], () => {
    grootHeader.getSessionStatus(updateBanner, () => {});
  });
};