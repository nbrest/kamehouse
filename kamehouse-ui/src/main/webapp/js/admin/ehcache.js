/**
 * EhCache main function.
 * 
 * Dependencies: timeUtils, logger, kameHouse.plugin.debugger.http.
 * 
 * @author nbrest
 */
/**
 * Manage ehcache in the current server.
 */
function EhCacheManager() {

  this.load = load;
  this.getAllCacheData = getAllCacheData;
  this.clearCacheData = clearCacheData;
  this.clearAllCaches = clearAllCaches;
  this.toggleCacheView = toggleCacheView;
  this.toggleAllCacheView = toggleAllCacheView;

  const ehcacheToggleTableRowIds = [
    []
  ];
  let ehcacheTableTemplate;
  let ehcacheErrorTableTemplate;

  function load() {
    kameHouse.logger.info("Started initializing ehcache");
    kameHouse.util.banner.setRandomAllBanner();
    kameHouse.util.module.waitForModules(["webappTabsManager"], () => {
      kameHouse.plugin.webappTabsManager.setCookiePrefix('kh-admin-ehcache');
      kameHouse.plugin.webappTabsManager.loadStateFromCookies();
    });
    kameHouse.util.module.waitForModules(["kameHouseDebugger", "webappTabsManager"], () => {
      init();
    });
  }

  /**
   * Load the templates and get the cache data.
   */
  async function init() {
    await loadEhCacheTableTemplate();
    getAllCacheData('admin');
    getAllCacheData('media');
    getAllCacheData('tennisworld');
    getAllCacheData('testmodule');
    getAllCacheData('ui');
    getAllCacheData('vlcrc');
  }

  /**
   * Loads the ehcache table html snippet into a variable to be reused as a template on render.
   */
  async function loadEhCacheTableTemplate() {
    ehcacheTableTemplate = await kameHouse.util.fetch.loadHtmlSnippet('/kame-house/html-snippets/ehcache-table.html');
    ehcacheErrorTableTemplate = await kameHouse.util.fetch.loadHtmlSnippet('/kame-house/html-snippets/ehcache-error-table.html');
  }

  /**
   * Get ehcache api url for each webapp.
   */
  function getApiUrl(webapp) {
    if (webapp == "ui") {
      return '/kame-house/api/v1/commons/ehcache';
    } else {
      return '/kame-house-' + webapp + '/api/v1/commons/ehcache';
    }
  }

  /**
   * Get all cache data.
   */
  function getAllCacheData(webapp) {
    kameHouse.logger.trace("getAllCacheData");
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, getApiUrl(webapp), null, null,
      (responseBody, responseCode, responseDescription, responseHeaders) => displayCacheData(responseBody, webapp),
      (responseBody, responseCode, responseDescription, responseHeaders) => displayErrorGettingCache(webapp));
  }

  /**
   * Display cache data.
   */
  function displayCacheData(caches, webapp) {
    emptyCacheDataDiv(webapp);
    ehcacheToggleTableRowIds[webapp] = [];
    const $cacheData = $("#cache-data-" + webapp);
    caches.forEach((cache) => {
      kameHouse.util.dom.append($cacheData, getEhCacheTableFromTemplate(cache.name));
      kameHouse.util.dom.append($cacheData, $(kameHouse.util.dom.getBr()));

      kameHouse.util.dom.setHtml($('#ehcache-table-' + cache.name + '-header'), cache.name);
      kameHouse.util.dom.setHtml($('#ehcache-table-' + cache.name + '-status-val'), cache["status"]);
      kameHouse.util.dom.setHtml($('#ehcache-table-' + cache.name + '-keys-val'), cache["keys"]);
      kameHouse.util.dom.setHtml($('#ehcache-table-' + cache.name + '-values-val'), cache["values"]);

      kameHouse.util.dom.setClick($("#clear-ehcache-table-" + cache.name), null,
        () => clearCacheData(cache.name, webapp)
      );
      kameHouse.util.dom.setClick($("#toggle-view-ehcache-table-" + cache.name), null,
        () => toggleCacheView("toggle-ehcache-table-" + cache.name)
      );
      kameHouse.util.dom.toggle("toggle-ehcache-table-" + cache.name);
      ehcacheToggleTableRowIds[webapp].push("toggle-ehcache-table-" + cache.name);
    });
  }

  /**
   * Update the ids and classes of the template ehcache table just inserted to the dom.
   */
  function getEhCacheTableFromTemplate(cacheName) {
    // Create a wrapper div to insert the table template
    const ehcacheTableDiv = kameHouse.util.dom.getElementFromTemplate(ehcacheTableTemplate);
    
    // Update the ids and classes on the table generated from the template
    kameHouse.util.dom.setId(ehcacheTableDiv, "ehcache-table-" + cacheName);
    kameHouse.util.dom.setId(ehcacheTableDiv.querySelector('tr td #ehcache-table-template-header'), "ehcache-table-" + cacheName + "-header");
    kameHouse.util.dom.setId(ehcacheTableDiv.querySelector('tr td #clear-ehcache-table-template'), "clear-ehcache-table-" + cacheName);
    kameHouse.util.dom.setId(ehcacheTableDiv.querySelector('tr td #toggle-view-ehcache-table-template'), "toggle-view-ehcache-table-" + cacheName);
    kameHouse.util.dom.setId(ehcacheTableDiv.querySelector('tr #ehcache-table-template-status-val'), "ehcache-table-" + cacheName + "-status-val");
    kameHouse.util.dom.setId(ehcacheTableDiv.querySelector('tr #ehcache-table-template-keys-val'), "ehcache-table-" + cacheName + "-keys-val");
    kameHouse.util.dom.setId(ehcacheTableDiv.querySelector('tr #ehcache-table-template-values-val'), "ehcache-table-" + cacheName + "-values-val");

    const toggeableClasses = ehcacheTableDiv.getElementsByClassName("toggle-ehcache-table-template");
    for (let i = 0; i < toggeableClasses.length; i++) {
      kameHouse.util.dom.classListAdd(toggeableClasses.item(i), "toggle-ehcache-table-" + cacheName);
    }
    for (let i = 0; i < toggeableClasses.length; i++) {
      kameHouse.util.dom.classListRemove(toggeableClasses.item(i), "toggle-ehcache-table-template");
    }
   
    return ehcacheTableDiv;
  }

  /**
   * Display error getting cache data.
   */
  function displayErrorGettingCache(webapp) {
    // Create a wrapper div to insert the error table template
    const ehcacheErrorTableDiv = kameHouse.util.dom.getElementFromTemplate(ehcacheErrorTableTemplate);
    // Update the id
    kameHouse.util.dom.setId(ehcacheErrorTableDiv.querySelector('tr #ehcache-table-template-error-val'), "ehcache-table-" + webapp + "-error-val");
    // Attach the error table to the dom
    emptyCacheDataDiv(webapp);
    const $cacheData = $("#cache-data-" + webapp);
    kameHouse.util.dom.append($cacheData, ehcacheErrorTableDiv);
    // Update the message
    kameHouse.util.dom.setHtml($("#ehcache-table-" + webapp + "-error-val"), kameHouse.util.time.getTimestamp() +
      " : Error retrieving cache data. Please try again later.");

    kameHouse.logger.error("Error retrieving cache data. Please try again later.");
  }

  /**
   * Clear cache data.
   */
  function clearCacheData(cacheName, webapp) {
    const url = getApiUrl(webapp);
    const params = {
      "name" : cacheName
    };
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, url, kameHouse.http.getUrlEncodedHeaders(), params,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.plugin.modal.basicModal.openAutoCloseable("Cache " + cacheName + " cleared successfully", 3000);
        getAllCacheData(webapp);
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.error("Error clearing cache " + cacheName);
        kameHouse.plugin.modal.basicModal.openAutoCloseable("Error clearing cache " + cacheName, 3000);
        getAllCacheData(webapp);
      });
  }

  /**
   * Clear all caches.
   */
  function clearAllCaches(webapp) {
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, getApiUrl(webapp), null, null,
      (responseBody, responseCode, responseDescription, responseHeaders) => { 
        kameHouse.plugin.modal.basicModal.openAutoCloseable("All caches cleared successfully", 3000);
        getAllCacheData(webapp);
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.error("Error clearing all caches");
        kameHouse.plugin.modal.basicModal.openAutoCloseable("Error clearing all caches", 3000);
        getAllCacheData(webapp);
      });
  }

  /**
   * Empty cache data div.
   */
  function emptyCacheDataDiv(webapp) {
    const $cacheData = $("#cache-data-" + webapp);
    kameHouse.util.dom.empty($cacheData);
  }

  /**
   * Toggle cache view (expand/collapse).
   */
  function toggleCacheView(className) {
    kameHouse.util.dom.toggle(className);
  }

  /**
   * Toggle cache view for all caches (expand/collapse).
   */
  function toggleAllCacheView(webapp) {
    for (const className of ehcacheToggleTableRowIds[webapp]) {
      toggleCacheView(className);
    }
  }
}

$(document).ready(() => {
  kameHouse.addExtension("ehCacheManager", new EhCacheManager());
});