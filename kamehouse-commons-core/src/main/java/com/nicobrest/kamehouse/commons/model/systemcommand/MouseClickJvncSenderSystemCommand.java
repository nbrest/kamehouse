package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.jvncsender.VncServer;

/**
 * JvncSender system command to send a mouse click to a VNC server.
 *
 * @author nbrest
 */
public class MouseClickJvncSenderSystemCommand extends JvncSenderSystemCommand {

  private MouseButton mouseButton;
  private Integer positionX;
  private Integer positionY;
  private Integer clickCount;

  /**
   * Setup jvncsender left mouse click system command.
   */
  public MouseClickJvncSenderSystemCommand(int positionX, int positionY, int clickCount) {
    this(MouseButton.LEFT, positionX, positionY, clickCount);
  }

  /**
   * Setup jvncsender left or right mouse click system command.
   */
  public MouseClickJvncSenderSystemCommand(MouseButton mouseButton, int positionX, int positionY,
      int clickCount) {
    super();
    this.mouseButton = mouseButton;
    this.positionX = positionX;
    this.positionY = positionY;
    this.clickCount = clickCount;
  }

  /**
   * Setup jvncsender left mouse click system command with sleep.
   */
  public MouseClickJvncSenderSystemCommand(int positionX, int positionY, int clickCount,
      int sleepTime) {
    this(MouseButton.LEFT, positionX, positionY, clickCount, sleepTime);
  }

  /**
   * Setup jvncsender left or right mouse click system command with sleep.
   */
  public MouseClickJvncSenderSystemCommand(MouseButton mouseButton, int positionX, int positionY,
      int clickCount, int sleepTime) {
    super();
    setSleepTime(sleepTime);
    this.mouseButton = mouseButton;
    this.positionX = positionX;
    this.positionY = positionY;
    this.clickCount = clickCount;
  }

  @Override
  protected void sendCommand(VncServer vncServer) {
    try {
      vncServer.sendMouseClick(mouseButton.getJvncSenderButton(), positionX, positionY, clickCount);
    } catch (Exception e) {
      throw new KameHouseException(e);
    }
  }

  @Override
  protected String buildKameHouseCmdJvncSenderOperationArgs() {
    return "-mouseClick " + mouseButton.name() + "," + positionX + "," + positionY + ","
        + clickCount;
  }
}
