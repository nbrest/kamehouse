package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.TextJvncSenderSystemCommand;

/**
 * KameHouseSystemCommand to send a key press.
 *
 * @author nbrest
 */
public class KeyPressKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public KeyPressKameHouseSystemCommand(KeyPress key, Integer keyPresses) {
    String text = key.get(keyPresses);
    systemCommands.add(new TextJvncSenderSystemCommand(text));
  }
}
