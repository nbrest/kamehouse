function ExecScriptLoader() {
  this.load = load;

  function load() {
    kameHouse.util.banner.setRandomAllBanner();
    kameHouse.util.module.waitForModules(["scriptExecutor", "kameHouseGroot"], () => {
      kameHouse.extension.scriptExecutor.setScriptNameAndArgsFromUrlParams();
      kameHouse.extension.scriptExecutor.handleSessionStatus();
    });
  }
}

$(document).ready(() => {
  kameHouse.addExtension("execScriptLoader", new ExecScriptLoader());
});