package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.JvncSenderSystemCommand;

/**
 * KameHouseSystemCommand to send an ALT+TAB key press and switch the specified number of tabs.
 *
 * @author nbrest
 */
public class AltTabKeyKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public AltTabKeyKameHouseSystemCommand(Integer tabs) {
    if (tabs == null || tabs < 1 || tabs > 50) {
      tabs = 1;
    }
    StringBuilder keyPressCommand = new StringBuilder("<ALT>");
    for (int i = 0; i < tabs; i++) {
      keyPressCommand.append("<TAB>");
    }
    systemCommands.add(new JvncSenderSystemCommand(keyPressCommand.toString()));
  }
}
