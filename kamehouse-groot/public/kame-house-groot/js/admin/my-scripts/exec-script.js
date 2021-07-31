function main() {
  bannerUtils.setRandomAllBanner();
  moduleUtils.waitForModules(["scriptExecutor"], () => {
    scriptExecutor.setScriptNameAndArgsFromUrlParams();
    grootHeader.getSessionStatus(scriptExecutor.handleSessionStatus, () => { logger.error("Error getting session status"); });
  });
}

window.onload = () => {
  main();
}