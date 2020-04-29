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
  waitForModules(loadingModules, function initServerManager() {
    logger.info("Started initializing server management");
    serverManager = new ServerManager();
  });
};

function importServerManagementCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/server-management.css">');
}

function ServerManager() {

  this.execAdminShutdown = function execAdminShutdown(url, time) {
    logger.traceFunctionCall();
    logger.trace("Shutdown delay: " + time);
    var requestParam = "delay=" + time;
    apiCallTable.postUrlEncoded(url, requestParam);
  }

  this.get = function httpGet(url) {
    apiCallTable.get(url);
  }

  this.post = function httpPost(url, requestBody) {
    apiCallTable.post(url, requestBody);
  }
 
  this.postUrlEncoded = function httpPostUrlEncoded(url, requestParam) {
    apiCallTable.postUrlEncoded(url, requestParam);
  }

  this.delete = function httpDelete(url, requestBody) { 
    apiCallTable.delete(url, requestBody)
  }
}

/**
 * Call main.
 */
$(document).ready(main);