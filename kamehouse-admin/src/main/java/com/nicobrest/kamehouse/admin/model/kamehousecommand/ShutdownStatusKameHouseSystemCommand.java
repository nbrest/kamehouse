package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.ShutdownStatusSystemCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;

/**
 * KameHouseSystemCommand to get the status of a scheduled shutdown of the server.
 *
 * @author nbrest
 *
 */
public class ShutdownStatusKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public ShutdownStatusKameHouseSystemCommand() {
    systemCommands.add(new ShutdownStatusSystemCommand());
  }
}
