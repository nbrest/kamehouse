function main() {
  bannerUtils.setRandomAllBanner();
  moduleUtils.waitForModules(["scriptExecutor", "grootHeader"], () => {
    scriptExecutor.setScriptNameAndArgsFromUrlParams();
    scriptExecutor.handleSessionStatus();
  });
}

window.onload = () => {
  main();
}