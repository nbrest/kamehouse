function ExecScriptLoader() {
  this.load = load;

  function load() {
    kameHouse.util.banner.setRandomAllBanner();
    kameHouse.util.module.waitForModules(["scriptExecutor", "kameHouseGroot"], () => {
      scriptExecutor.setScriptNameAndArgsFromUrlParams();
      scriptExecutor.handleSessionStatus();
    });
  }
}

$(document).ready(() => {
  kameHouse.addExtension("execScriptLoader", new ExecScriptLoader());
});