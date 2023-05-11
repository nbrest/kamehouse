/**
 * Admin Server Management functions.
 * 
 * @author nbrest
 */
/**
 * Manager to execute the admin commands in the current server.
 */
function ServerManager() {

  this.load = load;
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

  function load() {
    kameHouse.logger.info("Started initializing server management");
    kameHouse.util.banner.setRandomAllBanner();
    importServerManagementCss();
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger"], () => {
      getSuspendStatus(false);
      getShutdownStatus(false);
      getHttpdStatus(false);
    });
  }
  
  function importServerManagementCss() {
    kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/server-management.css">');
  }

  /**
   * --------------------------------------------------------------------------
   * WakeOnLan functions
   */
  function execAdminWakeOnLan(url, server) {
    const requestParam =  {
      "server" : server
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, ADMIN_API_URL + url, kameHouse.http.getUrlEncodedHeaders(), requestParam, processSuccess, processError);
  }

  /**
   * --------------------------------------------------------------------------
   * SHUTDOWN functions
   */
  /** Set a Shutdown command */
  function setShutdownCommand() {
    const shutdownDelay = document.getElementById("shutdown-delay-dropdown").value;
    kameHouse.logger.trace("Shutdown delay: " + shutdownDelay);
    const requestParam = {
      "delay" : shutdownDelay
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, ADMIN_API_URL + SHUTDOWN_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, processSuccessShutdown, processErrorShutdown);
  }

  /** Cancel a Shutdown command */
  function cancelShutdownCommand() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, ADMIN_API_URL + SHUTDOWN_URL, null, null, processSuccessShutdown, processErrorShutdown);
  }

  /** Get the Shutdown command status */
  function getShutdownStatus(openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, ADMIN_API_URL + SHUTDOWN_URL, null, null, processSuccessShutdownStatus, processErrorShutdownStatus);
  }

  /** Process the success response of a Shutdown command (set/cancel) */
  function processSuccessShutdown(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    getShutdownStatus();
  }

  /** Process the error response of a Shutdown command (set/cancel) */
  function processErrorShutdown(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    getShutdownStatus();
  }

  /** Update the status of Shutdown command */
  function processSuccessShutdownStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.util.dom.setHtml($("#shutdown-status"), responseBody.message);
  }

  /** Update the status of Shutdown command with an error */
  function processErrorShutdownStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    kameHouse.util.dom.setHtml($("#shutdown-status"), "Error getting the status of Shutdown command");
  }

  /**
   * --------------------------------------------------------------------------
   * SUSPEND functions
   */
  /** Set a suspend command */
  function setSuspendCommand() {
    const suspendDelayHours = document.getElementById("suspend-delay-dropdown-hours").value;
    const suspendDelayMinutes = document.getElementById("suspend-delay-dropdown-minutes").value;
    const suspendDelay = Number(suspendDelayHours) + Number(suspendDelayMinutes);
    kameHouse.logger.trace("Suspend delay: " + suspendDelay);
    const requestParam = {
      "delay" : suspendDelay
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, ADMIN_API_URL + SUSPEND_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, processSuccessSuspend, processErrorSuspend);
  }

  /** Cancel a suspend command */
  function cancelSuspendCommand() { 
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, ADMIN_API_URL + SUSPEND_URL, null, null, processSuccessSuspend, processErrorSuspend);
  }

  /** Get the suspend command status */
  function getSuspendStatus(openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, ADMIN_API_URL + SUSPEND_URL, null, null, processSuccessSuspendStatus, processErrorSuspendStatus);
  }

  /** Process the success response of a suspend command (set/cancel) */
  function processSuccessSuspend(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    getSuspendStatus();
  }

  /** Process the error response of a suspend command (set/cancel) */
  function processErrorSuspend(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    getSuspendStatus();
  }

  /** Update the status of suspend command */
  function processSuccessSuspendStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.util.dom.setHtml($("#suspend-status"), responseBody.message);
  }

  /** Update the status of suspend command with an error */
  function processErrorSuspendStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    kameHouse.util.dom.setHtml($("#suspend-status"), "Error getting the status of Suspend command");
  }

  /**
   * --------------------------------------------------------------------------
   * REBOOT functions
   */
  /**
   * Open a modal to confirm rebooting the server.
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
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, ADMIN_API_URL + REBOOT_URL, null, null, processSuccess, processError);
  }

  /**
   * --------------------------------------------------------------------------
   * SYSTEM STATE functions
   */
  /**
   * Check the uptime.
   */
  function uptime() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, ADMIN_API_URL + UPTIME_URL, null, null, processSuccessSystemCommand, processErrorSystemCommand);
  }

  /**
   * Check the available memory.
   */
  function free() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, ADMIN_API_URL + FREE_URL, null, null, processSuccessSystemCommand, processErrorSystemCommand);
  }

  /**
   * Check the available disk space.
   */
  function df() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, ADMIN_API_URL + DF_URL, null, null, processSuccessSystemCommand, processErrorSystemCommand);
  }

  /** Get the httpd server status */
  function getHttpdStatus(openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, ADMIN_API_URL + HTTPD_URL, null, null, processSuccessHttpdStatus, processErrorHttpdStatus);
  }

  /** Restart apache httpd server */
  function restartHttpd(openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, ADMIN_API_URL + HTTPD_URL, null, null, processSuccessHttpdRestart, processErrorHttpdRestart);
  }

  /**
   * Callback after successful system command execution.
   */
  function processSuccessSystemCommand(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.systemCommandManager.renderCommandOutput(responseBody, false, null);
  }

  /**
   * Callback after error executing a system command.
   */
  function processErrorSystemCommand(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    kameHouse.plugin.systemCommandManager.renderErrorExecutingCommand();
  }

  /**
   * Callback after successfully getting the httpd process status.
   */
  function processSuccessHttpdStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.systemCommandManager.renderCommandOutput(responseBody, false, "httpd-status");
  }

  /**
   * Callback after an error getting the httpd process status.
   */
  function processErrorHttpdStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    if (responseCode != 404) {
      kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
      kameHouse.util.dom.setHtml($("#httpd-status"), "Error getting the status of the apache httpd server");      
    } else {
      kameHouse.util.dom.setHtml($("#httpd-status"), "Unable to get the status of apache httpd server. Is it running?");   
    }
  }

  /**
   * Callback after successfully restarting httpd server.
   */
  function processSuccessHttpdRestart(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.systemCommandManager.renderCommandOutput(responseBody, false, null);
    setTimeout(() => { 
      getHttpdStatus(false);
    }, 5000);
  }

  /**
   * Callback after an error restarting httpd server.
   */
  function processErrorHttpdRestart(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    kameHouse.plugin.systemCommandManager.renderErrorExecutingCommand();
    setTimeout(() => { 
      getHttpdStatus(false);
    }, 5000);
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
      onClick: () => { rebootServer(); }
    });
  }

  /** 
   * --------------------------------------------------------------------------
   */
  function post(url, requestBody) {
    kameHouse.plugin.modal.loadingWheelModal.open();
    let headers = null;
    if (!kameHouse.core.isEmpty(requestBody)) {
      headers = kameHouse.http.getApplicationJsonHeaders();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, ADMIN_API_URL + url, headers, requestBody, processSuccess, processError);
  }

  /** Generic process success response */
  function processSuccess(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
  }

  /** Generic process error response */
  function processError(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
  }
}

$(document).ready(() => {
  kameHouse.addExtension("serverManager", new ServerManager());
});
