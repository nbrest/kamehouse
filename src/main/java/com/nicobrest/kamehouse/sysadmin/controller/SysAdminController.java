package com.nicobrest.kamehouse.sysadmin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Locale;

@Controller
@RequestMapping(value = "/api/v1/sysadmin")
public class SysAdminController {

  /**
   * Test method to start a vlc player. 
   */
  @RequestMapping(value = "/vlc-player-start", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> startVlcPlayer() throws IOException {

    System.out.println("begin start vlc player");

    boolean isWindowsOpSys = System.getProperty("os.name").toLowerCase(Locale.getDefault())
        .startsWith("windows");
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (isWindowsOpSys) {
      processBuilder.command("cmd.exe", "/c", "start", "vlc",
          "D:\\Series\\game_of_thrones\\GameOfThrones.m3u");
    } else {
      processBuilder.command("vlc", "/home/nbrest/Videos/lleyton.hewitt.m3u");
    }
    System.out.println("processBuilder.command()" + processBuilder.command().toString());
    processBuilder.start();
    System.out.println("finish start vlc player");
    return new ResponseEntity<String>("Started vlc player", HttpStatus.OK);
  }

  /**
   * Test method to stop vlc player. 
   */
  @RequestMapping(value = "/vlc-player-stop", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> stopVlcPlayer() throws IOException {

    System.out.println("begin stop vlc player");

    boolean isWindowsOpSys = System.getProperty("os.name").toLowerCase(Locale.getDefault())
        .startsWith("windows");
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (isWindowsOpSys) {
      processBuilder.command("cmd.exe", "/c", "start", "taskkill", "/im", "vlc.exe");
    } else {
      processBuilder.command("skill", "-9", "vlc");
    }
    System.out.println("processBuilder.command()" + processBuilder.command().toString());
    processBuilder.start();
    System.out.println("finish stop vlc player");
    return new ResponseEntity<String>("Stopped vlc player", HttpStatus.OK);
  }

}
