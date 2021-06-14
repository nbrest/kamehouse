package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.VncDoMouseClickSystemCommand;

/**
 * KameHouseSystemCommand to wake up the screen.
 * 
 * @author nbrest
 *
 */
public class ScreenWakeUpKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public ScreenWakeUpKameHouseSystemCommand() {
    systemCommands.add(new VncDoMouseClickSystemCommand("1", "400", "400"));
    systemCommands.add(new VncDoMouseClickSystemCommand("1", "400", "500"));
    systemCommands.add(new VncDoMouseClickSystemCommand("1", "500", "500"));
  }
}
