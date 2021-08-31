package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.ShutdownCancelSystemCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;

/**
 * KameHouseSystemCommand to cancel a scheduled shutdown of the server.
 *
 * @author nbrest
 */
public class ShutdownCancelKameHouseSystemCommand extends KameHouseSystemCommand {

  /** Sets the required SystemCommands to achieve this KameHouseSystemCommand. */
  public ShutdownCancelKameHouseSystemCommand() {
    systemCommands.add(new ShutdownCancelSystemCommand());
  }
}
