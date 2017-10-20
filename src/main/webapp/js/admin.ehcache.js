/**
 * Admin Ehcache main function.
 * 
 * @author nbrest
 */
var ehcacheToggleTableRowIds = [];
var EHCACHE_REST_API = '/kame-house/api/v1/admin/ehcache';

var main = function() {
  getCacheData();
};

/**
 * Get cache data.
 */
function getCacheData() {
  $.get(EHCACHE_REST_API)
    .success(function(result) {
      displayCacheData(result);
    })
    .error(function(jqXHR, textStatus, errorThrown) {
      displayErrorGettingCache();
    });
}

/**
 * Display cache data.
 */
function displayCacheData(caches) {
  emptyCacheDataDiv();
  ehcacheToggleTableRowIds = [];
  var $cacheData = $("#cache-data");
  caches.forEach(function(cache) {
    var $cacheTable = $('<table id="table-' + cache.name +
      '" class="table table-bordered table-ehcache">');
    var $cacheTableRow;

    $cacheTableRow = $("<tr>");
    $cacheTableRow.append($('<td class="td-ehcache-header">').text("name"));
    $cacheTableRowContent = $("<td>");
    $cacheTableRowContent.text(cache.name);
    $cacheTableRowContent.append("<input id='clear-" + cache.name +
      "' type='button' value='Clear Cache' class='btn btn-outline-danger table-ehcache-button' />");
    $cacheTableRowContent.append("<input id='toggle-view-" + cache.name +
      "' type='button' value='Expand/Collapse' " +
      "class='btn btn-outline-secondary table-ehcache-button' />");
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
  emptyCacheDataDiv();
  var $cacheData = $("#cache-data");
  var $errorTable = $('<table class="table table-bordered table-responsive table-ehcache">');
  var $errorTableRow = $("<tr>");
  $errorTableRow.append($('<td>').text(getTimestamp() +
    " : Error retrieving cache data. Please try again later."));
  $errorTable.append($errorTableRow);
  $cacheData.append($errorTable);
  console.error(getTimestamp() + " : Error retrieving cache data. Please try again later.");
}

/**
 * Clear cache data.
 */
function clearCacheData(cacheName) {
  //console.debug("Clearing " + cacheName);
  $.ajax({
    beforeSend : function(request) {
      request.setRequestHeader(getCsrfHeader(), getCsrfToken());
    },
    url : EHCACHE_REST_API + '?name=' + cacheName,
    type : 'DELETE',
    success : function(result) {
      getCacheData();
    },
    error : function(result) {
      console.error("Error clearing cache " + cacheName);
      getCacheData();
    }
  });
}

/**
 * Clear all caches.
 */
function clearAllCaches() {
  $.ajax({
    beforeSend : function(request) {
      request.setRequestHeader(getCsrfHeader(), getCsrfToken());
    },
    url : EHCACHE_REST_API,
    type : 'DELETE',
    success : function(result) {
      getCacheData();
    },
    error : function(result) {
      console.error("Error clearing all caches");
      getCacheData();
    }
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
 * Get timestamp.
 */
function getTimestamp() {
  return new Date().toISOString().replace("T", " ").slice(0, 19);
}

/**
 * Get CSRF token.
 */
function getCsrfToken() {
  var token = $("meta[name='_csrf']").attr("content");
  //console.log("getCsrfToken: " + token);
  return token;
}

/**
 * Get CSRF header.
 */
function getCsrfHeader() {
  var header = $("meta[name='_csrf_header']").attr("content");
  //console.log("getCsrfHeader: " + header);
  return header;
}

/**
 * Call main.
 */
$(document).ready(main);