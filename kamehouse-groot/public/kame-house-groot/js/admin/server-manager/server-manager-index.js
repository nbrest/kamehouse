var serverManager;
var gitManager;
var deploymentManager;
var tailLogManagerWrapper;

function main() {
  bannerUtils.setRandomAllBanner();
  moduleUtils.waitForModules(["logger", "httpClient", "tailLogManager", "scriptExecutor", "grootHeader"], () => {
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
  let self = this;
  this.isLinuxHost = false;
  this.isCommandRunningFlag = false;

  /**
   * Load the current state from the cookies.
   */
  this.loadStateFromCookies = () => {
    tabUtils.openTabFromCookies('kh-groot-server-manager', 'tab-deployment');
  }

  this.setCommandRunning = () => {
    self.isCommandRunningFlag = true;
  }

  this.setCommandNotRunning = () => {
    self.isCommandRunningFlag = false;
  }

  /**
   * Callback to execute after every command finishes.
   */
  this.completeCommandCallback = () => {
    self.setCommandNotRunning();
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
  this.isCommandRunning = () => {
    if (self.isCommandRunningFlag) {
      basicKamehouseModal.openAutoCloseable("There's a command already executing. Please wait and retry", 3000);
    }
    return self.isCommandRunningFlag;
  }

  /**
   * Open modal.
   */
  this.openExecutingCommandModal = () => {
    loadingWheelModal.openAutoCloseable("Executing command. Check command output", 2000);
  }

  /**
   * Get host os.
   */
  this.getHostOs = () => {
    if (self.isLinuxHost) {
      return "lin";
    } else {
      return "win";
    }
  }

  /** Handle Session Status */
  this.handleSessionStatus = () => {
    sessionStatus = global.groot.session;
    self.isLinuxHost = sessionStatus.isLinuxHost;
    self.updateServerName(sessionStatus);
    deploymentManager.getTomcatProcessStatus();
  }
  
  /** Update server name */
  this.updateServerName = (sessionStatus) => {
    if (!isNullOrUndefined(sessionStatus.server)) {
      $("#banner-server-name").text(sessionStatus.server);
    }
  }
  
  /**
   * Open modal to confirm reboot.
   */
  this.confirmRebootServer = () => {
    basicKamehouseModal.setHtml(self.getRebootServerModalMessage());
    basicKamehouseModal.appendHtml(self.createRebootImg());
    basicKamehouseModal.open();
  }

  /**
   * Reboot the server.
   */
  this.rebootServer = () => {
    if (self.isCommandRunning()) {
      return;
    }
    self.setCommandRunning();
    let hostOs = self.getHostOs();
    scriptExecutor.execute(hostOs + '/shutdown/reboot.sh', "", self.completeCommandCallback);
  }

  /**
   * Create all video playlists.
   */
  this.createAllVideoPlaylists = () => {
    if (self.isCommandRunning()) {
      return;
    }
    self.setCommandRunning();
    self.openExecutingCommandModal();
    scriptExecutor.execute('win/video-playlists/create-all-video-playlists.sh', "", self.completeCommandCallback);
  }

  /** Dynamic DOM element generation ------------------------------------------ */
  this.getRebootServerModalMessage = () => {
    return "Are you sure you want to reboot the server? <br><br>";
  }

  this.createRebootImg = () => {
    let img = new Image();
    img.src = "/kame-house/img/pc/shutdown-red.png";
    img.className = "img-btn-kh";
    img.alt = "Reboot";
    img.title = "Reboot";
    img.onclick = () => self.rebootServer();
    return img;
  }
}

/**
 * Manager to execute git commands.
 */
function GitManager() {
  let self = this;

  /**
   * Pull all from all git repos.
   */
  this.pullAll = () => {
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
  this.pullAllAllServers = () => {
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
  let self = this;
  this.statusBallBlueImg = null;
  this.statusBallRedImg = null;
  this.statusBallGreenImg = null;

  this.init = function init() {
    self.statusBallBlueImg = self.createStatusBallBlueImg();
    self.statusBallRedImg = self.createStatusBallRedImg();
    self.statusBallGreenImg = self.createStatusBallGreenImg();
  }

  /**
   * Get status from all tomcat modules.
   */
  this.getTomcatModulesStatus = () => {
    scriptExecutor.execute('kamehouse/status-java-web-kamehouse.sh', "", self.displayTomcatModulesStatus, true);
  }

  /**
   * Get status from non tomcat modules.
   */
  this.getNonTomcatModulesStatus = () => {
    logger.debug("Getting non tomcat modules status");
    scriptExecutor.execute('kamehouse/kamehouse-cmd.sh', "-V", self.displayModuleCmdStatus, true);
    scriptExecutor.execute('kamehouse/groot-version.sh', "", self.displayModuleGrootStatus, true);
    scriptExecutor.execute('kamehouse/shell-version.sh', "", self.displayModuleShellStatus, true);
  }

  /**
   * Get the tomcat process status.
   */
  this.getTomcatProcessStatus = () => {
    let hostOs = serverManager.getHostOs();
    scriptExecutor.execute(hostOs + '/kamehouse/tomcat-status.sh', "", self.displayTomcatProcessStatus, true);
  }

  /**
   * Render tomcat modules status.
   */
  this.displayTomcatModulesStatus = (scriptOutput) => {
    collapsibleDivUtils.refreshCollapsibleDiv();
    scriptOutput.htmlConsoleOutput.forEach((scriptOutputLine) => {
      if (scriptOutputLine.startsWith("/kame-house")) {
        let scriptOutputLineArray = scriptOutputLine.split(":");
        let webapp = scriptOutputLineArray[0];
        let status = scriptOutputLineArray[1];
        let module = self.getModule(webapp);
        if (status == "running") {
          $("#mst-" + module + "-status-val").html(self.statusBallGreenImg.cloneNode(true));
        } else if (status == "stopped") {
          $("#mst-" + module + "-status-val").html(self.statusBallRedImg.cloneNode(true));
        } else {
          $("#mst-" + module + "-status-val").html(self.statusBallBlueImg.cloneNode(true));
        }        
      }
    });
  }

  /**
   * Render cmd module status.
   */
  this.displayModuleCmdStatus = (scriptOutput) => {
    self.displayNonTomcatModuleStatus(scriptOutput, "cmd");
  }

  /**
   * Render groot module status.
   */
  this.displayModuleGrootStatus = (scriptOutput) => {
    self.displayNonTomcatModuleStatus(scriptOutput, "groot");
  }

  /**
   * Render shell module status.
   */
  this.displayModuleShellStatus = (scriptOutput) => {
    self.displayNonTomcatModuleStatus(scriptOutput, "shell");
  }

  /**
   * Render non tomcat module status.
   */
  this.displayNonTomcatModuleStatus = (scriptOutput, module) => {
    scriptOutput.htmlConsoleOutput.forEach((scriptOutputLine) => {
      if (scriptOutputLine.startsWith("buildVersion")) {
        let scriptOutputLineArray = scriptOutputLine.split("=");
        let buildVersion = scriptOutputLineArray[1];
        $("#mst-" + module + "-build-version-val").html(buildVersion);    
      }
      if (scriptOutputLine.startsWith("buildDate")) {
        let scriptOutputLineArray = scriptOutputLine.split("=");
        let buildDate = scriptOutputLineArray[1];
        $("#mst-" + module + "-build-date-val").html(buildDate);    
      }
    });
  }

  /**
   * Render tomcat process status.
   */
  this.displayTomcatProcessStatus = (scriptOutput) => {
    let tomcatProcessStatusDiv = "#tomcat-process-status-val";
    $(tomcatProcessStatusDiv).empty();
    scriptOutput.htmlConsoleOutput.forEach((scriptOutputLine) => {
      if (!scriptOutputLine.includes("Started executing") && 
          !scriptOutputLine.includes("Finished executing") &&
          !scriptOutputLine.includes("Searching for tomcat process") &&
          !scriptOutputLine.includes("TCP") &&
          !scriptOutputLine.includes("tcp") &&
          !scriptOutputLine.includes("Executing script")) {
        $(tomcatProcessStatusDiv).append(scriptOutputLine);
        $(tomcatProcessStatusDiv).append("<br>");
      }
    });
    $(tomcatProcessStatusDiv).children().last().remove();
  }

  /**
   * Get the module from the webapp.
   */
  this.getModule = (webapp) => {
    if (webapp == "/kame-house") {
      return "ui";
    }
    return webapp.substring(12);
  }

  /**
   * Refresh the server view.
   */
  this.refreshServerView = () => {
    logger.info("Refreshing server view");
    self.resetAllModulesStatus();
    self.getTomcatModulesStatus();
    self.getTomcatProcessStatus();
    moduleStatusManager.getAllModulesStatus();
    self.getNonTomcatModulesStatus();
    serverManager.completeCommandCallback();
  }

  /**
   * Reset view of module status.
   */
  this.resetModuleStatus = (module) => {
    $("#mst-" + module + "-status-val").html(self.statusBallBlueImg.cloneNode(true));
    $("#mst-" + module + "-build-version-val").text("N/A");
    $("#mst-" + module + "-build-date-val").text("N/A");
  }

  /**
   * Reset view of all tomcat modules.
   */
  this.resetAllModulesStatus = () => {
    self.resetModuleStatus("admin");
    self.resetModuleStatus("media");
    self.resetModuleStatus("tennisworld");
    self.resetModuleStatus("testmodule");
    self.resetModuleStatus("ui");
    self.resetModuleStatus("vlcrc");
  }

  /**
   * Start tomcat module.
   */
  this.startModule = (module) => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-m " + module;
    scriptExecutor.execute('kamehouse/start-java-web-kamehouse.sh', args, self.refreshServerView);
  }

  /**
   * Stop tomcat module.
   */
  this.stopModule = (module) => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-m " + module;
    scriptExecutor.execute('kamehouse/stop-java-web-kamehouse.sh', args, self.refreshServerView);
  }

  /**
   * Deploy module.
   */
  this.deployModule = (module) => {
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

    scriptExecutor.execute(script, args, self.refreshServerView);
  }

  /**
   * Deploy module in all servers.
   */
  this.deployModuleAllServers = (module) => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-m " + module;
    scriptExecutor.execute('kamehouse/deploy-all-servers.sh', args, self.refreshServerView);
  }

  /**
   * Undeploy module.
   */
  this.undeployModule = (module) => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-m " + module;
    scriptExecutor.execute('kamehouse/undeploy-java-web-kamehouse.sh', args, self.refreshServerView);
  }

  /**
   * Deploy all modules in the local server.
   */
  this.deployAllModules = () => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-f";
    scriptExecutor.execute('kamehouse/deploy-java-web-kamehouse.sh', args, self.refreshServerView);
  }

  /**
   * Deploy all modules in all servers.
   */
  this.deployAllModulesAllServers = () => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    scriptExecutor.execute('kamehouse/deploy-all-servers.sh', "", self.refreshServerView);
  }

  /**
   * Start tomcat.
   */
  this.startTomcat = () => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    scriptExecutor.execute('kamehouse/tomcat-startup.sh', "", self.refreshServerView);
  }

  /**
   * Stop tomcat.
   */
  this.stopTomcat = () => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let hostOs = serverManager.getHostOs();
    scriptExecutor.execute(hostOs + '/kamehouse/tomcat-stop.sh', "", self.refreshServerView);
  }

  /** Dynamic DOM element generation ------------------------------------------ */
  this.createStatusBallRedImg = () => {
    let img = new Image();
    img.src = "/kame-house/img/other/ball-red.png";
    img.className = "img-tomcat-manager-status";
    img.alt = "Status";
    img.title = "Status";
    return img;
  }  

  this.createStatusBallGreenImg = () => {
    let img = new Image();
    img.src = "/kame-house/img/other/ball-green.png";
    img.className = "img-tomcat-manager-status";
    img.alt = "Status";
    img.title = "Status";
    return img;
  }  

  this.createStatusBallBlueImg = () => {
    let img = new Image();
    img.src = "/kame-house/img/other/ball-blue.png";
    img.className = "img-tomcat-manager-status";
    img.alt = "Status";
    img.title = "Status";
    return img;
  }  
}

/**
 * Manager to execute tail log commands.
 */
function TailLogManagerWrapper() {
  let self = this;
  this.isTailLogRunning = false;

  this.stopImg = null;
  this.startImg = null;

  this.init = function init() {
    self.stopImg = self.createStopImg();
    self.startImg = self.createStartImg();
  }

  /**
   * Toggle start and stop tailing log.
   */
  this.toggleTailLog = () => {
    if (self.isTailLogRunning) {
      logger.info("Stopped tailLog loop");
      self.isTailLogRunning = false;
      $("#toggle-tail-log-img").replaceWith(self.startImg);
      return;
    }
    logger.info("Started tailLog loop");
    self.isTailLogRunning = true;
    $("#toggle-tail-log-img").replaceWith(self.stopImg);
    self.tailLog();
  }

  /**
   * Tail the log selected in the ui.
   */
  this.tailLog = async () => {
    while (self.isTailLogRunning) {
      logger.trace(" tailLog loop running");
      let tailLogScript = document.getElementById("tail-log-dropdown").value;
      let numberOfLines = document.getElementById("tail-log-num-lines-dropdown").value;
      tailLogManager.tailLog(tailLogScript, numberOfLines, collapsibleDivUtils.refreshCollapsibleDiv);
  
      await sleep(5000);
    }
    logger.info("Finished tailLog loop");
  }

  /** Dynamic DOM element generation ------------------------------------------ */
  this.createStopImg = () => {
    let img = new Image();
    img.id = "toggle-tail-log-img";
    img.src = "/kame-house/img/mplayer/stop.png";
    img.className = "img-btn-kh m-7-d-r-kh";
    img.alt = "Stop Tail Log";
    img.title = "Stop Tail Log";
    img.onclick = () => self.toggleTailLog();
    return img;
  }

  this.createStartImg = () => {
    let img = new Image();
    img.id = "toggle-tail-log-img";
    img.src = "/kame-house/img/mplayer/play-green.png";
    img.className = "img-btn-kh m-7-d-r-kh";
    img.alt = "Start Tail Log";
    img.title = "Start Tail Log";
    img.onclick = () => self.toggleTailLog();
    return img;
  }
}

window.onload = () => {
  main();
}