package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.DfSystemCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;

/**
 * KameHouseSystemCommand to get the available disk space of the server.
 *
 * @author nbrest
 */
public class DfKameHouseSystemCommand extends KameHouseSystemCommand {

  /** Sets the required SystemCommands to achieve this KameHouseSystemCommand. */
  public DfKameHouseSystemCommand() {
    systemCommands.add(new DfSystemCommand());
  }
}
