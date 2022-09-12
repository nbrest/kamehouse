package com.nicobrest.kamehouse.commons.service;

import static com.nicobrest.kamehouse.commons.utils.StringUtils.sanitizeInput;

import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.model.ApplicationCache;
import java.util.ArrayList;
import java.util.List;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Service;

/**
 * Service class to manage the ehcache in the system.
 *
 * @author nbrest
 */
@Service
public class EhCacheService {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private static final String CACHE_MANAGER_NULL = "Cache manager is null";

  @Autowired
  @Qualifier("cacheManager")
  private EhCacheCacheManager ehCacheManager;

  /**
   * Returns the cache information of the cache specified as a parameter.
   */
  public ApplicationCache get(String cacheName) {
    logger.trace("get {}", cacheName);
    CacheManager cacheManager = getCacheManager();
    Cache cache = cacheManager.getCache(cacheName);
    ApplicationCache applicationCache = getCacheInformation(cache);
    logger.trace("get {} response {}", cacheName, applicationCache);
    return applicationCache;
  }

  /**
   * Returns the status of all the ehcaches.
   */
  public List<ApplicationCache> getAll() {
    logger.trace("getAll");
    CacheManager cacheManager = getCacheManager();
    String[] cacheNames = cacheManager.getCacheNames();
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
    Cache cache = cacheManager.getCache(cacheName);
    if (cache != null) {
      cache.removeAll();
      logger.trace("clear {} successfully", cacheName);
    } else {
      logger.warn("cache {} not found", sanitizeInput(cacheName));
    }
  }

  /**
   * Clears all the ehcaches.
   */
  public void clearAll() {
    logger.trace("clearAll");
    CacheManager cacheManager = getCacheManager();
    String[] cacheNames = cacheManager.getCacheNames();
    for (int i = 0; i < cacheNames.length; i++) {
      clear(cacheNames[i]);
    }
  }

  /**
   * Get the internal cache manager.
   */
  private CacheManager getCacheManager() {
    CacheManager cacheManager = ehCacheManager.getCacheManager();
    if (cacheManager == null) {
      logger.error(CACHE_MANAGER_NULL);
      throw new KameHouseServerErrorException(CACHE_MANAGER_NULL);
    }
    return cacheManager;
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
        if (cacheElement != null && cacheElement.getObjectValue() != null) {
          cacheValues.add(cacheElement.getObjectValue().toString());
        }
      }
      applicationCache.setKeys(cache.getKeys().toString());
    }
    return applicationCache;
  }
}
