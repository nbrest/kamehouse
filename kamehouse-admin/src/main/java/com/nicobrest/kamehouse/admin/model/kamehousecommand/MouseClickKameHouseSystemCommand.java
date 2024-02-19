package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.MouseClickJvncSenderSystemCommand;

/**
 * KameHouseSystemCommand to send a mouse click.
 *
 * @author nbrest
 */
public class MouseClickKameHouseSystemCommand extends KameHouseSystemCommand {

  private static final int MIN_SCREEN_POS = 0;
  private static final int MAX_SCREEN_POS = 4096;
  private static final int MIN_CLICK_COUNT = 1;
  private static final int MAX_CLICK_COUNT = 5;

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public MouseClickKameHouseSystemCommand(Integer positionX, Integer positionY,
      Integer clickCount) {
    this(positionX, positionY, clickCount, true);
  }

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public MouseClickKameHouseSystemCommand(Integer positionX, Integer positionY,
      Integer clickCount, boolean isLeftClick) {
    if (clickCount == null) {
      clickCount = 1;
    }
    validateParameters(positionX, positionY, clickCount);
    systemCommands.add(
        new MouseClickJvncSenderSystemCommand(positionX, positionY, clickCount, isLeftClick));
  }

  /**
   * Validate mouse click settings.
   */
  private static void validateParameters(Integer positionX, Integer positionY,
      Integer clickCount) {
    if (positionX < MIN_SCREEN_POS || positionX > MAX_SCREEN_POS) {
      throw new KameHouseInvalidCommandException("Invalid positionX " + positionX);
    }
    if (positionY < MIN_SCREEN_POS || positionY > MAX_SCREEN_POS) {
      throw new KameHouseInvalidCommandException("Invalid positionY " + positionX);
    }
    if (clickCount < MIN_CLICK_COUNT || clickCount > MAX_CLICK_COUNT) {
      throw new KameHouseInvalidCommandException("Invalid positionY " + positionX);
    }
  }
}
