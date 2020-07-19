/**
 * Admin EhCache main function.
 * 
 * Dependencies: timeUtils, logger, httpClient.
 * 
 * @author nbrest
 */
var ehCacheManager;

var main = function () {
  importEhcacheCss();
  var loadingModules = ["timeUtils", "logger", "httpClient"];
  waitForModules(loadingModules, function initEhCache() {
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
  this.ehcacheToggleTableRowIds = [];
  var EHCACHE_REST_API = '/kame-house/api/v1/admin/ehcache';

  /**
   * Get all cache data.
   */
  this.getAllCacheData = function getAllCacheData() {
    logger.traceFunctionCall();
    httpClient.get(EHCACHE_REST_API, null,
      function success(responseBody, responseCode, responseDescription) {
        self.displayCacheData(responseBody);
      },
      function error(responseBody, responseCode, responseDescription) {
        self.displayErrorGettingCache();
      });
  }

  /**
   * Display cache data.
   */
  this.displayCacheData = function displayCacheData(caches) {
    logger.traceFunctionCall();
    self.emptyCacheDataDiv();
    self.ehcacheToggleTableRowIds = [];
    var $cacheData = $("#cache-data");
    caches.forEach(function (cache) {
      var $cacheTable = $('<table id="table-' + cache.name +
        '" class="table table-bordered table-ehcache table-bordered-kh table-responsive-kh table-responsive">');
      var $cacheTableRow;

      $cacheTableRow = $("<tr>");
      $cacheTableRow.append($('<td class="td-ehcache-header">').text("name"));
      var $cacheTableRowContent = $("<td>");
      $cacheTableRowContent.text(cache.name);
      $cacheTableRowContent.append("<input id='clear-" + cache.name +
        "' type='button' value='Clear Cache' class='btn btn-outline-danger table-ehcache-button btn-borderless' />");
      $cacheTableRowContent.append("<input id='toggle-view-" + cache.name +
        "' type='button' value='Expand/Collapse' " +
        "class='btn btn-outline-secondary table-ehcache-button btn-borderless' />");
      $cacheTableRow.append($cacheTableRowContent);
      $cacheTable.append($cacheTableRow);

      var cacheTableHeaders = ["status", "keys", "values"];
      for (var i = 0; i < cacheTableHeaders.length; i++) {
        $cacheTableRow = $('<tr class="toggle-' + cache.name + '">');
        $cacheTableRow.append($('<td class="td-ehcache-header">').text(cacheTableHeaders[i]));
        $cacheTableRow.append($("<td>").text(cache[cacheTableHeaders[i]]));
        $cacheTable.append($cacheTableRow);
      }
      $cacheData.append($cacheTable);
      $cacheData.append("<br>");

      $("#clear-" + cache.name).click(function () {
        self.clearCacheData(cache.name);
      });
      $("#toggle-view-" + cache.name).click(function () {
        self.toggleCacheView(".toggle-" + cache.name);
      });
      self.ehcacheToggleTableRowIds.push(".toggle-" + cache.name);
    });
  }

  /**
   * Display error getting cache data.
   */
  this.displayErrorGettingCache = function displayErrorGettingCache() {
    logger.traceFunctionCall();
    self.emptyCacheDataDiv();
    var $cacheData = $("#cache-data");
    var $errorTable = $('<table class="table table-bordered table-ehcache table-responsive-kh table-responsive">');
    var $errorTableRow = $("<tr>");
    $errorTableRow.append($('<td>').text(timeUtils.getTimestamp() +
      " : Error retrieving cache data. Please try again later."));
    $errorTable.append($errorTableRow);
    $cacheData.append($errorTable);
    logger.error("Error retrieving cache data. Please try again later.");
  }

  /**
   * Clear cache data.
   */
  this.clearCacheData = function clearCacheData(cacheName) {
    logger.traceFunctionCall();
    var requestHeaders = httpClient.getApplicationJsonHeaders();
    var url = EHCACHE_REST_API + '?name=' + cacheName;
    httpClient.delete(url, requestHeaders, null,
      function success(responseBody, responseCode, responseDescription) {
        self.getAllCacheData();
      },
      function error(responseBody, responseCode, responseDescription) {
        logger.error("Error clearing cache " + cacheName);
        self.getAllCacheData();
      });
  }

  /**
   * Clear all caches.
   */
  this.clearAllCaches = function clearAllCaches() {
    logger.traceFunctionCall();
    var requestHeaders = httpClient.getApplicationJsonHeaders();
    httpClient.delete(EHCACHE_REST_API, requestHeaders, null,
      function success(responseBody, responseCode, responseDescription) {
        self.getAllCacheData();
      },
      function error(responseBody, responseCode, responseDescription) {
        logger.error("Error clearing all caches");
        self.getAllCacheData();
      });
  }

  /**
   * Empty cache data div.
   */
  this.emptyCacheDataDiv = () => {
    var $cacheData = $("#cache-data");
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
    for (var i = 0; i < self.ehcacheToggleTableRowIds.length; i++) {
      self.toggleCacheView(self.ehcacheToggleTableRowIds[i]);
    }
  }
}

/**
 * Call main.
 */
$(document).ready(main);