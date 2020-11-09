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
  });
};

function importServerManagementCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/server-management.css">');
}

function ServerManager() {
  const ADMIN_API_URL = "/kame-house/api/v1/admin";
  this.debugger = new Debugger();

  this.execAdminShutdown = (url) => {
    let shutdownDelay = document.getElementById("shutdown-delay-dropdown").value;
    logger.trace("Shutdown delay: " + shutdownDelay);
    let requestParam = "delay=" + shutdownDelay;
    loadingWheelModal.open();
    apiCallTable.postUrlEncoded(ADMIN_API_URL + url, requestParam, processSuccess, processError);
  }

  this.execAdminWakeOnLan = (url, server) => {
    let requestParam = "server=" + server;
    loadingWheelModal.open();
    apiCallTable.postUrlEncoded(ADMIN_API_URL + url, requestParam, processSuccess, processError);
  }


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

  /** Process success response */
  function processSuccess(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
  }

  /** Process error response */
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
