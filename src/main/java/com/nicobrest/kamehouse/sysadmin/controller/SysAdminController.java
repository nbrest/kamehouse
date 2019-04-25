package com.nicobrest.kamehouse.sysadmin.controller;

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
import java.io.OutputStream;
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
  public ResponseEntity<String> startVlcPlayer() throws IOException {

    logger.debug("begin start vlc player");

    boolean isWindowsOpSys = System.getProperty("os.name").toLowerCase(Locale.getDefault())
        .startsWith("windows");
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (isWindowsOpSys) {
      processBuilder.command("cmd.exe", "/c", "start", "vlc",
          "D:\\Series\\game_of_thrones\\GameOfThrones.m3u");
    } else {
      processBuilder.command("vlc", "/home/nbrest/Videos/lleyton.hewitt.m3u");
    }
    logger.debug("processBuilder.command()" + processBuilder.command().toString());
    processBuilder.start();
    logger.debug("finish start vlc player");
    return new ResponseEntity<String>("Started vlc player", HttpStatus.OK);
  }

  /**
   * Test method to stop vlc player.
   */
  @RequestMapping(value = "/vlc-player-stop", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> stopVlcPlayer() throws IOException {

    logger.debug("begin stop vlc player");

    boolean isWindowsOpSys = System.getProperty("os.name").toLowerCase(Locale.getDefault())
        .startsWith("windows");
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (isWindowsOpSys) {
      processBuilder.command("cmd.exe", "/c", "start", "taskkill", "/im", "vlc.exe");
    } else {
      processBuilder.command("skill", "-9", "vlc");
    }
    logger.debug("processBuilder.command()" + processBuilder.command().toString());
    processBuilder.start();
    logger.debug("finish stop vlc player");
    return new ResponseEntity<String>("Stopped vlc player", HttpStatus.OK);
  }

  /**
   * Test method to get the status of vlc player.
   */
  @RequestMapping(value = "/vlc-player-status", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> statusVlcPlayer() throws IOException,
      InterruptedException {

    logger.debug("begin status vlc player");

    boolean isWindowsOpSys = System.getProperty("os.name").toLowerCase(Locale.getDefault())
        .startsWith("windows");
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (isWindowsOpSys) {
      processBuilder.command("tasklist", "/FI", "IMAGENAME eq vlc.exe");
    } else {
      processBuilder.command("ps", "aux", "|", "grep", "-e", "vlc\\|COMMAND", "|", "grep", "-v",
          "grep");
    }
    logger.debug("processBuilder.command()" + processBuilder.command().toString());
    Process process = processBuilder.start();
    process.waitFor();
    InputStream processInputStream = process.getInputStream();
    BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(
        processInputStream));
    String inputStreamLine;
    List<String> processOuputList = new ArrayList<String>();
    while ((inputStreamLine = inputStreamReader.readLine()) != null) {
      logger.info("inputStreamLine : " + inputStreamLine);
      processOuputList.add(inputStreamLine);
    }

    Map<String, Object> processOutput = new HashMap<String, Object>();
    processOutput.put("exitStatus", process.exitValue());
    processOutput.put("output", processOuputList);
    logger.debug("finish status vlc player");
    return new ResponseEntity<Map<String, Object>>(processOutput, HttpStatus.OK);
  }

}
