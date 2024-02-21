package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.model.SystemCommandStatus;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.jvncsender.VncServer;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JvncSender system command to control a VNC server. When running on docker it sends the commands
 * through ssh and kamehouse-cmd for mouse clicks only. Text strings are sent to the host directly
 * from the webapps running in the docker container.
 *
 * @author nbrest
 */
public abstract class JvncSenderSystemCommand extends SystemCommand {

  private static final int VNC_PORT = 5900;

  /**
   * Send the command to execute an action on the vnc server.
   */
  protected abstract void sendCommand(VncServer vncServer) throws KameHouseException;

  @Override
  public SystemCommand.Output execute() {
    String host = DockerUtils.getHostname();
    String password = getVncServerPassword();
    logger.debug("execute {}", output.getCommand());
    try {
      VncServer vncServer = new VncServer(host, VNC_PORT, password);
      sendCommand(vncServer);
      output.setExitCode(0);
      output.setStatus(SystemCommandStatus.COMPLETED.getStatus());
      output.setStandardOutput(List.of("JVNCSender command executed successfully"));
    } catch (KameHouseException e) {
      logger.error("Error sending command to vnc server", e);
      output.setExitCode(1);
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
}
