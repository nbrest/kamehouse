package com.nicobrest.kamehouse.vlcrc.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractSystemCommandController;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.vlcrc.model.kamehousecommand.VlcStartKameHouseSystemCommand;
import com.nicobrest.kamehouse.vlcrc.model.kamehousecommand.VlcStatusKameHouseSystemCommand;
import com.nicobrest.kamehouse.vlcrc.model.kamehousecommand.VlcStopKameHouseSystemCommand;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * Controller class to start, stop and get the status of a local VLC player.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/vlc-rc")
public class VlcProcessController extends AbstractSystemCommandController {

  /**
   * Starts a vlc player in the local server.
   */
  @PostMapping(path = "/vlc-process")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>>
      startVlcPlayer(@RequestParam(value = "file", required = false) String file,
                     HttpServletRequest request) {
    logTraceRequest(request);
    return execKameHouseSystemCommand(new VlcStartKameHouseSystemCommand(file));
  }

  /**
   * Stops vlc player in the local server.
   */
  @DeleteMapping(path = "/vlc-process")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> stopVlcPlayer(HttpServletRequest request) {
    logTraceRequest(request);
    return execKameHouseSystemCommand(new VlcStopKameHouseSystemCommand());
  }

  /**
   * Gets the status of vlc player in the local server.
   */
  @GetMapping(path = "/vlc-process")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> statusVlcPlayer(HttpServletRequest request) {
    logTraceRequest(request);
    return execKameHouseSystemCommand(new VlcStatusKameHouseSystemCommand());
  }
}
