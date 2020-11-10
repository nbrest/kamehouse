/**
 * Admin EhCache main function.
 * 
 * Dependencies: timeUtils, logger, httpClient.
 * 
 * @author nbrest
 */
var ehCacheManager;

var main = () => {
  bannerUtils.setRandomPrinceOfTennisBanner();
  importEhcacheCss();
  moduleUtils.waitForModules(["logger", "httpClient"], () => {
    logger.info("Started initializing ehcache");
    ehCacheManager = new EhCacheManager();
    ehCacheManager.getAllCacheData();
  });
};

/** Import ehcache css */
function importEhcacheCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/ehcache.css">');
}

function EhCacheManager() {
  let self = this;
  let EHCACHE_REST_API = '/kame-house/api/v1/admin/ehcache';
  this.ehcacheToggleTableRowIds = [];

  /**
   * Get all cache data.
   */
  this.getAllCacheData = () => {
    logger.traceFunctionCall();
    httpClient.get(EHCACHE_REST_API, null,
      (responseBody, responseCode, responseDescription) => self.displayCacheData(responseBody),
      (responseBody, responseCode, responseDescription) => self.displayErrorGettingCache());
  }

  /**
   * Display cache data.
   */
  this.displayCacheData = (caches) => { 
    self.emptyCacheDataDiv();
    self.ehcacheToggleTableRowIds = [];
    let $cacheData = $("#cache-data");
    caches.forEach((cache) => {
      let $cacheTable = $('<table id="table-' + cache.name +
        '" class="table table-bordered table-ehcache table-bordered-kh table-responsive-kh table-responsive">');
      let $cacheTableRow;

      $cacheTableRow = $("<tr>");
      $cacheTableRow.append($('<td class="td-ehcache-header">').append($('<div class="ehcache-table-header-txt">').text("name")));
      let $cacheTableRowContent = $("<td>");
      $cacheTableRowContent.append($('<div class="ehcache-table-header-txt">').text(cache.name));
      $cacheTableRowContent.append("<input id='clear-" + cache.name +
        "' type='button' value='Clear Cache' class='btn btn-outline-danger table-ehcache-button btn-borderless' />");
      $cacheTableRowContent.append("<input id='toggle-view-" + cache.name +
        "' type='button' value='Expand/Collapse' " +
        "class='btn btn-outline-secondary table-ehcache-button btn-borderless' />");
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

      $("#clear-" + cache.name).click(() => self.clearCacheData(cache.name));
      $("#toggle-view-" + cache.name).click(() => self.toggleCacheView(".toggle-" + cache.name));
      $(".toggle-" + cache.name).toggle();
      self.ehcacheToggleTableRowIds.push(".toggle-" + cache.name);
    });
  }

  /**
   * Display error getting cache data.
   */
  this.displayErrorGettingCache = () => { 
    self.emptyCacheDataDiv();
    let $cacheData = $("#cache-data");
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
  this.clearCacheData = (cacheName) => { 
    let requestHeaders = httpClient.getApplicationJsonHeaders();
    let url = EHCACHE_REST_API + '?name=' + cacheName;
    httpClient.delete(url, requestHeaders, null,
      (responseBody, responseCode, responseDescription) => self.getAllCacheData(),
      (responseBody, responseCode, responseDescription) => {
        logger.error("Error clearing cache " + cacheName);
        self.getAllCacheData();
      });
  }

  /**
   * Clear all caches.
   */
  this.clearAllCaches = () => {
    let requestHeaders = httpClient.getApplicationJsonHeaders();
    httpClient.delete(EHCACHE_REST_API, requestHeaders, null,
      (responseBody, responseCode, responseDescription) => self.getAllCacheData(),
      (responseBody, responseCode, responseDescription) => {
        logger.error("Error clearing all caches");
        self.getAllCacheData();
      });
  }

  /**
   * Empty cache data div.
   */
  this.emptyCacheDataDiv = () => {
    let $cacheData = $("#cache-data");
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
  this.toggleAllCacheView = () => {
    for (let i = 0; i < self.ehcacheToggleTableRowIds.length; i++) {
      self.toggleCacheView(self.ehcacheToggleTableRowIds[i]);
    }
  }
}

/**
 * Call main.
 */
$(document).ready(main);