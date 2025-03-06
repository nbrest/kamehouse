/**
 * Main object of the groot server manager page, to handle commands execution and some view updates.
 * 
 * - Each tomcat module's build date and version are pulled with the tomcat-module-status-manager.js tool
 * - The rest is done through this script.
 * 
 * @author nbrest
 */
class GrootServerManager {

  #isLinuxHost = false;
  #isLinuxDockerHost = false;
  #isDockerContainer = false;
  #dockerControlHost = false;
  #isCommandRunningFlag = false;

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Loading ServerManager", null);
    this.setBanners();
    this.loadStateFromCookies();
    kameHouse.util.module.waitForModules(["kameHouseGrootSession"], () => {
      this.#handleSessionStatus();
      kameHouse.util.collapsibleDiv.resize("tail-log-output-wrapper");
      kameHouse.util.collapsibleDiv.resize("command-output-wrapper");
    });
  }

  /**
   * Load the current state from the cookies.
   */
  loadStateFromCookies() {
    kameHouse.util.tab.openTabFromCookies('kh-groot-server-manager', 'tab-deployment');
  }

  /**
   * Get shell to execute shell scripts.
   */
  getShell() {
    return kameHouse.extension.kameHouseShell;
  }

  /**
   * Set random banners.
   */
  setBanners() {
    kameHouse.util.banner.setRandomAllBanner(null);
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
    kameHouse.util.collapsibleDiv.resize("command-output-wrapper");
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
    kameHouse.plugin.modal.basicModal.appendHtml(this.#createRebootButton());
    kameHouse.plugin.modal.basicModal.open();
  }

  /**
   * Execute shell script.
   */
  executeShellScript(script, args) {
    if (this.isCommandRunning()) {
      return;
    }
    this.setCommandRunning();
    this.openExecutingCommandModal();
    this.getShell().execute(script, args, true, 600, 
      () => this.completeCommandCallback(), 
      () => this.completeCommandCallback());
  }

  /**
   * Check the status of docker containers.
   */
  dockerStatus() {
    this.executeShellScript('kamehouse/docker/docker-status-kamehouse.sh', "");
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
      kameHouse.util.dom.setHtmlById("banner-server-name", sessionStatus.server);
    }
  }

  /**
   * Reboot the server.
   */
  #rebootServer() {
    kameHouse.plugin.modal.basicModal.close();
    const hostOs = this.getExecutionOs();
    this.executeShellScript(hostOs + '/shutdown/reboot.sh', "");
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
   * Create reboot button.
   */
  #createRebootButton() {
    return kameHouse.util.dom.getButton({
      attr: {
        class: "img-btn-kh"
      },
      mobileClass: null,
      backgroundImg: "/kame-house/img/pc/shutdown-red.png",
      html: null,
      data: null,
      click: (event, data) => this.#rebootServer()
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
    kameHouse.logger.info("Loading GitManager", null);
    kameHouse.util.module.setModuleLoaded("gitManager");
  }

  /**
   * Pull kamehouse repo.
   */
  pullKameHouse() {
    kameHouse.extension.serverManager.executeShellScript('common/git/git-pull-kamehouse.sh', "");
  }

  /**
   * Pull kamehouse in all servers.
   */
  pullKameHouseAllServers() {
    kameHouse.extension.serverManager.executeShellScript('kamehouse/exec-kamehouse-all-servers.sh', "-s common/git/git-pull-kamehouse.sh");
  }

} // GitManager

/**
 * Manager to execute deployment tasks.
 * 
 * @author nbrest
 */
class DeploymentManager {

  #DEV_PORTS = ["9980", "9989", "9949"];
  #TOMCAT_DEV_PORT = "9980";

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
    kameHouse.logger.info("Loading DeploymentManager", null);
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
    kameHouse.extension.kameHouseShell.execute('kamehouse/status-kamehouse.sh', scriptArgs, false, 60, (scriptOutput) => this.#displayTomcatModulesStatus(scriptOutput), () => {});
  }

  /**
   * Get status from non tomcat modules.
   */
  getNonTomcatModulesStatus() {
    kameHouse.logger.debug("Getting non tomcat modules status", null);
    kameHouse.extension.kameHouseShell.execute('kamehouse/kamehouse-cmd-version.sh', "", false, 60, 
      (scriptOutput) => this.#displayModuleCmdStatus(scriptOutput), 
      () => {
        kameHouse.util.dom.setHtmlById("mst-cmd-build-version-val", "Error getting data");  
        kameHouse.util.dom.setHtmlById("mst-cmd-build-date-val", "Error getting data");   
      });
    kameHouse.extension.kameHouseShell.execute('kamehouse/kamehouse-groot-version.sh', "", false, 60, 
      (scriptOutput) => this.#displayModuleGrootStatus(scriptOutput), 
      () => {
        kameHouse.util.dom.setHtmlById("mst-groot-build-version-val", "Error getting data");   
        kameHouse.util.dom.setHtmlById("mst-groot-build-date-val", "Error getting data");   
      });
    kameHouse.extension.kameHouseShell.execute('kamehouse/kamehouse-shell-version.sh', "", false, 60, 
      (scriptOutput) => this.#displayModuleShellStatus(scriptOutput), 
      () => {
        kameHouse.util.dom.setHtmlById("mst-shell-build-version-val", "Error getting data");
        kameHouse.util.dom.setHtmlById("mst-shell-build-date-val", "Error getting data");   
      });
  }

  /**
   * Get the tomcat process status.
   */
  getTomcatProcessStatus() {
    const hostOs = kameHouse.extension.serverManager.getHostOs();
    const args = this.#getDevTomcatPortArgument();
    kameHouse.extension.kameHouseShell.execute(hostOs + '/kamehouse/tomcat-status.sh', args, false, 60, (scriptOutput) => this.#displayTomcatProcessStatus(scriptOutput), () => {});
  }
  
  /**
   * Refresh the server view.
   */
  refreshServerView() {
    kameHouse.logger.info("Refreshing server view", null);
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
    kameHouse.extension.kameHouseShell.execute('kamehouse/start-kamehouse.sh', args, false, 600, () => this.refreshServerView(), () => {});
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
    kameHouse.extension.kameHouseShell.execute('kamehouse/stop-kamehouse.sh', args, false, 600, () => this.refreshServerView(), () => {});
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
    let args = "-m " + module;
    if (this.#isDevEnvironment()) {
      script = 'kamehouse/deploy-kamehouse-dev.sh';
    }
    kameHouse.extension.kameHouseShell.execute(script, args, false, 600, () => this.refreshServerView(), () => {});
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
    kameHouse.extension.kameHouseShell.execute('kamehouse/undeploy-kamehouse.sh', args, false, 600, () => this.refreshServerView(), () => {});
  }

  /**
   * Deploy module in all servers.
   */
  deployModuleAllServers(module) {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    const args = "-s kamehouse/deploy-kamehouse.sh -a -m " + module;
    kameHouse.extension.kameHouseShell.execute('kamehouse/exec-kamehouse-all-servers.sh', args, false, 600, () => this.refreshServerView(), () => {});
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
    let args = "";
    if (this.#isDevEnvironment()) {
      script = 'kamehouse/deploy-kamehouse-dev.sh';
    }
    kameHouse.extension.kameHouseShell.execute(script, args, false, 600, () => this.refreshServerView(), () => {});
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
    kameHouse.extension.kameHouseShell.execute('kamehouse/exec-kamehouse-all-servers.sh', "-s kamehouse/deploy-kamehouse.sh", false, 600, () => this.refreshServerView(), () => {});
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
    kameHouse.extension.kameHouseShell.execute(script, stringArgs, false, 600, () => this.refreshServerView(), () => {});
  }

  /**
   * Check if it's dev environment.
   */
  #isDevEnvironment() {
    return kameHouse.util.mobile.exec(
      () => {
        const port = location.port;
        return !kameHouse.core.isEmpty(port) && this.#DEV_PORTS.includes(port);
      },
      () => {
        kameHouse.logger.trace("Checking if it's dev environment on mobile", null);
        const selectedBackend = kameHouse.extension.mobile.core.getSelectedBackendServer();
        if (kameHouse.core.isEmpty(selectedBackend) || kameHouse.core.isEmpty(selectedBackend.name)) {
          kameHouse.logger.warn("Selected backend name is empty", null);
          return false;
        }
        if (selectedBackend.name == "Dev Apache" 
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
      return "-p " + this.#TOMCAT_DEV_PORT;
    }
    return "";
  }

  /**
   * Render tomcat modules status.
   */
  #displayTomcatModulesStatus(scriptOutput) {
    kameHouse.util.collapsibleDiv.resize("command-output-wrapper");
    scriptOutput.htmlConsoleOutput.forEach((scriptOutputLine) => {
      if (scriptOutputLine.startsWith("/kame-house")) {
        const scriptOutputLineArray = scriptOutputLine.split(":");
        const webapp = scriptOutputLineArray[0];
        const status = scriptOutputLineArray[1];
        const module = this.#getModule(webapp);
        if (status == "running") {
          kameHouse.util.dom.setHtmlById("mst-" + module + "-status-val", kameHouse.util.dom.cloneNode(this.#statusBallGreenImg, true));
        } else if (status == "stopped") {
          kameHouse.util.dom.setHtmlById("mst-" + module + "-status-val", kameHouse.util.dom.cloneNode(this.#statusBallRedImg, true));
        } else {
          kameHouse.util.dom.setHtmlById("mst-" + module + "-status-val", kameHouse.util.dom.cloneNode(this.#statusBallBlueImg, true));
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
        kameHouse.util.dom.setHtmlById("mst-" + module + "-build-version-val", buildVersion);    
      }
      if (scriptOutputLine.startsWith("buildDate")) {
        const scriptOutputLineArray = scriptOutputLine.split("=");
        const buildDate = scriptOutputLineArray[1];
        kameHouse.util.dom.setHtmlById("mst-" + module + "-build-date-val", buildDate);    
      }
    });
  }

  /**
   * Render tomcat process status.
   */
  #displayTomcatProcessStatus(scriptOutput) {
    const tomcatProcessStatusDivId = "tomcat-process-status-val";
    const tomcatProcessStatusDiv = document.getElementById(tomcatProcessStatusDivId);
    kameHouse.util.dom.empty(tomcatProcessStatusDiv);
    scriptOutput.htmlConsoleOutput.forEach((scriptOutputLine) => {
      if (!scriptOutputLine.includes("Started executing") && 
          !scriptOutputLine.includes("Finished executing") &&
          !scriptOutputLine.includes("Script start time: ") &&
          !scriptOutputLine.includes("Searching for tomcat process") &&
          !scriptOutputLine.includes("TCP") &&
          !scriptOutputLine.includes("tcp") &&
          !scriptOutputLine.includes("Executing script")) {
        kameHouse.util.dom.append(tomcatProcessStatusDiv, scriptOutputLine);
        kameHouse.util.dom.append(tomcatProcessStatusDiv, kameHouse.util.dom.getBr());
      }
    });
    kameHouse.util.dom.remove(tomcatProcessStatusDiv.lastElementChild);
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
    kameHouse.util.dom.setHtmlById("mst-" + module + "-status-val", kameHouse.util.dom.cloneNode(this.#statusBallBlueImg, true));
    kameHouse.util.dom.setHtmlById("mst-" + module + "-build-version-val", "N/A");
    kameHouse.util.dom.setHtmlById("mst-" + module + "-build-date-val", "N/A");
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
    const tomcatDebugModeCheckbox = document.getElementById("tomcat-debug-mode") as HTMLInputElement;
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
    return kameHouse.util.dom.getImg({
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

  #TAIL_LOG_REFRESH_WAIT_MS = 5000;

  #stopButton = null;
  #startButton = null;
  #isTailLogRunning = false;
  #tailLogCount = 0;
  #resume = false;

  constructor() {
    this.#stopButton = this.#createStopButton();
    this.#startButton = this.#createStartButton();
  }

  /**
   * Get tail log manager.
   */
  getTailLogManager() {
    return kameHouse.extension.tailLogManager;
  }

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Loading TailLogManagerWrapper", null);
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
      kameHouse.logger.info("KameHouse sent to background. Pausing tail logs", null);
      this.#resume = true;
    } else {
      kameHouse.logger.info("KameHouse sent to background. Tail log not running", null);
    }
    this.#isTailLogRunning = false;
  }

  /**
   * Resume tail log.
   */
  #resumeTailLog() {
    if (this.#resume) {
      kameHouse.logger.info("KameHouse sent to foreground. Resuming tail logs", null);
      this.#resume = false;
      this.#tailLog();
    } else {
      kameHouse.logger.info("KameHouse sent to foreground. Tail log not running", null);
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
    kameHouse.logger.info("Started tailLog loop", null);
    kameHouse.util.dom.replaceWithById("toggle-tail-log-btn", this.#stopButton);
    this.#tailLogCount++;
    this.#isTailLogRunning = true;
    while (this.#isTailLogRunning) {
      kameHouse.logger.trace("tailLog loop running. tailLogCount: " + this.#tailLogCount, null);
      let tailLogFile = (document.getElementById("tail-log-dropdown") as HTMLSelectElement).value;
      let numberOfLines = (document.getElementById("tail-log-num-lines-dropdown") as HTMLSelectElement).value;
      let logLevel = (document.getElementById("tail-log-level-dropdown") as HTMLSelectElement).value;
      let executeOnDockerHost = this.#getExecuteOnDockerHost(tailLogFile);
      this.getTailLogManager().tailLog(tailLogFile, numberOfLines, logLevel, executeOnDockerHost, () => {});
  
      await kameHouse.core.sleep(this.#TAIL_LOG_REFRESH_WAIT_MS);
      if (this.#tailLogCount > 1) {
        kameHouse.logger.info("tailLog loop: Running multiple tailLog, exiting this loop", null);
        break;
      }
    }
    this.#tailLogCount--;
    if (this.#tailLogCount == 0) {
      kameHouse.util.dom.replaceWithById("toggle-tail-log-btn", this.#startButton);
    }
    kameHouse.logger.info("Finished tailLog loop", null);
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
   * Create start tail log button.
   */
  #createStartButton() {
    return kameHouse.util.dom.getButton({
      attr: {
        id: "toggle-tail-log-btn",
        class: "link-image-img"
      },
      mobileClass: "link-image-img-mobile",
      backgroundImg: "/kame-house/img/mplayer/play-circle-green.png",
      html: null,
      data: null,
      click: (event, data) => this.toggleTailLog()
    });
  }

  /**
   * Create stop tail log button.
   */
  #createStopButton() {
    return kameHouse.util.dom.getButton({
      attr: {
        id: "toggle-tail-log-btn",
        class: "link-image-img"
      },
      mobileClass: "link-image-img-mobile",
      backgroundImg: "/kame-house/img/other/stop-red-dark.png",
      html: null,
      data: null,
      click: (event, data) => this.toggleTailLog()
    });
  }
  
} // TailLogManagerWrapper

kameHouse.ready(() => {
  kameHouse.addExtension("serverManager", new GrootServerManager());
  kameHouse.addExtension("gitManager", new GitManager());
  kameHouse.addExtension("deploymentManager", new DeploymentManager());
  kameHouse.addExtension("tailLogManagerWrapper", new TailLogManagerWrapper());
});