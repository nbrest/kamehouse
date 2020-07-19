/**
 * Admin Server Management functions.
 * 
 * Dependencies: logger, apiCallTable.
 * 
 * @author nbrest
 */
var serverManager;

var main = function() {  
  importServerManagementCss();
  var loadingModules = ["logger", "apiCallTable"];
  waitForModules(loadingModules, () => {
    logger.info("Started initializing server management");
    serverManager = new ServerManager();
  });
};

function importServerManagementCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/server-management.css">');
}

function ServerManager() {

  this.execAdminShutdown = (url) => {
    let shutdownDelay = document.getElementById("shutdown-delay-dropdown").value;
    logger.trace("Shutdown delay: " + shutdownDelay);
    let requestParam = "delay=" + shutdownDelay;
    loadingWheelModal.open();
    apiCallTable.postUrlEncoded(url, requestParam, processSuccess, processError);
  }

  this.get = (url) => {
    loadingWheelModal.open();
    apiCallTable.get(url, processSuccess, processError);
  }

  this.post = (url, requestBody) => {
    loadingWheelModal.open();
    apiCallTable.post(url, requestBody, processSuccess, processError);
  }
 
  this.postUrlEncoded = (url, requestParam) => {
    loadingWheelModal.open();
    apiCallTable.postUrlEncoded(url, requestParam, processSuccess, processError);
  }

  this.delete = (url, requestBody) => { 
    loadingWheelModal.open();
    apiCallTable.delete(url, requestBody, processSuccess, processError);
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
}

/**
 * Call main.
 */
$(document).ready(main);