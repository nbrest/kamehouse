package com.nicobrest.kamehouse.admin.testutils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.nicobrest.kamehouse.admin.model.ApplicationCache;
import com.nicobrest.kamehouse.main.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.main.testutils.TestUtils;

import java.util.LinkedList;

/**
 * Test data and common test methods to test ApplicationCaches in all layers of
 * the application.
 * 
 * @author nbrest
 *
 */
public class ApplicationCacheTestUtils extends AbstractTestUtils<ApplicationCache, Object>
    implements TestUtils<ApplicationCache, Object> {

  @Override
  public void initTestData() {
    initSingleTestData();
    initTestDataList();
  }

  @Override
  public void assertEqualsAllAttributes(ApplicationCache expected, ApplicationCache returned) {
    assertEquals(expected.getName(), returned.getName());
    assertEquals(expected.getStatus(), returned.getStatus());
    assertEquals(expected.getKeys(), returned.getKeys());
    assertThat(returned.getValues(), is(expected.getValues()));
  }

  private void initSingleTestData() {
    singleTestData = new ApplicationCache();
    singleTestData.setName("dragonBallUsers");
    singleTestData.setStatus("STATUS_ALIVE");
    singleTestData.setKeys("[]");
  }

  private void initTestDataList() {
    ApplicationCache applicationCache = new ApplicationCache();
    applicationCache.setName("vlcPlayer");
    applicationCache.setStatus("STATUS_ALIVE");
    applicationCache.setKeys("[]");
    
    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    testDataList.add(applicationCache);
  }
}
