package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.JvncSenderSystemCommand;

/**
 * KameHouseSystemCommand to send an RIGHT key press the specified amount of times.
 *
 * @author nbrest
 */
public class RightKeyKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public RightKeyKameHouseSystemCommand(Integer keyPresses) {
    if (keyPresses == null || keyPresses < 1 || keyPresses > 50) {
      keyPresses = 1;
    }
    StringBuilder keyPressCommand = new StringBuilder();
    for (int i = 0; i < keyPresses; i++) {
      keyPressCommand.append("<RIGHT>");
    }
    keyPressCommand.append("<RETURN>");
    systemCommands.add(new JvncSenderSystemCommand(keyPressCommand.toString()));
  }
}
