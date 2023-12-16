package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.model.SystemCommandStatus;
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
 * JvncSender system command to send text and mouse clicks to a VNC server. When running on docker
 * it sends the commands through ssh and kamehouse-cmd for mouse clicks only. Text strings are sent
 * to the host directly from the webapps running in the docker container.
 *
 * @author nbrest
 */
public class JvncSenderSystemCommand extends KameHouseCmdSystemCommand {

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
    executeOnDockerHost = false;
    this.text = text;
    setOutputCommand();
  }

  /**
   * Setup jvncsender text system command.
   */
  public JvncSenderSystemCommand(String text, int sleepTime) {
    logCommand = false;
    executeOnDockerHost = false;
    setSleepTime(sleepTime);
    this.text = text;
    setOutputCommand();
  }

  /**
   * Setup jvncsender mouse click system command.
   */
  public JvncSenderSystemCommand(int positionX, int positionY, int clickCount) {
    logCommand = false;
    executeOnDockerHost = true;
    this.positionX = positionX;
    this.positionY = positionY;
    this.clickCount = clickCount;
    setKameHouseCmdCommands();
    setOutputCommand();
  }

  /**
   * Setup jvncsender mouse click system command.
   */
  public JvncSenderSystemCommand(int positionX, int positionY, int clickCount, int sleepTime) {
    logCommand = false;
    executeOnDockerHost = true;
    setSleepTime(sleepTime);
    this.positionX = positionX;
    this.positionY = positionY;
    this.clickCount = clickCount;
    setKameHouseCmdCommands();
    setOutputCommand();
  }

  @Override
  public SystemCommand.Output execute() {
    String host = DockerUtils.getHostname();
    String password = getVncServerPassword();
    logger.debug("execute {}", output.getCommand());
    try {
      VncSender vncSender = new VncSender(host, VNC_PORT, password);
      if (!StringUtils.isEmpty(text)) {
        vncSender.sendText(text);
      } else {
        vncSender.sendMouseClick(positionX, positionY, clickCount);
      }
      output.setStatus(SystemCommandStatus.COMPLETED.getStatus());
      output.setStandardOutput(List.of("JVNCSender command executed successfully"));
    } catch (Exception e) {
      logger.error("Error sending command to vnc server", e);
      output.setStatus(SystemCommandStatus.FAILED.getStatus());
      output.setStandardError(List.of("Error executing VNC command", e.getMessage()));
    }
    if (SystemCommandStatus.FAILED.getStatus().equals(output.getStatus())) {
      logger.error("execute {} response {}", output.getCommand(), output);
    } else {
      logger.debug("execute {} response {}", output.getCommand(), output);
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

  @Override
  protected String getKameHouseCmdArguments() {
    String host = DockerUtils.getHostname();
    String password = getVncServerPassword();
    StringBuilder args = new StringBuilder("-o jvncsender -host \"");
    args.append(host);
    args.append("\" -port ");
    args.append(VNC_PORT);
    args.append(" -password \"");
    args.append(password);
    args.append("\" -mouseClick \"");
    args.append(positionX);
    args.append(",");
    args.append(positionY);
    args.append(",");
    args.append(clickCount);
    args.append("\"");
    return args.toString();
  }
}
