package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Possible key presses allowed in KameHouse through JVNCSender.
 *
 * @author nbrest
 */
public enum KeyPress {
  ALT_F4,
  ALT_TAB,
  ARROW_UP,
  ARROW_DOWN,
  ARROW_LEFT,
  ARROW_RIGHT,
  CTRL_F,
  ENTER,
  ESC,
  WIN,
  WIN_TAB;

  private static final Logger LOGGER = LoggerFactory.getLogger(KeyPress.class);

  /**
   * Get JVNCSender key press string.
   */
  public String get() {
    return get(1);
  }

  /**
   * Get JVNCSender key press string.
   */
  public String get(Integer keyPresses) {
    if (keyPresses == null || keyPresses < 1 || keyPresses > 50) {
      LOGGER.info("Invalid amount of key presses, using 1 as default");
      keyPresses = 1;
    }
    switch (this) {
      case ALT_F4:
        return "<ALT><F4>";
      case ALT_TAB:
        return "<ALT><TAB>";
      case ARROW_UP:
        return getMultiplePresses("<UP>", keyPresses);
      case ARROW_DOWN:
        return getMultiplePresses("<DOWN>", keyPresses);
      case ARROW_LEFT:
        return getMultiplePresses("<LEFT>", keyPresses);
      case ARROW_RIGHT:
        return getMultiplePresses("<RIGHT>", keyPresses);
      case CTRL_F:
        return "<CONTROL>F";
      case ENTER:
        return "<RETURN>";
      case ESC:
        return "<ESC>";
      case WIN:
        return "<WINDOWS>";
      case WIN_TAB:
        return "<WINDOWS><TAB>";
      default:
        throw new KameHouseBadRequestException("Invalid KeyPress selected");
    }
  }

  /**
   * Get the specified amount of key presses for the key.
   */
  private String getMultiplePresses(String key, Integer keyPresses) {
    StringBuilder keyPressCommand = new StringBuilder();
    for (int i = 0; i < keyPresses; i++) {
      keyPressCommand.append(key);
    }
    return keyPressCommand.toString();
  }
}
