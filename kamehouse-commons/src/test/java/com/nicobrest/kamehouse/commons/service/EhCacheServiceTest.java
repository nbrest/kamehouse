package com.nicobrest.kamehouse.commons.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.model.KameHouseCache;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for the EhCacheService class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class EhCacheServiceTest {

  @Autowired
  private EhCacheService ehCacheService;

  /**
   * Tests retrieving all caches.
   */
  @Test
  public void readAllTest() {
    List<KameHouseCache> cacheList = ehCacheService.getAll();

    assertEquals(1, cacheList.size());
  }

  /**
   * Tests clearing all caches. Should fail if an exception clearing caches is thrown.
   */
  @Test
  public void clearAllCachesTest() {
    List<String> emptyList = new ArrayList<>();

    ehCacheService.clearAll();

    for (KameHouseCache kameHouseCache : ehCacheService.getAll()) {
      if ("dragonBallUsers".equals(kameHouseCache.getName())) {
        assertEquals(emptyList, kameHouseCache.getValues());
      }
    }
  }

  /**
   * Tests clearing an invalid cache. Should fail silently without throwing exception.
   */
  @Test
  public void clearInvalidCacheTest() {
    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          ehCacheService.clear("invalid-cache");
        });
  }
}
