package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.HttpdStatusSystemCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;

/**
 * KameHouseSystemCommand to get the status of the httpd server.
 *
 * @author nbrest
 *
 */
public class HttpdStatusKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public HttpdStatusKameHouseSystemCommand() {
    systemCommands.add(new HttpdStatusSystemCommand());
  }
}
