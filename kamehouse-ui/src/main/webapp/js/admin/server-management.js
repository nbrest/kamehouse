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
  });
};

function importServerManagementCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/server-management.css">');
}

function ServerManager() {
  let self = this;
  const ADMIN_API_URL = "/kame-house-admin/api/v1/admin";
  const SUSPEND_URL = '/power-management/suspend';
  const SHUTDOWN_URL = '/power-management/shutdown';

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
   this.confirmRebootServer = () => {
    basicKamehouseModal.setHtml(self.getRebootServerModalMessage());
    basicKamehouseModal.appendHtml(self.createRebootImg());
    basicKamehouseModal.open();
  }

  this.rebootServer = () => {
    alert("reboot here");
  }

  /**
   * --------------------------------------------------------------------------
   * SYSTEM STATE functions
   */
  this.uptime = () => {
    alert("uptime here");
  }

  this.free = () => {
    alert("free here");
  }

  this.df = () => {
    alert("df here");
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
