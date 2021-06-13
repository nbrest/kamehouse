package com.nicobrest.kamehouse.admin.model.admincommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.ShutdownCancelSystemCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.AdminCommand;

/**
 * AdminCommand to cancel a scheduled shutdown of the server.
 * 
 * @author nbrest
 *
 */
public class ShutdownCancelAdminCommand extends AdminCommand {

  /**
   * Sets the required SystemCommands to achieve this AdminCommand.
   */
  public ShutdownCancelAdminCommand() {
    systemCommands.add(new ShutdownCancelSystemCommand());
  }
}
