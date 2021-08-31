package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.ScreenLockSystemCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;

/**
 * KameHouseSystemCommand to lock the screen.
 *
 * @author nbrest
 */
public class ScreenLockKameHouseSystemCommand extends KameHouseSystemCommand {

  /** Sets the required SystemCommands to achieve this KameHouseSystemCommand. */
  public ScreenLockKameHouseSystemCommand() {
    systemCommands.add(new ScreenLockSystemCommand());
  }
}
