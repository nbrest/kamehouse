package com.nicobrest.kamehouse.cmd.config;

import com.nicobrest.kamehouse.cmd.service.KameHouseCmdService;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class that contains the starting point for the command line app.
 * 
 * @author nbrest
 */
@Component
public class KameHouseCmd {

  private final Logger logger = LoggerFactory.getLogger(KameHouseCmd.class);

  @Autowired
  KameHouseCmdService kameHouseCmdService;

  /**
   * Start kamehouse cmd.
   */
  public void execute(String[] args) {
    logger.info("Started KameHouseCmd");

    logger.info("module: " + PropertiesUtils.getModuleName());
    kameHouseCmdService.execute(args);

    logger.info("Finished KameHouseCmd");
  }
}