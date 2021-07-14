package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.HttpdStartSystemCommand;
import com.nicobrest.kamehouse.admin.model.systemcommand.HttpdStopSystemCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;

/**
 * KameHouseSystemCommand to restart the httpd server.
 *
 * @author nbrest
 *
 */
public class HttpdRestartKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public HttpdRestartKameHouseSystemCommand() {
    systemCommands.add(new HttpdStopSystemCommand());
    systemCommands.add(new HttpdStartSystemCommand());
  }
}
