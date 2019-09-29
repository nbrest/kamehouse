package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.systemcommand.model.VncDoMouseClickSystemCommand;

/**
 * AdminCommand to wake up the screen.
 * 
 * @author nbrest
 *
 */
public class ScreenWakeUpAdminCommand extends AdminCommand {

  /**
   * Default constructor.
   */
  public ScreenWakeUpAdminCommand() {
    systemCommands.add(new VncDoMouseClickSystemCommand("1", "400", "400"));
    systemCommands.add(new VncDoMouseClickSystemCommand("1", "400", "500"));
    systemCommands.add(new VncDoMouseClickSystemCommand("1", "500", "500"));
  }
}
