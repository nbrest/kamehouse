package com.nicobrest.kamehouse.admin.model.admincommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.VncDoMouseClickSystemCommand;

/**
 * AdminCommand to wake up the screen.
 * 
 * @author nbrest
 *
 */
public class ScreenWakeUpAdminCommand extends AdminCommand {

  /**
   * Set the required SystemCommands to achieve this AdminCommand.
   */
  public ScreenWakeUpAdminCommand() {
    systemCommands.add(new VncDoMouseClickSystemCommand("1", "400", "400"));
    systemCommands.add(new VncDoMouseClickSystemCommand("1", "400", "500"));
    systemCommands.add(new VncDoMouseClickSystemCommand("1", "500", "500"));
  }
}
