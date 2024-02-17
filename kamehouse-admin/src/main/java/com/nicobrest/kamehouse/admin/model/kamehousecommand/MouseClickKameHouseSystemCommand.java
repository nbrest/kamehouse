package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.JvncSenderSystemCommand;

/**
 * KameHouseSystemCommand to send a mouse click.
 *
 * @author nbrest
 */
public class MouseClickKameHouseSystemCommand extends KameHouseSystemCommand {

  private static int MIN_SCREEN_POS = 0;
  private static int MAX_SCREEN_POS = 4096;
  private static int MIN_CLICK_COUNT = 1;
  private static int MAX_CLICK_COUNT = 5;

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public MouseClickKameHouseSystemCommand(Integer xPosition, Integer yPosition,
      Integer clickCount) {
    if (clickCount == null) {
      clickCount = 1;
    }
    validateParameters(xPosition, yPosition, clickCount);
    systemCommands.add(new JvncSenderSystemCommand(xPosition, yPosition, clickCount));
  }

  /**
   * Validate mouse click settings.
   */
  private static void validateParameters(Integer xPosition, Integer yPosition,
      Integer clickCount) {
    if (xPosition < MIN_SCREEN_POS || xPosition > MAX_SCREEN_POS) {
      throw new KameHouseInvalidCommandException("Invalid xPosition " + xPosition);
    }
    if (yPosition < MIN_SCREEN_POS || yPosition > MAX_SCREEN_POS) {
      throw new KameHouseInvalidCommandException("Invalid yPosition " + xPosition);
    }
    if (clickCount < MIN_CLICK_COUNT || clickCount > MAX_CLICK_COUNT) {
      throw new KameHouseInvalidCommandException("Invalid yPosition " + xPosition);
    }
  }
}
