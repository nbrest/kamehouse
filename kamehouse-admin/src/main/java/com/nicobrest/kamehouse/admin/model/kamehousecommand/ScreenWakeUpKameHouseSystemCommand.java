package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.JvncSenderSystemCommand;

/**
 * KameHouseSystemCommand to wake up the screen with a few mouse clicks.
 *
 * @author nbrest
 */
public class ScreenWakeUpKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public ScreenWakeUpKameHouseSystemCommand() {
    systemCommands.add(new JvncSenderSystemCommand(400, 400, 1, 1));
    systemCommands.add(new JvncSenderSystemCommand(400, 500, 1, 1));
    systemCommands.add(new JvncSenderSystemCommand(500, 500, 1));
  }
}
