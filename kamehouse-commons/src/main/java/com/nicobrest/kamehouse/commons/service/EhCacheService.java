package com.nicobrest.kamehouse.commons.service;

import com.nicobrest.kamehouse.commons.model.KameHouseCache;
import com.nicobrest.kamehouse.commons.model.KameHouseCacheManager;
import java.util.ArrayList;
import java.util.List;
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
  @Qualifier("kameHouseCacheManager")
  private KameHouseCacheManager kameHouseCacheManager;

  /**
   * Returns the cache information of the cache specified as a parameter.
   */
  public KameHouseCache get(String cacheName) {
    logger.trace("get {}", cacheName);
    KameHouseCache kameHouseCache = kameHouseCacheManager.getKameHouseCache(cacheName);
    logger.trace("get {} response {}", cacheName, kameHouseCache);
    return kameHouseCache;
  }

  /**
   * Returns the status of all the ehcaches.
   */
  public List<KameHouseCache> getAll() {
    logger.trace("getAll");
    List<String> cacheNames = kameHouseCacheManager.getAllCacheNames();
    List<KameHouseCache> cacheList = new ArrayList<>();
    for (String cacheName : cacheNames) {
      KameHouseCache kameHouseCache = get(cacheName);
      if (kameHouseCache != null) {
        cacheList.add(kameHouseCache);
      }
    }
    logger.trace("getAll response {}", cacheList);
    return cacheList;
  }

  /**
   * Clears the ehcache specified as a parameter.
   */
  public void clear(String cacheName) {
    kameHouseCacheManager.clearCache(cacheName);
  }

  /**
   * Clears all the ehcaches.
   */
  public void clearAll() {
    logger.trace("clearAll");
    List<String> cacheNames = kameHouseCacheManager.getAllCacheNames();
    for (String cacheName : cacheNames) {
      kameHouseCacheManager.clearCache(cacheName);
    }
  }
}
