package com.nicobrest.kamehouse.cmd.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Sample service to test kamehouse-cmd.
 */
@Service
public class KameHouseCmdService {
  private final Logger logger = LoggerFactory.getLogger(KameHouseCmdService.class);

  /**
   * Sample test service.
   */
  public void execute(String[] args) {
    logger.info("started executing service");
    logger.info("The service args are:");
    for (String arg : args) {
      logger.info(arg);
    }
    logger.info("finished executing service");
  }
}
