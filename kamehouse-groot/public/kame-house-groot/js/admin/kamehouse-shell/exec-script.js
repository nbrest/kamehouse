function main() {
  kameHouse.util.banner.setRandomAllBanner();
  kameHouse.util.module.waitForModules(["scriptExecutor", "grootHeader"], () => {
    scriptExecutor.setScriptNameAndArgsFromUrlParams();
    scriptExecutor.handleSessionStatus();
  });
}

window.onload = () => {
  main();
}