/**
 * Admin Server Management functions.
 * 
 * Dependencies: logger, apiCallTable.
 * 
 * @author nbrest
 */
var serverManager;

var main = () => {
  bannerUtils.setRandomAllBanner();
  importServerManagementCss();
  moduleUtils.waitForModules(["logger", "apiCallTable"], () => {
    logger.info("Started initializing server management");
    serverManager = new ServerManager();
    serverManager.getSuspendStatus();
  });
};

function importServerManagementCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/server-management.css">');
}

function ServerManager() {
  let self = this;
  const ADMIN_API_URL = "/kame-house/api/v1/admin";
  const SUSPEND_URL = '/power-management/suspend';
  this.debugger = new Debugger();

  /**
   * --------------------------------------------------------------------------
   * WakeOnLan functions
   */
  this.execAdminWakeOnLan = (url, server) => {
    let requestParam = "server=" + server;
    loadingWheelModal.open();
    apiCallTable.postUrlEncoded(ADMIN_API_URL + url, requestParam, processSuccess, processError);
  }

  /**
   * --------------------------------------------------------------------------
   * SHUTDOWN functions
   */
  this.execAdminShutdown = (url) => {
    let shutdownDelay = document.getElementById("shutdown-delay-dropdown").value;
    logger.trace("Shutdown delay: " + shutdownDelay);
    let requestParam = "delay=" + shutdownDelay;
    loadingWheelModal.open();
    apiCallTable.postUrlEncoded(ADMIN_API_URL + url, requestParam, processSuccess, processError);
  }

  /**
   * --------------------------------------------------------------------------
   * SUSPEND functions
   */
  /** Set a suspend command */
  this.setSuspendCommand = () => {
    let shutdownDelay = document.getElementById("suspend-delay-dropdown").value;
    logger.trace("Shutdown delay: " + shutdownDelay);
    let requestParam = "delay=" + shutdownDelay;
    loadingWheelModal.open();
    apiCallTable.postUrlEncoded(ADMIN_API_URL + SUSPEND_URL, requestParam, processSuccessSuspend, processErrorSuspend);
  }

  /** Cancel a suspend command */
  this.cancelSuspendCommand = () => { 
    loadingWheelModal.open();
    apiCallTable.delete(ADMIN_API_URL + SUSPEND_URL, null, processSuccessSuspend, processErrorSuspend);
  }

  /** Get the suspend command status */
  this.getSuspendStatus = () => {
    loadingWheelModal.open();
    apiCallTable.get(ADMIN_API_URL + SUSPEND_URL, processSuccessSuspendStatus, processErrorSuspendStatus);
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
   * REST API calls
   */
  this.get = (url) => {
    loadingWheelModal.open();
    apiCallTable.get(ADMIN_API_URL + url, processSuccess, processError);
  }

  this.post = (url, requestBody) => {
    loadingWheelModal.open();
    apiCallTable.post(ADMIN_API_URL + url, requestBody, processSuccess, processError);
  }
 
  this.postUrlEncoded = (url, requestParam) => {
    loadingWheelModal.open();
    apiCallTable.postUrlEncoded(ADMIN_API_URL + url, requestParam, processSuccess, processError);
  }

  this.delete = (url, requestBody) => { 
    loadingWheelModal.open();
    apiCallTable.delete(ADMIN_API_URL + url, requestBody, processSuccess, processError);
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

  /**
   * --------------------------------------------------------------------------
   * Debugger functionality
   */
  this.getDebugger = () => this.debugger;
}

/** 
 * Handles the debugger functionality.
 * 
 * @author nbrest
 */
function Debugger() {

  /** Toggle debug mode. */
  this.toggleDebugMode = () => {
    logger.debug("Toggled debug mode")
    let debugModeDiv = document.getElementById("debug-mode");
    debugModeDiv.classList.toggle("hidden-kh");
  }
}

/**
 * Call main.
 */
$(document).ready(main);
