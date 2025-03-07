package com.nicobrest.kamehouse.commons.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.model.KameHouseCommandStatus;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.jvncsender.VncServer;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JvncSender kamehouse command to control a VNC server. This class extends
 * KameHouseCmdKameHouseCommand because jvncsender can also be used via kamehouse-cmd. The current
 * implementation sends the vnc command to the vnc server directly from the app executing this
 * kamehouse command by default but the behavior can be overriden to use kamehouse-cmd overriding
 * useKameHouseCmd.
 *
 * @author nbrest
 */
public abstract class JvncSenderKameHouseCommand extends KameHouseCmdKameHouseCommand {

  private static final int VNC_PORT = 5900;

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
  public KameHouseCommandResult execute() {
    if (useKameHouseCmd()) {
      return super.execute();
    }
    return sendCommandToVncServer();
  }

  @Override
  public boolean hasSensitiveInformation() {
    return true;
  }

  @Override
  public boolean executeOnDockerHost() {
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
   * Override in subclasses where jvncsender needs to be executed via kamehouse-cmd.
   */
  protected boolean useKameHouseCmd() {
    return false;
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
  private KameHouseCommandResult sendCommandToVncServer() {
    init();
    String host = DockerUtils.getHostname();
    String password = getVncServerPassword();
    KameHouseCommandResult kameHouseCommandResult = new KameHouseCommandResult(this);
    logger.debug("execute {}", kameHouseCommandResult.getCommand());
    try {
      VncServer vncServer = new VncServer(host, VNC_PORT, password);
      sendCommand(vncServer);
      kameHouseCommandResult.setExitCode(0);
      kameHouseCommandResult.setStatus(KameHouseCommandStatus.COMPLETED.getStatus());
      kameHouseCommandResult.setStandardOutput(List.of("JVNCSender command executed successfully"));
    } catch (KameHouseException e) {
      logger.error("Error sending command to vnc server", e);
      kameHouseCommandResult.setExitCode(1);
      kameHouseCommandResult.setStatus(KameHouseCommandStatus.FAILED.getStatus());
      kameHouseCommandResult.setStandardError(
          List.of("Error executing VNC command", e.getMessage()));
    }
    if (KameHouseCommandStatus.FAILED.getStatus().equals(kameHouseCommandResult.getStatus())) {
      logger.error("execute {} response {}", kameHouseCommandResult.getCommand(),
          kameHouseCommandResult);
    } else {
      logger.debug("execute {} response {}", kameHouseCommandResult.getCommand(),
          kameHouseCommandResult);
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
    kameHouseCommandResult.setHtmlOutputs();
    return kameHouseCommandResult;
  }
}
