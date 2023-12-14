package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.JvncSenderSystemCommand;

/**
 * KameHouseSystemCommand to send an WIN+TAB key press.
 *
 * @author nbrest
 */
public class WinTabKeyKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public WinTabKeyKameHouseSystemCommand() {
    systemCommands.add(new JvncSenderSystemCommand("<WINDOWS><TAB>"));
  }
}
