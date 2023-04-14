var serverManager;
var gitManager;
var deploymentManager;
var tailLogManagerWrapper;

function mainServerManager() {
  bannerUtils.setRandomAllBanner();
  moduleUtils.waitForModules(["tailLogManager", "scriptExecutor", "grootHeader"], () => {
    gitManager = new GitManager();
    deploymentManager = new DeploymentManager();
    serverManager = new ServerManager();
    tailLogManagerWrapper = new TailLogManagerWrapper();
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
  this.isRunningInDockerContainer = isRunningInDockerContainer;
  this.openExecutingCommandModal = openExecutingCommandModal;
  this.getHostOs = getHostOs;
  this.getExecutionOs = getExecutionOs;
  this.handleSessionStatus = handleSessionStatus;
  this.confirmRebootServer = confirmRebootServer;
  this.createAllVideoPlaylists = createAllVideoPlaylists;

  let isLinuxHost = false;
  let isLinuxDockerHost = false;
  let isDockerContainer = false;
  let dockerControlHost = false;
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

  /**
   * Get the os to execute the command on, considering if it's running inside a docker container.
   */
  function getExecutionOs() {
    if (isDockerContainer && dockerControlHost) {
      if (isLinuxDockerHost) {
        return "lin";
      } else {
        return "win";
      }
    }
    return getHostOs();
  }

  /**
   * Returns true if it's running inside a docker container.
   */
  function isRunningInDockerContainer() {
    return isDockerContainer;
  }

  /** Handle Session Status */
  function handleSessionStatus() {
    sessionStatus = kameHouse.groot.session;
    isLinuxHost = sessionStatus.isLinuxHost;
    isLinuxDockerHost = sessionStatus.isLinuxDockerHost;
    isDockerContainer = sessionStatus.isDockerContainer;
    dockerControlHost = sessionStatus.dockerControlHost;
    updateServerName(sessionStatus);
    deploymentManager.getTomcatProcessStatus();
  }
  
  /** Update server name */
  function updateServerName(sessionStatus) {
    if (!isEmpty(sessionStatus.server)) {
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
    basicKamehouseModal.close();
    if (isCommandRunning()) {
      return;
    }
    setCommandRunning();
    const hostOs = getExecutionOs();
    scriptExecutor.execute(hostOs + '/shutdown/reboot.sh', "", true, completeCommandCallback);
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
    scriptExecutor.execute('win/video-playlists/create-all-video-playlists.sh', "", true, completeCommandCallback);
  }

  function getRebootServerModalMessage() {
    const rebootModalMessage = domUtils.getSpan({}, "Are you sure you want to reboot the server? ");
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
    const hostOs = serverManager.getExecutionOs();
    scriptExecutor.execute(hostOs + '/git/git-pull-all.sh', "", true, serverManager.completeCommandCallback);
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
    scriptExecutor.execute('kamehouse/git-pull-all-all-servers.sh', "", false, serverManager.completeCommandCallback);
  }
}

/**
 * Manager to execute deployment tasks.
 */
function DeploymentManager() {

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
  this.restartTomcat = restartTomcat;

  const statusBallBlueImg = createStatusBallBlueImg();
  const statusBallRedImg = createStatusBallRedImg();
  const statusBallGreenImg = createStatusBallGreenImg();

  /**
   * Get status from all tomcat modules.
   */
  function getTomcatModulesStatus() {
    scriptExecutor.execute('kamehouse/status-kamehouse.sh', "", false, displayTomcatModulesStatus, true);
  }

  /**
   * Get status from non tomcat modules.
   */
  function getNonTomcatModulesStatus() {
    logger.debug("Getting non tomcat modules status");
    scriptExecutor.execute('kamehouse/kamehouse-cmd-version.sh', "", false, displayModuleCmdStatus, true);
    scriptExecutor.execute('kamehouse/kamehouse-groot-version.sh', "", false, displayModuleGrootStatus, true);
    scriptExecutor.execute('kamehouse/kamehouse-shell-version.sh', "", false, displayModuleShellStatus, true);
  }

  /**
   * Get the tomcat process status.
   */
  function getTomcatProcessStatus() {
    const hostOs = serverManager.getHostOs();
    scriptExecutor.execute(hostOs + '/kamehouse/tomcat-status.sh', "", false, displayTomcatProcessStatus, true);
  }

  /**
   * Render tomcat modules status.
   */
  function displayTomcatModulesStatus(scriptOutput) {
    collapsibleDivUtils.refreshCollapsibleDiv();
    scriptOutput.htmlConsoleOutput.forEach((scriptOutputLine) => {
      if (scriptOutputLine.startsWith("/kame-house")) {
        const scriptOutputLineArray = scriptOutputLine.split(":");
        const webapp = scriptOutputLineArray[0];
        const status = scriptOutputLineArray[1];
        const module = getModule(webapp);
        if (status == "running") {
          domUtils.setHtml($("#mst-" + module + "-status-val"), domUtils.cloneNode(statusBallGreenImg, true));
        } else if (status == "stopped") {
          domUtils.setHtml($("#mst-" + module + "-status-val"), domUtils.cloneNode(statusBallRedImg, true));
        } else {
          domUtils.setHtml($("#mst-" + module + "-status-val"), domUtils.cloneNode(statusBallBlueImg, true));
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
        const scriptOutputLineArray = scriptOutputLine.split("=");
        const buildVersion = scriptOutputLineArray[1];
        domUtils.setHtml($("#mst-" + module + "-build-version-val"), buildVersion);    
      }
      if (scriptOutputLine.startsWith("buildDate")) {
        const scriptOutputLineArray = scriptOutputLine.split("=");
        const buildDate = scriptOutputLineArray[1];
        domUtils.setHtml($("#mst-" + module + "-build-date-val"), buildDate);    
      }
    });
  }

  /**
   * Render tomcat process status.
   */
  function displayTomcatProcessStatus(scriptOutput) {
    const tomcatProcessStatusDiv = "#tomcat-process-status-val";
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
    domUtils.setHtml($("#mst-" + module + "-status-val"), domUtils.cloneNode(statusBallBlueImg, true));
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
    const args = "-m " + module;
    scriptExecutor.execute('kamehouse/start-kamehouse.sh', args, false, refreshServerView);
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
    const args = "-m " + module;
    scriptExecutor.execute('kamehouse/stop-kamehouse.sh', args, false, refreshServerView);
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
    const script = 'kamehouse/deploy-kamehouse.sh';
    const args = "-m " + module;

    scriptExecutor.execute(script, args, false, refreshServerView);
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
    const args = "-m " + module;
    scriptExecutor.execute('kamehouse/deploy-all-servers.sh', args, false, refreshServerView);
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
    const args = "-m " + module;
    scriptExecutor.execute('kamehouse/undeploy-kamehouse.sh', args, false, refreshServerView);
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
    scriptExecutor.execute('kamehouse/deploy-kamehouse.sh', "", false, refreshServerView);
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
    scriptExecutor.execute('kamehouse/deploy-all-servers.sh', "", false, refreshServerView);
  }

  /**
   * Restart tomcat.
   */
  function restartTomcat() {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();

    const stringArgs = getRestartTomcatParams();
    scriptExecutor.execute('kamehouse/tomcat-restart.sh', stringArgs, false, refreshServerView);
  }

  /**
   * Get the parameters for tomcat restart script.
   */
  function getRestartTomcatParams() {
    const tomcatDebugModeCheckbox = document.getElementById("tomcat-debug-mode");
    if (tomcatDebugModeCheckbox.checked) {
      return "-d";
    } else {
      return "";
    }
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

  this.toggleTailLog = toggleTailLog;

  const stopImg = createStopImg();
  const startImg = createStartImg();
  let isTailLogRunning = false;

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
      let logLevel = document.getElementById("tail-log-level-dropdown").value;
      let executeOnDockerHost = getExecuteOnDockerHost(tailLogScript);
      tailLogManager.tailLog(tailLogScript, numberOfLines, logLevel, executeOnDockerHost, collapsibleDivUtils.refreshCollapsibleDiv);
  
      await sleep(5000);
    }
    logger.info("Finished tailLog loop");
  }

  /**
   * Get the value of execute on docker host for the specified script.
   */
  function getExecuteOnDockerHost(tailLogScript) {
    return tailLogScript == "common/logs/cat-create-all-video-playlists-log.sh" || 
      tailLogScript == "common/logs/cat-git-pull-all-log.sh";
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
  mainServerManager();
}