package com.nicobrest.kamehouse.admin.model.admincommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.ScreenLockSystemCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.AdminCommand;

/**
 * AdminCommand to lock the screen.
 * 
 * @author nbrest
 *
 */
public class ScreenLockAdminCommand extends AdminCommand {

  /**
   * Sets the required SystemCommands to achieve this AdminCommand.
   */
  public ScreenLockAdminCommand() {
    systemCommands.add(new ScreenLockSystemCommand());
  }
}
