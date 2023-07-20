package com.nicobrest.kamehouse.commons.service;

import static com.nicobrest.kamehouse.commons.utils.StringUtils.sanitizeInput;

import com.nicobrest.kamehouse.commons.model.ApplicationCache;
import java.util.ArrayList;
import java.util.List;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service class to manage the ehcache in the system.
 *
 * @author nbrest
 */
@Service
public class EhCacheService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  @Qualifier("ehCacheManager")
  private CacheManager ehCacheManager;

  /**
   * Returns the cache information of the cache specified as a parameter.
   */
  public ApplicationCache get(String cacheName) {
    logger.trace("get {}", cacheName);
    CacheManager cacheManager = getCacheManager();
    Cache cache = cacheManager.getCache(cacheName, java.lang.Long.class,
        com.nicobrest.kamehouse.commons.model.KameHouseUser.class);
    ApplicationCache applicationCache = getCacheInformation(cache);
    logger.trace("get {} response {}", cacheName, applicationCache);
    return applicationCache;
  }

  /**
   * Returns the status of all the ehcaches.
   */
  public List<ApplicationCache> getAll() {
    logger.trace("getAll");
    //TODO UPGRADE BROKEN
    //CacheManager cacheManager = getCacheManager();
    String[] cacheNames = {"testCommonsCache"}; //cacheManager.getCacheNames();
    List<ApplicationCache> cacheList = new ArrayList<>();
    for (int i = 0; i < cacheNames.length; i++) {
      ApplicationCache applicationCache = get(cacheNames[i]);
      if (applicationCache != null) {
        cacheList.add(applicationCache);
      }
    }
    logger.trace("getAll response {}", cacheList);
    return cacheList;
  }

  /**
   * Clears the ehcache specified as a parameter.
   */
  public void clear(String cacheName) {
    logger.trace("clear {}", cacheName);
    CacheManager cacheManager = getCacheManager();
    Cache cache = cacheManager.getCache(cacheName, java.lang.Long.class,
        com.nicobrest.kamehouse.commons.model.KameHouseUser.class);
    if (cache != null) {
      cache.clear();
      logger.trace("clear {} successfully", cacheName);
    } else {
      if (logger.isWarnEnabled()) {
        logger.warn("cache {} not found", sanitizeInput(cacheName));
      }
    }
  }

  /**
   * Clears all the ehcaches.
   */
  public void clearAll() {
    logger.trace("clearAll");
    //CacheManager cacheManager = getCacheManager();
    //TODO UPGRADE BROKEN
    String[] cacheNames = {"testCommonsCache"}; //cacheManager.getCacheNames();
    for (int i = 0; i < cacheNames.length; i++) {
      clear(cacheNames[i]);
    }
  }

  /**
   * Get the internal cache manager.
   */
  private CacheManager getCacheManager() {
    return ehCacheManager;
  }

  /**
   * Populates the map that represents the cache with the cache information.
   */
  private ApplicationCache getCacheInformation(Cache cache) {
    ApplicationCache applicationCache = null;
    if (cache != null) {
      applicationCache = new ApplicationCache();
      //TODO UPGRADE BROKEN
      applicationCache.setName(null);
      applicationCache.setStatus(null); //cache.getStatus().toString());
      //List<String> cacheValues = applicationCache.getValues();
      //List<?> cacheKeys = null; //cache.getKeys();
      //for (Object key : cacheKeys) {
      //  Element cacheElement = cache.get(key);
      //  if (cacheElement != null && cacheElement.getObjectValue() != null) {
      //    cacheValues.add(cacheElement.getObjectValue().toString());
      //  }
      //}
      applicationCache.setKeys(null); //cache.getKeys().toString());
    }
    return applicationCache;
  }
}
