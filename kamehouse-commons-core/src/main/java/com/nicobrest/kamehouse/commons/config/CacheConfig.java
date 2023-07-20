package com.nicobrest.kamehouse.commons.config;

import java.net.URL;
import javax.cache.integration.CacheLoader;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration beans for all modules.
 */
@Configuration
public class CacheConfig {

  /**
   * Default ehCacheManager.
   */
  @Bean
  public CacheManager ehCacheManager() {
    URL url = getClass().getResource("/ehcache.xml");
    XmlConfiguration xmlConfig = new XmlConfiguration(url);
    CacheManager cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
    cacheManager.init();
    return cacheManager;
  }

  //TODO UPGRADE BROKEN
  // need to make sure my caches are working even with this dummy cacheManager set
  @Bean
  public org.springframework.cache.CacheManager cacheManager() {
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    return cacheManager;
  }
}
