package com.nicobrest.kamehouse.cmd.executor;

import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Bean to execute the specified kamehouse command.
 *
 * @author nbrest
 */
@Component
public class KameHouseCmdExecutor {

  private final Logger logger = LoggerFactory.getLogger(KameHouseCmdExecutor.class);

  @Autowired
  private EncryptExecutor encryptExecutor;

  /**
   * Delegate the execution of the command to the correct executor.
   */
  public void execute(CmdArgumentHandler cmdArgumentHandler) {
    switch (cmdArgumentHandler.getOperation()) {
      case ENCRYPT:
        encryptExecutor.execute(cmdArgumentHandler);
        break;
      default:
        logger.error("Unhandled operation");
        break;
    }
  }
}
