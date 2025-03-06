package com.nicobrest.kamehouse.commons.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.jvncsender.VncServer;

/**
 * JvncSender kamehouse command to send text to a VNC server.
 *
 * @author nbrest
 */
public class TextJvncSenderKameHouseCommand extends JvncSenderKameHouseCommand {

  private String text;

  /**
   * Setup jvncsender text kamehouse command.
   */
  public TextJvncSenderKameHouseCommand(String text) {
    super();
    this.text = text;
  }

  /**
   * Setup jvncsender text kamehouse command.
   */
  public TextJvncSenderKameHouseCommand(String text, int sleepTime) {
    super();
    setSleepTime(sleepTime);
    this.text = text;
  }

  @Override
  protected void sendCommand(VncServer vncServer) {
    try {
      vncServer.sendText(text);
    } catch (Exception e) {
      throw new KameHouseException(e);
    }
  }

  @Override
  protected String getKameHouseCmdJvncSenderOperationArgs() {
    return "-text " + text;
  }
}
