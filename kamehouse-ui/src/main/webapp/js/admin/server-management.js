/**
 * Admin Server Management functions.
 * 
 * Dependencies: logger, debuggerHttpClient.
 * 
 * @author nbrest
 */
var serverManager;

var main = () => {
  bannerUtils.setRandomAllBanner();
  importServerManagementCss();
  moduleUtils.waitForModules(["logger", "debuggerHttpClient"], () => {
    logger.info("Started initializing server management");
    serverManager = new ServerManager();
    serverManager.getSuspendStatus(false);
    serverManager.getShutdownStatus(false);
    serverManager.getHttpdStatus(false);
  });
};

function importServerManagementCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/server-management.css">');
}

/**
 * Manager to execute the admin commands in the current server.
 */
function ServerManager() {
  let self = this;
  const ADMIN_API_URL = "/kame-house-admin/api/v1/admin";
  const SUSPEND_URL = '/power-management/suspend';
  const SHUTDOWN_URL = '/power-management/shutdown';
  const REBOOT_URL = '/power-management/reboot';
  const UPTIME_URL = '/system-state/uptime';
  const FREE_URL = '/system-state/free';
  const DF_URL = '/system-state/df';
  const HTTPD_URL = '/system-state/httpd';

  /**
   * --------------------------------------------------------------------------
   * WakeOnLan functions
   */
  this.execAdminWakeOnLan = (url, server) => {
    let requestParam = "server=" + server;
    loadingWheelModal.open();
    debuggerHttpClient.postUrlEncoded(ADMIN_API_URL + url, requestParam, processSuccess, processError);
  }

  /**
   * --------------------------------------------------------------------------
   * SHUTDOWN functions
   */
  /** Set a Shutdown command */
  this.setShutdownCommand = () => {
    let shutdownDelay = document.getElementById("shutdown-delay-dropdown").value;
    logger.trace("Shutdown delay: " + shutdownDelay);
    let requestParam = "delay=" + shutdownDelay;
    loadingWheelModal.open();
    debuggerHttpClient.postUrlEncoded(ADMIN_API_URL + SHUTDOWN_URL, requestParam, processSuccessShutdown, processErrorShutdown);
  }

  /** Cancel a Shutdown command */
  this.cancelShutdownCommand = () => {
    loadingWheelModal.open();
    debuggerHttpClient.delete(ADMIN_API_URL + SHUTDOWN_URL, null, processSuccessShutdown, processErrorShutdown);
  }

  /** Get the Shutdown command status */
  this.getShutdownStatus = (openModal) => {
    if (openModal) {
      loadingWheelModal.open();
    }
    debuggerHttpClient.get(ADMIN_API_URL + SHUTDOWN_URL, processSuccessShutdownStatus, processErrorShutdownStatus);
  }

  /** Process the success response of a Shutdown command (set/cancel) */
  function processSuccessShutdown(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    self.getShutdownStatus();
  }

  /** Process the error response of a Shutdown command (set/cancel) */
  function processErrorShutdown(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
    self.getShutdownStatus();
  }

  /** Update the status of Shutdown command */
  function processSuccessShutdownStatus(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    $("#shutdown-status").text(responseBody.message);
  }

  /** Update the status of Shutdown command with an error */
  function processErrorShutdownStatus(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
    $("#shutdown-status").text("Error getting the status of Shutdown command");
  }

  /**
   * --------------------------------------------------------------------------
   * SUSPEND functions
   */
  /** Set a suspend command */
  this.setSuspendCommand = () => {
    let suspendDelay = document.getElementById("suspend-delay-dropdown").value;
    logger.trace("Suspend delay: " + suspendDelay);
    let requestParam = "delay=" + suspendDelay;
    loadingWheelModal.open();
    debuggerHttpClient.postUrlEncoded(ADMIN_API_URL + SUSPEND_URL, requestParam, processSuccessSuspend, processErrorSuspend);
  }

  /** Cancel a suspend command */
  this.cancelSuspendCommand = () => { 
    loadingWheelModal.open();
    debuggerHttpClient.delete(ADMIN_API_URL + SUSPEND_URL, null, processSuccessSuspend, processErrorSuspend);
  }

  /** Get the suspend command status */
  this.getSuspendStatus = (openModal) => {
    if (openModal) {
      loadingWheelModal.open();
    }
    debuggerHttpClient.get(ADMIN_API_URL + SUSPEND_URL, processSuccessSuspendStatus, processErrorSuspendStatus);
  }

  /** Process the success response of a suspend command (set/cancel) */
  function processSuccessSuspend(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    self.getSuspendStatus();
  }

  /** Process the error response of a suspend command (set/cancel) */
  function processErrorSuspend(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
    self.getSuspendStatus();
  }

  /** Update the status of suspend command */
  function processSuccessSuspendStatus(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    $("#suspend-status").text(responseBody.message);
  }

  /** Update the status of suspend command with an error */
  function processErrorSuspendStatus(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
    $("#suspend-status").text("Error getting the status of Suspend command");
  }

  /**
   * --------------------------------------------------------------------------
   * REBOOT functions
   */
  /**
   * Open a modal to confirm rebooting the server.
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
    loadingWheelModal.open();
    debuggerHttpClient.post(ADMIN_API_URL + REBOOT_URL, null, processSuccess, processError);
  }

  /**
   * --------------------------------------------------------------------------
   * SYSTEM STATE functions
   */
  /**
   * Check the uptime.
   */
  this.uptime = () => {
    loadingWheelModal.open();
    debuggerHttpClient.get(ADMIN_API_URL + UPTIME_URL, processSuccessSystemCommand, processErrorSystemCommand);
  }

  /**
   * Check the available memory.
   */
  this.free = () => {
    loadingWheelModal.open();
    debuggerHttpClient.get(ADMIN_API_URL + FREE_URL, processSuccessSystemCommand, processErrorSystemCommand);
  }

  /**
   * Check the available disk space.
   */
  this.df = () => {
    loadingWheelModal.open();
    debuggerHttpClient.get(ADMIN_API_URL + DF_URL, processSuccessSystemCommand, processErrorSystemCommand);
  }

  /** Get the httpd server status */
  this.getHttpdStatus = (openModal) => {
    if (openModal) {
      loadingWheelModal.open();
    }
    debuggerHttpClient.get(ADMIN_API_URL + HTTPD_URL, processSuccessHttpdStatus, processErrorHttpdStatus);
  }

  /** Restart apache httpd server */
  this.restartHttpd = (openModal) => {
    if (openModal) {
      loadingWheelModal.open();
    }
    debuggerHttpClient.post(ADMIN_API_URL + HTTPD_URL, null, processSuccessHttpdRestart, processErrorHttpdRestart, null);
  }

  /**
   * Callback after successful system command execution.
   */
  function processSuccessSystemCommand(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    systemCommandManager.renderCommandOutput(responseBody, false, null);
  }

  /**
   * Callback after error executing a system command.
   */
  function processErrorSystemCommand(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
    systemCommandManager.renderErrorExecutingCommand();
  }

  /**
   * Callback after successfully getting the httpd process status.
   */
  function processSuccessHttpdStatus(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    systemCommandManager.renderCommandOutput(responseBody, false, "httpd-status");
  }

  /**
   * Callback after an error getting the httpd process status.
   */
  function processErrorHttpdStatus(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    if (responseCode != 404) {
      basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
      $("#httpd-status").text("Error getting the status of the apache httpd server");      
    } else {
      $("#httpd-status").text("Unable to get the status of apache httpd server. Is it running?");   
    }
  }

  /**
   * Callback after successfully restarting httpd server.
   */
  function processSuccessHttpdRestart(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    systemCommandManager.renderCommandOutput(responseBody, false, null);
    setTimeout(() => { 
      self.getHttpdStatus(false);
    }, 5000);
  }

  /**
   * Callback after an error restarting httpd server.
   */
  function processErrorHttpdRestart(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
    systemCommandManager.renderErrorExecutingCommand();
    setTimeout(() => { 
      self.getHttpdStatus(false);
    }, 5000);
  }

  /** 
   * --------------------------------------------------------------------------
   * REST API calls
   */
  this.post = (url, requestBody) => {
    loadingWheelModal.open();
    debuggerHttpClient.post(ADMIN_API_URL + url, requestBody, processSuccess, processError);
  }

  /** Generic process success response */
  function processSuccess(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
  }

  /** Generic process error response */
  function processError(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
  }

  /** Dynamic DOM element generation ------------------------------------------ */
  this.getRebootServerModalMessage = () => {
    return "Are you sure you want to reboot the server? <br><br>";
  }

  this.createRebootImg = () => {
    let img = new Image();
    img.src = "/kame-house/img/pc/shutdown-red.png";
    img.className = "sm-btn-img";
    img.alt = "Reboot";
    img.title = "Reboot";
    img.onclick = () => self.rebootServer();
    return img;
  }
}

/**
 * Call main.
 */
$(document).ready(main);
