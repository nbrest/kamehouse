package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.JvncSenderSystemCommand;

/**
 * KameHouseSystemCommand to send an ENTER key press.
 *
 * @author nbrest
 */
public class EnterKeyKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public EnterKeyKameHouseSystemCommand() {
    systemCommands.add(new JvncSenderSystemCommand("<RETURN>"));
  }
}
