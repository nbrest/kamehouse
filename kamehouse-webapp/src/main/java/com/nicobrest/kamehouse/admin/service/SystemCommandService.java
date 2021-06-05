package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.admin.model.admincommand.AdminCommand;
import com.nicobrest.kamehouse.admin.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.main.utils.ProcessUtils;

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
import java.util.concurrent.TimeUnit;

/**
 * Service to execute and manage system commands.
 *
 * @author nbrest
 */
@Service
public class SystemCommandService {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private static final String COMPLETED = "completed";
  private static final String FAILED = "failed";
  private static final String RUNNING = "running";
  private static final String EXCEPTION_EXECUTING_PROCESS =
      "Error occurred while executing the process.";

  /**
   * Executes an AdminCommand. Translates it to system commands and executes them.
   */
  public List<SystemCommand.Output> execute(AdminCommand adminCommand) {
    return execute(adminCommand.getSystemCommands());
  }

  /**
   * Executes the specified SystemCommand.
   */
  public SystemCommand.Output execute(SystemCommand systemCommand) {
    SystemCommand.Output commandOutput = systemCommand.getOutput();
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command(systemCommand.getCommand());
    logger.debug("execute {}", commandOutput.getCommand());
    Process process;
    try {
      process = ProcessUtils.start(processBuilder);
      if (!systemCommand.isDaemon()) {
        // Not an ongoing process. Wait until the process finishes and then read
        // standard ouput and error streams.
        ProcessUtils.waitFor(process);
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
      logger.error(EXCEPTION_EXECUTING_PROCESS, e);
      commandOutput.setExitCode(1);
      commandOutput.setStatus(FAILED);
      commandOutput.setStandardError(Arrays.asList(
          "An error occurred executing the command. Message: " + e.getMessage()));
    } catch (InterruptedException e) {
      logger.error(EXCEPTION_EXECUTING_PROCESS, e);
      Thread.currentThread().interrupt();
    }
    if (FAILED.equals(commandOutput.getStatus())) {
      logger.error("execute {} response {}", commandOutput.getCommand(), commandOutput);
    } else {
      logger.trace("execute {} response {}", commandOutput.getCommand(), commandOutput);
    }
    try {
      int sleepTime = systemCommand.getSleepTime();
      if (sleepTime > 0) {
        logger.debug("Sleeping for {} seconds", sleepTime);
        TimeUnit.SECONDS.sleep(sleepTime);
      }
    } catch (InterruptedException e) {
      logger.warn("Interrupted exception", e);
      Thread.currentThread().interrupt();
    }
    return commandOutput;
  }

  /**
   * Executes the specified list of system commands.
   */
  public List<SystemCommand.Output> execute(List<SystemCommand> systemCommands) {
    List<SystemCommand.Output> systemCommandOutputs = new ArrayList<>();
    for (SystemCommand systemCommand : systemCommands) {
      SystemCommand.Output systemCommandOutput = execute(systemCommand);
      systemCommandOutputs.add(systemCommandOutput);
    }
    return systemCommandOutputs;
  }

  /**
   * Gets input and error streams from process and add them to the system command
   * output.
   */
  private void getStreamsFromProcess(Process process, SystemCommand.Output commandOutput)
      throws IOException {
    try (InputStream processInputStream = ProcessUtils.getInputStream(process);
         BufferedReader processBufferedReader =
             new BufferedReader(new InputStreamReader(processInputStream, StandardCharsets.UTF_8));
         InputStream processErrorStream = ProcessUtils.getErrorStream(process);
         BufferedReader processErrorBufferedReader = new BufferedReader(
             new InputStreamReader(processErrorStream, StandardCharsets.UTF_8))) {
      // Read command standard output stream
      List<String> processStandardOuputList = readStreamIntoList(processBufferedReader);
      commandOutput.setStandardOutput(processStandardOuputList);
      // Read command standard error stream
      List<String> processStandardErrorList = readStreamIntoList(processErrorBufferedReader);
      commandOutput.setStandardError(processStandardErrorList);
    }
  }

  /**
   * Reads the stream from a buffered reader and store it in a List of Strings.
   */
  private List<String> readStreamIntoList(BufferedReader bufferedReader) throws IOException {
    List<String> streamAsList = new ArrayList<>();
    String streamLine;
    while ((streamLine = bufferedReader.readLine()) != null) {
      if (!StringUtils.isEmpty(streamLine)) {
        streamAsList.add(streamLine);
      }
    }
    return streamAsList;
  }
}
