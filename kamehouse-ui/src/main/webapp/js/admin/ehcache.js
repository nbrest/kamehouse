/**
 * EhCache main function.
 * 
 * Dependencies: timeUtils, logger, debuggerHttpClient.
 * 
 * @author nbrest
 */
var ehCacheManager;

var main = () => {
  bannerUtils.setRandomPrinceOfTennisBanner();
  moduleUtils.waitForModules(["logger", "debuggerHttpClient", "kameHouseWebappTabsManager"], () => {
    logger.info("Started initializing ehcache");
    ehCacheManager = new EhCacheManager();
    kameHouseWebappTabsManager.setCookiePrefix('kh-admin-ehcache');
    kameHouseWebappTabsManager.loadStateFromCookies();
    ehCacheManager.init();
  });
};

/**
 * Manage ehcache in the current server.
 */
function EhCacheManager() {
  let self = this;
  this.ehcacheToggleTableRowIds = [
    []
  ];
  this.ehcacheTableTemplate;
  this.ehcacheErrorTableTemplate;

  /**
   * Load the templates and get the cache data.
   */
  this.init = async () => {
    await self.loadEhCacheTableTemplate();
    ehCacheManager.getAllCacheData('admin');
    ehCacheManager.getAllCacheData('media');
    ehCacheManager.getAllCacheData('tennisworld');
    ehCacheManager.getAllCacheData('testmodule');
    ehCacheManager.getAllCacheData('ui');
    ehCacheManager.getAllCacheData('vlcrc');
  }

  /**
   * Loads the ehcache table html snippet into a variable to be reused as a template on render.
   */
  this.loadEhCacheTableTemplate = async () => {
    const ehcacheTableResponse = await fetch('/kame-house/html-snippets/ehcache-table.html');
    self.ehcacheTableTemplate = await ehcacheTableResponse.text();
    const ehcacheErrorTableResponse = await fetch('/kame-house/html-snippets/ehcache-error-table.html');
    self.ehcacheErrorTableTemplate = await ehcacheErrorTableResponse.text();
  }

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
    debuggerHttpClient.get(self.getApiUrl(webapp),
      (responseBody, responseCode, responseDescription) => self.displayCacheData(responseBody, webapp),
      (responseBody, responseCode, responseDescription) => self.displayErrorGettingCache(webapp));
  }

  /**
   * Display cache data.
   */
  this.displayCacheData = (caches, webapp) => {
    self.emptyCacheDataDiv(webapp);
    self.ehcacheToggleTableRowIds[webapp] = [];
    let $cacheData = $("#cache-data-" + webapp);
    caches.forEach((cache) => {
      domUtils.append($cacheData, self.getEhCacheTableFromTemplate(cache.name));
      domUtils.append($cacheData, $(domUtils.getBr()));

      domUtils.setHtml($('#ehcache-table-' + cache.name + '-header'), cache.name);
      domUtils.setHtml($('#ehcache-table-' + cache.name + '-status-val'), cache["status"]);
      domUtils.setHtml($('#ehcache-table-' + cache.name + '-keys-val'), cache["keys"]);
      domUtils.setHtml($('#ehcache-table-' + cache.name + '-values-val'), cache["values"]);

      domUtils.setClick($("#clear-ehcache-table-" + cache.name), null,
        () => self.clearCacheData(cache.name, webapp)
      );
      domUtils.setClick($("#toggle-view-ehcache-table-" + cache.name), null,
        () => self.toggleCacheView(".toggle-ehcache-table-" + cache.name)
      );
      $(".toggle-ehcache-table-" + cache.name).toggle();
      self.ehcacheToggleTableRowIds[webapp].push(".toggle-ehcache-table-" + cache.name);
    });
  }

  /**
   * Update the ids and classes of the template ehcache table just inserted to the dom.
   */
  this.getEhCacheTableFromTemplate = (cacheName) => {
    // Create a wrapper div to insert the table template
    let ehcacheTableDiv = domUtils.getElementFromTemplate(self.ehcacheTableTemplate);
    
    // Update the ids and classes on the table generated from the template
    ehcacheTableDiv.id = "ehcache-table-" + cacheName;
    ehcacheTableDiv.querySelector('tr td #ehcache-table-template-header').id = "ehcache-table-" + cacheName + "-header";
    ehcacheTableDiv.querySelector('tr td #clear-ehcache-table-template').id = "clear-ehcache-table-" + cacheName;
    ehcacheTableDiv.querySelector('tr td #toggle-view-ehcache-table-template').id = "toggle-view-ehcache-table-" + cacheName;
    ehcacheTableDiv.querySelector('tr #ehcache-table-template-status-val').id = "ehcache-table-" + cacheName + "-status-val";
    ehcacheTableDiv.querySelector('tr #ehcache-table-template-keys-val').id = "ehcache-table-" + cacheName + "-keys-val";
    ehcacheTableDiv.querySelector('tr #ehcache-table-template-values-val').id = "ehcache-table-" + cacheName + "-values-val";

    let toggeableClasses = ehcacheTableDiv.getElementsByClassName("toggle-ehcache-table-template")
    for (var i = 0; i < toggeableClasses.length; i++) {
      domUtils.classListAdd(toggeableClasses.item(i), "toggle-ehcache-table-" + cacheName);
    }
    for (var i = 0; i < toggeableClasses.length; i++) {
      domUtils.classListRemove(toggeableClasses.item(i), "toggle-ehcache-table-template");
    }
   
    return ehcacheTableDiv;
  }

  /**
   * Display error getting cache data.
   */
  this.displayErrorGettingCache = (webapp) => {
    // Create a wrapper div to insert the error table template
    let ehcacheErrorTableDiv = domUtils.getElementFromTemplate(self.ehcacheErrorTableTemplate);
    // Update the id
    ehcacheErrorTableDiv.querySelector('tr #ehcache-table-template-error-val').id = "ehcache-table-" + webapp + "-error-val";
    // Attach the error table to the dom
    self.emptyCacheDataDiv(webapp);
    let $cacheData = $("#cache-data-" + webapp);
    domUtils.append($cacheData, ehcacheErrorTableDiv);
    // Update the message
    domUtils.setHtml($("#ehcache-table-" + webapp + "-error-val"), timeUtils.getTimestamp() +
      " : Error retrieving cache data. Please try again later.");

    logger.error("Error retrieving cache data. Please try again later.");
  }

  /**
   * Clear cache data.
   */
  this.clearCacheData = (cacheName, webapp) => {
    let url = self.getApiUrl(webapp) + '?name=' + cacheName;
    debuggerHttpClient.delete(url, null,
      (responseBody, responseCode, responseDescription) => {
        basicKamehouseModal.openAutoCloseable("Cache " + cacheName + " cleared successfully", 3000);
        self.getAllCacheData(webapp);
      },
      (responseBody, responseCode, responseDescription) => {
        logger.error("Error clearing cache " + cacheName);
        basicKamehouseModal.openAutoCloseable("Error clearing cache " + cacheName, 3000);
        self.getAllCacheData(webapp);
      });
  }

  /**
   * Clear all caches.
   */
  this.clearAllCaches = (webapp) => {
    debuggerHttpClient.delete(self.getApiUrl(webapp), null,
      (responseBody, responseCode, responseDescription) => { 
        basicKamehouseModal.openAutoCloseable("All caches cleared successfully", 3000);
        self.getAllCacheData(webapp);
      },
      (responseBody, responseCode, responseDescription) => {
        logger.error("Error clearing all caches");
        basicKamehouseModal.openAutoCloseable("Error clearing all caches", 3000);
        self.getAllCacheData(webapp);
      });
  }

  /**
   * Empty cache data div.
   */
  this.emptyCacheDataDiv = (webapp) => {
    let $cacheData = $("#cache-data-" + webapp);
    domUtils.empty($cacheData);
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