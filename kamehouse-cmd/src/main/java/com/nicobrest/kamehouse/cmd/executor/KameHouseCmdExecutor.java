package com.nicobrest.kamehouse.cmd.executor;

import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.service.LogLevelManagerService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

  private LogLevelManagerService logLevelManagerService;
  private DecryptExecutor decryptExecutor;
  private EncryptExecutor encryptExecutor;
  private JvncSenderExecutor jvncSenderExecutor;
  private WolExecutor wolExecutor;

  /**
   * Autowired constructor.
   */
  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public KameHouseCmdExecutor(LogLevelManagerService logLevelManagerService,
      DecryptExecutor decryptExecutor, EncryptExecutor encryptExecutor,
      JvncSenderExecutor jvncSenderExecutor, WolExecutor wolExecutor) {
    this.logLevelManagerService = logLevelManagerService;
    this.decryptExecutor = decryptExecutor;
    this.encryptExecutor = encryptExecutor;
    this.jvncSenderExecutor = jvncSenderExecutor;
    this.wolExecutor = wolExecutor;
  }

  /**
   * Delegate the execution of the command to the correct executor.
   */
  public void execute(CmdArgumentHandler cmdArgumentHandler) {
    logger.info("Started kamehouse-cmd-executor");
    checkVerboseMode(cmdArgumentHandler);
    Executor operationExecutor = getOperationExecutor(cmdArgumentHandler);
    logger.debug("Started executing command");
    try {
      operationExecutor.execute(cmdArgumentHandler);
    } catch (KameHouseException e) {
      logger.error("Error executing operation", e);
    }
    logger.debug("Finished executing command");
    logger.info("Finished kamehouse-cmd-executor");
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
      case WOL:
        return wolExecutor;
      default:
        logger.error("Unhandled operation");
        throw new KameHouseInvalidDataException("Invalid operation");
    }
  }

  /**
   * Check if verbose mode is enabled and set logging to trace.
   */
  private void checkVerboseMode(CmdArgumentHandler cmdArgumentHandler) {
    logger.info("Setting log level");
    if (cmdArgumentHandler.hasArgument("v")) {
      logLevelManagerService.setLogLevel(TRACE, "com.nicobrest.kamehouse");
      logLevelManagerService.setLogLevel(TRACE, "com.nicobrest.kamehouse.cmd");
      logLevelManagerService.setLogLevel(TRACE, "com.nicobrest.kamehouse.commons");
      logger.trace("verbose mode enabled");
    }
  }
}
