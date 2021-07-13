package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.UptimeSystemCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;

/**
 * KameHouseSystemCommand to check the uptime of the server.
 * 
 * @author nbrest
 *
 */
public class UptimeKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public UptimeKameHouseSystemCommand() {
    systemCommands.add(new UptimeSystemCommand());
  }
}
