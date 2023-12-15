package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.jvncsender.VncSender;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JvncSender system command to send text and mouse clicks to a VNC server.
 *
 * @author nbrest
 */
public class JvncSenderSystemCommand extends SystemCommand {

  private static final int VNC_PORT = 5900;

  private String text = null;
  private Integer positionX = null;
  private Integer positionY = null;
  private Integer clickCount = null;

  /**
   * Setup jvncsender text system command.
   */
  public JvncSenderSystemCommand(String text) {
    logCommand = false;
    this.text = text;
    setOutputCommand();
  }

  /**
   * Setup jvncsender text system command.
   */
  public JvncSenderSystemCommand(String text, int sleepTime) {
    logCommand = false;
    setSleepTime(sleepTime);
    this.text = text;
    setOutputCommand();
  }

  /**
   * Setup jvncsender mouse click system command.
   */
  public JvncSenderSystemCommand(int positionX, int positionY, int clickCount) {
    logCommand = false;
    this.positionX = positionX;
    this.positionY = positionY;
    this.clickCount = clickCount;
    setOutputCommand();
  }

  /**
   * Setup jvncsender mouse click system command.
   */
  public JvncSenderSystemCommand(int positionX, int positionY, int clickCount, int sleepTime) {
    logCommand = false;
    setSleepTime(sleepTime);
    this.positionX = positionX;
    this.positionY = positionY;
    this.clickCount = clickCount;
    setOutputCommand();
  }

  @Override
  public SystemCommand.Output execute() {
    String host = DockerUtils.getHostname();
    String password = getVncServerPassword();
    try {
      VncSender vncSender = new VncSender(host, VNC_PORT, password);
      if (!StringUtils.isEmpty(text)) {
        vncSender.sendText(text);
      } else {
        vncSender.sendMouseClick(positionX, positionY, clickCount);
      }
      output.setStandardOutput(List.of("JVNCSender command executed successfully"));
    } catch (Exception e) {
      logger.error("Error sending text to vnc server", e);
      output.setStandardError(List.of("Error executing VNC command", e.getMessage()));
    }
    try {
      if (getSleepTime() > 0) {
        logger.debug("Sleeping for {} seconds", getSleepTime());
        TimeUnit.SECONDS.sleep(getSleepTime());
      }
    } catch (InterruptedException e) {
      logger.warn("Interrupted exception", e);
      Thread.currentThread().interrupt();
    }
    return getOutput();
  }

  /**
   * Gets the vnc server password from a file.
   */
  protected String getVncServerPassword() {
    String vncServerPwdFile =
        PropertiesUtils.getUserHome() + "/" + PropertiesUtils.getProperty("vnc.server.pwd.file");
    try {
      String decryptedFile = EncryptionUtils.decryptKameHouseFileToString(vncServerPwdFile);
      if (StringUtils.isEmpty(decryptedFile)) {
        decryptedFile = FileUtils.EMPTY_FILE_CONTENT;
      }
      return decryptedFile;
    } catch (KameHouseInvalidDataException e) {
      return FileUtils.EMPTY_FILE_CONTENT;
    }
  }

  /**
   * Hide the output of jvncsender commands, as it contains passwords. Call this method in the
   * constructor of <b>EVERY</b> concrete subclass, after initializing the command lists.
   */
  @Override
  protected void setOutputCommand() {
    output.setCommand("[jvncsender (hidden as it contains passwords)]");
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
