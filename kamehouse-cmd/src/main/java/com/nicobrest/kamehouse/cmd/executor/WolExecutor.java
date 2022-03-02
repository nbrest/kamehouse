package com.nicobrest.kamehouse.cmd.executor;

import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;
import com.nicobrest.kamehouse.commons.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Executor for the wol operation.
 *
 * @author nbrest
 */
@Component
public class WolExecutor implements Executor {

  private final Logger logger = LoggerFactory.getLogger(WolExecutor.class);

  /**
   * Execute the operation.
   */
  public void execute(CmdArgumentHandler cmdArgumentHandler) {
    String macAddress = cmdArgumentHandler.getArgument("mac");
    String broadcastAddress = cmdArgumentHandler.getArgument("broadcast");
    logger.info("Sending wol packet to mac {} and broadcast {}", macAddress, broadcastAddress);
    NetworkUtils.wakeOnLan(macAddress, broadcastAddress);
  }
}
