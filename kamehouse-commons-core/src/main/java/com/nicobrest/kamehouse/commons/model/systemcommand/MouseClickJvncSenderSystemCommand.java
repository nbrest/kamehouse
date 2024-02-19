package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.jvncsender.VncSender;

/**
 * JvncSender system command to send a mouse click to a VNC server.
 *
 * @author nbrest
 */
public class MouseClickJvncSenderSystemCommand extends JvncSenderSystemCommand {

  private Integer positionX;
  private Integer positionY;
  private Integer clickCount;
  private boolean isLeftClick;

  /**
   * Setup jvncsender left mouse click system command.
   */
  public MouseClickJvncSenderSystemCommand(int positionX, int positionY, int clickCount) {
    this(positionX, positionY, clickCount, true);
  }

  /**
   * Setup jvncsender left or right mouse click system command.
   */
  public MouseClickJvncSenderSystemCommand(int positionX, int positionY, int clickCount,
      boolean isLeftClick) {
    logCommand = false;
    executeOnDockerHost = true;
    this.positionX = positionX;
    this.positionY = positionY;
    this.clickCount = clickCount;
    this.isLeftClick = isLeftClick;
    setOutputCommand();
  }

  /**
   * Setup jvncsender left mouse click system command with sleep.
   */
  public MouseClickJvncSenderSystemCommand(int positionX, int positionY, int clickCount,
      int sleepTime) {
    this(positionX, positionY, clickCount, true, sleepTime);
  }

  /**
   * Setup jvncsender left or right mouse click system command with sleep.
   */
  public MouseClickJvncSenderSystemCommand(int positionX, int positionY, int clickCount,
      boolean isLeftClick, int sleepTime) {
    logCommand = false;
    executeOnDockerHost = true;
    setSleepTime(sleepTime);
    this.positionX = positionX;
    this.positionY = positionY;
    this.clickCount = clickCount;
    this.isLeftClick = isLeftClick;
    setOutputCommand();
  }

  @Override
  protected void sendCommandToVncServer(VncSender vncSender) {
    try {
      if (this.isLeftClick) {
        vncSender.sendMouseLeftClick(positionX, positionY, clickCount);
      } else {
        vncSender.sendMouseRightClick(positionX, positionY, clickCount);
      }
    } catch (Exception e) {
      throw new KameHouseException(e);
    }
  }
}
