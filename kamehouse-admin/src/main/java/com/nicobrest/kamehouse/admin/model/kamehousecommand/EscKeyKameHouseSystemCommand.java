package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.JvncSenderSystemCommand;

/**
 * KameHouseSystemCommand to send an ESC key press.
 *
 * @author nbrest
 */
public class EscKeyKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public EscKeyKameHouseSystemCommand() {
    systemCommands.add(new JvncSenderSystemCommand("<ESC>"));
  }
}
