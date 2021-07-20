var serverManager;
var gitManager;
var tomcatManager;
var tailLogManagerWrapper;

function main() {
  bannerUtils.setRandomAllBanner();
  renderRootMenu();
  moduleUtils.waitForModules(["logger", "httpClient", "tailLogManager", "scriptExecutor"], () => {
    gitManager = new GitManager();
    tomcatManager = new TomcatManager();
    tomcatManager.init();
    serverManager = new ServerManager();
    tailLogManagerWrapper = new TailLogManagerWrapper();
    tailLogManagerWrapper.init();
    getSessionStatus(serverManager.handleSessionStatus, () => { logger.error("Error getting session status"); });
    tomcatManager.getAppsStatus();
    serverManager.loadStateFromCookies();
  });
}

function ServerManager() {
  let self = this;
  this.isLinuxHost = false;
  this.isCommandRunningFlag = false;

  /**
   * Load the current state from the cookies.
   */
  this.loadStateFromCookies = () => {
    let currentTab = cookiesUtils.getCookie('kh-groot-server-manager-current-tab');
    if (!currentTab || currentTab == '') {
      currentTab = 'tab-tomcat';
    }
    openTab(currentTab, 'kh-groot-server-manager');
  }

  this.setCommandRunning = () => {
    self.isCommandRunningFlag = true;
  }

  this.setCommandNotRunning = () => {
    self.isCommandRunningFlag = false;
  }

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

  this.openExecutingCommandModal = () => {
    loadingWheelModal.openAutoCloseable("Executing command. Check command output", 2000);
  }

  this.getHostOs = () => {
    if (self.isLinuxHost) {
      return "lin";
    } else {
      return "win";
    }
  }

  /** Handle Session Status */
  this.handleSessionStatus = (sessionStatus) => {
    self.isLinuxHost = sessionStatus.isLinuxHost;
    self.updateServerName(sessionStatus);
    tomcatManager.getTomcatProcessStatus();
  }
  
  /** Update server name */
  this.updateServerName = (sessionStatus) => {
    if (!isNullOrUndefined(sessionStatus.server)) {
      $("#banner-server-name").text(sessionStatus.server);
    }
  }
  
  this.confirmRebootServer = () => {
    basicKamehouseModal.setHtml(self.getRebootServerModalMessage());
    basicKamehouseModal.appendHtml(self.createRebootImg());
    basicKamehouseModal.open();
  }

  this.rebootServer = () => {
    if (self.isCommandRunning()) {
      return;
    }
    self.setCommandRunning();
    let hostOs = self.getHostOs();
    scriptExecutor.execute(hostOs + '/shutdown/reboot.sh', "", self.completeCommandCallback);
  }

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
    img.className = "img-btn-groot-app";
    img.alt = "Reboot";
    img.title = "Reboot";
    img.onclick = () => self.rebootServer();
    return img;
  }
}

function GitManager() {
  let self = this;

  this.pullAll = () => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let hostOs = serverManager.getHostOs();
    scriptExecutor.execute(hostOs + '/git/git-pull-all.sh', "", serverManager.completeCommandCallback);
  }

  this.pullAllAllServers = () => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    scriptExecutor.execute('kamehouse/git-pull-all-all-servers.sh', "", serverManager.completeCommandCallback);
  }
}

function TomcatManager() {
  let self = this;
  this.statusBallBlueImg = null;
  this.statusBallRedImg = null;
  this.statusBallGreenImg = null;

  this.init = function init() {
    self.statusBallBlueImg = self.createStatusBallBlueImg();
    self.statusBallRedImg = self.createStatusBallRedImg();
    self.statusBallGreenImg = self.createStatusBallGreenImg();
  }

  this.getAppsStatus = () => {
    scriptExecutor.execute('kamehouse/status-java-web-kamehouse.sh', "", self.displayAppsStatus, true);
  }

  this.getTomcatProcessStatus = () => {
    let hostOs = serverManager.getHostOs();
    scriptExecutor.execute(hostOs + '/kamehouse/tomcat-status.sh', "", self.displayTomcatProcessStatus, true);
  }

  this.displayAppsStatus = (scriptOutput) => {
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

  this.getModule = (webapp) => {
    if (webapp == "/kame-house") {
      return "ui";
    }
    return webapp.substring(12);
  }

  this.refreshTomcatView = () => {
    logger.info("Refreshing tomcat view");
    self.resetAllModulesStatus();
    self.getAppsStatus();
    self.getTomcatProcessStatus();
    moduleStatusManager.getAllModulesStatus();
    serverManager.completeCommandCallback();
  }

  this.resetModuleStatus = (module) => {
    $("#mst-" + module + "-status-val").html(self.statusBallBlueImg.cloneNode(true));
    $("#mst-" + module + "-build-version-val").text("N/A");
    $("#mst-" + module + "-build-date-val").text("N/A");
  }

  this.resetAllModulesStatus = () => {
    self.resetModuleStatus("admin");
    self.resetModuleStatus("media");
    self.resetModuleStatus("tennisworld");
    self.resetModuleStatus("testmodule");
    self.resetModuleStatus("ui");
    self.resetModuleStatus("vlcrc");
  }

  this.startModule = (module) => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-m " + module;
    scriptExecutor.execute('kamehouse/start-java-web-kamehouse.sh', args, self.refreshTomcatView);
  }

  this.stopModule = (module) => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-m " + module;
    scriptExecutor.execute('kamehouse/stop-java-web-kamehouse.sh', args, self.refreshTomcatView);
  }

  this.deployModule = (module) => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-f -m " + module;
    scriptExecutor.execute('kamehouse/deploy-java-web-kamehouse.sh', args, self.refreshTomcatView);
  }

  this.deployModuleAllServers = (module) => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-m " + module;
    scriptExecutor.execute('kamehouse/deploy-all-servers.sh', args, self.refreshTomcatView);
  }

  this.undeployModule = (module) => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-m " + module;
    scriptExecutor.execute('kamehouse/undeploy-java-web-kamehouse.sh', args, self.refreshTomcatView);
  }

  this.deployAllModules = () => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let args = "-f";
    scriptExecutor.execute('kamehouse/deploy-java-web-kamehouse.sh', args, self.refreshTomcatView);
  }

  this.deployAllModulesAllServers = () => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    scriptExecutor.execute('kamehouse/deploy-all-servers.sh', "", self.refreshTomcatView);
  }

  this.startTomcat = () => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    scriptExecutor.execute('kamehouse/tomcat-startup.sh', "", self.refreshTomcatView);
  }

  this.stopTomcat = () => {
    if (serverManager.isCommandRunning()) {
      return;
    }
    serverManager.setCommandRunning();
    serverManager.openExecutingCommandModal();
    let hostOs = serverManager.getHostOs();
    scriptExecutor.execute(hostOs + '/kamehouse/tomcat-stop.sh', "", self.refreshTomcatView);
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

function TailLogManagerWrapper() {
  let self = this;
  this.isTailLogRunning = false;

  this.stopImg = null;
  this.startImg = null;

  this.init = function init() {
    self.stopImg = self.createStopImg();
    self.startImg = self.createStartImg();
  }

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
    img.className = "img-btn-groot-app m-7-d-r-kh";
    img.alt = "Stop Tail Log";
    img.title = "Stop Tail Log";
    img.onclick = () => self.toggleTailLog();
    return img;
  }

  this.createStartImg = () => {
    let img = new Image();
    img.id = "toggle-tail-log-img";
    img.src = "/kame-house/img/mplayer/play-green.png";
    img.className = "img-btn-groot-app m-7-d-r-kh";
    img.alt = "Start Tail Log";
    img.title = "Start Tail Log";
    img.onclick = () => self.toggleTailLog();
    return img;
  }
}

window.onload = () => {
  main();
}