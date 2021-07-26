package com.nicobrest.kamehouse.cmd.executor;

import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Executor for the encrypt operation.
 *
 * @author nbrest
 */
@Component
public class EncryptExecutor {

  private final Logger logger = LoggerFactory.getLogger(EncryptExecutor.class);

  /**
   * Execute the operation.
   */
  public void execute(CmdArgumentHandler cmdArgumentHandler) {
    String inputFile = cmdArgumentHandler.getArgument("if");
    String outputFile = cmdArgumentHandler.getArgument("of");
    logger.info("Encrypting contents of {} into {}", inputFile, outputFile);
  }
}
