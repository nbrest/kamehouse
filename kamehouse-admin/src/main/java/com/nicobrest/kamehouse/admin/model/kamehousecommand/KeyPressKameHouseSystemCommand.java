package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.JvncSenderSystemCommand;

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
    String jvncSenderCommand = key.get(keyPresses);
    systemCommands.add(new JvncSenderSystemCommand(jvncSenderCommand));
  }
}
