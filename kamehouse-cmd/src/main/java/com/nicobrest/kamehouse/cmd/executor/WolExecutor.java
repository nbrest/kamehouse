package com.nicobrest.kamehouse.cmd.executor;

import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;
import com.nicobrest.kamehouse.commons.utils.NetworkUtils;
import org.springframework.stereotype.Component;

/**
 * Executor for the wol operation.
 *
 * @author nbrest
 */
@Component
public class WolExecutor implements Executor {

  /**
   * Execute the operation.
   */
  public void execute(CmdArgumentHandler cmdArgumentHandler) {
    String macAddress = cmdArgumentHandler.getArgument("mac");
    String broadcastAddress = cmdArgumentHandler.getArgument("broadcast");
    NetworkUtils.wakeOnLan(macAddress, broadcastAddress);
  }
}
