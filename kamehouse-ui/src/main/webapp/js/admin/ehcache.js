/**
 * EhCache main function.
 * 
 * Dependencies: timeUtils, logger, apiCallTable.
 * 
 * @author nbrest
 */
var ehCacheManager;

var main = () => {
  bannerUtils.setRandomPrinceOfTennisBanner();
  importEhcacheCss();
  moduleUtils.waitForModules(["logger", "apiCallTable", "kameHouseWebappTabsManager"], () => {
    logger.info("Started initializing ehcache");
    ehCacheManager = new EhCacheManager();
    kameHouseWebappTabsManager.openTab('tab-media');
    ehCacheManager.getAllCacheData('admin');
    ehCacheManager.getAllCacheData('media');
    ehCacheManager.getAllCacheData('tennisworld');
    ehCacheManager.getAllCacheData('testmodule');
    ehCacheManager.getAllCacheData('ui');
    ehCacheManager.getAllCacheData('vlcrc');
  });
};

/** Import ehcache css */
function importEhcacheCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/ehcache.css">');
}

function EhCacheManager() {
  let self = this;
  this.ehcacheToggleTableRowIds = [
    []
  ];

  /**
   * Get ehcache api url for each webapp.
   */
  this.getApiUrl = (webapp) => {
    if (webapp == "ui") {
      return '/kame-house/api/v1/commons/ehcache';
    } else {
      return '/kame-house-' + webapp + '/api/v1/commons/ehcache';
    }
  }

  /**
   * Get all cache data.
   */
  this.getAllCacheData = (webapp) => {
    logger.trace("getAllCacheData");
    apiCallTable.get(self.getApiUrl(webapp),
      (responseBody, responseCode, responseDescription) => self.displayCacheData(responseBody, webapp),
      (responseBody, responseCode, responseDescription) => self.displayErrorGettingCache());
  }

  /**
   * Display cache data.
   */
  this.displayCacheData = (caches, webapp) => {
    self.emptyCacheDataDiv(webapp);
    self.ehcacheToggleTableRowIds[webapp] = [];
    let $cacheData = $("#cache-data-" + webapp);
    caches.forEach((cache) => {
      let $cacheTable = $('<table id="table-' + cache.name +
        '" class="table table-bordered table-ehcache table-bordered-kh table-responsive-kh table-responsive">');
      let $cacheTableRow;

      $cacheTableRow = $("<tr>");
      $cacheTableRow.append($('<td class="td-ehcache-header">').append($('<div class="ehcache-table-header-txt">').text("name")));
      let $cacheTableRowContent = $("<td>");
      $cacheTableRowContent.append($('<div class="ehcache-table-header-txt">').text(cache.name));
      $cacheTableRowContent.append("<img id='clear-" + cache.name + "' class='btn-ehcache cache-status-buttons'" +
        "src='/kame-house/img/other/cancel.png' alt='Clear' title='Clear' />");
      $cacheTableRowContent.append("<img id='toggle-view-" + cache.name + "' class='btn-ehcache cache-status-buttons m-15-d-r-kh m-15-m-r-kh'" +
        "src='/kame-house/img/other/resize-vertical-gray-dark.png' alt='Expand/Collapse' title='Expand/Collapse' />");
      $cacheTableRow.append($cacheTableRowContent);
      $cacheTable.append($cacheTableRow);

      let cacheTableHeaders = ["status", "keys", "values"];
      for (let i = 0; i < cacheTableHeaders.length; i++) {
        $cacheTableRow = $('<tr class="toggle-' + cache.name + '">');
        $cacheTableRow.append($('<td class="td-ehcache-header">').text(cacheTableHeaders[i]));
        $cacheTableRow.append($("<td>").text(cache[cacheTableHeaders[i]]));
        $cacheTable.append($cacheTableRow);
      }
      $cacheData.append($cacheTable);
      $cacheData.append("<br>");

      $("#clear-" + cache.name).click(() => self.clearCacheData(cache.name, webapp));
      $("#toggle-view-" + cache.name).click(() => self.toggleCacheView(".toggle-" + cache.name));
      $(".toggle-" + cache.name).toggle();
      self.ehcacheToggleTableRowIds[webapp].push(".toggle-" + cache.name);
    });
  }

  /**
   * Display error getting cache data.
   */
  this.displayErrorGettingCache = (webapp) => {
    self.emptyCacheDataDiv(webapp);
    let $cacheData = $("#cache-data" + webapp);
    let $errorTable = $('<table class="table table-bordered table-ehcache table-responsive-kh table-responsive">');
    let $errorTableRow = $("<tr>");
    $errorTableRow.append($('<td>').text(timeUtils.getTimestamp() +
      " : Error retrieving cache data. Please try again later."));
    $errorTable.append($errorTableRow);
    $cacheData.append($errorTable);
    logger.error("Error retrieving cache data. Please try again later.");
  }

  /**
   * Clear cache data.
   */
  this.clearCacheData = (cacheName, webapp) => {
    let url = self.getApiUrl(webapp) + '?name=' + cacheName;
    apiCallTable.delete(url, null,
      (responseBody, responseCode, responseDescription) => self.getAllCacheData(webapp),
      (responseBody, responseCode, responseDescription) => {
        logger.error("Error clearing cache " + cacheName);
        self.getAllCacheData(webapp);
      });
  }

  /**
   * Clear all caches.
   */
  this.clearAllCaches = (webapp) => {
    apiCallTable.delete(self.getApiUrl(webapp), null,
      (responseBody, responseCode, responseDescription) => self.getAllCacheData(webapp),
      (responseBody, responseCode, responseDescription) => {
        logger.error("Error clearing all caches");
        self.getAllCacheData(webapp);
      });
  }

  /**
   * Empty cache data div.
   */
  this.emptyCacheDataDiv = (webapp) => {
    let $cacheData = $("#cache-data-" + webapp);
    $cacheData.empty();
  }

  /**
   * Toggle cache view (expand/collapse).
   */
  this.toggleCacheView = (className) => {
    $(className).toggle();
  }

  /**
   * Toggle cache view for all caches (expand/collapse).
   */
  this.toggleAllCacheView = (webapp) => {
    for (let i = 0; i < self.ehcacheToggleTableRowIds[webapp].length; i++) {
      self.toggleCacheView(self.ehcacheToggleTableRowIds[webapp][i]);
    }
  }
}

/**
 * Call main.
 */
$(document).ready(main);