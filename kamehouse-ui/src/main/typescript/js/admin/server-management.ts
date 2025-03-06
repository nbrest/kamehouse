/**
 * Admin Server Management functionality.
 * Manager to execute the admin commands in the current server.
 * 
 * @author nbrest
 */
class ServerManager {

  #ADMIN_API_URL = "/kame-house-admin/api/v1/admin";
  #SUSPEND_URL = '/power-management/suspend';
  #SHUTDOWN_URL = '/power-management/shutdown';
  #REBOOT_URL = '/power-management/reboot';
  #UPTIME_URL = '/system-state/uptime';
  #FREE_URL = '/system-state/free';
  #DF_URL = '/system-state/df';
  #TOP_URL = '/system-state/top';
  #HTTPD_URL = '/system-state/httpd';

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Started initializing server management", null);
    kameHouse.util.banner.setRandomAllBanner(null);
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
    kameHouse.plugin.debugger.http.post(config, this.#ADMIN_API_URL + url, kameHouse.http.getUrlEncodedHeaders(), requestParam, this.#processSuccess, this.#processError);
  }

  /** Set a Shutdown command */
  setShutdownCommand() {
    const shutdownDelay = (document.getElementById("shutdown-delay-dropdown") as HTMLSelectElement).value;
    kameHouse.logger.trace("Shutdown delay: " + shutdownDelay, null);
    const requestParam = {
      "delay" : shutdownDelay
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, this.#ADMIN_API_URL + this.#SHUTDOWN_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, 
    (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessShutdown(responseBody, responseCode, responseDescription, responseHeaders)}, 
    (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorShutdown(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** Cancel a Shutdown command */
  cancelShutdownCommand() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, this.#ADMIN_API_URL + this.#SHUTDOWN_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessShutdown(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorShutdown(responseBody, responseCode, responseDescription, responseHeaders)});
  }
  
  /** Get the Shutdown command status */
  getShutdownStatus(openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, this.#ADMIN_API_URL + this.#SHUTDOWN_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessShutdownStatus(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorShutdownStatus(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** Set a suspend command */
  setSuspendCommand() {
    const suspendDelayHours = (document.getElementById("suspend-delay-dropdown-hours") as HTMLSelectElement).value;
    const suspendDelayMinutes = (document.getElementById("suspend-delay-dropdown-minutes") as HTMLSelectElement).value;
    const suspendDelay = Number(suspendDelayHours) + Number(suspendDelayMinutes);
    kameHouse.logger.trace("Suspend delay: " + suspendDelay, null);
    const requestParam = {
      "delay" : suspendDelay
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, this.#ADMIN_API_URL + this.#SUSPEND_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, 
    (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessSuspend(responseBody, responseCode, responseDescription, responseHeaders)}, 
    (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorSuspend(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** Cancel a suspend command */
  cancelSuspendCommand() { 
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, this.#ADMIN_API_URL + this.#SUSPEND_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessSuspend(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorSuspend(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** Get the suspend command status */
  getSuspendStatus(openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, this.#ADMIN_API_URL + this.#SUSPEND_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessSuspendStatus(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorSuspendStatus(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /**
   * Open a modal to confirm rebooting the server.
   */
  confirmRebootServer() {
    kameHouse.plugin.modal.basicModal.setHtml(this.#getRebootServerModalMessage());
    kameHouse.plugin.modal.basicModal.appendHtml(this.#createRebootButton());
    kameHouse.plugin.modal.basicModal.open();
  }

  /**
   * Check the uptime.
   */
  uptime() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, this.#ADMIN_API_URL + this.#UPTIME_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessKameHouseCommand(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorKameHouseCommand(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /**
   * Check the available memory.
   */
  free() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, this.#ADMIN_API_URL + this.#FREE_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessKameHouseCommand(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorKameHouseCommand(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /**
   * Check the available disk space.
   */
  df() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, this.#ADMIN_API_URL + this.#DF_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessKameHouseCommand(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorKameHouseCommand(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /**
   * Check the state of the system processes consuming most resources.
   */
  top() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, this.#ADMIN_API_URL + this.#TOP_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessKameHouseCommand(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorKameHouseCommand(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** Get the httpd server status */
  getHttpdStatus(openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, this.#ADMIN_API_URL + this.#HTTPD_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessHttpdStatus(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorHttpdStatus(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** Restart apache httpd server */
  restartHttpd(openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, this.#ADMIN_API_URL + this.#HTTPD_URL, null, null, 
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
    this.getShutdownStatus(null);
  }

  /** Process the error response of a Shutdown command (set/cancel) */
  #processErrorShutdown(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    this.getShutdownStatus(null);
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
    this.getSuspendStatus(null);
  }

  /** Process the error response of a suspend command (set/cancel) */
  #processErrorSuspend(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    this.getSuspendStatus(null);
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
    kameHouse.plugin.debugger.http.post(config, this.#ADMIN_API_URL + this.#REBOOT_URL, null, null, this.#processSuccess, this.#processError);
  }

  /**
   * --------------------------------------------------------------------------
   * SYSTEM STATE functions
   */
  /**
   * Callback after successful kamehouse command execution.
   */
  #processSuccessKameHouseCommand(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.kameHouseCommandManager.renderKameHouseCommandResult(responseBody, false, null, "system-health-command-output");
  }

  /**
   * Callback after error executing a kamehouse command.
   */
  #processErrorKameHouseCommand(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    kameHouse.plugin.kameHouseCommandManager.renderErrorExecutingCommand("system-health-command-output");
  }

  /**
   * --------------------------------------------------------------------------
   */  
  /**
   * Callback after successfully getting the httpd process status.
   */
  #processSuccessHttpdStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.kameHouseCommandManager.renderKameHouseCommandResult(responseBody, false, "httpd-status", null);
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
    kameHouse.plugin.kameHouseCommandManager.renderKameHouseCommandResult(responseBody, false, null, null);
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
    kameHouse.plugin.kameHouseCommandManager.renderErrorExecutingCommand(null);
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
   * Get reboot button.
   */
  #createRebootButton() {
    return kameHouse.util.dom.getButton({
      attr: {
        class: "img-btn-kh",
      },
      mobileClass: null,
      backgroundImg: "/kame-house/img/pc/shutdown-red.png",
      html: null,
      data: null,
      click: (event, data) => this.#rebootServer()
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
