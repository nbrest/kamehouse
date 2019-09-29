package com.nicobrest.kamehouse.systemcommand.service;

import com.nicobrest.kamehouse.systemcommand.model.SystemCommand;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;
import com.nicobrest.kamehouse.utils.ProcessUtils;

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

/**
 * Service to execute and manage system commands.
 * 
 * @author nbrest
 *
 */
@Service
public class SystemCommandService {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private static final String COMPLETED = "completed";
  private static final String FAILED = "failed";
  private static final String RUNNING = "running";
  private static final String EXCEPTION_EXECUTING_PROCESS =
      "Exception occurred while executing the process. Message: {}";

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
}
