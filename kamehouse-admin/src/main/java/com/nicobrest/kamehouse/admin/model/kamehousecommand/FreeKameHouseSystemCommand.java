package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.FreeSystemCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;

/**
 * KameHouseSystemCommand to get the available memory of the server.
 *
 * @author nbrest
 */
public class FreeKameHouseSystemCommand extends KameHouseSystemCommand {

  /** Sets the required SystemCommands to achieve this KameHouseSystemCommand. */
  public FreeKameHouseSystemCommand() {
    systemCommands.add(new FreeSystemCommand());
  }
}
