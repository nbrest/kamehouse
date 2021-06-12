package com.nicobrest.kamehouse.admin.testutils;

import static org.junit.Assert.assertEquals;

import com.nicobrest.kamehouse.admin.model.SessionStatus;
import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;

/**
 * Test data and common test methods to test SessionStatus in all layers of
 * the application.
 * 
 * @author nbrest
 *
 */
public class SessionStatusTestUtils extends AbstractTestUtils<SessionStatus, Object>
    implements TestUtils<SessionStatus, Object> {

  @Override
  public void initTestData() {
    initSingleTestData();
  }

  @Override
  public void assertEqualsAllAttributes(SessionStatus expected, SessionStatus returned) {
    assertEquals(expected, returned);
    assertEquals(expected.getUsername(), returned.getUsername());
    assertEquals(expected.getFirstName(), returned.getFirstName());
    assertEquals(expected.getLastName(), returned.getLastName());
    assertEquals(expected.getServer(), returned.getServer());
  }

  private void initSingleTestData() {
    singleTestData = new SessionStatus();
    singleTestData.setUsername("anonymousUser");
    singleTestData.setServer(PropertiesUtils.getHostname());
  }
}
