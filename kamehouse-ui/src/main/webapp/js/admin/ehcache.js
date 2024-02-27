/**
 * EhCache main functionality.
 * 
 * Dependencies: timeUtils, logger, kameHouse.plugin.debugger.http.
 * 
 * Manage ehcache in the current server.
 * 
 * @author nbrest
 */
class EhCacheManager {

  #ehcacheToggleTableRowIds = [
    []
  ];
  #ehcacheTableTemplate;
  #ehcacheErrorTableTemplate;

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Started initializing ehcache");
    kameHouse.util.banner.setRandomAllBanner();
    kameHouse.util.module.waitForModules(["webappTabsManager"], () => {
      kameHouse.plugin.webappTabsManager.cookiePrefix('kh-admin-ehcache');
      kameHouse.plugin.webappTabsManager.loadStateFromCookies();
    });
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger", "webappTabsManager"], () => {
      this.#init();
    });
  }

  /**
   * Get all cache data.
   */
  getAllCacheData(webapp) {
    kameHouse.logger.trace("getAllCacheData");
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, this.#getApiUrl(webapp), null, null,
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#displayCacheData(responseBody, webapp),
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#displayErrorGettingCache(webapp));
  }

  /**
   * Clear cache data.
   */
  clearCacheData(cacheName, webapp) {
    const url = this.#getApiUrl(webapp);
    const params = {
      "name" : cacheName
    };
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, url, kameHouse.http.getUrlEncodedHeaders(), params,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.plugin.modal.basicModal.openAutoCloseable("Cache " + cacheName + " cleared successfully", 3000);
        this.getAllCacheData(webapp);
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        const message = "Error clearing cache " + cacheName;
        kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
        kameHouse.plugin.modal.basicModal.openAutoCloseable("Error clearing cache " + cacheName, 3000);
        this.getAllCacheData(webapp);
      });
  }

  /**
   * Clear all caches.
   */
  clearAllCaches(webapp) {
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, this.#getApiUrl(webapp), null, null,
      (responseBody, responseCode, responseDescription, responseHeaders) => { 
        kameHouse.plugin.modal.basicModal.openAutoCloseable("All caches cleared successfully", 3000);
        this.getAllCacheData(webapp);
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        const message = "Error clearing all caches";
        kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
        kameHouse.plugin.modal.basicModal.openAutoCloseable("Error clearing all caches", 3000);
      this.getAllCacheData(webapp);
      });
  }

  /**
   * Toggle cache view (expand/collapse).
   */
  toggleCacheView(className) {  
    kameHouse.util.dom.toggleClass(className);
  }

  /**
   * Toggle cache view for all caches (expand/collapse).
   */
  toggleAllCacheView(webapp) {
    for (const className of this.#ehcacheToggleTableRowIds[webapp]) {
      this.toggleCacheView(className);
    }
  }

  /**
   * Load the templates and get the cache data.
   */
  async #init() {
    await this.#loadEhCacheTableTemplate();
    this.getAllCacheData('admin');
    this.getAllCacheData('media');
    this.getAllCacheData('tennisworld');
    this.getAllCacheData('testmodule');
    this.getAllCacheData('ui');
    this.getAllCacheData('vlcrc');
  }

  /**
   * Loads the ehcache table html snippet into a variable to be reused as a template on render.
   */
  async #loadEhCacheTableTemplate() {
    this.#ehcacheTableTemplate = await kameHouse.util.fetch.loadHtmlSnippet('/kame-house/html-snippets/ehcache-table.html');
    this.#ehcacheErrorTableTemplate = await kameHouse.util.fetch.loadHtmlSnippet('/kame-house/html-snippets/ehcache-error-table.html');
  }

  /**
   * Get ehcache api url for each webapp.
   */
  #getApiUrl(webapp) {
    if (webapp == "ui") {
      return '/kame-house/api/v1/commons/ehcache';
    } else {
      return '/kame-house-' + webapp + '/api/v1/commons/ehcache';
    }
  }

  /**
   * Display cache data.
   */
  #displayCacheData(caches, webapp) {
    this.#emptyCacheDataDiv(webapp);
    this.#ehcacheToggleTableRowIds[webapp] = [];
    const cacheData = document.getElementById("cache-data-" + webapp);
    caches.forEach((cache) => {
      kameHouse.util.dom.append(cacheData, this.#getEhCacheTableFromTemplate(cache.name));
      kameHouse.util.dom.append(cacheData, kameHouse.util.dom.getBr());
      kameHouse.util.dom.setHtmlById('ehcache-table-' + cache.name + '-header', cache.name);
      kameHouse.util.dom.setHtmlById('ehcache-table-' + cache.name + '-status-val', cache["status"]);
      kameHouse.util.dom.setHtmlById('ehcache-table-' + cache.name + '-keys-val', cache["keys"]);
      kameHouse.util.dom.setHtmlById('ehcache-table-' + cache.name + '-values-val', cache["values"]);

      kameHouse.util.dom.setClickById("clear-ehcache-table-" + cache.name, null,
        () => this.clearCacheData(cache.name, webapp)
      );
      kameHouse.util.dom.setClickById("toggle-view-ehcache-table-" + cache.name, null,
        () => this.toggleCacheView("toggle-ehcache-table-" + cache.name)
      );
      kameHouse.util.dom.toggleClass("toggle-ehcache-table-" + cache.name);
      this.#ehcacheToggleTableRowIds[webapp].push("toggle-ehcache-table-" + cache.name);
    });
  }

  /**
   * Update the ids and classes of the template ehcache table just inserted to the dom.
   */
  #getEhCacheTableFromTemplate(cacheName) {
    // Create a wrapper div to insert the table template
    const ehcacheTableDiv = kameHouse.util.dom.getElementFromTemplate(this.#ehcacheTableTemplate);
    
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
  #displayErrorGettingCache(webapp) {
    // Create a wrapper div to insert the error table template
    const ehcacheErrorTableDiv = kameHouse.util.dom.getElementFromTemplate(this.#ehcacheErrorTableTemplate);
    // Update the id
    kameHouse.util.dom.setId(ehcacheErrorTableDiv.querySelector('tr #ehcache-table-template-error-val'), "ehcache-table-" + webapp + "-error-val");
    // Attach the error table to the dom
    this.#emptyCacheDataDiv(webapp);
    const cacheData = document.getElementById("cache-data-" + webapp);
    kameHouse.util.dom.append(cacheData, ehcacheErrorTableDiv);
    // Update the message
    kameHouse.util.dom.setHtmlById("ehcache-table-" + webapp + "-error-val", kameHouse.util.time.getTimestamp() +
      " : Error retrieving cache data. Please try again later.");

    const message = "Error retrieving cache data. Please try again later.";
    kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
  }

  /**
   * Empty cache data div.
   */
  #emptyCacheDataDiv(webapp) {
    const cacheData = document.getElementById("cache-data-" + webapp);
    kameHouse.util.dom.empty(cacheData);
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("ehCacheManager", new EhCacheManager());
});