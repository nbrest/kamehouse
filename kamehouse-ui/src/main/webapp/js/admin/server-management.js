/**
 * Admin Server Management functionality.
 * Manager to execute the admin commands in the current server.
 * 
 * @author nbrest
 */
class ServerManager {

  static #ADMIN_API_URL = "/kame-house-admin/api/v1/admin";
  static #SUSPEND_URL = '/power-management/suspend';
  static #SHUTDOWN_URL = '/power-management/shutdown';
  static #REBOOT_URL = '/power-management/reboot';
  static #UPTIME_URL = '/system-state/uptime';
  static #FREE_URL = '/system-state/free';
  static #DF_URL = '/system-state/df';
  static #TOP_URL = '/system-state/top';
  static #HTTPD_URL = '/system-state/httpd';

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Started initializing server management");
    kameHouse.util.banner.setRandomAllBanner();
    this.#importServerManagementCss();
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger"], () => {
      this.getSuspendStatus(false);
      this.getShutdownStatus(false);
      this.getHttpdStatus(false);
    });
  }
  
  /**
   * WakeOnLan
   */
  execAdminWakeOnLan(url, server) {
    const requestParam =  {
      "server" : server
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, ServerManager.#ADMIN_API_URL + url, kameHouse.http.getUrlEncodedHeaders(), requestParam, this.#processSuccess, this.#processError);
  }

  /** Set a Shutdown command */
  setShutdownCommand() {
    const shutdownDelay = document.getElementById("shutdown-delay-dropdown").value;
    kameHouse.logger.trace("Shutdown delay: " + shutdownDelay);
    const requestParam = {
      "delay" : shutdownDelay
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, ServerManager.#ADMIN_API_URL + ServerManager.#SHUTDOWN_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, 
    (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessShutdown(responseBody, responseCode, responseDescription, responseHeaders)}, 
    (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorShutdown(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** Cancel a Shutdown command */
  cancelShutdownCommand() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, ServerManager.#ADMIN_API_URL + ServerManager.#SHUTDOWN_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessShutdown(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorShutdown(responseBody, responseCode, responseDescription, responseHeaders)});
  }
  
  /** Get the Shutdown command status */
  getShutdownStatus(openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, ServerManager.#ADMIN_API_URL + ServerManager.#SHUTDOWN_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessShutdownStatus(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorShutdownStatus(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** Set a suspend command */
  setSuspendCommand() {
    const suspendDelayHours = document.getElementById("suspend-delay-dropdown-hours").value;
    const suspendDelayMinutes = document.getElementById("suspend-delay-dropdown-minutes").value;
    const suspendDelay = Number(suspendDelayHours) + Number(suspendDelayMinutes);
    kameHouse.logger.trace("Suspend delay: " + suspendDelay);
    const requestParam = {
      "delay" : suspendDelay
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, ServerManager.#ADMIN_API_URL + ServerManager.#SUSPEND_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, 
    (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessSuspend(responseBody, responseCode, responseDescription, responseHeaders)}, 
    (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorSuspend(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** Cancel a suspend command */
  cancelSuspendCommand() { 
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, ServerManager.#ADMIN_API_URL + ServerManager.#SUSPEND_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessSuspend(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorSuspend(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** Get the suspend command status */
  getSuspendStatus(openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, ServerManager.#ADMIN_API_URL + ServerManager.#SUSPEND_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessSuspendStatus(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorSuspendStatus(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /**
   * Open a modal to confirm rebooting the server.
   */
  confirmRebootServer() {
    kameHouse.plugin.modal.basicModal.setHtml(this.#getRebootServerModalMessage());
    kameHouse.plugin.modal.basicModal.appendHtml(this.#createRebootImg());
    kameHouse.plugin.modal.basicModal.open();
  }

  /**
   * Check the uptime.
   */
  uptime() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, ServerManager.#ADMIN_API_URL + ServerManager.#UPTIME_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessSystemCommand(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorSystemCommand(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /**
   * Check the available memory.
   */
  free() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, ServerManager.#ADMIN_API_URL + ServerManager.#FREE_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessSystemCommand(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorSystemCommand(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /**
   * Check the available disk space.
   */
  df() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, ServerManager.#ADMIN_API_URL + ServerManager.#DF_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessSystemCommand(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorSystemCommand(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /**
   * Check the state of the system processes consuming most resources.
   */
  top() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, ServerManager.#ADMIN_API_URL + ServerManager.#TOP_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessSystemCommand(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorSystemCommand(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** Get the httpd server status */
  getHttpdStatus(openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, ServerManager.#ADMIN_API_URL + ServerManager.#HTTPD_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessHttpdStatus(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorHttpdStatus(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** Restart apache httpd server */
  restartHttpd(openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, ServerManager.#ADMIN_API_URL + ServerManager.#HTTPD_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessHttpdRestart(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorHttpdRestart(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /**
   * Import css.
   */
  #importServerManagementCss() {
    kameHouse.util.dom.append(kameHouse.util.dom.getHead(), '<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/server-management.css">');
  }

  /**
   * --------------------------------------------------------------------------
   * SHUTDOWN functions
   */
  /** Process the success response of a Shutdown command (set/cancel) */
  #processSuccessShutdown(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    this.getShutdownStatus();
  }

  /** Process the error response of a Shutdown command (set/cancel) */
  #processErrorShutdown(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    this.getShutdownStatus();
  }

  /** Update the status of Shutdown command */
  #processSuccessShutdownStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.util.dom.setHtmlById("shutdown-status", responseBody.message);
  }

  /** Update the status of Shutdown command with an error */
  #processErrorShutdownStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    kameHouse.util.dom.setHtmlById("shutdown-status", "Error getting the status of Shutdown command");
  }

  /**
   * --------------------------------------------------------------------------
   * SUSPEND functions
   */
  /** Process the success response of a suspend command (set/cancel) */
  #processSuccessSuspend(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    this.getSuspendStatus();
  }

  /** Process the error response of a suspend command (set/cancel) */
  #processErrorSuspend(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    this.getSuspendStatus();
  }

  /** Update the status of suspend command */
  #processSuccessSuspendStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.util.dom.setHtmlById("suspend-status", responseBody.message);
  }

  /** Update the status of suspend command with an error */
  #processErrorSuspendStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    kameHouse.util.dom.setHtmlById("suspend-status", "Error getting the status of Suspend command");
  }

  /**
   * --------------------------------------------------------------------------
   * REBOOT functions
   */
  /**
   * Reboot the server.
   */
  #rebootServer() {
    kameHouse.plugin.modal.basicModal.close();
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, ServerManager.#ADMIN_API_URL + ServerManager.#REBOOT_URL, null, null, this.#processSuccess, this.#processError);
  }

  /**
   * --------------------------------------------------------------------------
   * SYSTEM STATE functions
   */
  /**
   * Callback after successful system command execution.
   */
  #processSuccessSystemCommand(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.systemCommandManager.renderCommandOutput(responseBody, false, null);
  }

  /**
   * Callback after error executing a system command.
   */
  #processErrorSystemCommand(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    kameHouse.plugin.systemCommandManager.renderErrorExecutingCommand();
  }

  /**
   * --------------------------------------------------------------------------
   */  
  /**
   * Callback after successfully getting the httpd process status.
   */
  #processSuccessHttpdStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.systemCommandManager.renderCommandOutput(responseBody, false, "httpd-status");
  }

  /**
   * Callback after an error getting the httpd process status.
   */
  #processErrorHttpdStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    if (responseCode != 404) {
      kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
      kameHouse.util.dom.setHtmlById("httpd-status", "Error getting the status of the apache httpd server");      
    } else {
      kameHouse.util.dom.setHtmlById("httpd-status", "Unable to get the status of apache httpd server. Is it running?");   
    }
  }

  /**
   * Callback after successfully restarting httpd server.
   */
  #processSuccessHttpdRestart(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.systemCommandManager.renderCommandOutput(responseBody, false, null);
    setTimeout(() => { 
      this.getHttpdStatus(false);
    }, 5000);
  }

  /**
   * Callback after an error restarting httpd server.
   */
  #processErrorHttpdRestart(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    kameHouse.plugin.systemCommandManager.renderErrorExecutingCommand();
    setTimeout(() => { 
      this.getHttpdStatus(false);
    }, 5000);
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
   * Get reboot clickable image.
   */
  #createRebootImg() {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/pc/shutdown-red.png",
      className: "img-btn-kh",
      alt: "Reboot",
      onClick: () => { this.#rebootServer(); }
    });
  }

  /** Generic process success response */
  #processSuccess(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
  }

  /** Generic process error response */
  #processError(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("serverManager", new ServerManager());
});
