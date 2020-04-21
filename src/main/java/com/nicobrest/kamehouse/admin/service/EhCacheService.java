package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.admin.model.ApplicationCache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class to manage the ehcache in the system.
 * 
 * @author nbrest
 *
 */
@Service
public class EhCacheService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  @Qualifier("cacheManager")
  private EhCacheCacheManager cacheManager;

  public void setCacheManager(EhCacheCacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  public EhCacheCacheManager getCacheManager() {
    return cacheManager;
  }

  /**
   * Returns the cache information of the cache specified as a parameter.
   */
  public ApplicationCache get(String cacheName) {
    logger.trace("Getting information for cache: {}", cacheName);
    Cache cache = cacheManager.getCacheManager().getCache(cacheName);
    ApplicationCache applicationCache = getCacheInformation(cache);
    if (applicationCache != null) {
      logger.trace(applicationCache.toString());
    }
    return applicationCache;
  }

  /**
   * Returns the status of all the ehcaches.
   */
  public List<ApplicationCache> getAll() {
    logger.trace("Getting information for all caches");
    String[] cacheNames = cacheManager.getCacheManager().getCacheNames();
    List<ApplicationCache> cacheList = new ArrayList<>();

    for (int i = 0; i < cacheNames.length; i++) {
      ApplicationCache applicationCache = get(cacheNames[i]);
      if (applicationCache != null) {
        cacheList.add(applicationCache);
      }
    }
    logger.trace(cacheList.toString());
    return cacheList;
  }

  /**
   * Clears the ehcache specified as a parameter.
   */
  public void clear(String cacheName) {
    logger.trace("Clearing cache: {}", cacheName);
    Cache cache = cacheManager.getCacheManager().getCache(cacheName);
    if (cache != null) {
      cache.removeAll();
    }
  }

  /**
   * Clears all the ehcaches.
   */
  public void clearAll() {
    logger.trace("Clearing all caches");
    String[] cacheNames = cacheManager.getCacheManager().getCacheNames();
    for (int i = 0; i < cacheNames.length; i++) {
      clear(cacheNames[i]);
    }
  }

  /**
   * Populates the map that represents the cache with the cache information.
   */
  private ApplicationCache getCacheInformation(Cache cache) {
    ApplicationCache applicationCache = null;
    if (cache != null) {
      applicationCache = new ApplicationCache();
      applicationCache.setName(cache.getName());
      applicationCache.setStatus(cache.getStatus().toString());
      List<String> cacheValues = applicationCache.getValues();
      List<?> cacheKeys = cache.getKeys();
      for (Object key : cacheKeys) {
        Element cacheElement = cache.get(key);
        if (cacheElement != null) {
          cacheValues.add(cacheElement.getObjectValue().toString());
        }
      }
      applicationCache.setKeys(cache.getKeys().toString());
    }
    return applicationCache;
  }
}
