function main() {
  bannerUtils.setRandomAllBanner();
  renderRootMenu();
  
  moduleUtils.waitForModules(["tailLogManager"], () => {
    tailLogManager.setScriptName();
    getSessionStatus(tailLogManager.handleSessionStatus, () => { logger.error("Error getting session status"); });
    setInterval(() => {
      tailLogManager.tailLogFromUrlParams();
    }, 5000);
  });
}

window.onload = () => {
  main();
}