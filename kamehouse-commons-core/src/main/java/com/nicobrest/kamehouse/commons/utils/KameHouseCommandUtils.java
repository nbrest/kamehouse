package com.nicobrest.kamehouse.commons.utils;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;

/**
 * Utility class to execute kamehouse commands in the application.
 *
 * @author nbrest
 */
public class KameHouseCommandUtils {

  private KameHouseCommandUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Execute the specified kamehouse command. Where possible, use KameHouseCommandService to execute
   * kamehouse commands over this.
   */
  public static KameHouseCommandResult execute(KameHouseCommand kameHouseCommand) {
    return kameHouseCommand.execute();
  }
}
