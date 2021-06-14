package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.ShutdownSystemCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;

/**
 * KameHouseSystemCommand to shutdown the server.
 * 
 * @author nbrest
 *
 */
public class ShutdownKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public ShutdownKameHouseSystemCommand(int shutdownDelaySeconds) {
    systemCommands.add(new ShutdownSystemCommand(shutdownDelaySeconds));
  }
}
