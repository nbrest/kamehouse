/**
 * Main object of the groot server manager page, to handle commands execution and some view updates.
 * 
 * - Each tomcat module's build date and version are pulled with the tomcat-module-status-manager.js tool
 * - The rest is done through this script.
 * 
 * @author nbrest
 */
class ServerManager {

  #isLinuxHost = false;
  #isLinuxDockerHost = false;
  #isDockerContainer = false;
  #dockerControlHost = false;
  #isCommandRunningFlag = false;

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Loading ServerManager");
    kameHouse.util.banner.setRandomAllBanner();
    this.loadStateFromCookies();
    kameHouse.util.module.waitForModules(["kameHouseGrootSession"], () => {
      this.#handleSessionStatus();
    });
  }

  /**
   * Load the current state from the cookies.
   */
  loadStateFromCookies() {
    kameHouse.util.tab.openTabFromCookies('kh-groot-server-manager', 'tab-deployment');
  }

  /**
   * Set command running.
   */
  setCommandRunning() {
    this.#isCommandRunningFlag = true;
  }

  /**
   * Set command not running.
   */
  setCommandNotRunning() {
    this.#isCommandRunningFlag = false;
  }

  /**
   * Callback to execute after every command finishes.
   */
  completeCommandCallback() {
    this.setCommandNotRunning();
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
  isCommandRunning() {
    if (this.#isCommandRunningFlag) {
      kameHouse.plugin.modal.basicModal.openAutoCloseable("There's a command already executing. Please wait and retry", 3000);
    }
    return this.#isCommandRunningFlag;
  }

  /**
   * Returns true if it's running inside a docker container.
   */
  isRunningInDockerContainer() {
    return this.#isDockerContainer;
  }

  /**
   * Open modal.
   */
  openExecutingCommandModal() {
    kameHouse.plugin.modal.loadingWheelModal.openAutoCloseable("Executing command. Check command output", 2000);
  }

  /**
   * Get host os.
   */
  getHostOs() {
    if (this.#isLinuxHost) {
      return "lin";
    } else {
      return "win";
    }
  }

  /**
   * Get the os to execute the command on, considering if it's running inside a docker container.
   */
  getExecutionOs() {
    if (this.#isDockerContainer && this.#dockerControlHost) {
      if (this.#isLinuxDockerHost) {
        return "lin";
      } else {
        return "win";
      }
    }
    return this.getHostOs();
  }

  /**
   * Open modal to confirm reboot.
   */
  confirmRebootServer() {
    kameHouse.plugin.modal.basicModal.setHtml(this.#getRebootServerModalMessage());
    kameHouse.plugin.modal.basicModal.appendHtml(this.#createRebootImg());
    kameHouse.plugin.modal.basicModal.open();
  }

  /**
   * Create all video playlists.
   */
  createAllVideoPlaylists() {
    if (this.isCommandRunning()) {
      return;
    }
    this.setCommandRunning();
    this.openExecutingCommandModal();
    const stringArgs = this.#getCreateVideoPlaylistsParams();
    kameHouse.extension.kameHouseShell.execute('win/video-playlists/create-all-video-playlists.sh', stringArgs, true, 600, 
      (scriptOutput) => this.completeCommandCallback(scriptOutput), 
      (scriptOutput) => this.completeCommandCallback(scriptOutput));
  }

  /**
   * Create all audio playlists.
   */
  createAllAudioPlaylists() {
    if (this.isCommandRunning()) {
      return;
    }
    this.setCommandRunning();
    this.openExecutingCommandModal();
    kameHouse.extension.kameHouseShell.execute('win/audio-playlists/create-all-audio-playlists.sh', "", true, 600, 
      (scriptOutput) => this.completeCommandCallback(scriptOutput), 
      (scriptOutput) => this.completeCommandCallback(scriptOutput));
  }  

  /**
   * Get the parameters for tomcat restart script.
   */
  #getCreateVideoPlaylistsParams() {
    const removeSpecialCharsCheckbox = document.getElementById("remove-special-chars-video-pls");
    if (removeSpecialCharsCheckbox.checked) {
      return "-s";
    } else {
      return "";
    }
  }

  /** Handle Session Status */
  #handleSessionStatus() {
    const sessionStatus = kameHouse.extension.groot.session;
    this.#isLinuxHost = sessionStatus.isLinuxHost;
    this.#isLinuxDockerHost = sessionStatus.isLinuxDockerHost;
    this.#isDockerContainer = sessionStatus.isDockerContainer;
    this.#dockerControlHost = sessionStatus.dockerControlHost;
    this.#updateServerName(sessionStatus);
  }
  
  /** Update server name */
  #updateServerName(sessionStatus) {
    if (!kameHouse.core.isEmpty(sessionStatus.server)) {
      kameHouse.util.dom.setHtml($("#banner-server-name"), sessionStatus.server);
    }
  }

  /**
   * Reboot the server.
   */
  #rebootServer() {
    kameHouse.plugin.modal.basicModal.close();
    if (this.isCommandRunning()) {
      return;
    }
    this.setCommandRunning();
    const hostOs = this.getExecutionOs();
    kameHouse.extension.kameHouseShell.execute(hostOs + '/shutdown/reboot.sh', "", true, 15, 
      (scriptOutput) => this.completeCommandCallback(scriptOutput), 
      (scriptOutput) => this.completeCommandCallback(scriptOutput));
  }

  /**
   * Get reboot server modal message.
   */
  #getRebootServerModalMessage() {
    const rebootModalMessage = kameHouse.util.dom.getSpan({}, "Are you sure you want to reboot the server? ");
    kameHouse.util.dom.append(rebootModalMessage, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(rebootModalMessage, kameHouse.util.dom.getBr());
    return rebootModalMessage;
  }

  /**
   * Create reboot clickable image.
   */
  #createRebootImg() {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/pc/shutdown-red.png",
      className: "img-btn-kh",
      alt: "Reboot",
      onClick: () => { this.#rebootServer() }
    });
  }

} // ServerManager

/**
 * Manager to execute git commands.
 * 
 * @author nbrest
 */
class GitManager {

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Loading GitManager");
    kameHouse.util.module.setModuleLoaded("gitManager");
  }

  /**
   * Pull kamehouse repo.
   */
  pullKameHouse() {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    kameHouse.extension.kameHouseShell.execute('common/git/git-pull-kamehouse.sh', "", true, 600, 
      (scriptOutput) => kameHouse.extension.serverManager.completeCommandCallback(scriptOutput), 
      (scriptOutput) => kameHouse.extension.serverManager.completeCommandCallback(scriptOutput));
  }

  /**
   * Pull all from all repos, in all servers.
   */
  pullKameHouseAllServers() {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    kameHouse.extension.kameHouseShell.execute('kamehouse/exec-script-all-servers.sh', "-s common/git/git-pull-kamehouse.sh", false, 600, 
      (scriptOutput) => kameHouse.extension.serverManager.completeCommandCallback(scriptOutput), 
      (scriptOutput) => kameHouse.extension.serverManager.completeCommandCallback(scriptOutput));
  }

} // GitManager

/**
 * Manager to execute deployment tasks.
 * 
 * @author nbrest
 */
class DeploymentManager {

  static #DEV_PORTS = ["9980", "9989", "9988", "9949", "9948"];
  static #ECLIPSE_PORTS = ["9988", "9948"];
  static #TOMCAT_DEV_PORT = "9980";
  static #TOMCAT_MODULES = ["admin", "media", "tennisworld", "testmodule", "ui", "vlcrc"];

  #statusBallBlueImg = null;
  #statusBallRedImg = null;
  #statusBallGreenImg = null;

  constructor() {
    this.#statusBallBlueImg = this.#createStatusBallBlueImg();
    this.#statusBallRedImg = this.#createStatusBallRedImg();
    this.#statusBallGreenImg = this.#createStatusBallGreenImg();
  }

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Loading DeploymentManager");
    kameHouse.util.module.waitForModules(["kameHouseShell", "kameHouseModal", "kameHouseDebugger", "kameHouseGrootSession"], () => {
      this.getTomcatModulesStatus();
      this.getNonTomcatModulesStatus();
      this.getTomcatProcessStatus();
    });
  }

  /**
   * Get status from all tomcat modules.
   */
  getTomcatModulesStatus() {
    const scriptArgs = this.#getDevTomcatPortArgument();
    kameHouse.extension.kameHouseShell.execute('kamehouse/status-kamehouse.sh', scriptArgs, false, 15, (scriptOutput) => this.#displayTomcatModulesStatus(scriptOutput), () => {});
  }

  /**
   * Get status from non tomcat modules.
   */
  getNonTomcatModulesStatus() {
    kameHouse.logger.debug("Getting non tomcat modules status");
    kameHouse.extension.kameHouseShell.execute('kamehouse/kamehouse-cmd-version.sh', "", false, 15, (scriptOutput) => this.#displayModuleCmdStatus(scriptOutput), () => {});
    kameHouse.extension.kameHouseShell.execute('kamehouse/kamehouse-groot-version.sh', "", false, 15, (scriptOutput) => this.#displayModuleGrootStatus(scriptOutput), () => {});
    kameHouse.extension.kameHouseShell.execute('kamehouse/kamehouse-shell-version.sh', "", false, 15, (scriptOutput) => this.#displayModuleShellStatus(scriptOutput), () => {});
  }

  /**
   * Get the tomcat process status.
   */
  getTomcatProcessStatus() {
    const hostOs = kameHouse.extension.serverManager.getHostOs();
    const args = this.#getDevTomcatPortArgument();
    kameHouse.extension.kameHouseShell.execute(hostOs + '/kamehouse/tomcat-status.sh', args, false, 15, (scriptOutput) => this.#displayTomcatProcessStatus(scriptOutput), () => {});
  }
  
  /**
   * Refresh the server view.
   */
  refreshServerView() {
    kameHouse.logger.info("Refreshing server view");
    this.#resetAllModulesStatus();
    this.getTomcatModulesStatus();
    this.getTomcatProcessStatus();
    kameHouse.extension.tomcatModuleStatusManager.getAllModulesStatus();
    this.getNonTomcatModulesStatus();
    kameHouse.extension.serverManager.completeCommandCallback();
  }
  
  /**
   * Start tomcat module.
   */
  startModule(module) {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    const args = "-m " + module + " " + this.#getDevTomcatPortArgument();
    kameHouse.extension.kameHouseShell.execute('kamehouse/start-kamehouse.sh', args, false, 600, (scriptOutput) => this.refreshServerView(scriptOutput), () => {});
  }

  /**
   * Stop tomcat module.
   */
  stopModule(module) {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    const args = "-m " + module + " " + this.#getDevTomcatPortArgument();
    kameHouse.extension.kameHouseShell.execute('kamehouse/stop-kamehouse.sh', args, false, 600, (scriptOutput) => this.refreshServerView(scriptOutput), () => {});
  }  

  /**
   * Deploy module.
   */
  deployModule(module) {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    let script = 'kamehouse/deploy-kamehouse.sh';
    if (this.#isDevEnvironment()) {
      script = 'kamehouse/deploy-kamehouse-dev.sh';
    }
    let args = "-m " + module;
    if (this.#isEclipseEnvironment()) {
      args = args + " -i eclipse";
    }
    kameHouse.extension.kameHouseShell.execute(script, args, false, 600, (scriptOutput) => this.refreshServerView(scriptOutput), () => {});
  }

  /**
   * Undeploy module.
   */
  undeployModule(module) {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    const args = "-m " + module + " " + this.#getDevTomcatPortArgument();
    kameHouse.extension.kameHouseShell.execute('kamehouse/undeploy-kamehouse.sh', args, false, 600, (scriptOutput) => this.refreshServerView(scriptOutput), () => {});
  }

  /**
   * Deploy module in all servers.
   */
  deployModuleAllServers(module) {
    const SPACE = "EXEC_SCRIPT_ALL_SERVERS_ARG_SPACE";
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    const args = "-s kamehouse/deploy-kamehouse.sh -a -m" + SPACE + module;
    kameHouse.extension.kameHouseShell.execute('kamehouse/exec-script-all-servers.sh', args, false, 600, (scriptOutput) => this.refreshServerView(scriptOutput), () => {});
  }

  /**
   * Deploy all modules in the local server.
   */
  deployAllModules() {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    let script = 'kamehouse/deploy-kamehouse.sh';
    if (this.#isDevEnvironment()) {
      script = 'kamehouse/deploy-kamehouse-dev.sh';
    }
    let args = "";
    if (this.#isEclipseEnvironment()) {
      args = args + " -i eclipse";
    }
    kameHouse.extension.kameHouseShell.execute(script, args, false, 600, (scriptOutput) => this.refreshServerView(scriptOutput), () => {});
  }

  /**
   * Deploy all modules in all servers.
   */
  deployAllModulesAllServers() {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    kameHouse.extension.kameHouseShell.execute('kamehouse/exec-script-all-servers.sh', "-s kamehouse/deploy-kamehouse.sh", false, 600, (scriptOutput) => this.refreshServerView(scriptOutput), () => {});
  }

  /**
   * Restart tomcat.
   */
  restartTomcat() {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    let script = 'kamehouse/tomcat-restart.sh';
    if (this.#isDevEnvironment()) {
      script = 'kamehouse/tomcat-restart-dev.sh';
    }
    const stringArgs = this.#getRestartTomcatParams();
    kameHouse.extension.kameHouseShell.execute(script, stringArgs, false, 600, (scriptOutput) => this.refreshServerView(scriptOutput), () => {});
  }

  /**
   * Check if the file system switched to read only.
   */
  isReadOnlyFs() {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    const script = 'is-read-only-fs.sh';
    kameHouse.extension.kameHouseShell.execute(script, null, false, 600, (scriptOutput) => kameHouse.extension.serverManager.completeCommandCallback(), () => {});
  }

  /**
   * Check if it's eclipse dev environment.
   */
  #isEclipseEnvironment() {
    return kameHouse.util.mobile.exec(
      () => {
        const port = location.port;
        if (!kameHouse.core.isEmpty(port) && DeploymentManager.#ECLIPSE_PORTS.includes(port)) {
          return true;
        }
        return false;
      },
      () => {
        const selectedBackend = kameHouse.extension.mobile.core.getSelectedBackendServer();
        if (kameHouse.core.isEmpty(selectedBackend) || kameHouse.core.isEmpty(selectedBackend.name)) {
          kameHouse.logger.warn("Selected backend name is empty");
          return false;
        }
        if (selectedBackend.name == "Dev Eclipse") {
          return true;
        }
        return false;
      }
    );
  }

  /**
   * Check if it's dev environment.
   */
  #isDevEnvironment() {
    return kameHouse.util.mobile.exec(
      () => {
        const port = location.port;
        return !kameHouse.core.isEmpty(port) && DeploymentManager.#DEV_PORTS.includes(port);
      },
      () => {
        kameHouse.logger.trace("Checking if it's dev environment on mobile");
        const selectedBackend = kameHouse.extension.mobile.core.getSelectedBackendServer();
        if (kameHouse.core.isEmpty(selectedBackend) || kameHouse.core.isEmpty(selectedBackend.name)) {
          kameHouse.logger.warn("Selected backend name is empty");
          return false;
        }
        if (selectedBackend.name == "Dev Intellij" 
              || selectedBackend.name == "Dev Eclipse"
              || selectedBackend.name == "Dev Tomcat HTTP") {
          return true;
        }
        return false;
      }
    );
  }

  /**
   * Get dev tomcat port argument.
   */
  #getDevTomcatPortArgument() {
    if (this.#isDevEnvironment()) {
      return "-p " + DeploymentManager.#TOMCAT_DEV_PORT;
    }
    return "";
  }

  /**
   * Render tomcat modules status.
   */
  #displayTomcatModulesStatus(scriptOutput) {
    kameHouse.util.collapsibleDiv.refreshCollapsibleDiv();
    scriptOutput.htmlConsoleOutput.forEach((scriptOutputLine) => {
      if (scriptOutputLine.startsWith("/kame-house")) {
        const scriptOutputLineArray = scriptOutputLine.split(":");
        const webapp = scriptOutputLineArray[0];
        const status = scriptOutputLineArray[1];
        const module = this.#getModule(webapp);
        if (status == "running") {
          kameHouse.util.dom.setHtml($("#mst-" + module + "-status-val"), kameHouse.util.dom.cloneNode(this.#statusBallGreenImg, true));
        } else if (status == "stopped") {
          kameHouse.util.dom.setHtml($("#mst-" + module + "-status-val"), kameHouse.util.dom.cloneNode(this.#statusBallRedImg, true));
        } else {
          kameHouse.util.dom.setHtml($("#mst-" + module + "-status-val"), kameHouse.util.dom.cloneNode(this.#statusBallBlueImg, true));
        }        
      }
    });
  }

  /**
   * Render cmd module status.
   */
  #displayModuleCmdStatus(scriptOutput) {
    this.#displayNonTomcatModuleStatus(scriptOutput, "cmd");
  }

  /**
   * Render groot module status.
   */
  #displayModuleGrootStatus(scriptOutput) {
    this.#displayNonTomcatModuleStatus(scriptOutput, "groot");
  }

  /**
   * Render shell module status.
   */
  #displayModuleShellStatus(scriptOutput) {
    this.#displayNonTomcatModuleStatus(scriptOutput, "shell");
  }

  /**
   * Render non tomcat module status.
   */
  #displayNonTomcatModuleStatus(scriptOutput, module) {
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
  #displayTomcatProcessStatus(scriptOutput) {
    const tomcatProcessStatusDiv = "#tomcat-process-status-val";
    kameHouse.util.dom.empty($(tomcatProcessStatusDiv));
    scriptOutput.htmlConsoleOutput.forEach((scriptOutputLine) => {
      if (!scriptOutputLine.includes("Started executing") && 
          !scriptOutputLine.includes("Finished executing") &&
          !scriptOutputLine.includes(" (rt in secs: ") &&
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
  #getModule(webapp) {
    if (webapp == "/kame-house") {
      return "ui";
    }
    return webapp.substring(12);
  }

  /**
   * Reset view of module status.
   */
  #resetModuleStatus(module) {
    kameHouse.util.dom.setHtml($("#mst-" + module + "-status-val"), kameHouse.util.dom.cloneNode(this.#statusBallBlueImg, true));
    kameHouse.util.dom.setHtml($("#mst-" + module + "-build-version-val"), "N/A");
    kameHouse.util.dom.setHtml($("#mst-" + module + "-build-date-val"), "N/A");
  }

  /**
   * Reset view of all tomcat modules.
   */
  #resetAllModulesStatus() {
    this.#resetModuleStatus("admin");
    this.#resetModuleStatus("media");
    this.#resetModuleStatus("tennisworld");
    this.#resetModuleStatus("testmodule");
    this.#resetModuleStatus("ui");
    this.#resetModuleStatus("vlcrc");
  }

  /**
   * Get the parameters for tomcat restart script.
   */
  #getRestartTomcatParams() {
    const tomcatDebugModeCheckbox = document.getElementById("tomcat-debug-mode");
    if (tomcatDebugModeCheckbox.checked) {
      return "-d";
    } else {
      return "";
    }
  }

  /**
   * Create red status ball image.
   */
  #createStatusBallRedImg() {
    return this.#getStatusBallImg("red");
  }

  /**
   * Create green status ball image.
   */
  #createStatusBallGreenImg() {
    return this.#getStatusBallImg("green");
  }  

  /**
   * Create blue status ball image.
   */
  #createStatusBallBlueImg() {
    return this.#getStatusBallImg("blue");
  }

  /**
   * Get status ball image.
   */
  #getStatusBallImg(color) {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/other/ball-" + color + ".png",
      className: "img-tomcat-manager-status",
      alt: "Status"
    });
  }

} // DeploymentManager

/**
 * Manager to execute tail log commands.
 * 
 * @author nbrest
 */
class TailLogManagerWrapper {

  #stopImg = null;
  #startImg = null;
  #isTailLogRunning = false;
  #tailLogCount = 0;
  #resume = false;

  constructor() {
    this.#stopImg = this.#createStopImg();
    this.#startImg = this.#createStartImg();
  }

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Loading TailLogManagerWrapper");
    kameHouse.util.module.setModuleLoaded("tailLogManagerWrapper");
    kameHouse.util.mobile.setMobileEventListeners(() => {this.#pauseTailLog()}, () => {this.#resumeTailLog()});
  }

  /**
   * Toggle start and stop tailing log.
   */
  toggleTailLog() {
    if (this.#isTailLogRunning) {
      this.#isTailLogRunning = false;
      kameHouse.plugin.modal.loadingWheelModal.open("Stopping tail log...");
      return;
    }
    this.#tailLog();
  }

  /**
   * Pause tail log.
   */
  #pauseTailLog() {
    if (this.#isTailLogRunning) {
      kameHouse.logger.info("KameHouse sent to background. Pausing tail logs");
      this.#resume = true;
    } else {
      kameHouse.logger.info("KameHouse sent to background. Tail log not running");
    }
    this.#isTailLogRunning = false;
  }

  /**
   * Resume tail log.
   */
  #resumeTailLog() {
    if (this.#resume) {
      kameHouse.logger.info("KameHouse sent to foreground. Resuming tail logs");
      this.#resume = false;
      this.#tailLog();
    } else {
      kameHouse.logger.info("KameHouse sent to foreground. Tail log not running");
    }
  }

  /**
   * Tail the log selected in the ui.
   */
  async #tailLog() {
    if (this.#isTailLogRunning || this.#tailLogCount > 1) {
      const message = "tail log is already running";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
      return;
    }
    kameHouse.logger.info("Started tailLog loop");
    kameHouse.util.dom.replaceWith($("#toggle-tail-log-img"), this.#stopImg);
    this.#tailLogCount++;
    this.#isTailLogRunning = true;
    while (this.#isTailLogRunning) {
      kameHouse.logger.trace("tailLog loop running. tailLogCount: " + this.#tailLogCount);
      let tailLogFile = document.getElementById("tail-log-dropdown").value;
      let numberOfLines = document.getElementById("tail-log-num-lines-dropdown").value;
      let logLevel = document.getElementById("tail-log-level-dropdown").value;
      let executeOnDockerHost = this.#getExecuteOnDockerHost(tailLogFile);
      kameHouse.extension.tailLogManager.tailLog(tailLogFile, numberOfLines, logLevel, executeOnDockerHost, (responseBody) => kameHouse.util.collapsibleDiv.refreshCollapsibleDiv());
  
      await kameHouse.core.sleep(5000);
      if (this.#tailLogCount > 1) {
        kameHouse.logger.info("tailLog loop: Running multiple tailLog, exiting this loop");
        break;
      }
    }
    this.#tailLogCount--;
    if (this.#tailLogCount == 0) {
      kameHouse.util.dom.replaceWith($("#toggle-tail-log-img"), this.#startImg);
    }
    kameHouse.logger.info("Finished tailLog loop");
    kameHouse.plugin.modal.loadingWheelModal.close();
  }

  /**
   * Get the value of execute on docker host for the specified script.
   */
  #getExecuteOnDockerHost(tailLogFile) {
    return tailLogFile == "create-all-video-playlists.log" ||
    tailLogFile == "create-all-audio-playlists.log" || 
    tailLogFile == "git-pull-all.log";
  }

  /**
   * Create start tail log image.
   */
  #createStartImg() {
    return kameHouse.util.dom.getImgBtn({
      id: "toggle-tail-log-img",
      src: "/kame-house/img/mplayer/play-circle-green.png",
      className: "link-image-img",
      alt: "Start Tail Log",
      onClick: () => this.toggleTailLog()
    });
  }

  /**
   * Create stop tail log image.
   */
  #createStopImg() {
    return kameHouse.util.dom.getImgBtn({
      id: "toggle-tail-log-img",
      src: "/kame-house/img/other/stop-red-dark.png",
      className: "link-image-img",
      alt: "Stop Tail Log",
      onClick: () => this.toggleTailLog()
    });
  }
  
} // TailLogManagerWrapper

$(document).ready(() => {
  kameHouse.addExtension("serverManager", new ServerManager());
  kameHouse.addExtension("gitManager", new GitManager());
  kameHouse.addExtension("deploymentManager", new DeploymentManager());
  kameHouse.addExtension("tailLogManagerWrapper", new TailLogManagerWrapper());
});