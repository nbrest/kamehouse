package com.nicobrest.kamehouse.systemcommand.service;

import com.nicobrest.kamehouse.admin.model.CommandOutput;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SystemCommandService {

  private static final Logger logger = LoggerFactory.getLogger(SystemCommandService.class);

  /**
   * Start vlc player.
   */
  public CommandOutput startVlcPlayer() {

    CommandOutput commandOutput = new CommandOutput();
    boolean isWindowsOpSys = System.getProperty("os.name").toLowerCase(Locale.getDefault())
        .startsWith("windows");
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (isWindowsOpSys) {
      processBuilder.command("cmd.exe", "/c", "start", "vlc",
          "D:\\Series\\game_of_thrones\\GameOfThrones.m3u");
    } else {
      processBuilder.command("vlc", "/home/nbrest/Videos/lleyton.hewitt.m3u");
    }
    logger.trace("Executing system command: " + processBuilder.command().toString());
    try {
      processBuilder.start();
      // TODO: Create an enum of possible statuses (running, stopped, completed, failed)
      commandOutput.setStatus("running");
      commandOutput.setStandardOutput(Arrays.asList("VlcPlayer started successfully"));
    } catch (IOException e) {
      logger.error("Exception occurred while executing the process. Message: " + e.getMessage());
      e.printStackTrace();
      commandOutput.setExitCode(1);
      commandOutput.setStatus("failed");
      commandOutput.setStandardError(Arrays.asList("An error occurred starting VlcPlayer"));
    }
    return commandOutput;
  }

  /**
   * Stop vlc player.
   */
  public CommandOutput stopVlcPlayer() {

    CommandOutput commandOutput = new CommandOutput();
    boolean isWindowsOpSys = System.getProperty("os.name").toLowerCase(Locale.getDefault())
        .startsWith("windows");
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (isWindowsOpSys) {
      processBuilder.command("cmd.exe", "/c", "start", "taskkill", "/im", "vlc.exe");
    } else {
      processBuilder.command("skill", "-9", "vlc");
    }
    logger.trace("Executing system command: " + processBuilder.command().toString());
    try {
      processBuilder.start();
      // TODO wait for process to finish and get standard output and error
      // streams
      commandOutput.setExitCode(0);
      commandOutput.setStatus("stopped");
      commandOutput.setStandardOutput(Arrays.asList("VlcPlayer stopped successfully"));
    } catch (IOException e) {
      logger.error("Exception occurred while executing the process. Message: " + e.getMessage());
      e.printStackTrace();
      commandOutput.setExitCode(1);
      commandOutput.setStatus("failed");
      commandOutput.setStandardError(Arrays.asList("An error occurred stopping VlcPlayer"));
    }
    return commandOutput;
  }

  /**
   * Status vlc player.
   */
  public CommandOutput statusVlcPlayer() {

    CommandOutput commandOutput = new CommandOutput();
    boolean isWindowsOpSys = System.getProperty("os.name").toLowerCase(Locale.getDefault())
        .startsWith("windows");
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (isWindowsOpSys) {
      processBuilder.command("tasklist", "/FI", "IMAGENAME eq vlc.exe");
    } else {
      processBuilder.command("/bin/bash", "-c",
          "ps aux | grep -e \"vlc\\|COMMAND\" | grep -v grep");
    }
    logger.trace("processBuilder.command()" + processBuilder.command().toString());
    Process process;
    InputStream processInputStream = null;
    BufferedReader processBufferedReader = null;
    InputStream processErrorStream = null;
    BufferedReader processErrorBufferedReader = null;
    try {
      process = processBuilder.start();
      process.waitFor();
      commandOutput.setStatus("completed");
      commandOutput.setExitCode(process.exitValue());
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
    } catch (IOException | InterruptedException e) {
      logger.error("Exception occurred while executing the process. Message: " + e.getMessage());
      e.printStackTrace();
      commandOutput.setExitCode(1);
      commandOutput.setStatus("failed");
      commandOutput.setStandardError(Arrays.asList(
          "An error occurred getting the status of VlcPlayer"));
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
}
