package com.nicobrest.kamehouse.systemcommand.service;

import com.nicobrest.kamehouse.admin.model.AdminCommand;
import com.nicobrest.kamehouse.main.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.systemcommand.model.CommandLine;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommand;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;
import com.nicobrest.kamehouse.utils.ProcessUtils;
import com.nicobrest.kamehouse.utils.PropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * Service to execute and manage system commands.
 * 
 * @author nbrest
 *
 */
@Service
public class SystemCommandService {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private static final int VNCDO_CMD_LINUX_INDEX = 2;
  private static final String COMPLETED = "completed";
  private static final String FAILED = "failed";
  private static final String RUNNING = "running";
  private static final String EXCEPTION_EXECUTING_PROCESS =
      "Exception occurred while executing the process. Message: {}";
  private static final String LINUX = "_LINUX";
  private static final String WINDOWS = "_WINDOWS";

  /**
   * Get the list of system commands for the specified AdminCommand.
   */
  public List<SystemCommand> getSystemCommands(AdminCommand adminCommand) {
    List<SystemCommand> systemCommands = new ArrayList<>();
    switch (adminCommand.getCommand()) {
      case AdminCommand.SCREEN_LOCK:
        systemCommands.add(getLockScreenSystemCommand());
        break;
      case AdminCommand.SCREEN_UNLOCK:
        systemCommands.addAll(getUnlockScreenSystemCommands());
        break;
      case AdminCommand.SCREEN_WAKE_UP:
        systemCommands.addAll(getScreenWakeUpSystemCommands());
        break;
      case AdminCommand.SHUTDOWN_CANCEL:
        systemCommands.add(getCancelShutdownSystemCommand());
        break;
      case AdminCommand.SHUTDOWN_SET:
        systemCommands.add(getSetShutdownSystemCommand(adminCommand));
        break;
      case AdminCommand.SHUTDOWN_STATUS:
        systemCommands.add(getStatusShutdownSystemCommand());
        break;
      case AdminCommand.SUSPEND:
        systemCommands.add(getSuspendSystemCommand());
        break;
      case AdminCommand.VLC_START:
        systemCommands.add(getStopVlcSystemCommand());
        systemCommands.add(getStartVlcSystemCommand(adminCommand));
        break;
      case AdminCommand.VLC_STATUS:
        systemCommands.add(getStatusVlcSystemCommand());
        break;
      case AdminCommand.VLC_STOP:
        systemCommands.add(getStopVlcSystemCommand());
        break;
      default:
        logger.error("Invalid AdminCommand {}", adminCommand.getCommand());
        throw new KameHouseInvalidCommandException("Invalid AdminCommand " + adminCommand
            .getCommand());
    }
    return systemCommands;
  }

  /**
   * Execute the specified SystemCommand.
   */
  public SystemCommandOutput execute(SystemCommand systemCommand) {
    SystemCommandOutput commandOutput = new SystemCommandOutput();
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command(systemCommand.getCommand());
    if (isVncdoCommand(systemCommand)) {
      // Don't show the command in the logs or the output for vncdo commands as
      // it may contain passwords
      commandOutput.setCommand("[vncdo (hidden from logs as it contains passwords)]");
      logger.trace("Executing system command [vncdo (hidden from logs as it contains passwords)]");
    } else {
      String command = processBuilder.command().toString();
      commandOutput.setCommand(command);
      logger.trace("Executing system command {}", command);
    }
    Process process;
    try {
      process = ProcessUtils.startProcess(processBuilder);
      if (!systemCommand.isDaemon()) {
        // Not an ongoing process. Wait until the process finishes and then read
        // standard ouput and error streams.
        ProcessUtils.waitForProcess(process);
        getStreamsFromProcess(process, commandOutput);
        int exitValue = ProcessUtils.getExitValue(process);
        commandOutput.setExitCode(exitValue);
        if (exitValue > 0) {
          commandOutput.setStatus(FAILED);
        } else {
          commandOutput.setStatus(COMPLETED);
        }
      } else {
        // Ongoing process
        commandOutput.setExitCode(-1); // process is still running.
        commandOutput.setStatus(RUNNING);
      }
    } catch (IOException e) {
      logger.error(EXCEPTION_EXECUTING_PROCESS, e.getMessage());
      commandOutput.setExitCode(1);
      commandOutput.setStatus(FAILED);
      commandOutput.setStandardError(Arrays.asList("An error occurred executing the command"));
    } catch (InterruptedException e) {
      logger.error(EXCEPTION_EXECUTING_PROCESS, e.getMessage());
      Thread.currentThread().interrupt();
    }
    return commandOutput;
  }

  /**
   * Execute the specified list of system commands.
   */
  public List<SystemCommandOutput> execute(List<SystemCommand> systemCommands) {
    List<SystemCommandOutput> systemCommandOutputs = new ArrayList<>();
    for (SystemCommand systemCommand : systemCommands) {
      SystemCommandOutput systemCommandOutput = execute(systemCommand);
      systemCommandOutputs.add(systemCommandOutput);
    }
    return systemCommandOutputs;
  }

  /**
   * Returns true if the command is a vncdo command.
   */
  private boolean isVncdoCommand(SystemCommand systemCommand) {
    return systemCommand.getCommand().contains("vncdo") || (systemCommand.getCommand().size() >= 3
        && systemCommand.getCommand().get(2).contains("vncdo"));
  }

  /**
   * Get the system command to stop a VLC player.
   */
  private SystemCommand getStopVlcSystemCommand() {
    SystemCommand stopVlcSystemCommand = getSystemCommand("VLC_STOP");
    stopVlcSystemCommand.setIsDaemon(false);
    return stopVlcSystemCommand;
  }

  /**
   * Get the system command to start a VLC player.
   */
  private SystemCommand getStartVlcSystemCommand(AdminCommand vlcStartAdminCommand) {
    SystemCommand startVlcSystemCommand = getSystemCommand("VLC_START");
    startVlcSystemCommand.setIsDaemon(true);
    String file = "";
    if (vlcStartAdminCommand.getFile() != null) {
      file = vlcStartAdminCommand.getFile();
    }
    List<String> command = startVlcSystemCommand.getCommand();
    command.add(file);
    return startVlcSystemCommand;
  }

  /**
   * Get the system command to check the status of a VLC player.
   */
  private SystemCommand getStatusVlcSystemCommand() {
    SystemCommand statusVlcSystemCommand = getSystemCommand("VLC_STATUS");
    statusVlcSystemCommand.setIsDaemon(false);
    return statusVlcSystemCommand;
  }

  /**
   * Get the system command to set the server shutdown.
   */
  private SystemCommand getSetShutdownSystemCommand(AdminCommand shutdownSetAdminCommand) {
    SystemCommand setShutdownSystemCommand = getSystemCommand("SHUTDOWN");
    setShutdownSystemCommand.setIsDaemon(false);
    if (shutdownSetAdminCommand.getTime() <= 0) {
      throw new KameHouseInvalidCommandException("Invalid time for shutdown command "
          + shutdownSetAdminCommand.getTime());
    }
    int timeToShutdown = shutdownSetAdminCommand.getTime();
    List<String> command = setShutdownSystemCommand.getCommand();
    if (!PropertiesUtils.isWindowsHost()) {
      timeToShutdown = timeToShutdown / 60;
    }
    command.add(String.valueOf(timeToShutdown));
    return setShutdownSystemCommand;
  }

  /**
   * Get the system command to cancel a server shutdown.
   */
  private SystemCommand getCancelShutdownSystemCommand() {
    SystemCommand cancelShutdownSystemCommand = getSystemCommand("SHUTDOWN_CANCEL");
    cancelShutdownSystemCommand.setIsDaemon(false);
    return cancelShutdownSystemCommand;
  }

  /**
   * Get the system command to check the status of a scheduled shutdown.
   */
  private SystemCommand getStatusShutdownSystemCommand() {
    SystemCommand statusShutdownSystemCommand = getSystemCommand("SHUTDOWN_STATUS");
    statusShutdownSystemCommand.setIsDaemon(false);
    return statusShutdownSystemCommand;
  }

  /**
   * Get the system command to suspend the server.
   */
  private SystemCommand getSuspendSystemCommand() {
    SystemCommand suspendSystemCommand = getSystemCommand("SUSPEND");
    // Set daemon to true, otherwise the process will wait until suspend command
    // finishes to return and that won't happen
    suspendSystemCommand.setIsDaemon(true);
    return suspendSystemCommand;
  }

  /**
   * Get the system command to lock the screen.
   */
  private SystemCommand getLockScreenSystemCommand() {
    SystemCommand lockScreenSystemCommand = getSystemCommand("LOCK_SCREEN");
    lockScreenSystemCommand.setIsDaemon(false);
    return lockScreenSystemCommand;
  }

  /**
   * Get the system commands to unlock the screen.
   */
  private List<SystemCommand> getUnlockScreenSystemCommands() {
    // Lock screen first so if it is already unlocked, I don't type the password
    // anywhere
    List<SystemCommand> unlockScreenSystemCommands = new ArrayList<>();
    unlockScreenSystemCommands.add(getLockScreenSystemCommand());

    // Press ESC key command
    SystemCommand vncdoKeyEscSystemCommand = getSystemCommand("VNCDO_KEY");
    vncdoKeyEscSystemCommand.setIsDaemon(false);
    List<String> vncdoKeyEscCommandList = vncdoKeyEscSystemCommand.getCommand();
    setVncdoHostnameAndPassword(vncdoKeyEscCommandList);
    if (PropertiesUtils.isWindowsHost()) {
      vncdoKeyEscCommandList.add("esc");
    } else {
      String vncdoCommand = vncdoKeyEscCommandList.get(VNCDO_CMD_LINUX_INDEX).concat(" esc");
      vncdoKeyEscCommandList.set(VNCDO_CMD_LINUX_INDEX, vncdoCommand);
    }
    unlockScreenSystemCommands.add(vncdoKeyEscSystemCommand);

    // Type user password command
    SystemCommand vncdoTypePasswordSystemCommand = getSystemCommand("VNCDO_TYPE");
    vncdoTypePasswordSystemCommand.setIsDaemon(false);
    List<String> vncdoTypePasswordCommandList = vncdoTypePasswordSystemCommand.getCommand();
    setVncdoHostnameAndPassword(vncdoTypePasswordCommandList);
    String unlockScreenPassword = getUnlockScreenPassword();
    if (PropertiesUtils.isWindowsHost()) {
      vncdoTypePasswordCommandList.add(unlockScreenPassword);
    } else {
      String vncdoCommand = vncdoTypePasswordCommandList.get(VNCDO_CMD_LINUX_INDEX).concat(" "
          + unlockScreenPassword);
      vncdoTypePasswordCommandList.set(VNCDO_CMD_LINUX_INDEX, vncdoCommand);
    }
    unlockScreenSystemCommands.add(vncdoTypePasswordSystemCommand);

    // Press Enter key command
    SystemCommand vncdoKeyEnterSystemCommand = getSystemCommand("VNCDO_KEY");
    vncdoKeyEnterSystemCommand.setIsDaemon(false);
    List<String> vncdoKeyEnterCommandList = vncdoKeyEnterSystemCommand.getCommand();
    setVncdoHostnameAndPassword(vncdoKeyEnterCommandList);
    if (PropertiesUtils.isWindowsHost()) {
      vncdoKeyEnterCommandList.add("enter");
    } else {
      String vncdoCommand = vncdoKeyEnterCommandList.get(VNCDO_CMD_LINUX_INDEX).concat(" enter");
      vncdoKeyEnterCommandList.set(VNCDO_CMD_LINUX_INDEX, vncdoCommand);
    }
    unlockScreenSystemCommands.add(vncdoKeyEnterSystemCommand);
    return unlockScreenSystemCommands;
  }

  /**
   * Get the system commands to wake up the screen. Execute 3 clicks on
   * different parts of the screen to wake it up.
   */
  private List<SystemCommand> getScreenWakeUpSystemCommands() {
    List<SystemCommand> screenWakeUpSystemCommands = new ArrayList<>();
    screenWakeUpSystemCommands.add(getSingleClick("400", "400"));
    screenWakeUpSystemCommands.add(getSingleClick("400", "500"));
    screenWakeUpSystemCommands.add(getSingleClick("500", "500"));
    return screenWakeUpSystemCommands;
  }

  /**
   * Execute a single click in the specified coordinates. The coordinates are a
   * string in the format "HORIZONTAL_POSITION VERTICAL_POSITION" starting from
   * "0 0" on the top left of the screen.
   */
  private SystemCommand getSingleClick(String horizontalPosition, String verticalPosition) {
    SystemCommand vncdoSingleClickSystemCommand = getSystemCommand("VNCDO_CLICK_SINGLE");
    vncdoSingleClickSystemCommand.setIsDaemon(false);
    List<String> vncdoSingleClickCommandList = vncdoSingleClickSystemCommand.getCommand();
    setVncdoHostnameAndPassword(vncdoSingleClickCommandList);
    if (PropertiesUtils.isWindowsHost()) {
      int vncdoSingleClickHorizontalPositionIndex = 8;
      int vncdoSingleClickVerticalPositionIndex = 9;
      vncdoSingleClickCommandList.set(vncdoSingleClickHorizontalPositionIndex, horizontalPosition);
      vncdoSingleClickCommandList.set(vncdoSingleClickVerticalPositionIndex, verticalPosition);
    } else {
      String vncdoCommand = vncdoSingleClickCommandList.get(VNCDO_CMD_LINUX_INDEX);
      vncdoCommand = vncdoCommand.replaceFirst("HORIZONTAL_POSITION", horizontalPosition);
      vncdoCommand = vncdoCommand.replaceFirst("VERTICAL_POSITION", verticalPosition);
      vncdoSingleClickCommandList.set(VNCDO_CMD_LINUX_INDEX, vncdoCommand);
    }
    return vncdoSingleClickSystemCommand;
  }

  /**
   * Set hostname and vnc server password for vncdo commands.
   */
  private void setVncdoHostnameAndPassword(List<String> vncdoCommandList) {
    String hostname = PropertiesUtils.getHostname();
    String vncServerPassword = getVncServerPassword();
    if (PropertiesUtils.isWindowsHost()) {
      int vncdoHostnameIndex = 4;
      int vncdoVncServerPasswordIndex = 6;
      vncdoCommandList.set(vncdoHostnameIndex, hostname);
      vncdoCommandList.set(vncdoVncServerPasswordIndex, vncServerPassword);
    } else {
      String vncdoCommand = vncdoCommandList.get(VNCDO_CMD_LINUX_INDEX);
      vncdoCommand = vncdoCommand.replaceFirst("HOSTNAME", hostname);
      vncdoCommand = vncdoCommand.replaceFirst("VNC_SERVER_PASSWORD", vncServerPassword);
      vncdoCommandList.set(VNCDO_CMD_LINUX_INDEX, vncdoCommand);
    }
  }

  /**
   * Get the unlock screen password from a file.
   */
  private String getUnlockScreenPassword() {
    String unlockScreenPwdFile = PropertiesUtils.getUserHome() + "/" + PropertiesUtils
        .getAdminProperty("unlock.screen.pwd.file");
    return getDecodedPasswordFromFile(unlockScreenPwdFile);
  }

  /**
   * Get the vnc server password from a file.
   */
  private String getVncServerPassword() {
    String vncServerPwdFile = PropertiesUtils.getUserHome() + "/" + PropertiesUtils
        .getAdminProperty("vnc.server.pwd.file");
    return getDecodedPasswordFromFile(vncServerPwdFile);
  }

  /**
   * Get the encoded password from the specified file and decode it.
   */
  private String getDecodedPasswordFromFile(String passwordFile) {
    String decodedFileContent = null;
    try {
      List<String> encodedFileContentList = Files.readAllLines(Paths.get(passwordFile));
      if (encodedFileContentList != null && !encodedFileContentList.isEmpty()) {
        String encodedFileContent = encodedFileContentList.get(0);
        byte[] decodedFileContentBytes = Base64.getDecoder().decode(encodedFileContent);
        decodedFileContent = new String(decodedFileContentBytes, StandardCharsets.UTF_8);
      }
    } catch (IOException | IllegalArgumentException e) {
      logger.error("Error while reading pwd from file. Message: {}", e.getMessage());
      decodedFileContent = "ERROR_READING_FILE";
    }
    if (StringUtils.isEmpty(decodedFileContent)) {
      decodedFileContent = "''";
    }
    return decodedFileContent;
  }

  /**
   * Get input and error streams from process and add them to the system command
   * output.
   */
  private void getStreamsFromProcess(Process process, SystemCommandOutput commandOutput)
      throws IOException {
    try (InputStream processInputStream = ProcessUtils.getInputStreamFromProcess(process);
        BufferedReader processBufferedReader = new BufferedReader(new InputStreamReader(
            processInputStream, StandardCharsets.UTF_8)); 
        InputStream processErrorStream = ProcessUtils.getErrorStreamFromProcess(process);
        BufferedReader processErrorBufferedReader = new BufferedReader(new InputStreamReader(
            processErrorStream, StandardCharsets.UTF_8))) {
      // Read command standard output stream
      List<String> processStandardOuputList = new ArrayList<>();
      String inputStreamLine;
      while ((inputStreamLine = processBufferedReader.readLine()) != null) {
        if (!StringUtils.isEmpty(inputStreamLine)) {
          processStandardOuputList.add(inputStreamLine);
        }
      }
      commandOutput.setStandardOutput(processStandardOuputList);
      // Read command standard error stream
      List<String> processStandardErrorList = new ArrayList<>();
      String errorStreamLine;
      while ((errorStreamLine = processErrorBufferedReader.readLine()) != null) {
        if (!StringUtils.isEmpty(errorStreamLine)) {
          processStandardErrorList.add(errorStreamLine);
        }
      }
      commandOutput.setStandardError(processStandardErrorList);
    }
  }

  /**
   * Get the specified system command for the correct operating system.
   */
  private SystemCommand getSystemCommand(String systemCommandName) {
    SystemCommand systemCommand = new SystemCommand();
    List<String> systemCommandList = new ArrayList<>();
    if (PropertiesUtils.isWindowsHost()) {
      Collections.addAll(systemCommandList, CommandLine.valueOf(systemCommandName + WINDOWS)
          .get());
    } else {
      Collections.addAll(systemCommandList, CommandLine.valueOf(systemCommandName + LINUX).get());
    }
    systemCommand.setCommand(systemCommandList);
    return systemCommand;
  }
}
