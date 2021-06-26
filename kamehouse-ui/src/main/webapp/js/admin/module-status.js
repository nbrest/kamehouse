/**
 * Module Status main function.
 * 
 * Dependencies: timeUtils, logger, debuggerHttpClient.
 * 
 * @author nbrest
 */
var moduleStatusManager;

var main = () => {
  bannerUtils.setRandomPrinceOfTennisBanner();
  importModuleStatusCss();
  moduleUtils.waitForModules(["logger", "debuggerHttpClient"], () => {
    logger.info("Started initializing module status");
    moduleStatusManager = new ModuleStatusManager();
    moduleStatusManager.init();
  });
};

/** Import module status css */
function importModuleStatusCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/module-status.css">');
}

function ModuleStatusManager() {
  let self = this;
  this.moduleStatusTableTemplate;
  this.moduleStatusErrorTableTemplate;

  /**
   * Load the templates and get the cache data.
   */
  this.init = async () => {
    await self.loadModuleStatusTableTemplate();
    moduleStatusManager.getModuleStatus('admin');
    moduleStatusManager.getModuleStatus('media');
    moduleStatusManager.getModuleStatus('tennisworld');
    moduleStatusManager.getModuleStatus('testmodule');
    moduleStatusManager.getModuleStatus('ui');
    moduleStatusManager.getModuleStatus('vlcrc');
  }

  /**
   * Loads the table html snippet into a variable to be reused as a template on render.
   */
  this.loadModuleStatusTableTemplate = async () => {
    const moduleStatusTableResponse = await fetch('/kame-house/html-snippets/module-status-table.html');
    self.moduleStatusTableTemplate = await moduleStatusTableResponse.text();
    const moduleStatusErrorTableResponse = await fetch('/kame-house/html-snippets/module-status-error-table.html');
    self.moduleStatusErrorTableTemplate = await moduleStatusErrorTableResponse.text();
  }

  /**
   * Get module status api url for each webapp.
   */
  this.getApiUrl = (webapp) => {
    if (webapp == "ui") {
      return '/kame-house/api/v1/commons/module/status';
    } else {
      return '/kame-house-' + webapp + '/api/v1/commons/module/status';
    }
  }

  /**
   * Get module status.
   */
  this.getModuleStatus = (webapp) => {
    logger.trace("getModuleStatus");
    debuggerHttpClient.get(self.getApiUrl(webapp),
      (responseBody, responseCode, responseDescription) => self.displayModuleStatus(responseBody),
      (responseBody, responseCode, responseDescription) => self.displayErrorGettingModuleStatus(webapp));
  }

  /**
   * Display module status.
   */
  this.displayModuleStatus = (moduleStatus) => {
    let webapp = moduleStatus["module"];
    let server = moduleStatus["server"];
    let buildVersion = moduleStatus["buildVersion"];
    let buildDate = moduleStatus["buildDate"];
    
    self.emptyModuleStatusDataDiv(webapp);
    let $moduleStatusData = $("#module-status-data-" + webapp);
    $moduleStatusData.append(self.getModuleStatusTableFromTemplate(webapp));
    $moduleStatusData.append($(self.getBr()));

    $('#mst-' + webapp + '-header-val').text(server + " - " + webapp);
    $('#mst-' + webapp + '-build-version-val').text(buildVersion);
    $('#mst-' + webapp + '-build-date-val').text(buildDate);
  }

  /**
   * Update the ids and classes of the template table just inserted to the dom.
   */
  this.getModuleStatusTableFromTemplate = (webapp) => {
    // Create a wrapper div to insert the table template
    let tableDiv = self.getModuleStatusTableDivInstance();
    
    // Update the ids and classes on the table generated from the template
    tableDiv.id = "mst-" + webapp;
    tableDiv.querySelector('tr td #mst-TEMPLATE-header-val').id = "mst-" + webapp + "-header-val";
    tableDiv.querySelector('tr #mst-TEMPLATE-build-version-val').id = "mst-" + webapp + "-build-version-val";
    tableDiv.querySelector('tr #mst-TEMPLATE-build-date-val').id = "mst-" + webapp + "-build-date-val";
   
    return tableDiv;
  }

  /**
   * Display error getting cache data.
   */
  this.displayErrorGettingModuleStatus = (webapp) => {
    // Create a wrapper div to insert the error table template
    let tableDiv = self.getModuleStatusErrorTableDivInstance();
    // Update the id
    tableDiv.querySelector('tr #mst-TEMPLATE-error-val').id = "mst-" + webapp + "-error-val";
    // Attach the error table to the dom
    self.emptyModuleStatusDataDiv(webapp);
    let $moduleStatusData = $("#module-status-data-" + webapp);
    $moduleStatusData.append(tableDiv);
    // Update the message
    $("#mst-" + webapp + "-error-val").text(timeUtils.getTimestamp() +
      " : Error retrieving module status data for " + webapp + ". Please try again later.");

    logger.error("Error retrieving module status data for " + webapp + ". Please try again later.");
  }

  /**
   * Empty cache data div.
   */
  this.emptyModuleStatusDataDiv = (webapp) => {
    let moduleStatus = $("#module-status-data-" + webapp);
    moduleStatus.empty();
  }

  /** Dynamic DOM element generation ------------------------------------------ */
  this.getModuleStatusTableDivInstance = () => {
    let tableDivWrapper = document.createElement('div');
    tableDivWrapper.innerHTML = self.moduleStatusTableTemplate;
    return tableDivWrapper.firstChild;
  }

  this.getModuleStatusErrorTableDivInstance = () => {
    let tableDivWrapper = document.createElement('div');
    tableDivWrapper.innerHTML = self.moduleStatusErrorTableTemplate;
    return tableDivWrapper.firstChild;
  }

  this.getBr = () => {
    return $('<br>');
  }
}

/**
 * Call main.
 */
$(document).ready(main);