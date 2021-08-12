var serverManager;
var gitManager;
var deploymentManager;
var tailLogManagerWrapper;

function main() {
  bannerUtils.setRandomAllBanner();
  moduleUtils.waitForModules(["tailLogManager", "scriptExecutor", "grootHeader"], () => {
    gitManager = new GitManager();
    deploymentManager = new DeploymentManager();
    deploymentManager.init();
    serverManager = new ServerManager();
    tailLogManagerWrapper = new TailLogManagerWrapper();
    tailLogManagerWrapper.init();
    serverManager.handleSessionStatus();
    deploymentManager.getTomcatModulesStatus();
    deploymentManager.getNonTomcatModulesStatus();
    serverManager.loadStateFromCookies();
  });
}

/**
 * Main object of the server manager page, to handle commands execution and some view updates.
 */
function ServerManager() {

  this.loadStateFromCookies = loadStateFromCookies;
  this.setCommandRunning = setCommandRunning;
  this.setCommandNotRunning = setCommandNotRunning;
  this.completeCommandCallback = completeCommandCallback;
  this.isCommandRunning = isCommandRunning;
  this.openExecutingCommandModal = openExecutingCommandModal;
  this.getHostOs = getHostOs;
  this.handleSessionStatus = handleSessionStatus;
  this.confirmRebootServer = confirmRebootServer;
  this.createAllVideoPlaylists = createAllVideoPlaylists;

  let isLinuxHost = false;
  let isCommandRunningFlag = false;

  /**
   * Load the current state from the cookies.
   */
  function loadStateFromCookies() {
    tabUtils.openTabFromCookies('kh-groot-server-manager', 'tab-deployment');
  }

  function setCommandRunning() {
    isCommandRunningFlag = true;
  }

  function setCommandNotRunning() {
    isCommandRunningFlag = false;
  }

  /**
   * Callback to execute after every command finishes.
   */
  function completeCommandCallback() {
    setCommandNotRunning();
    collapsibleDivUtils.refreshCollapsibleDiv();
  }

  /**
   * Call with:
   * if (serverManager.isCommandRunning()) {
   *  return;
   * }
   * serverManager.setCommandRunning();
   * Before executing any command, to prevent multiple commands running at the same time.
   */ 
  function isCommandRunning() {
    if (isCommandRunningFlag) {
      basicKamehouseModal.openAutoCloseable("There's a command already executing. Please wait and retry", 3000);
    }
    return isCommandRunningFlag;
  }

  /**
   * Open modal.
   */
  function openExecutingCommandModal() {
    loadingWheelModal.openAutoCloseable("Executing command. Check command output", 2000);
  }

  /**
   * Get host os.
   */
  function getHostOs() {
    if (isLinuxHost) {
      return "lin";
    } else {
      return "win";
    }
  }

  /** Handle Session Status */
  function handleSessionStatus() {
    sessionStatus = global.groot.session;
    isLinuxHost = sessionStatus.isLinuxHost;
    updateServerName(sessionStatus);
    deploymentManager.getTomcatProcessStatus();
  }
  
  /** Update server name */
  function updateServerName(sessionStatus) {
    if (!isNullOrUndefined(sessionStatus.server)) {
      domUtils.setHtml($("#banner-server-name"), sessionStatus.server);
    }
  }
  
  /**
   * Open modal to confirm reboot.
   */
  function confirmRebootServer() {
    basicKamehouseModal.setHtml(getRebootServerModalMessage());
    basicKamehouseModal.appendHtml(createRebootImg());
    basicKamehouseModal.open();
  }

  /**
   * Reboot the server.
   */
  function rebootServer() {
    if (isCommandRunning()) {
      return;
    }
    setCommandRunning();
    let hostOs = getHostOs();
    scriptExecutor.execute(hostOs + '/shutdown/reboot.sh', "", completeCommandCallback);
  }

  /**
   * Create all video playlists.
   */
  function createAllVideoPlaylists() {
    if (isCommandRunning()) {
      return;
    }
    setCommandRunning();
    openExecutingCommandModal();
    scriptExecutor.execute('win/video-playlists/create-all-video-playlists.sh', "", completeCommandCallback);
  }

  function getRebootServerModalMessage() {
    let rebootModalMessage = domUtils.getSpan({}, "Are you sure you want to reboot the server? ");
    domUtils.append(rebootModalMessage, domUtils.getBr());
    domUtils.append(rebootModalMessage, domUtils.getBr());
    return rebootModalMessage;
  }

  function createRebootImg() {
    return domUtils.getImgBtn({
      src: "/kame-house/img/pc/shutdown-red.png",
      className: "img-btn-kh",
      alt: "Reboot",
      onClick: () => { rebootServer() }
    });
  }
}

/**
 * Manager to execute git commands.
 */
function GitManager() {

  this.pullAll = pullAll;
  this.pullAllAllServers = pullAllAllServers;

  /**
   * Pull all from all git repos.
   */
  function pullAll() {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let hostOs = serverManager.getHostOs();
    scriptExecutor.execute(hostOs + '/git/git-pull-all.sh', "", serverManager.completeCommandCallback);
  }

  /**
   * Pull all from all repos, in all servers.
   */
  function pullAllAllServers() {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    scriptExecutor.execute('kamehouse/git-pull-all-all-servers.sh', "", serverManager.completeCommandCallback);
  }
}

/**
 * Manager to execute deployment tasks.
 */
function DeploymentManager() {

  this.init = init;
  this.getTomcatModulesStatus = getTomcatModulesStatus;
  this.getNonTomcatModulesStatus = getNonTomcatModulesStatus;
  this.getTomcatProcessStatus = getTomcatProcessStatus;
  this.refreshServerView = refreshServerView;
  this.startModule = startModule;
  this.stopModule = stopModule;
  this.deployModule = deployModule;
  this.undeployModule = undeployModule;
  this.deployModuleAllServers = deployModuleAllServers;
  this.deployAllModules = deployAllModules;
  this.deployAllModulesAllServers = deployAllModulesAllServers;
  this.startTomcat = startTomcat;
  this.stopTomcat = stopTomcat;

  let statusBallBlueImg = null;
  let statusBallRedImg = null;
  let statusBallGreenImg = null;

  function init() {
    statusBallBlueImg = createStatusBallBlueImg();
    statusBallRedImg = createStatusBallRedImg();
    statusBallGreenImg = createStatusBallGreenImg();
  }

  /**
   * Get status from all tomcat modules.
   */
  function getTomcatModulesStatus() {
    scriptExecutor.execute('kamehouse/status-java-web-kamehouse.sh', "", displayTomcatModulesStatus, true);
  }

  /**
   * Get status from non tomcat modules.
   */
  function getNonTomcatModulesStatus() {
    logger.debug("Getting non tomcat modules status");
    scriptExecutor.execute('kamehouse/kamehouse-cmd.sh', "-V", displayModuleCmdStatus, true);
    scriptExecutor.execute('kamehouse/groot-version.sh', "", displayModuleGrootStatus, true);
    scriptExecutor.execute('kamehouse/shell-version.sh', "", displayModuleShellStatus, true);
  }

  /**
   * Get the tomcat process status.
   */
  function getTomcatProcessStatus() {
    let hostOs = serverManager.getHostOs();
    scriptExecutor.execute(hostOs + '/kamehouse/tomcat-status.sh', "", displayTomcatProcessStatus, true);
  }

  /**
   * Render tomcat modules status.
   */
  function displayTomcatModulesStatus(scriptOutput) {
    collapsibleDivUtils.refreshCollapsibleDiv();
    scriptOutput.htmlConsoleOutput.forEach((scriptOutputLine) => {
      if (scriptOutputLine.startsWith("/kame-house")) {
        let scriptOutputLineArray = scriptOutputLine.split(":");
        let webapp = scriptOutputLineArray[0];
        let status = scriptOutputLineArray[1];
        let module = getModule(webapp);
        if (status == "running") {
          domUtils.setHtml($("#mst-" + module + "-status-val"), statusBallGreenImg.cloneNode(true));
        } else if (status == "stopped") {
          domUtils.setHtml($("#mst-" + module + "-status-val"), statusBallRedImg.cloneNode(true));
        } else {
          domUtils.setHtml($("#mst-" + module + "-status-val"), statusBallBlueImg.cloneNode(true));
        }        
      }
    });
  }

  /**
   * Render cmd module status.
   */
  function displayModuleCmdStatus(scriptOutput) {
    displayNonTomcatModuleStatus(scriptOutput, "cmd");
  }

  /**
   * Render groot module status.
   */
  function displayModuleGrootStatus(scriptOutput) {
    displayNonTomcatModuleStatus(scriptOutput, "groot");
  }

  /**
   * Render shell module status.
   */
  function displayModuleShellStatus(scriptOutput) {
    displayNonTomcatModuleStatus(scriptOutput, "shell");
  }

  /**
   * Render non tomcat module status.
   */
  function displayNonTomcatModuleStatus(scriptOutput, module) {
    scriptOutput.htmlConsoleOutput.forEach((scriptOutputLine) => {
      if (scriptOutputLine.startsWith("buildVersion")) {
        let scriptOutputLineArray = scriptOutputLine.split("=");
        let buildVersion = scriptOutputLineArray[1];
        domUtils.setHtml($("#mst-" + module + "-build-version-val"), buildVersion);    
      }
      if (scriptOutputLine.startsWith("buildDate")) {
        let scriptOutputLineArray = scriptOutputLine.split("=");
        let buildDate = scriptOutputLineArray[1];
        domUtils.setHtml($("#mst-" + module + "-build-date-val"), buildDate);    
      }
    });
  }

  /**
   * Render tomcat process status.
   */
  function displayTomcatProcessStatus(scriptOutput) {
    let tomcatProcessStatusDiv = "#tomcat-process-status-val";
    domUtils.empty($(tomcatProcessStatusDiv));
    scriptOutput.htmlConsoleOutput.forEach((scriptOutputLine) => {
      if (!scriptOutputLine.includes("Started executing") && 
          !scriptOutputLine.includes("Finished executing") &&
          !scriptOutputLine.includes("Searching for tomcat process") &&
          !scriptOutputLine.includes("TCP") &&
          !scriptOutputLine.includes("tcp") &&
          !scriptOutputLine.includes("Executing script")) {
        domUtils.append($(tomcatProcessStatusDiv), scriptOutputLine);
        domUtils.append($(tomcatProcessStatusDiv), domUtils.getBr());
      }
    });
    $(tomcatProcessStatusDiv).children().last().remove();
  }

  /**
   * Get the module from the webapp.
   */
  function getModule(webapp) {
    if (webapp == "/kame-house") {
      return "ui";
    }
    return webapp.substring(12);
  }

  /**
   * Refresh the server view.
   */
  function refreshServerView() {
    logger.info("Refreshing server view");
    resetAllModulesStatus();
    getTomcatModulesStatus();
    getTomcatProcessStatus();
    moduleStatusManager.getAllModulesStatus();
    getNonTomcatModulesStatus();
    serverManager.completeCommandCallback();
  }

  /**
   * Reset view of module status.
   */
  function resetModuleStatus(module) {
    domUtils.setHtml($("#mst-" + module + "-status-val"), statusBallBlueImg.cloneNode(true));
    domUtils.setHtml($("#mst-" + module + "-build-version-val"), "N/A");
    domUtils.setHtml($("#mst-" + module + "-build-date-val"), "N/A");
  }

  /**
   * Reset view of all tomcat modules.
   */
  function resetAllModulesStatus() {
    resetModuleStatus("admin");
    resetModuleStatus("media");
    resetModuleStatus("tennisworld");
    resetModuleStatus("testmodule");
    resetModuleStatus("ui");
    resetModuleStatus("vlcrc");
  }

  /**
   * Start tomcat module.
   */
  function startModule(module) {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-m " + module;
    scriptExecutor.execute('kamehouse/start-java-web-kamehouse.sh', args, refreshServerView);
  }

  /**
   * Stop tomcat module.
   */
  function stopModule(module) {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-m " + module;
    scriptExecutor.execute('kamehouse/stop-java-web-kamehouse.sh', args, refreshServerView);
  }

  /**
   * Deploy module.
   */
  function deployModule(module) {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let hostOs = serverManager.getHostOs();
    let script = 'kamehouse/deploy-java-web-kamehouse.sh';
    let args = "-f -m " + module;

    if (module == "groot") {
      script = hostOs + '/git/git-pull-prod-java-web-kamehouse.sh';
      args = "";
    }

    if (module == "shell") {
      script = hostOs + '/git/git-pull-my-scripts.sh';
      args = "";
    }

    scriptExecutor.execute(script, args, refreshServerView);
  }

  /**
   * Deploy module in all servers.
   */
  function deployModuleAllServers(module) {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-m " + module;
    scriptExecutor.execute('kamehouse/deploy-all-servers.sh', args, refreshServerView);
  }

  /**
   * Undeploy module.
   */
  function undeployModule(module) {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-m " + module;
    scriptExecutor.execute('kamehouse/undeploy-java-web-kamehouse.sh', args, refreshServerView);
  }

  /**
   * Deploy all modules in the local server.
   */
  function deployAllModules() {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-f";
    scriptExecutor.execute('kamehouse/deploy-java-web-kamehouse.sh', args, refreshServerView);
  }

  /**
   * Deploy all modules in all servers.
   */
  function deployAllModulesAllServers() {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    scriptExecutor.execute('kamehouse/deploy-all-servers.sh', "", refreshServerView);
  }

  /**
   * Start tomcat.
   */
  function startTomcat() {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    scriptExecutor.execute('kamehouse/tomcat-startup.sh', "", refreshServerView);
  }

  /**
   * Stop tomcat.
   */
  function stopTomcat() {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let hostOs = serverManager.getHostOs();
    scriptExecutor.execute(hostOs + '/kamehouse/tomcat-stop.sh', "", refreshServerView);
  }

  function createStatusBallRedImg() {
    return getStatusBallImg("red");
  }

  function createStatusBallGreenImg() {
    return getStatusBallImg("green");
  }  

  function createStatusBallBlueImg() {
    return getStatusBallImg("blue");
  }

  function getStatusBallImg(color) {
    return domUtils.getImgBtn({
      src: "/kame-house/img/other/ball-" + color + ".png",
      className: "img-tomcat-manager-status",
      alt: "Status"
    });
  }
}

/**
 * Manager to execute tail log commands.
 */
function TailLogManagerWrapper() {

  this.init = init;
  this.toggleTailLog = toggleTailLog;

  let isTailLogRunning = false;
  let stopImg = null;
  let startImg = null;

  function init() {
    stopImg = createStopImg();
    startImg = createStartImg();
  }

  /**
   * Toggle start and stop tailing log.
   */
  function toggleTailLog() {
    if (isTailLogRunning) {
      logger.info("Stopped tailLog loop");
      isTailLogRunning = false;
      domUtils.replaceWith($("#toggle-tail-log-img"), startImg);
      return;
    }
    logger.info("Started tailLog loop");
    isTailLogRunning = true;
    domUtils.replaceWith($("#toggle-tail-log-img"), stopImg);
    tailLog();
  }

  /**
   * Tail the log selected in the ui.
   */
  async function tailLog() {
    while (isTailLogRunning) {
      logger.trace(" tailLog loop running");
      let tailLogScript = document.getElementById("tail-log-dropdown").value;
      let numberOfLines = document.getElementById("tail-log-num-lines-dropdown").value;
      tailLogManager.tailLog(tailLogScript, numberOfLines, collapsibleDivUtils.refreshCollapsibleDiv);
  
      await sleep(5000);
    }
    logger.info("Finished tailLog loop");
  }

  function createStartImg() {
    return domUtils.getImgBtn({
      id: "toggle-tail-log-img",
      src: "/kame-house/img/mplayer/play-green.png",
      className: "img-btn-kh m-7-d-r-kh",
      alt: "Start Tail Log",
      onClick: () => toggleTailLog()
    });
  }

  function createStopImg() {
    return domUtils.getImgBtn({
      id: "toggle-tail-log-img",
      src: "/kame-house/img/mplayer/stop.png",
      className: "img-btn-kh m-7-d-r-kh",
      alt: "Stop Tail Log",
      onClick: () => toggleTailLog()
    });
  }
}

window.onload = () => {
  main();
}