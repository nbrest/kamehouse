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
    this.getShell().execute(script, args, true, false, 600, 
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
    kameHouse.extension.serverManager.executeShellScript('kamehouse/deploy/exec-kamehouse-all-servers.sh', "-s common/git/git-pull-kamehouse.sh");
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
      this.hideUndeployedModules();
      this.getTomcatModulesStatus();
      this.getAllNonTomcatModulesStatus();
      this.getTomcatProcessStatus();
    });
  }

  /**
   * Hide undeployed kamehouse modules from the ui.
   */
  hideUndeployedModules() {
    kameHouse.logger.info("Hiding undeployed kamehouse modules", null);
    kameHouse.extension.kameHouseShell.execute('kamehouse/deploy/get-undeployed-kamehouse-modules.sh', "", false, false, 60, (kameHouseCommandResult) => this.#processUndeployedKameHouseModulesResponse(kameHouseCommandResult), () => {});
  }

  /**
   * Get running/stopped status from all tomcat modules.
   */
  getTomcatModulesStatus() {
    const scriptArgs = this.#getDevTomcatPortArgument();
    kameHouse.extension.kameHouseShell.execute('kamehouse/tomcat/kamehouse-tomcat-modules-status.sh', scriptArgs, false, false, 60, (kameHouseCommandResult) => this.#displayTomcatModulesRunningStatusImg(kameHouseCommandResult), () => {});
  }

  /**
   * Get status from all non tomcat modules.
   */
  getAllNonTomcatModulesStatus() {
    kameHouse.logger.debug("Getting all non tomcat modules status", null);
    this.getNonTomcatModulesStatus("cmd");
    this.getNonTomcatModulesStatus("desktop");
    this.getNonTomcatModulesStatus("groot");
    this.getNonTomcatModulesStatus("shell");
    this.getNonTomcatModulesStatus("snape");
    this.getNonTomcatModulesStatus("ui");
  }

  /**
   * Get status from a non tomcat module.
   */
  getNonTomcatModulesStatus(module) {
    kameHouse.logger.debug("Getting module " + module + " status", null);
    kameHouse.extension.kameHouseShell.execute('kamehouse/' + module + '/kamehouse-' + module + '-version.sh', "", false, false, 60, 
      (kameHouseCommandResult) => this.#displayNonTomcatModuleStatus(kameHouseCommandResult, module), 
      () => {
        kameHouse.util.dom.setHtmlById("mst-" + module + "-build-version-val", "Error getting data");  
        kameHouse.util.dom.setHtmlById("mst-" + module + "-build-date-val", "Error getting data");   
      });
  }

  /**
   * Get the tomcat process status.
   */
  getTomcatProcessStatus() {
    const hostOs = kameHouse.extension.serverManager.getHostOs();
    const args = this.#getDevTomcatPortArgument();
    kameHouse.extension.kameHouseShell.execute(hostOs + '/kamehouse/tomcat-status.sh', args, false, false, 60, (kameHouseCommandResult) => this.#displayTomcatProcessStatus(kameHouseCommandResult), () => {});
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
    this.getAllNonTomcatModulesStatus();
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
    kameHouse.extension.kameHouseShell.execute('kamehouse/tomcat/start-kamehouse.sh', args, false, false, 600, () => this.refreshServerView(), () => {});
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
    kameHouse.extension.kameHouseShell.execute('kamehouse/tomcat/stop-kamehouse.sh', args, false, false, 600, () => this.refreshServerView(), () => {});
  }  

  /**
   * Restart kamehouse desktop app.
   */
  restartDesktop() {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    kameHouse.extension.kameHouseShell.execute('kamehouse/desktop/kamehouse-desktop-restart.sh', null, false, false, 10, () => {
        kameHouse.extension.serverManager.completeCommandCallback();
      }, 
      () => {
        kameHouse.extension.serverManager.completeCommandCallback();
      });
  }

  /**
   * Stop kamehouse desktop app.
   */
  stopDesktop() {
    if (kameHouse.extension.serverManager.isCommandRunning()) {
      return;
    }
    kameHouse.extension.serverManager.setCommandRunning();
    kameHouse.extension.serverManager.openExecutingCommandModal();
    kameHouse.extension.kameHouseShell.execute('kamehouse/desktop/kamehouse-desktop-stop.sh', null, false, false, 600, () => {
        kameHouse.extension.serverManager.completeCommandCallback();
      }, 
      () => {
        kameHouse.extension.serverManager.completeCommandCallback();
      });
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
    let script = 'kamehouse/deploy/deploy-kamehouse.sh';
    let args = "-m " + module;
    if (this.#isDevEnvironment()) {
      script = 'kamehouse/deploy/deploy-kamehouse-dev.sh';
    }
    kameHouse.extension.kameHouseShell.execute(script, args, false, false, 600, () => this.refreshServerView(), () => {});
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
    kameHouse.extension.kameHouseShell.execute('kamehouse/deploy/undeploy-kamehouse.sh', args, false, false, 600, () => this.refreshServerView(), () => {});
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
    const args = "-s kamehouse/deploy/deploy-kamehouse.sh -a -m " + module;
    kameHouse.extension.kameHouseShell.execute('kamehouse/deploy/exec-kamehouse-all-servers.sh', args, false, false, 600, () => this.refreshServerView(), () => {});
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
    let script = 'kamehouse/deploy/deploy-kamehouse.sh';
    let args = "";
    if (this.#isDevEnvironment()) {
      script = 'kamehouse/deploy/deploy-kamehouse-dev.sh';
    }
    kameHouse.extension.kameHouseShell.execute(script, args, false, false, 600, () => this.refreshServerView(), () => {});
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
    kameHouse.extension.kameHouseShell.execute('kamehouse/deploy/exec-kamehouse-all-servers.sh', "-s kamehouse/deploy/deploy-kamehouse.sh", false, false, 600, () => this.refreshServerView(), () => {});
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
    let script = 'kamehouse/tomcat/tomcat-restart.sh';
    if (this.#isDevEnvironment()) {
      script = 'kamehouse/tomcat/tomcat-restart-dev.sh';
    }
    const stringArgs = this.#getRestartTomcatParams();
    kameHouse.extension.kameHouseShell.execute(script, stringArgs, false, false, 600, () => this.refreshServerView(), () => {});
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
   * Hide undeployed kamehouse modules from the ui.
   */
  #processUndeployedKameHouseModulesResponse(kameHouseCommandResult) {
    kameHouse.util.collapsibleDiv.resize("command-output-wrapper");
    const standardOutputHtml = kameHouseCommandResult.standardOutputHtml;
    if (kameHouse.core.isEmpty(standardOutputHtml)) {
      return;
    }
    standardOutputHtml.forEach((line) => {
      if (!kameHouse.core.isValidKameHouseConfig(line, "UNDEPLOYED_MODULES")) {
        return;
      }
      const modules = kameHouse.core.getKameHouseConfigValue(line);
      const modulesArray = modules.split(",");
      modulesArray.forEach((module) => {
        if (kameHouse.core.isEmpty(module)) {
          return;
        }
        kameHouse.logger.info("Hiding undeployed module " + module, null);
        const moduleRow = document.getElementById("mst-" + module);
        kameHouse.util.dom.classListAdd(moduleRow, "hidden-kh");
        if (module == "desktop") {
          const desktopStatusTable = document.getElementById("kamehouse-desktop-status-table");
          kameHouse.util.dom.classListAdd(desktopStatusTable, "hidden-kh");
        }
      });
    });
  }

  /**
   * Set tomcat modules running/stopped status images.
   */
  #displayTomcatModulesRunningStatusImg(kameHouseCommandResult) {
    kameHouse.util.collapsibleDiv.resize("command-output-wrapper");
    const standardOutputHtml = kameHouseCommandResult.standardOutputHtml;
    if (kameHouse.core.isEmpty(standardOutputHtml)) {
      return;
    }
    standardOutputHtml.forEach((line) => {
      if (line.startsWith("/kame-house")) {
        const lineArray = line.split(":");
        const webapp = lineArray[0];
        const status = lineArray[1];
        const module = this.#getModule(webapp);
        if (status == "running") {
          kameHouse.util.dom.setHtmlById("mst-" + module + "-status-val", kameHouse.util.dom.cloneNode(this.#statusBallGreenImg, true));
        } else if (status == "stopped") {
          kameHouse.util.dom.setHtmlById("mst-" + module + "-status-val", kameHouse.util.dom.cloneNode(this.#statusBallRedImg, true));
        } else {
          // unknown status
          kameHouse.util.dom.setHtmlById("mst-" + module + "-status-val", kameHouse.util.dom.cloneNode(this.#statusBallBlueImg, true));
        }        
      }
    });
  }

  /**
   * Render non tomcat module status.
   */
  #displayNonTomcatModuleStatus(kameHouseCommandResult, module) {
    const standardOutputHtml = kameHouseCommandResult.standardOutputHtml;
    if (kameHouse.core.isEmpty(standardOutputHtml)) {
      return;
    }
    standardOutputHtml.forEach((line) => {
      if (kameHouse.core.isEmpty(line)) {  
        return;
      }
      if (!line.includes("buildVersion") || !line.includes("buildDate")) {
        return;
      }
      const buildInfo = kameHouse.json.parse(line);
      kameHouse.logger.info("Loaded " + module + " buildInfo: " + line, null);
      if (!kameHouse.core.isEmpty(buildInfo.buildVersion)) {
        kameHouse.util.dom.setHtmlById("mst-" + module + "-build-version-val", buildInfo.buildVersion);
      }
      if (!kameHouse.core.isEmpty(buildInfo.buildDate)) {
        kameHouse.util.dom.setHtmlById("mst-" + module + "-build-date-val", buildInfo.buildDate);
      }
    });
  }

  /**
   * Render tomcat process status.
   */
  #displayTomcatProcessStatus(kameHouseCommandResult) {
    const tomcatProcessStatusDivId = "tomcat-process-status-val";
    const tomcatProcessStatusDiv = document.getElementById(tomcatProcessStatusDivId);
    kameHouse.util.dom.empty(tomcatProcessStatusDiv);
    const standardOutputHtml = kameHouseCommandResult.standardOutputHtml;
    if (kameHouse.core.isEmpty(standardOutputHtml)) {
      return;
    }
    standardOutputHtml.forEach((line) => {
      if (this.#isTomcatProcessOutputNoiseLine(line)) {
        return;
      }
      kameHouse.util.dom.append(tomcatProcessStatusDiv, line);
      kameHouse.util.dom.append(tomcatProcessStatusDiv, kameHouse.util.dom.getBr());
    });
    kameHouse.util.dom.remove(tomcatProcessStatusDiv.lastElementChild);
  }

  /**
   * Returns true for standard ouput noise lines for tomcat process status.
   */
  #isTomcatProcessOutputNoiseLine(line) {
    return kameHouse.core.isEmpty(line) ||
            line.includes("Started executing") ||
            line.includes("Finished executing") ||
            line.includes("Script start time: ") ||
            line.includes("Searching for tomcat process") ||
            line.includes("Not all processes could be identified, non-owned process info") ||
            line.includes("will not be shown, you would have to be root to see it all.") ||
            line.includes("Validating command line arguments") ||
            line.includes("Executing script");
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
      const isDaemon = false;
      this.getTailLogManager().tailLog(tailLogFile, numberOfLines, logLevel, executeOnDockerHost, isDaemon, () => {});
  
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