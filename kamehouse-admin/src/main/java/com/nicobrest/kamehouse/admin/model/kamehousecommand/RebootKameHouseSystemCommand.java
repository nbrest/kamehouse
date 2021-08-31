package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.RebootSystemCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;

/**
 * KameHouseSystemCommand to reboot the server.
 *
 * @author nbrest
 */
public class RebootKameHouseSystemCommand extends KameHouseSystemCommand {

  /** Sets the required SystemCommands to achieve this KameHouseSystemCommand. */
  public RebootKameHouseSystemCommand() {
    systemCommands.add(new RebootSystemCommand());
  }
}
