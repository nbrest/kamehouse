package com.nicobrest.kamehouse.sysadmin.controller;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/v1/sysadmin")
public class SysAdminController {

  private static final Logger logger = LoggerFactory.getLogger(SysAdminController.class);

  /**
   * Test method to start a vlc player.
   */
  @RequestMapping(value = "/vlc-player-start", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> startVlcPlayer() {

    logger.trace("begin start vlc player");

    boolean isWindowsOpSys = System.getProperty("os.name").toLowerCase(Locale.getDefault())
        .startsWith("windows");
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (isWindowsOpSys) {
      processBuilder.command("cmd.exe", "/c", "start", "vlc",
          "D:\\Series\\game_of_thrones\\GameOfThrones.m3u");
    } else {
      processBuilder.command("vlc", "/home/nbrest/Videos/lleyton.hewitt.m3u");
    }
    logger.trace("processBuilder.command()" + processBuilder.command().toString());
    try {
      processBuilder.start();
    } catch (IOException e) {
      logger.error("Exception occurred while executing the process. Message: " + e.getMessage());
      e.printStackTrace();
    }
    logger.trace("finish start vlc player");
    return new ResponseEntity<String>("Started vlc player", HttpStatus.OK);
  }

  /**
   * Test method to stop vlc player.
   */
  @RequestMapping(value = "/vlc-player-stop", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> stopVlcPlayer() {

    logger.trace("begin stop vlc player");

    boolean isWindowsOpSys = System.getProperty("os.name").toLowerCase(Locale.getDefault())
        .startsWith("windows");
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (isWindowsOpSys) {
      processBuilder.command("cmd.exe", "/c", "start", "taskkill", "/im", "vlc.exe");
    } else {
      processBuilder.command("skill", "-9", "vlc");
    }
    logger.trace("processBuilder.command()" + processBuilder.command().toString());
    try {
      processBuilder.start();
    } catch (IOException e) {
      logger.error("Exception occurred while executing the process. Message: " + e.getMessage());
      e.printStackTrace();
    }
    logger.trace("finish stop vlc player");
    return new ResponseEntity<String>("Stopped vlc player", HttpStatus.OK);
  }

  /**
   * Test method to get the status of vlc player.
   */
  @RequestMapping(value = "/vlc-player-status", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> statusVlcPlayer() {

    logger.trace("begin status vlc player");

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
    Map<String, Object> processOutput = new HashMap<String, Object>();
    try {
      process = processBuilder.start();
      process.waitFor();
      List<String> processOuputList = new ArrayList<String>();
      // Read command standard output stream
      processInputStream = process.getInputStream();
      processBufferedReader = new BufferedReader(new InputStreamReader(processInputStream,
          StandardCharsets.UTF_8));
      String inputStreamLine;
      while ((inputStreamLine = processBufferedReader.readLine()) != null) {
        if (!StringUtils.isEmpty(inputStreamLine)) {
          processOuputList.add(inputStreamLine);
        }
      }
      // Read command standard error stream
      processErrorStream = process.getErrorStream();
      processErrorBufferedReader = new BufferedReader(new InputStreamReader(processErrorStream,
          StandardCharsets.UTF_8));
      String errorStreamLine;
      while ((errorStreamLine = processErrorBufferedReader.readLine()) != null) {
        if (!StringUtils.isEmpty(errorStreamLine)) {
          processOuputList.add(errorStreamLine);
        }
      }
      processOutput.put("exitStatus", process.exitValue());
      processOutput.put("output", processOuputList);
    } catch (IOException | InterruptedException e) {
      logger.error("Exception occurred while executing the process. Message: " + e.getMessage());
      e.printStackTrace();
    } finally {
      if (processBufferedReader != null) {
        try {
          processBufferedReader.close();
        } catch (IOException e) {
          logger.error("Exception occurred while executing the process. Message: " + e
              .getMessage());
          e.printStackTrace();
        }
      }
      if (processErrorBufferedReader != null) {
        try {
          processErrorBufferedReader.close();
        } catch (IOException e) {
          logger.error("Exception occurred while executing the process. Message: " + e
              .getMessage());
          e.printStackTrace();
        }
      }
    }
    logger.trace("finish status vlc player");
    return new ResponseEntity<Map<String, Object>>(processOutput, HttpStatus.OK);
  }

}
