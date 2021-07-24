package com.nicobrest.kamehouse.commons.utils;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * ThreadUtils tests.
 *
 * @author nbrest
 */
public class ThreadUtilsTest {

  /**
   * Test get and set the current thread name.
   */
  @Test
  public void getAndSetCurrentThreadName() {
    String newThreadName = "pegasus-seiya-yukimura";
    ThreadUtils.setCurrentThreadName(newThreadName);
    assertEquals(newThreadName, ThreadUtils.getCurrentThreadName());
  }
}
