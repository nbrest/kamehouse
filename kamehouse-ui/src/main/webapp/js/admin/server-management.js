/**
 * Admin Server Management functions.
 * 
 * Dependencies: logger, debuggerHttpClient.
 * 
 * @author nbrest
 */
var serverManager;

function mainServerManagement() {
  bannerUtils.setRandomAllBanner();
  importServerManagementCss();
  moduleUtils.waitForModules(["debuggerHttpClient"], () => {
    logger.info("Started initializing server management");
    serverManager = new ServerManager();
    serverManager.getSuspendStatus(false);
    serverManager.getShutdownStatus(false);
    serverManager.getHttpdStatus(false);
  });
};

function importServerManagementCss() {
  domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/server-management.css">');
}

/**
 * Manager to execute the admin commands in the current server.
 */
function ServerManager() {

  this.execAdminWakeOnLan = execAdminWakeOnLan;
  this.setShutdownCommand = setShutdownCommand;
  this.cancelShutdownCommand = cancelShutdownCommand;
  this.getShutdownStatus = getShutdownStatus;
  this.setSuspendCommand = setSuspendCommand;
  this.cancelSuspendCommand = cancelSuspendCommand;
  this.getSuspendStatus = getSuspendStatus;
  this.confirmRebootServer = confirmRebootServer;
  this.uptime = uptime;
  this.free = free;
  this.df = df;
  this.getHttpdStatus = getHttpdStatus;
  this.restartHttpd = restartHttpd;
  this.post = post;

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
  function execAdminWakeOnLan(url, server) {
    const requestParam = "server=" + server;
    loadingWheelModal.open();
    debuggerHttpClient.postUrlEncoded(ADMIN_API_URL + url, requestParam, processSuccess, processError);
  }

  /**
   * --------------------------------------------------------------------------
   * SHUTDOWN functions
   */
  /** Set a Shutdown command */
  function setShutdownCommand() {
    const shutdownDelay = document.getElementById("shutdown-delay-dropdown").value;
    logger.trace("Shutdown delay: " + shutdownDelay);
    const requestParam = "delay=" + shutdownDelay;
    loadingWheelModal.open();
    debuggerHttpClient.postUrlEncoded(ADMIN_API_URL + SHUTDOWN_URL, requestParam, processSuccessShutdown, processErrorShutdown);
  }

  /** Cancel a Shutdown command */
  function cancelShutdownCommand() {
    loadingWheelModal.open();
    debuggerHttpClient.delete(ADMIN_API_URL + SHUTDOWN_URL, null, processSuccessShutdown, processErrorShutdown);
  }

  /** Get the Shutdown command status */
  function getShutdownStatus(openModal) {
    if (openModal) {
      loadingWheelModal.open();
    }
    debuggerHttpClient.get(ADMIN_API_URL + SHUTDOWN_URL, processSuccessShutdownStatus, processErrorShutdownStatus);
  }

  /** Process the success response of a Shutdown command (set/cancel) */
  function processSuccessShutdown(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    getShutdownStatus();
  }

  /** Process the error response of a Shutdown command (set/cancel) */
  function processErrorShutdown(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
    getShutdownStatus();
  }

  /** Update the status of Shutdown command */
  function processSuccessShutdownStatus(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    domUtils.setHtml($("#shutdown-status"), responseBody.message);
  }

  /** Update the status of Shutdown command with an error */
  function processErrorShutdownStatus(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
    domUtils.setHtml($("#shutdown-status"), "Error getting the status of Shutdown command");
  }

  /**
   * --------------------------------------------------------------------------
   * SUSPEND functions
   */
  /** Set a suspend command */
  function setSuspendCommand() {
    const suspendDelay = document.getElementById("suspend-delay-dropdown").value;
    logger.trace("Suspend delay: " + suspendDelay);
    const requestParam = "delay=" + suspendDelay;
    loadingWheelModal.open();
    debuggerHttpClient.postUrlEncoded(ADMIN_API_URL + SUSPEND_URL, requestParam, processSuccessSuspend, processErrorSuspend);
  }

  /** Cancel a suspend command */
  function cancelSuspendCommand() { 
    loadingWheelModal.open();
    debuggerHttpClient.delete(ADMIN_API_URL + SUSPEND_URL, null, processSuccessSuspend, processErrorSuspend);
  }

  /** Get the suspend command status */
  function getSuspendStatus(openModal) {
    if (openModal) {
      loadingWheelModal.open();
    }
    debuggerHttpClient.get(ADMIN_API_URL + SUSPEND_URL, processSuccessSuspendStatus, processErrorSuspendStatus);
  }

  /** Process the success response of a suspend command (set/cancel) */
  function processSuccessSuspend(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    getSuspendStatus();
  }

  /** Process the error response of a suspend command (set/cancel) */
  function processErrorSuspend(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
    getSuspendStatus();
  }

  /** Update the status of suspend command */
  function processSuccessSuspendStatus(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    domUtils.setHtml($("#suspend-status"), responseBody.message);
  }

  /** Update the status of suspend command with an error */
  function processErrorSuspendStatus(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
    domUtils.setHtml($("#suspend-status"), "Error getting the status of Suspend command");
  }

  /**
   * --------------------------------------------------------------------------
   * REBOOT functions
   */
  /**
   * Open a modal to confirm rebooting the server.
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
  function uptime() {
    loadingWheelModal.open();
    debuggerHttpClient.get(ADMIN_API_URL + UPTIME_URL, processSuccessSystemCommand, processErrorSystemCommand);
  }

  /**
   * Check the available memory.
   */
  function free() {
    loadingWheelModal.open();
    debuggerHttpClient.get(ADMIN_API_URL + FREE_URL, processSuccessSystemCommand, processErrorSystemCommand);
  }

  /**
   * Check the available disk space.
   */
  function df() {
    loadingWheelModal.open();
    debuggerHttpClient.get(ADMIN_API_URL + DF_URL, processSuccessSystemCommand, processErrorSystemCommand);
  }

  /** Get the httpd server status */
  function getHttpdStatus(openModal) {
    if (openModal) {
      loadingWheelModal.open();
    }
    debuggerHttpClient.get(ADMIN_API_URL + HTTPD_URL, processSuccessHttpdStatus, processErrorHttpdStatus);
  }

  /** Restart apache httpd server */
  function restartHttpd(openModal) {
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
      domUtils.setHtml($("#httpd-status"), "Error getting the status of the apache httpd server");      
    } else {
      domUtils.setHtml($("#httpd-status"), "Unable to get the status of apache httpd server. Is it running?");   
    }
  }

  /**
   * Callback after successfully restarting httpd server.
   */
  function processSuccessHttpdRestart(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    systemCommandManager.renderCommandOutput(responseBody, false, null);
    setTimeout(() => { 
      getHttpdStatus(false);
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
      getHttpdStatus(false);
    }, 5000);
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
      onClick: () => { rebootServer(); }
    });
  }

  /** 
   * --------------------------------------------------------------------------
   */
  function post(url, requestBody) {
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
}

/**
 * Call main.
 */
$(document).ready(mainServerManagement);
