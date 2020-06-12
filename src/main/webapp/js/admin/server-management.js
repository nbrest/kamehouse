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

  this.execAdminShutdown = function execAdminShutdown(url) {
    logger.traceFunctionCall();
    let shutdownDelay = document.getElementById("shutdown-delay-dropdown").value;
    logger.trace("Shutdown delay: " + shutdownDelay);
    var requestParam = "delay=" + shutdownDelay;
    loadingWheelModal.open();
    apiCallTable.postUrlEncoded(url, requestParam, 
      function success() {
      loadingWheelModal.close();
    },
    function error() {
      loadingWheelModal.close();
    });
  }

  this.get = function httpGet(url) {
    loadingWheelModal.open();
    apiCallTable.get(url,
      function success() {
        loadingWheelModal.close();
      },
      function error() {
        loadingWheelModal.close();
      });
  }

  this.post = function httpPost(url, requestBody) {
    loadingWheelModal.open();
    apiCallTable.post(url, requestBody,
      function success() {
        loadingWheelModal.close();
      },
      function error() {
        loadingWheelModal.close();
      });
  }
 
  this.postUrlEncoded = function httpPostUrlEncoded(url, requestParam) {
    loadingWheelModal.open();
    apiCallTable.postUrlEncoded(url, requestParam,
      function success() {
        loadingWheelModal.close();
      },
      function error() {
        loadingWheelModal.close();
      });
  }

  this.delete = function httpDelete(url, requestBody) { 
    loadingWheelModal.open();
    apiCallTable.delete(url, requestBody,
      function success() {
        loadingWheelModal.close();
      },
      function error() {
        loadingWheelModal.close();
      })
  }
}

/**
 * Call main.
 */
$(document).ready(main);