package com.nicobrest.kamehouse.commons.testutils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.model.KameHouseCache;
import java.util.LinkedList;

/**
 * Test data and common test methods to test KameHouseCaches in all layers of the application.
 *
 * @author nbrest
 */
public class KameHouseCacheTestUtils extends AbstractTestUtils<KameHouseCache, Object>
    implements TestUtils<KameHouseCache, Object> {

  @Override
  public void initTestData() {
    initSingleTestData();
    initTestDataList();
  }

  @Override
  public void assertEqualsAllAttributes(KameHouseCache expected, KameHouseCache returned) {
    assertEquals(expected, returned);
    assertEquals(expected.getName(), returned.getName());
    assertEquals(expected.getStatus(), returned.getStatus());
    assertEquals(expected.getKeys(), returned.getKeys());
    assertThat(returned.getValues(), is(expected.getValues()));
  }

  private void initSingleTestData() {
    singleTestData = new KameHouseCache();
    singleTestData.setName("dragonBallUsers");
    singleTestData.setStatus("ACTIVE");
    singleTestData.setKeys("[]");
  }

  private void initTestDataList() {
    KameHouseCache kameHouseCache = new KameHouseCache();
    kameHouseCache.setName("vlcPlayer");
    kameHouseCache.setStatus("ACTIVE");
    kameHouseCache.setKeys("[]");

    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    testDataList.add(kameHouseCache);
  }
}
