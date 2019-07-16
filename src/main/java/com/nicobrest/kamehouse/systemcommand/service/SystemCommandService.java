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

  private static final Logger logger = LoggerFactory.getLogger(SystemCommandService.class);
  private static final int VNCDO_HOSTNAME_INDEX_WINDOWS = 4;
  private static final int VNCDO_VNC_SERVER_PASSWORD_INDEX_WINDOWS = 6;

  /**
   * Get the list of system commands for the specified AdminCommand.
   */
  public List<SystemCommand> getSystemCommands(AdminCommand adminCommand) {

    List<SystemCommand> systemCommands = new ArrayList<SystemCommand>();
    switch (adminCommand.getCommand()) {
      case AdminCommand.LOCK_SCREEN:
        systemCommands.add(getLockScreenSystemCommand());
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
      case AdminCommand.UNLOCK_SCREEN:
        systemCommands.addAll(getUnlockScreenSystemCommands());
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
        logger.error("Invalid AdminCommand " + adminCommand.getCommand());
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
      commandOutput.setCommand(processBuilder.command().toString());
      logger.trace("Executing system command " + processBuilder.command().toString());
    }
    Process process;
    InputStream processInputStream = null;
    BufferedReader processBufferedReader = null;
    InputStream processErrorStream = null;
    BufferedReader processErrorBufferedReader = null;
    try {
      process = ProcessUtils.startProcess(processBuilder);
      if (!systemCommand.isDaemon()) {
        // Not an ongoing process. Wait until the process finishes and then read
        // standard ouput and error streams.
        ProcessUtils.waitForProcess(process);
        // Read command standard output stream
        List<String> processStandardOuputList = new ArrayList<String>();
        processInputStream = ProcessUtils.getInputStreamFromProcess(process);
        processBufferedReader = new BufferedReader(new InputStreamReader(processInputStream,
            StandardCharsets.UTF_8));
        String inputStreamLine;
        while ((inputStreamLine = processBufferedReader.readLine()) != null) {
          if (!StringUtils.isEmpty(inputStreamLine)) {
            processStandardOuputList.add(inputStreamLine);
          }
        }
        commandOutput.setStandardOutput(processStandardOuputList);
        // Read command standard error stream
        List<String> processStandardErrorList = new ArrayList<String>();
        processErrorStream = ProcessUtils.getErrorStreamFromProcess(process);
        processErrorBufferedReader = new BufferedReader(new InputStreamReader(processErrorStream,
            StandardCharsets.UTF_8));
        String errorStreamLine;
        while ((errorStreamLine = processErrorBufferedReader.readLine()) != null) {
          if (!StringUtils.isEmpty(errorStreamLine)) {
            processStandardErrorList.add(errorStreamLine);
          }
        }
        commandOutput.setStandardError(processStandardErrorList);

        int exitValue = ProcessUtils.getExitValue(process);
        commandOutput.setExitCode(exitValue);
        if (exitValue > 0) {
          commandOutput.setStatus("failed");
        } else {
          commandOutput.setStatus("completed");
        }
      } else {
        // Ongoing process
        commandOutput.setExitCode(-1); // process is still running.
        commandOutput.setStatus("running");
      }

    } catch (IOException | InterruptedException e) {
      logger.error("Exception occurred while executing the process. Message: " + e.getMessage());
      e.printStackTrace();
      commandOutput.setExitCode(1);
      commandOutput.setStatus("failed");
      commandOutput.setStandardError(Arrays.asList("An error occurred executing the command"));
    } finally {
      if (processBufferedReader != null) {
        try {
          processBufferedReader.close();
        } catch (IOException e) {
          logger.error("Exception occurred while executing the process. Message: " + e
              .getMessage());
          e.printStackTrace();
          commandOutput.setExitCode(1);
          commandOutput.setStatus("failed");
          commandOutput.setStandardError(Arrays.asList(
              "An error occurred closing the input stream of the process."));
        }
      }
      if (processErrorBufferedReader != null) {
        try {
          processErrorBufferedReader.close();
        } catch (IOException e) {
          logger.error("Exception occurred while executing the process. Message: " + e
              .getMessage());
          e.printStackTrace();
          commandOutput.setExitCode(1);
          commandOutput.setStatus("failed");
          commandOutput.setStandardError(Arrays.asList(
              "An error occurred closing the error stream of the process."));
        }
      }
    }
    return commandOutput;
  }

  /**
   * Execute the specified list of system commands.
   */
  public List<SystemCommandOutput> execute(List<SystemCommand> systemCommands) {
    List<SystemCommandOutput> systemCommandOutputs = new ArrayList<SystemCommandOutput>();
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

    if (systemCommand.getCommand().contains("vncdo") || (systemCommand.getCommand().size() >= 3
        && systemCommand.getCommand().get(2).contains("vncdo"))) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Get the system command to stop a VLC player.
   */
  private SystemCommand getStopVlcSystemCommand() {

    SystemCommand stopVlcSystemCommand = new SystemCommand();
    stopVlcSystemCommand.setIsDaemon(false);
    List<String> command = new ArrayList<String>();
    if (PropertiesUtils.isWindowsHost()) {
      Collections.addAll(command, CommandLine.VLC_STOP_WINDOWS.get());
    } else {
      Collections.addAll(command, CommandLine.VLC_STOP_LINUX.get());
    }
    stopVlcSystemCommand.setCommand(command);
    return stopVlcSystemCommand;
  }

  /**
   * Get the system command to start a VLC player.
   */
  private SystemCommand getStartVlcSystemCommand(AdminCommand vlcStartAdminCommand) {

    SystemCommand startVlcSystemCommand = new SystemCommand();
    startVlcSystemCommand.setIsDaemon(true);
    String file = "";
    if (vlcStartAdminCommand.getFile() != null) {
      // TODO check if the file exists, if it doesn't throw an exception.
      file = vlcStartAdminCommand.getFile();
    }
    List<String> command = new ArrayList<String>();
    if (PropertiesUtils.isWindowsHost()) {
      Collections.addAll(command, CommandLine.VLC_START_WINDOWS.get());
      command.add(file);
    } else {
      Collections.addAll(command, CommandLine.VLC_START_LINUX.get());
      command.add(file);
    }
    startVlcSystemCommand.setCommand(command);
    return startVlcSystemCommand;
  }

  /**
   * Get the system command to check the status of a VLC player.
   */
  private SystemCommand getStatusVlcSystemCommand() {

    SystemCommand statusVlcSystemCommand = new SystemCommand();
    statusVlcSystemCommand.setIsDaemon(false);
    List<String> command = new ArrayList<String>();
    if (PropertiesUtils.isWindowsHost()) {
      Collections.addAll(command, CommandLine.VLC_STATUS_WINDOWS.get());
    } else {
      Collections.addAll(command, CommandLine.VLC_STATUS_LINUX.get());
    }
    statusVlcSystemCommand.setCommand(command);
    return statusVlcSystemCommand;
  }

  /**
   * Get the system command to set the server shutdown.
   */
  private SystemCommand getSetShutdownSystemCommand(AdminCommand shutdownSetAdminCommand) {

    SystemCommand setShutdownSystemCommand = new SystemCommand();
    setShutdownSystemCommand.setIsDaemon(false);
    if (shutdownSetAdminCommand.getTime() <= 0) {
      throw new KameHouseInvalidCommandException("Invalid time for shutdown command "
          + shutdownSetAdminCommand.getTime());
    }
    List<String> command = new ArrayList<String>();
    if (PropertiesUtils.isWindowsHost()) {
      Collections.addAll(command, CommandLine.SHUTDOWN_WINDOWS.get());
      command.add(String.valueOf(shutdownSetAdminCommand.getTime()));
    } else {
      int timeInMinutes = shutdownSetAdminCommand.getTime() / 60;
      Collections.addAll(command, CommandLine.SHUTDOWN_LINUX.get());
      command.add(String.valueOf(timeInMinutes));
    }
    setShutdownSystemCommand.setCommand(command);
    return setShutdownSystemCommand;
  }

  /**
   * Get the system command to cancel a server shutdown.
   */
  private SystemCommand getCancelShutdownSystemCommand() {

    SystemCommand cancelShutdownSystemCommand = new SystemCommand();
    cancelShutdownSystemCommand.setIsDaemon(false);
    List<String> command = new ArrayList<String>();
    if (PropertiesUtils.isWindowsHost()) {
      Collections.addAll(command, CommandLine.SHUTDOWN_CANCEL_WINDOWS.get());
    } else {
      Collections.addAll(command, CommandLine.SHUTDOWN_CANCEL_LINUX.get());
    }
    cancelShutdownSystemCommand.setCommand(command);
    return cancelShutdownSystemCommand;
  }

  /**
   * Get the system command to check the status of a scheduled shutdown.
   */
  private SystemCommand getStatusShutdownSystemCommand() {

    // TODO this doesn't work. Need to find a way to get the status both in win
    // and linux
    SystemCommand statusVlcSystemCommand = new SystemCommand();
    statusVlcSystemCommand.setIsDaemon(false);
    List<String> command = new ArrayList<String>();
    if (PropertiesUtils.isWindowsHost()) {
      Collections.addAll(command, CommandLine.SHUTDOWN_STATUS_WINDOWS.get());
    } else {
      Collections.addAll(command, CommandLine.SHUTDOWN_STATUS_LINUX.get());
    }
    statusVlcSystemCommand.setCommand(command);
    return statusVlcSystemCommand;
  }

  /**
   * Get the system command to lock the screen.
   */
  private SystemCommand getLockScreenSystemCommand() {

    SystemCommand lockScreenSystemCommand = new SystemCommand();
    lockScreenSystemCommand.setIsDaemon(false);
    List<String> lockScreenCommandList = new ArrayList<String>();
    if (PropertiesUtils.isWindowsHost()) {
      Collections.addAll(lockScreenCommandList, CommandLine.LOCK_SCREEN_WINDOWS.get());
    } else {
      Collections.addAll(lockScreenCommandList, CommandLine.LOCK_SCREEN_LINUX.get());
    }
    lockScreenSystemCommand.setCommand(lockScreenCommandList);
    return lockScreenSystemCommand;
  }

  /**
   * Get the system commands to unlock the screen.
   */
  private List<SystemCommand> getUnlockScreenSystemCommands() {

    // Lock screen first so if it is already unlocked, I don't type the password
    // anywhere
    List<SystemCommand> unlockScreenSystemCommands = new ArrayList<SystemCommand>();
    unlockScreenSystemCommands.add(getLockScreenSystemCommand());

    // Press ESC key command
    SystemCommand vncdoKeyEscSystemCommand = new SystemCommand();
    vncdoKeyEscSystemCommand.setIsDaemon(false);
    List<String> vncdoKeyEscCommandList = new ArrayList<String>();
    String hostname = PropertiesUtils.getHostname();
    String vncServerPassword = getVncServerPassword();
    if (PropertiesUtils.isWindowsHost()) {
      Collections.addAll(vncdoKeyEscCommandList, CommandLine.VNCDO_KEY_WINDOWS.get());
      vncdoKeyEscCommandList.set(VNCDO_HOSTNAME_INDEX_WINDOWS, PropertiesUtils.getHostname());
      vncdoKeyEscCommandList.set(VNCDO_VNC_SERVER_PASSWORD_INDEX_WINDOWS, vncServerPassword);
      vncdoKeyEscCommandList.add("esc");
    } else {
      Collections.addAll(vncdoKeyEscCommandList, CommandLine.VNCDO_KEY_LINUX.get());
      String vncdoCommand = vncdoKeyEscCommandList.get(2).replaceFirst("HOSTNAME", hostname)
          .replaceFirst("VNC_SERVER_PASSWORD", vncServerPassword).concat(" esc");
      vncdoKeyEscCommandList.set(2, vncdoCommand);
    }
    vncdoKeyEscSystemCommand.setCommand(vncdoKeyEscCommandList);
    unlockScreenSystemCommands.add(vncdoKeyEscSystemCommand);

    // Type user password command
    SystemCommand vncdoTypePasswordSystemCommand = new SystemCommand();
    vncdoTypePasswordSystemCommand.setIsDaemon(false);
    List<String> vncdoTypePasswordCommandList = new ArrayList<String>();
    String unlockScreenPassword = getUnlockScreenPassword();
    if (PropertiesUtils.isWindowsHost()) {
      Collections.addAll(vncdoTypePasswordCommandList, CommandLine.VNCDO_TYPE_WINDOWS.get());
      vncdoTypePasswordCommandList.set(VNCDO_HOSTNAME_INDEX_WINDOWS, PropertiesUtils
          .getHostname());
      vncdoTypePasswordCommandList.set(VNCDO_VNC_SERVER_PASSWORD_INDEX_WINDOWS, vncServerPassword);
      vncdoTypePasswordCommandList.add(unlockScreenPassword);
    } else {
      Collections.addAll(vncdoTypePasswordCommandList, CommandLine.VNCDO_TYPE_LINUX.get());
      String vncdoCommand = vncdoTypePasswordCommandList.get(2).replaceFirst("HOSTNAME", hostname)
          .replaceFirst("VNC_SERVER_PASSWORD", vncServerPassword).concat(" "
              + unlockScreenPassword);
      vncdoTypePasswordCommandList.set(2, vncdoCommand);
    }
    vncdoTypePasswordSystemCommand.setCommand(vncdoTypePasswordCommandList);
    unlockScreenSystemCommands.add(vncdoTypePasswordSystemCommand);

    // Press Enter key command
    SystemCommand vncdoKeyEnterSystemCommand = new SystemCommand();
    vncdoKeyEnterSystemCommand.setIsDaemon(false);
    List<String> vncdoKeyEnterCommandList = new ArrayList<String>();
    if (PropertiesUtils.isWindowsHost()) {
      Collections.addAll(vncdoKeyEnterCommandList, CommandLine.VNCDO_KEY_WINDOWS.get());
      vncdoKeyEnterCommandList.set(VNCDO_HOSTNAME_INDEX_WINDOWS, PropertiesUtils.getHostname());
      vncdoKeyEnterCommandList.set(VNCDO_VNC_SERVER_PASSWORD_INDEX_WINDOWS, vncServerPassword);
      vncdoKeyEnterCommandList.add("enter");
    } else {
      Collections.addAll(vncdoKeyEnterCommandList, CommandLine.VNCDO_KEY_LINUX.get());
      String vncdoCommand = vncdoKeyEnterCommandList.get(2).replaceFirst("HOSTNAME", hostname)
          .replaceFirst("VNC_SERVER_PASSWORD", vncServerPassword).concat(" enter");
      vncdoKeyEnterCommandList.set(2, vncdoCommand);
    }
    vncdoKeyEnterSystemCommand.setCommand(vncdoKeyEnterCommandList);
    unlockScreenSystemCommands.add(vncdoKeyEnterSystemCommand);

    return unlockScreenSystemCommands;
  }

  /**
   * Get the unlock screen password from a file.
   */
  private String getUnlockScreenPassword() {
    String unlockScreenPwdFile = PropertiesUtils.getUserHome() + "/" + PropertiesUtils
        .getAdminProperty("unlock.screen.pwd.file");
    String unlockScreenPassword = getDecodedPasswordFromFile(unlockScreenPwdFile);
    return unlockScreenPassword;
  }

  /**
   * Get the vnc server password from a file.
   */
  private String getVncServerPassword() {
    String vncServerPwdFile = PropertiesUtils.getUserHome() + "/" + PropertiesUtils
        .getAdminProperty("vnc.server.pwd.file");
    String vncServerPassword = getDecodedPasswordFromFile(vncServerPwdFile);
    return vncServerPassword;
  }

  /**
   * Get the encoded password from the specified file and decode it.
   */
  private String getDecodedPasswordFromFile(String passwordFile) {

    String decodedPassword = null;
    try {
      List<String> encodedPasswordList = Files.readAllLines(Paths.get(passwordFile));
      if (encodedPasswordList != null && !encodedPasswordList.isEmpty()) {
        String encodedPassword = encodedPasswordList.get(0);
        byte[] decodedPasswordBytes = Base64.getDecoder().decode(encodedPassword);
        decodedPassword = new String(decodedPasswordBytes, StandardCharsets.UTF_8);
      }
    } catch (IOException | IllegalArgumentException e) {
      logger.error("Error while reading password from file. Message: " + e.getMessage());
      decodedPassword = "ERROR_READING_PASSWORD";
    }
    if (StringUtils.isEmpty(decodedPassword)) {
      decodedPassword = "''";
    }
    return decodedPassword;
  }
}
