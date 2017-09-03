var ehcacheToggleTableRowIds = [];
var main = function() {
  getCacheData();
};

function getCacheData() {
  $.get("/kame-house/api/v1/admin/ehcache/status")
    .success(function(result) {
      displayCacheData(result);
    })
    .error(function(jqXHR, textStatus, errorThrown) {
      displayErrorGettingCache();
    });
}

function displayCacheData(caches) {
  emptyCacheDataDiv();
  ehcacheToggleTableRowIds = [];
  var $cacheData = $("#cache-data");
  caches.forEach(function(cache) {
    var $cacheTable = $('<table id="table-' + cache.name + '" class="table table-bordered table-ehcache">');
    var $cacheTableRow;

    $cacheTableRow = $("<tr>");
    $cacheTableRow.append($('<td class="td-ehcache-header">').text("name"));
    $cacheTableRowValue = $("<td>");
    $cacheTableRowValue.text(cache.name);
    $cacheTableRowValue.append("<input id='clear-" + cache.name + "' type='button' value='Clear Cache' class='btn btn-outline-danger table-ehcache-button' />");
    $cacheTableRowValue.append("<input id='toggle-view-" + cache.name + "' type='button' value='Expand/Collapse' class='btn btn-outline-secondary table-ehcache-button' />");
    $cacheTableRow.append($cacheTableRowValue);
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

    $("#clear-" + cache.name).click(function() {
      clearCacheData(cache.name);
    });
    $("#toggle-view-" + cache.name).click(function() {
      toggleCacheView(".toggle-" + cache.name);
    });
    ehcacheToggleTableRowIds.push(".toggle-" + cache.name);
  });
}

function displayErrorGettingCache() {
  emptyCacheDataDiv();
  var $cacheData = $("#cache-data");
  var $errorTable = $('<table class="table table-bordered table-responsive table-ehcache">');
  var $errorTableRow = $("<tr>");
  $errorTableRow.append($('<td>').text("Error retrieving cache data. Please try again later."));
  $errorTable.append($errorTableRow);
  $cacheData.append($errorTable);
  console.error("Error retrieving cache data. Please try again later.");
}

function clearCacheData(cacheName) {
  console.log("Clearing " + cacheName);
  $.ajax({
    url : '/kame-house/api/v1/admin/ehcache/clear?name=' + cacheName,
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

function clearAllCaches() {
  $.ajax({
    url : '/kame-house/api/v1/admin/ehcache/clear',
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

function emptyCacheDataDiv() {
  var $cacheData = $("#cache-data");
  $cacheData.empty();
}

function toggleCacheView(className) {
    $(className).toggle();
}

function toggleAllCacheView() {
  for (var i = 0; i < ehcacheToggleTableRowIds.length; i++) { 
    toggleCacheView(ehcacheToggleTableRowIds[i]);
  } 
}

$(document).ready(main);