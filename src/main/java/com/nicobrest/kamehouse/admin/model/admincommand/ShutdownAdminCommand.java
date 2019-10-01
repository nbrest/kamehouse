package com.nicobrest.kamehouse.admin.model.admincommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.ShutdownSystemCommand;

/**
 * AdminCommand to shutdown the server.
 * 
 * @author nbrest
 *
 */
public class ShutdownAdminCommand extends AdminCommand {

  /**
   * Set the required SystemCommands to achieve this AdminCommand.
   */
  public ShutdownAdminCommand(int shutdownDelaySeconds) {
    systemCommands.add(new ShutdownSystemCommand(shutdownDelaySeconds));
  }
}
