package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.systemcommand.model.ScreenLockSystemCommand;

/**
 * AdminCommand to lock the screen.
 * 
 * @author nbrest
 *
 */
public class ScreenLockAdminCommand extends AdminCommand {

  public ScreenLockAdminCommand() {
    systemCommands.add(new ScreenLockSystemCommand());
  }
}
