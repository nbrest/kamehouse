package com.nicobrest.kamehouse.groot.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main GRroot class.
 *
 * @author nbrest
 */
public class GrootMain {

  private static final Logger logger = LoggerFactory.getLogger(GrootMain.class);

  /**
   * GRoot main method.
   */
  public static void main(String[] args) {
    if (args.length > 0) {
      logger.debug("Ignoring command line arguments: {}", (Object) args);
    }
    logger.info("GRoot APIs are built on PHP. Only the integration tests are built in Java");
  }
}
