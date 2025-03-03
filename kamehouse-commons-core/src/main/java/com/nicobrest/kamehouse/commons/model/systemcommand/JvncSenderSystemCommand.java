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
 * JvncSender system command to control a VNC server. This class extends KameHouseCmdSystemCommand
 * because jvncsender can also be used via kamehouse-cmd. The current implementation sends the vnc
 * command to the vnc server directly from the app executing this system command by default. Set
 * useKameHouseCmd to true in subclasses to use kamehouse-cmd instead. When executing from docker
 * over ssh, it executes through kamehouse-cmd over ssh on the docker host.
 *
 * @author nbrest
 */
public abstract class JvncSenderSystemCommand extends KameHouseCmdSystemCommand {

  private static final int VNC_PORT = 5900;

  protected boolean useKameHouseCmd = false;

  protected JvncSenderSystemCommand() {
    logCommand = false;
    executeOnDockerHost = true;
  }

  /**
   * Send the command to execute an action on the vnc server directly without going through
   * kamehouse-cmd.
   */
  protected abstract void sendCommand(VncServer vncServer) throws KameHouseException;

  /**
   * Get the jvncsender arguments to be executed by kamehouse-cmd.
   */
  protected abstract String getKameHouseCmdJvncSenderOperationArgs();

  @Override
  public SystemCommand.Output execute() {
    if (useKameHouseCmd) {
      return super.execute();
    }
    return sendCommandToVncServer();
  }

  @Override
  protected boolean hideOutputCommand() {
    return true;
  }

  /**
   * Get kamehouse-cmd command arguments for executing through ssh on docker host.
   */
  @Override
  protected String getKameHouseCmdArguments() {
    String host = DockerUtils.getHostname();
    String password = getVncServerPassword();
    return "-o jvncsender -password " + password + " -host " + host + " -port " + VNC_PORT + " "
        + getKameHouseCmdJvncSenderOperationArgs();
  }

  /**
   * Gets the vnc server password from a file.
   */
  private static String getVncServerPassword() {
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
   * Send the command to the vnc server directly from the current app.
   */
  private SystemCommand.Output sendCommandToVncServer() {
    String host = DockerUtils.getHostname();
    String password = getVncServerPassword();
    initOutputCommand();
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

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
