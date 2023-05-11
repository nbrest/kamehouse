/**
 * Main object of the server manager page, to handle commands execution and some view updates.
 */
function ServerManager() {

  this.load = load;
  this.loadStateFromCookies = loadStateFromCookies;
  this.setCommandRunning = setCommandRunning;
  this.setCommandNotRunning = setCommandNotRunning;
  this.completeCommandCallback = completeCommandCallback;
  this.isCommandRunning = isCommandRunning;
  this.isRunningInDockerContainer = isRunningInDockerContainer;
  this.openExecutingCommandModal = openExecutingCommandModal;
  this.getHostOs = getHostOs;
  this.getExecutionOs = getExecutionOs;
  this.confirmRebootServer = confirmRebootServer;
  this.createAllVideoPlaylists = createAllVideoPlaylists;

  let isLinuxHost = false;
  let isLinuxDockerHost = false;
  let isDockerContainer = false;
  let dockerControlHost = false;
  let isCommandRunningFlag = false;

  function load() {
    kameHouse.logger.info("Loading ServerManager");
    kameHouse.util.banner.setRandomAllBanner();
    loadStateFromCookies();
    kameHouse.util.module.waitForModules(["kameHouseGrootSession"], () => {
      handleSessionStatus();
    });
  }

  /**
   * Load the current state from the cookies.
   */
  function loadStateFromCookies() {
    kameHouse.util.tab.openTabFromCookies('kh-groot-server-manager', 'tab-deployment');
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
    kameHouse.util.collapsibleDiv.refreshCollapsibleDiv();
  }

  /**
   * Call with:
   * if (kameHouse.extension.serverManager.isCommandRunning()) {
   *  return;
   * }
   * kameHouse.extension.serverManager.setCommandRunning();
   * Before executing any command, to prevent multiple commands running at the same time.
   */ 
  function isCommandRunning() {
    if (isCommandRunningFlag) {
      kameHouse.plugin.modal.basicModal.openAutoCloseable("There's a command already executing. Please wait and retry", 3000);
    }
    return isCommandRunningFlag;
  }

  /**
   * Open modal.
   */
  function openExecutingCommandModal() {
    kameHouse.plugin.modal.loadingWheelModal.openAutoCloseable("Executing command. Check command output", 2000);
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
    sessionStatus = kameHouse.extension.groot.session;
    isLinuxHost = sessionStatus.isLinuxHost;
    isLinuxDockerHost = sessionStatus.isLinuxDockerHost;
    isDockerContainer = sessionStatus.isDockerContainer;
    dockerControlHost = sessionStatus.dockerControlHost;
    updateServerName(sessionStatus);
  }
  
  /** Update server name */
  function updateServerName(sessionStatus) {
    if (!kameHouse.core.isEmpty(sessionStatus.server)) {
      kameHouse.util.dom.setHtml($("#banner-server-name"), sessionStatus.server);
    }
  }
  
  /**
   * Open modal to confirm reboot.
   */
  function confirmRebootServer() {
    kameHouse.plugin.modal.basicModal.setHtml(getRebootServerModalMessage());
    kameHouse.plugin.modal.basicModal.appendHtml(createRebootImg());
    kameHouse.plugin.modal.basicModal.open();
  }

  /**
   * Reboot the server.
   */
  function rebootServer() {
    kameHouse.plugin.modal.basicModal.close();
    if (isCommandRunning()) {
      return;
    }
    setCommandRunning();
    const hostOs = getExecutionOs();
    kameHouse.extension.kameHouseShell.execute(hostOs + '/shutdown/reboot.sh', "", true, completeCommandCallback, completeCommandCallback);
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
    kameHouse.extension.kameHouseShell.execute('win/video-playlists/create-all-video-playlists.sh', "", true, completeCommandCallback, completeCommandCallback);
  }

  function getRebootServerModalMessage() {
    const rebootModalMessage = kameHouse.util.dom.getSpan({}, "Are you sure you want to reboot the server? ");
    kameHouse.util.dom.append(rebootModalMessage, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(rebootModalMessage, kameHouse.util.dom.getBr());
    return rebootModalMessage;
  }

  function createRebootImg() {
    return kameHouse.util.dom.getImgBtn({
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

  this.load = load;
  this.pullAll = pullAll;
  this.pullAllAllServers = pullAllAllServers;

  function load() {
    kameHouse.logger.info("Loading GitManager");
    kameHouse.util.module.setModuleLoaded("gitManager");
  }

  /**
   * Pull all from all git repos.
   */
  function pullAll() {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    const hostOs = kameHouse.extension.serverManager.getExecutionOs();
    kameHouse.extension.kameHouseShell.execute(hostOs + '/git/git-pull-all.sh', "", true, kameHouse.extension.serverManager.completeCommandCallback, kameHouse.extension.serverManager.completeCommandCallback);
  }

  /**
   * Pull all from all repos, in all servers.
   */
  function pullAllAllServers() {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    kameHouse.extension.kameHouseShell.execute('kamehouse/git-pull-all-all-servers.sh', "", false, kameHouse.extension.serverManager.completeCommandCallback, kameHouse.extension.serverManager.completeCommandCallback);
  }
}

/**
 * Manager to execute deployment tasks.
 */
function DeploymentManager() {

  this.load = load;
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

  function load() {
    kameHouse.logger.info("Loading DeploymentManager");
    kameHouse.util.module.waitForModules(["kameHouseShell", "kameHouseModal", "kameHouseDebugger", "kameHouseGrootSession"], () => {
      getTomcatModulesStatus();
      getNonTomcatModulesStatus();
      getTomcatProcessStatus();
    });
  }

  /**
   * Get status from all tomcat modules.
   */
  function getTomcatModulesStatus() {
    kameHouse.extension.kameHouseShell.execute('kamehouse/status-kamehouse.sh', "", false, displayTomcatModulesStatus, () => {});
  }

  /**
   * Get status from non tomcat modules.
   */
  function getNonTomcatModulesStatus() {
    kameHouse.logger.debug("Getting non tomcat modules status");
    kameHouse.extension.kameHouseShell.execute('kamehouse/kamehouse-cmd-version.sh', "", false, displayModuleCmdStatus, () => {});
    kameHouse.extension.kameHouseShell.execute('kamehouse/kamehouse-groot-version.sh', "", false, displayModuleGrootStatus, () => {});
    kameHouse.extension.kameHouseShell.execute('kamehouse/kamehouse-shell-version.sh', "", false, displayModuleShellStatus, () => {});
  }

  /**
   * Get the tomcat process status.
   */
  function getTomcatProcessStatus() {
    const hostOs = kameHouse.extension.serverManager.getHostOs();
    kameHouse.extension.kameHouseShell.execute(hostOs + '/kamehouse/tomcat-status.sh', "", false, displayTomcatProcessStatus, () => {});
  }

  /**
   * Render tomcat modules status.
   */
  function displayTomcatModulesStatus(scriptOutput) {
    kameHouse.util.collapsibleDiv.refreshCollapsibleDiv();
    scriptOutput.htmlConsoleOutput.forEach((scriptOutputLine) => {
      if (scriptOutputLine.startsWith("/kame-house")) {
        const scriptOutputLineArray = scriptOutputLine.split(":");
        const webapp = scriptOutputLineArray[0];
        const status = scriptOutputLineArray[1];
        const module = getModule(webapp);
        if (status == "running") {
          kameHouse.util.dom.setHtml($("#mst-" + module + "-status-val"), kameHouse.util.dom.cloneNode(statusBallGreenImg, true));
        } else if (status == "stopped") {
          kameHouse.util.dom.setHtml($("#mst-" + module + "-status-val"), kameHouse.util.dom.cloneNode(statusBallRedImg, true));
        } else {
          kameHouse.util.dom.setHtml($("#mst-" + module + "-status-val"), kameHouse.util.dom.cloneNode(statusBallBlueImg, true));
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
        kameHouse.util.dom.setHtml($("#mst-" + module + "-build-version-val"), buildVersion);    
      }
      if (scriptOutputLine.startsWith("buildDate")) {
        const scriptOutputLineArray = scriptOutputLine.split("=");
        const buildDate = scriptOutputLineArray[1];
        kameHouse.util.dom.setHtml($("#mst-" + module + "-build-date-val"), buildDate);    
      }
    });
  }

  /**
   * Render tomcat process status.
   */
  function displayTomcatProcessStatus(scriptOutput) {
    const tomcatProcessStatusDiv = "#tomcat-process-status-val";
    kameHouse.util.dom.empty($(tomcatProcessStatusDiv));
    scriptOutput.htmlConsoleOutput.forEach((scriptOutputLine) => {
      if (!scriptOutputLine.includes("Started executing") && 
          !scriptOutputLine.includes("Finished executing") &&
          !scriptOutputLine.includes("Searching for tomcat process") &&
          !scriptOutputLine.includes("TCP") &&
          !scriptOutputLine.includes("tcp") &&
          !scriptOutputLine.includes("Executing script")) {
        kameHouse.util.dom.append($(tomcatProcessStatusDiv), scriptOutputLine);
        kameHouse.util.dom.append($(tomcatProcessStatusDiv), kameHouse.util.dom.getBr());
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
    kameHouse.logger.info("Refreshing server view");
    resetAllModulesStatus();
    getTomcatModulesStatus();
    getTomcatProcessStatus();
    kameHouse.extension.moduleStatusManager.getAllModulesStatus();
    getNonTomcatModulesStatus();
    kameHouse.extension.serverManager.completeCommandCallback();
  }

  /**
   * Reset view of module status.
   */
  function resetModuleStatus(module) {
    kameHouse.util.dom.setHtml($("#mst-" + module + "-status-val"), kameHouse.util.dom.cloneNode(statusBallBlueImg, true));
    kameHouse.util.dom.setHtml($("#mst-" + module + "-build-version-val"), "N/A");
    kameHouse.util.dom.setHtml($("#mst-" + module + "-build-date-val"), "N/A");
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
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    const args = "-m " + module;
    kameHouse.extension.kameHouseShell.execute('kamehouse/start-kamehouse.sh', args, false, refreshServerView, () => {});
  }

  /**
   * Stop tomcat module.
   */
  function stopModule(module) {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    const args = "-m " + module;
    kameHouse.extension.kameHouseShell.execute('kamehouse/stop-kamehouse.sh', args, false, refreshServerView, () => {});
  }

  /**
   * Deploy module.
   */
  function deployModule(module) {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    const script = 'kamehouse/deploy-kamehouse.sh';
    const args = "-m " + module;

    kameHouse.extension.kameHouseShell.execute(script, args, false, refreshServerView, () => {});
  }

  /**
   * Deploy module in all servers.
   */
  function deployModuleAllServers(module) {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    const args = "-m " + module;
    kameHouse.extension.kameHouseShell.execute('kamehouse/deploy-all-servers.sh', args, false, refreshServerView, () => {});
  }

  /**
   * Undeploy module.
   */
  function undeployModule(module) {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    const args = "-m " + module;
    kameHouse.extension.kameHouseShell.execute('kamehouse/undeploy-kamehouse.sh', args, false, refreshServerView, () => {});
  }

  /**
   * Deploy all modules in the local server.
   */
  function deployAllModules() {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    kameHouse.extension.kameHouseShell.execute('kamehouse/deploy-kamehouse.sh', "", false, refreshServerView, () => {});
  }

  /**
   * Deploy all modules in all servers.
   */
  function deployAllModulesAllServers() {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    kameHouse.extension.kameHouseShell.execute('kamehouse/deploy-all-servers.sh', "", false, refreshServerView, () => {});
  }

  /**
   * Restart tomcat.
   */
  function restartTomcat() {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();

    const stringArgs = getRestartTomcatParams();
    kameHouse.extension.kameHouseShell.execute('kamehouse/tomcat-restart.sh', stringArgs, false, refreshServerView, () => {});
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
    return kameHouse.util.dom.getImgBtn({
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

  this.load = load;
  this.toggleTailLog = toggleTailLog;

  const stopImg = createStopImg();
  const startImg = createStartImg();
  let isTailLogRunning = false;

  function load() {
    kameHouse.logger.info("Loading TailLogManagerWrapper");
    kameHouse.util.module.setModuleLoaded("tailLogManagerWrapper");
  }

  /**
   * Toggle start and stop tailing log.
   */
  function toggleTailLog() {
    if (isTailLogRunning) {
      kameHouse.logger.info("Stopped tailLog loop");
      isTailLogRunning = false;
      kameHouse.util.dom.replaceWith($("#toggle-tail-log-img"), startImg);
      return;
    }
    kameHouse.logger.info("Started tailLog loop");
    isTailLogRunning = true;
    kameHouse.util.dom.replaceWith($("#toggle-tail-log-img"), stopImg);
    tailLog();
  }

  /**
   * Tail the log selected in the ui.
   */
  async function tailLog() {
    while (isTailLogRunning) {
      kameHouse.logger.trace(" tailLog loop running");
      let tailLogScript = document.getElementById("tail-log-dropdown").value;
      let numberOfLines = document.getElementById("tail-log-num-lines-dropdown").value;
      let logLevel = document.getElementById("tail-log-level-dropdown").value;
      let executeOnDockerHost = getExecuteOnDockerHost(tailLogScript);
      kameHouse.extension.tailLogManager.tailLog(tailLogScript, numberOfLines, logLevel, executeOnDockerHost, kameHouse.util.collapsibleDiv.refreshCollapsibleDiv);
  
      await kameHouse.core.sleep(5000);
    }
    kameHouse.logger.info("Finished tailLog loop");
  }

  /**
   * Get the value of execute on docker host for the specified script.
   */
  function getExecuteOnDockerHost(tailLogScript) {
    return tailLogScript == "common/logs/cat-create-all-video-playlists-log.sh" || 
      tailLogScript == "common/logs/cat-git-pull-all-log.sh";
  }

  function createStartImg() {
    return kameHouse.util.dom.getImgBtn({
      id: "toggle-tail-log-img",
      src: "/kame-house/img/mplayer/play-gray.png",
      className: "img-btn-kh m-7-d-r-kh",
      alt: "Start Tail Log",
      onClick: () => toggleTailLog()
    });
  }

  function createStopImg() {
    return kameHouse.util.dom.getImgBtn({
      id: "toggle-tail-log-img",
      src: "/kame-house/img/mplayer/stop.png",
      className: "img-btn-kh m-7-d-r-kh",
      alt: "Stop Tail Log",
      onClick: () => toggleTailLog()
    });
  }
}

$(document).ready(() => {
  kameHouse.addExtension("serverManager", new ServerManager());
  kameHouse.addExtension("gitManager", new GitManager());
  kameHouse.addExtension("deploymentManager", new DeploymentManager());
  kameHouse.addExtension("tailLogManagerWrapper", new TailLogManagerWrapper());
});