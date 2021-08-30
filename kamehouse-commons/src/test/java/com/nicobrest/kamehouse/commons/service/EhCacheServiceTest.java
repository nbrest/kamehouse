package com.nicobrest.kamehouse.commons.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.model.ApplicationCache;

import com.nicobrest.kamehouse.commons.service.EhCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for the EhCacheService class.
 * 
 * @author nbrest
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class EhCacheServiceTest {

  @Autowired
  private EhCacheService ehCacheService;

  /**
   * Tests retrieving all caches.
   */
  @Test
  public void readAllTest() {
    List<ApplicationCache> cacheList = ehCacheService.getAll();

    assertEquals(1, cacheList.size());
  }

  /**
   * Tests clearing all caches. Should fail if an exception clearing caches is
   * thrown.
   */
  @Test
  public void clearAllCachesTest() {
    List<String> emptyList = new ArrayList<>();
    
    ehCacheService.clearAll();
    
    for (ApplicationCache applicationCache : ehCacheService.getAll()) {
      if ("dragonBallUsers".equals(applicationCache.getName())) {
        assertEquals(emptyList, applicationCache.getValues());
      }
    }
  }
}
