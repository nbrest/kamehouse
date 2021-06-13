package com.nicobrest.kamehouse.admin.model.admincommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.ShutdownSystemCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.AdminCommand;

/**
 * AdminCommand to shutdown the server.
 * 
 * @author nbrest
 *
 */
public class ShutdownAdminCommand extends AdminCommand {

  /**
   * Sets the required SystemCommands to achieve this AdminCommand.
   */
  public ShutdownAdminCommand(int shutdownDelaySeconds) {
    systemCommands.add(new ShutdownSystemCommand(shutdownDelaySeconds));
  }
}
