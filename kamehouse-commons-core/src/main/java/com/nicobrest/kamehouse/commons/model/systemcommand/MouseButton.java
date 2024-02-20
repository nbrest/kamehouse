package com.nicobrest.kamehouse.commons.model.systemcommand;

/**
 * Mouse button to be clicked in KameHouse through JVNCSender.
 *
 * @author nbrest
 */
public enum MouseButton {
  LEFT,
  RIGHT;

  /**
   * Return the jvncsender equivalent MouseButton.
   */
  public com.nicobrest.kamehouse.jvncsender.MouseButton getJvncSenderButton() {
    return com.nicobrest.kamehouse.jvncsender.MouseButton.valueOf(this.name());
  }
}
