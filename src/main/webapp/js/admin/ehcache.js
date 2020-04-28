/**
 * Admin Ehcache main function.
 * 
 * @author nbrest
 */
var ehcacheToggleTableRowIds = [];
var EHCACHE_REST_API = '/kame-house/api/v1/admin/ehcache';

var main = function() {
  importEhcacheCss();
  initKameHouse(getCacheData);
};

/** Import ehcache css */
function importEhcacheCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/ehcache.css">');
}

/**
 * Get cache data.
 */
function getCacheData() {
  logger.traceFunctionCall();
  httpClient.get(EHCACHE_REST_API, null,
    function success(responseBody, responseCode, responseDescription) {
      displayCacheData(responseBody);
    },
    function error(responseBody, responseCode, responseDescription) {
      displayErrorGettingCache();
    });
}

/**
 * Display cache data.
 */
function displayCacheData(caches) {
  logger.traceFunctionCall();
  emptyCacheDataDiv();
  ehcacheToggleTableRowIds = [];
  var $cacheData = $("#cache-data");
  caches.forEach(function(cache) {
    var $cacheTable = $('<table id="table-' + cache.name +
      '" class="table table-bordered table-ehcache table-bordered-kh">');
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

    var cacheTableHeaders = [ "status", "keys", "values" ];
    for (var i = 0; i < cacheTableHeaders.length; i++) {
      $cacheTableRow = $('<tr class="toggle-' + cache.name + '">');
      $cacheTableRow.append($('<td class="td-ehcache-header">').text(cacheTableHeaders[i]));
      $cacheTableRow.append($("<td>").text(cache[cacheTableHeaders[i]]));
      $cacheTable.append($cacheTableRow);
    }
    $cacheData.append($cacheTable);
    $cacheData.append("<br>");

    $("#clear-" + cache.name).click(function() {
      clearCacheData(cache.name);
    });
    $("#toggle-view-" + cache.name).click(function() {
      toggleCacheView(".toggle-" + cache.name);
    });
    ehcacheToggleTableRowIds.push(".toggle-" + cache.name);
  });
}

/**
 * Display error getting cache data.
 */
function displayErrorGettingCache() {
  logger.traceFunctionCall();
  emptyCacheDataDiv();
  var $cacheData = $("#cache-data");
  var $errorTable = $('<table class="table table-bordered table-responsive table-ehcache">');
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
function clearCacheData(cacheName) {
  logger.traceFunctionCall();
  var requestHeaders = httpClient.getCsrfRequestHeadersObject();
  var url = EHCACHE_REST_API + '?name=' + cacheName;
  httpClient.delete(url, requestHeaders, null,
    function success(responseBody, responseCode, responseDescription) {
      getCacheData();
    },
    function error(responseBody, responseCode, responseDescription) {
      logger.error("Error clearing cache " + cacheName);
      getCacheData();
    });
}

/**
 * Clear all caches.
 */
function clearAllCaches() {
  logger.traceFunctionCall();
  var requestHeaders = httpClient.getCsrfRequestHeadersObject();
  httpClient.delete(EHCACHE_REST_API, requestHeaders, null,
    function success(responseBody, responseCode, responseDescription) {
      getCacheData();
    },
    function error(responseBody, responseCode, responseDescription) {
      logger.error("Error clearing all caches");
      getCacheData();
    });
}

/**
 * Empty cache data div.
 */
function emptyCacheDataDiv() {
  var $cacheData = $("#cache-data");
  $cacheData.empty();
}

/**
 * Toggle cache view (expand/collapse).
 */
function toggleCacheView(className) {
  $(className).toggle();
}

/**
 * Toggle cache view for all caches (expand/collapse).
 */
function toggleAllCacheView() {
  for (var i = 0; i < ehcacheToggleTableRowIds.length; i++) {
    toggleCacheView(ehcacheToggleTableRowIds[i]);
  }
}

/**
 * Call main.
 */
$(document).ready(main);