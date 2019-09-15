package com.nicobrest.kamehouse.admin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for the EhCacheService class.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class EhCacheServiceTest {

  @Autowired
  private EhCacheService ehCacheService;

  /**
   * Tests retrieving all caches.
   */
  @Test
  public void getAllCachesTest() {

    try {
      List<Map<String, Object>> cacheList = ehCacheService.getAllCaches();
      assertEquals(5, cacheList.size());
    } catch (Exception ex) {
      ex.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Tests clearing all caches. Should fail if an exception clearing caches is thrown.
   */
  @Test
  public void clearAllCachesTest() {
    try {
      List<String> emptyList = new ArrayList<>();
      ehCacheService.clearAllCaches();
      for (Map<String, Object> cacheMap : ehCacheService.getAllCaches()) {
        if (cacheMap.get("name").equals("getAllDragonBallUsersCache")) {
          assertEquals(emptyList, cacheMap.get("values"));
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }
}
