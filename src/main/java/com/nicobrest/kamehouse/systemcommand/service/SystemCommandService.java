package com.nicobrest.kamehouse.systemcommand.service;

import com.nicobrest.kamehouse.admin.model.AdminShutdownCommand;
import com.nicobrest.kamehouse.admin.model.AdminVlcCommand;
import com.nicobrest.kamehouse.main.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommand;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;
import com.nicobrest.kamehouse.utils.SystemPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SystemCommandService {

  private static final Logger logger = LoggerFactory.getLogger(SystemCommandService.class);

  /**
   * Get the list of system commands for the specified AdminVlcCommand.
   */
  public List<SystemCommand> getSystemCommands(AdminVlcCommand adminVlcCommand) {

    List<SystemCommand> systemCommands = new ArrayList<SystemCommand>();
    switch (adminVlcCommand.getCommand()) {
      case "start":
        systemCommands.add(getStopVlcSystemCommand());
        systemCommands.add(getStartVlcSystemCommand(adminVlcCommand));
        break;
      case "stop":
        systemCommands.add(getStopVlcSystemCommand());
        break;
      case "status":
        systemCommands.add(getStatusVlcSystemCommand());
        break;
      default:
        logger.error("Invalid AdminVlcCommand " + adminVlcCommand.getCommand());
        throw new KameHouseInvalidCommandException("Invalid AdminVlcCommand " + adminVlcCommand
            .getCommand());
    }
    return systemCommands;
  }

  /**
   * Get the list of system commands for the specified AdminShutdownCommand.
   */
  public List<SystemCommand> getSystemCommands(AdminShutdownCommand adminShutdownCommand) {

    List<SystemCommand> systemCommands = new ArrayList<SystemCommand>();
    switch (adminShutdownCommand.getCommand()) {
      case "set":
        systemCommands.add(getSetShutdownSystemCommand(adminShutdownCommand));
        break;
      case "cancel":
        systemCommands.add(getCancelShutdownSystemCommand());
        break;
      case "status":
        systemCommands.add(getStatusShutdownSystemCommand());
        break;
      default:
        logger.error("Invalid AdminShutdownCommand " + adminShutdownCommand.getCommand());
        throw new KameHouseInvalidCommandException("Invalid AdminShutdownCommand "
            + adminShutdownCommand.getCommand());
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
    commandOutput.setCommand(processBuilder.command().toString());
    logger.trace("Executing system command " + processBuilder.command().toString());
    Process process;
    InputStream processInputStream = null;
    BufferedReader processBufferedReader = null;
    InputStream processErrorStream = null;
    BufferedReader processErrorBufferedReader = null;
    try {
      process = processBuilder.start();
      if (!systemCommand.isDaemon()) {
        // Not an ongoing process. Wait until the process finishes and then read
        // standard ouput and error streams.
        process.waitFor();
        // Read command standard output stream
        List<String> processStandardOuputList = new ArrayList<String>();
        processInputStream = process.getInputStream();
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
        processErrorStream = process.getErrorStream();
        processErrorBufferedReader = new BufferedReader(new InputStreamReader(processErrorStream,
            StandardCharsets.UTF_8));
        String errorStreamLine;
        while ((errorStreamLine = processErrorBufferedReader.readLine()) != null) {
          if (!StringUtils.isEmpty(errorStreamLine)) {
            processStandardErrorList.add(errorStreamLine);
          }
        }
        commandOutput.setStandardError(processStandardErrorList);

        commandOutput.setExitCode(process.exitValue());
        if (process.exitValue() > 0) {
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

  private SystemCommand getStopVlcSystemCommand() {
    SystemCommand stopVlcSystemCommand = new SystemCommand();
    stopVlcSystemCommand.setIsDaemon(false);
    if (SystemPropertiesUtils.IS_WINDOWS_HOST) {
      String[] command = { "cmd.exe", "/c", "start", "taskkill", "/im", "vlc.exe" };
      stopVlcSystemCommand.setCommand(command);
    } else {
      String[] command = { "skill", "-9", "vlc" };
      stopVlcSystemCommand.setCommand(command);
    }
    return stopVlcSystemCommand;
  }

  private SystemCommand getStartVlcSystemCommand(AdminVlcCommand adminVlcCommand) {
    SystemCommand startVlcSystemCommand = new SystemCommand();
    startVlcSystemCommand.setIsDaemon(true);
    String file = "";
    if (adminVlcCommand.getFile() != null) {
      // TODO check if the file exists, if it doesn't throw an exception.
      file = adminVlcCommand.getFile();
    }
    if (SystemPropertiesUtils.IS_WINDOWS_HOST) {
      String[] command = { "cmd.exe", "/c", "start", "vlc", file };
      startVlcSystemCommand.setCommand(command);
    } else {
      String[] command = { "vlc", file };
      startVlcSystemCommand.setCommand(command);
    }
    return startVlcSystemCommand;
  }

  private SystemCommand getStatusVlcSystemCommand() {
    SystemCommand statusVlcSystemCommand = new SystemCommand();
    statusVlcSystemCommand.setIsDaemon(false);
    if (SystemPropertiesUtils.IS_WINDOWS_HOST) {
      String[] command = { "tasklist", "/FI", "IMAGENAME eq vlc.exe" };
      statusVlcSystemCommand.setCommand(command);
    } else {
      String[] command = { "/bin/bash", "-c",
          "ps aux | grep -e \"vlc\\|COMMAND\" | grep -v grep" };
      statusVlcSystemCommand.setCommand(command);
    }
    return statusVlcSystemCommand;
  }

  private SystemCommand getSetShutdownSystemCommand(AdminShutdownCommand adminShutdownCommand) {
    SystemCommand setShutdownSystemCommand = new SystemCommand();
    setShutdownSystemCommand.setIsDaemon(false);
    if (adminShutdownCommand.getTime() <= 0) {
      throw new KameHouseInvalidCommandException("Invalid time for shutdown command "
          + adminShutdownCommand.getTime());
    }
    if (SystemPropertiesUtils.IS_WINDOWS_HOST) {
      String[] command = { "cmd.exe", "/c", "start", "shutdown", "/s", "/t", String.valueOf(
          adminShutdownCommand.getTime()) };
      setShutdownSystemCommand.setCommand(command);
    } else {
      int timeInMinutes = adminShutdownCommand.getTime() / 60;
      String[] command = { "/bin/bash", "-c", "sudo /sbin/shutdown -P " + String.valueOf(
          timeInMinutes) };
      setShutdownSystemCommand.setCommand(command);
    }
    return setShutdownSystemCommand;
  }

  private SystemCommand getCancelShutdownSystemCommand() {
    SystemCommand cancelShutdownSystemCommand = new SystemCommand();
    cancelShutdownSystemCommand.setIsDaemon(false);
    if (SystemPropertiesUtils.IS_WINDOWS_HOST) {
      String[] command = { "cmd.exe", "/c", "start", "shutdown", "/a" };
      cancelShutdownSystemCommand.setCommand(command);
    } else {
      String[] command = { "/bin/bash", "-c", "sudo /sbin/shutdown -c" };
      cancelShutdownSystemCommand.setCommand(command);
    }
    return cancelShutdownSystemCommand;
  }

  private SystemCommand getStatusShutdownSystemCommand() {
    // TODO this doesn't work. Need to find a way to get the status both in win
    // and linux
    SystemCommand statusVlcSystemCommand = new SystemCommand();
    statusVlcSystemCommand.setIsDaemon(false);
    if (SystemPropertiesUtils.IS_WINDOWS_HOST) {
      String[] command = { "tasklist", "/FI", "IMAGENAME eq shutdown.exe" };
      statusVlcSystemCommand.setCommand(command);
    } else {
      String[] command = { "/bin/bash", "-c",
          "ps aux | grep -e \"shutdown\\|COMMAND\" | grep -v grep" };
      statusVlcSystemCommand.setCommand(command);
    }
    return statusVlcSystemCommand;
  }
}
