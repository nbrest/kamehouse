package com.nicobrest.kamehouse.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to manage threads.
 */
public class ThreadUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ThreadUtils.class);

  private ThreadUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Get the current thread name.
   */
  public static String getCurrentThreadName() {
    return Thread.currentThread().getName();
  }

  /**
   * Set the current thread name.
   */
  public static void setCurrentThreadName(String threadName) {
    LOGGER.debug("Renaming current thread to {}", threadName);
    Thread.currentThread().setName(threadName);
  }
}
