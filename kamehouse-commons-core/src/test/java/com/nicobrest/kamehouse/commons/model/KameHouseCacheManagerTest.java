package com.nicobrest.kamehouse.commons.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;
import javax.cache.Cache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;

class KameHouseCacheManagerTest {

  @Mock
  private CacheManager cacheManager;

  @Mock
  private org.springframework.cache.Cache springCache;

  @Mock
  private Cache cache;

  @Mock
  private Iterator iterator;

  @InjectMocks
  private KameHouseCacheManager kameHouseCacheManager = new KameHouseCacheManager(cacheManager);

  /**
   * Tests setup.
   */
  @BeforeEach
  void before() {
    MockitoAnnotations.openMocks(this);
    when(cacheManager.getCacheNames()).thenReturn(List.of("cache1", "cache2"));
    when(cacheManager.getCache(any())).thenReturn(springCache);
    when(springCache.getNativeCache()).thenReturn(cache);
    when(cache.iterator()).thenReturn(iterator);
    when(cache.getName()).thenReturn("cache1");
    when(iterator.hasNext()).thenReturn(false);
  }

  /**
   * Get all cache names test.
   */
  @Test
  void getAllCacheNamesTest() {
    List<String> output = kameHouseCacheManager.getAllCacheNames();

    assertEquals(List.of("cache1", "cache2"), output);
  }

  /**
   * Get kamehouse cache test.
   */
  @Test
  void getKameHouseCacheTest() {
    KameHouseCache output = kameHouseCacheManager.getKameHouseCache("cache1");

    assertEquals("cache1", output.getName());
    assertEquals("ACTIVE", output.getStatus());
  }

  /**
   * Clear kamehouse cache test.
   */
  @Test
  void clearCacheTest() {
    Assertions.assertDoesNotThrow(() -> {
      kameHouseCacheManager.clearCache("cache1");
    });
  }
}
