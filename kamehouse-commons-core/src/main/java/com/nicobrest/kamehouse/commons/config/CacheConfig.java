package com.nicobrest.kamehouse.commons.config;

import com.nicobrest.kamehouse.commons.model.KameHouseCacheManager;
import java.net.URISyntaxException;
import java.net.URL;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration beans for all modules.
 */
@Configuration
@EnableCaching
public class CacheConfig {

  /**
   * KameHouseCacheManager instance.
   */
  @Bean
  public KameHouseCacheManager kameHouseCacheManager() {
    return new KameHouseCacheManager();
  }

  /**
   * Generate the ehcache manager instance.
   */
  @Bean
  public CacheManager cacheManager() throws URISyntaxException {
    JCacheCacheManager cacheManager = new JCacheCacheManager();
    CachingProvider cachingProvider = Caching.getCachingProvider();
    URL url = getClass().getResource("/ehcache.xml");
    javax.cache.CacheManager manager = cachingProvider.getCacheManager(url.toURI(),
        getClass().getClassLoader());
    cacheManager.setCacheManager(manager);
    return cacheManager;
  }
}
