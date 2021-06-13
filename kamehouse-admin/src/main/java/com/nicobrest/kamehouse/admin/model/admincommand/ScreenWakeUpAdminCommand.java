package com.nicobrest.kamehouse.admin.model.admincommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AdminCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.VncDoMouseClickSystemCommand;

/**
 * AdminCommand to wake up the screen.
 * 
 * @author nbrest
 *
 */
public class ScreenWakeUpAdminCommand extends AdminCommand {

  /**
   * Sets the required SystemCommands to achieve this AdminCommand.
   */
  public ScreenWakeUpAdminCommand() {
    systemCommands.add(new VncDoMouseClickSystemCommand("1", "400", "400"));
    systemCommands.add(new VncDoMouseClickSystemCommand("1", "400", "500"));
    systemCommands.add(new VncDoMouseClickSystemCommand("1", "500", "500"));
  }
}
