package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.jvncsender.VncSender;

/**
 * JvncSender system command to send text to a VNC server.
 *
 * @author nbrest
 */
public class TextJvncSenderSystemCommand extends JvncSenderSystemCommand {

  private String text;

  /**
   * Setup jvncsender text system command.
   */
  public TextJvncSenderSystemCommand(String text) {
    logCommand = false;
    executeOnDockerHost = false;
    this.text = text;
    setOutputCommand();
  }

  /**
   * Setup jvncsender text system command.
   */
  public TextJvncSenderSystemCommand(String text, int sleepTime) {
    logCommand = false;
    executeOnDockerHost = false;
    setSleepTime(sleepTime);
    this.text = text;
    setOutputCommand();
  }

  @Override
  protected void sendCommandToVncServer(VncSender vncSender) throws Exception {
    vncSender.sendText(text);
  }
}