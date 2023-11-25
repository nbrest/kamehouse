package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * ThreadUtils tests.
 *
 * @author nbrest
 */
class ThreadUtilsTest {

  /** Test get and set the current thread name. */
  @Test
  public void getAndSetCurrentThreadName() {
    String newThreadName = "pegasus-seiya-yukimura";
    ThreadUtils.setCurrentThreadName(newThreadName);
    assertEquals(newThreadName, ThreadUtils.getCurrentThreadName());
  }
}
