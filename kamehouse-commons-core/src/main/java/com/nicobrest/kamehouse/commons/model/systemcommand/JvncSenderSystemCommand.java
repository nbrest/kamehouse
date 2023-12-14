package com.nicobrest.kamehouse.commons.model.systemcommand;

import be.jedi.jvncsender.VncSender;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JvncSender kamehouse-cmd command.
 *
 * @author nbrest
 */
public class JvncSenderSystemCommand extends SystemCommand {

  private static final int VNC_PORT = 5900;

  private String text;

  /**
   * Setup jvncsender system command.
   */
  public JvncSenderSystemCommand(String text) {
    logCommand = false;
    this.text = text;
    setOutputCommand();
  }

  /**
   * Setup jvncsender system command.
   */
  public JvncSenderSystemCommand(String text, int sleepTime) {
    logCommand = false;
    setSleepTime(sleepTime);
    this.text = text;
    setOutputCommand();
  }

  @Override
  public SystemCommand.Output execute() {
    String host = DockerUtils.getHostname();
    String password = getVncServerPassword();
    logger.info("Sending text to vnc server {}:{}", host, VNC_PORT);
    try {
      VncSender vncSender = new VncSender(host, VNC_PORT, password);
      vncSender.sendText(text);
      output.setStandardOutput(List.of("VNC command executed successfully"));
    } catch (Exception e) {
      logger.error("Error sending text to vnc server", e);
      output.setStandardError(List.of("Error executing VNC command", e.getMessage()));
    }
    try {
      int sleepTime = getSleepTime();
      if (sleepTime > 0) {
        logger.debug("Sleeping for {} seconds", sleepTime);
        TimeUnit.SECONDS.sleep(sleepTime);
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
