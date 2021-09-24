package com.nicobrest.kamehouse.cmd.executor;

import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.service.LogLevelManagerService;
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
  private static final String TRACE = "TRACE";

  @Autowired
  private LogLevelManagerService logLevelManagerService;

  @Autowired
  private DecryptExecutor decryptExecutor;

  @Autowired
  private EncryptExecutor encryptExecutor;

  @Autowired
  private JvncSenderExecutor jvncSenderExecutor;

  /**
   * Delegate the execution of the command to the correct executor.
   */
  public void execute(CmdArgumentHandler cmdArgumentHandler) {
    checkVerboseMode(cmdArgumentHandler);
    Executor operationExecutor = getOperationExecutor(cmdArgumentHandler);
    logger.debug("Started executing command");
    try {
      operationExecutor.execute(cmdArgumentHandler);
    } catch (KameHouseException e) {
      logger.error("Error executing operation", e);
    }
    logger.debug("Finished executing command");
  }

  /**
   * Get the executor for the specified operation.
   */
  private Executor getOperationExecutor(CmdArgumentHandler cmdArgumentHandler) {
    switch (cmdArgumentHandler.getOperation()) {
      case DECRYPT:
        return decryptExecutor;
      case ENCRYPT:
        return encryptExecutor;
      case JVNCSENDER:
        return jvncSenderExecutor;
      default:
        logger.error("Unhandled operation");
        throw new KameHouseInvalidDataException("Invalid operation");
    }
  }

  /**
   * Check if verbose mode is enabled and set logging to trace.
   */
  private void checkVerboseMode(CmdArgumentHandler cmdArgumentHandler) {
    if (cmdArgumentHandler.hasArgument("v")) {
      logLevelManagerService.setLogLevel(TRACE, "com.nicobrest.kamehouse");
      logLevelManagerService.setLogLevel(TRACE, "com.nicobrest.kamehouse.cmd");
      logLevelManagerService.setLogLevel(TRACE, "com.nicobrest.kamehouse.commons");
      logger.trace("verbose mode enabled");
    }
  }
}
