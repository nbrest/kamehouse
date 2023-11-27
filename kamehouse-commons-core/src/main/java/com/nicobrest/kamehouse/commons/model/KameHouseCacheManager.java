package com.nicobrest.kamehouse.commons.model;

import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.cache.Cache;
import javax.cache.Cache.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

/**
 * Wrapper for EhCache manager to handle all caches in KameHouse.
 *
 * @author nbrest
 */
public class KameHouseCacheManager {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private CacheManager cacheManager;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public KameHouseCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  public CacheManager getCacheManager() {
    return cacheManager;
  }

  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  /**
   * Get all cache names.
   */
  public List<String> getAllCacheNames() {
    List<String> cacheNames = new ArrayList<>();
    cacheNames.addAll(cacheManager.getCacheNames());
    return cacheNames;
  }

  /**
   * Populates the map that represents the cache with the cache information.
   */
  public KameHouseCache getKameHouseCache(String cacheName) {
    Cache<?, ?> cache = getCache(cacheName);
    KameHouseCache kameHouseCache = new KameHouseCache();
    kameHouseCache.setName(cache.getName());
    kameHouseCache.setStatus("ACTIVE");
    kameHouseCache.setKeys(getCacheKeys(cache).toString());
    kameHouseCache.setValues(getCacheValues(cache));
    return kameHouseCache;
  }

  /**
   * Clear cache contents.
   */
  public void clearCache(String cacheName) {
    logger.trace("clear {}", cacheName);
    Cache<?, ?> cache = getCache(cacheName);
    cache.clear();
    logger.trace("clear {} successfully", cacheName);
  }

  /**
   * Get cache instance.
   */
  private Cache<?, ?> getCache(String cacheName) {
    org.springframework.cache.Cache springCache = cacheManager.getCache(cacheName);
    if (springCache == null) {
      logger.error("Cache {} not found", cacheName);
      throw new KameHouseServerErrorException("Cache " + cacheName + " not found");
    }
    return (Cache) springCache.getNativeCache();
  }

  /**
   * Get keys of the cache.
   */
  private List<String> getCacheKeys(Cache<?, ?> cache) {
    List<String> keys = new ArrayList<>();
    Iterator<? extends Entry<?, ?>> iterator = cache.iterator();
    while (iterator.hasNext()) {
      Entry<?, ?> entry = iterator.next();
      if (entry.getKey() != null) {
        keys.add(entry.getKey().toString());
      }
    }
    return keys;
  }

  /**
   * Get values of the cache.
   */
  private List<String> getCacheValues(Cache<?, ?> cache) {
    List<String> values = new ArrayList<>();
    Iterator<? extends Entry<?, ?>> iterator = cache.iterator();
    while (iterator.hasNext()) {
      Entry<?, ?> entry = iterator.next();
      if (entry.getValue() != null) {
        values.add(entry.getValue().toString());
      }
    }
    return values;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    KameHouseCacheManager that = (KameHouseCacheManager) obj;
    return Objects.equals(cacheManager, that.cacheManager);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cacheManager);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
