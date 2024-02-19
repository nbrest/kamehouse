package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.MouseClickJvncSenderSystemCommand;

/**
 * KameHouseSystemCommand to wake up the screen with a few left mouse clicks.
 *
 * @author nbrest
 */
public class ScreenWakeUpKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public ScreenWakeUpKameHouseSystemCommand() {
    systemCommands.add(new MouseClickJvncSenderSystemCommand(400, 400, 1, 3));
    systemCommands.add(new MouseClickJvncSenderSystemCommand(400, 500, 1, 3));
    systemCommands.add(new MouseClickJvncSenderSystemCommand(500, 500, 1));
  }
}
