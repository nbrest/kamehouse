function main() {
  bannerUtils.setRandomAllBanner();
  renderRootMenu();
  moduleUtils.waitForModules(["scriptExecutor"], () => {
    scriptExecutor.setScriptNameAndArgsFromUrlParams();
    getSessionStatus(scriptExecutor.handleSessionStatus, () => { logger.error("Error getting session status"); });
  });
}

window.onload = () => {
  main();
}