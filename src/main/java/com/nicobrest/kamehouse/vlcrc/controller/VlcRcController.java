package com.nicobrest.kamehouse.vlcrc.controller;

import com.nicobrest.kamehouse.main.controller.AbstractCrudController;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import com.nicobrest.kamehouse.vlcrc.service.VlcPlayerService;
import com.nicobrest.kamehouse.vlcrc.service.VlcRcService;
import com.nicobrest.kamehouse.vlcrc.service.dto.VlcPlayerDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Controller to manage the VLC Players registered in the application.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/vlc-rc")
public class VlcRcController extends AbstractCrudController {

  private static final String VLC_PLAYERS = "/vlc-rc/players";
  private static final String VLC_PLAYERS_ID = "/vlc-rc/players/{id}";
  
  @Autowired
  private VlcRcService vlcRcService;

  @Autowired
  private VlcPlayerService vlcPlayerService;

  /**
   * Creates a VLC Player.
   */
  @PostMapping(path = "/players")
  @ResponseBody
  public ResponseEntity<Long> create(@RequestBody VlcPlayerDto dto) {
    return create(VLC_PLAYERS, vlcPlayerService, dto);
  }
  
  /**
   * Reads a VLC Player by it's id.
   */
  @GetMapping(path = "/players/{id}")
  @ResponseBody
  public ResponseEntity<VlcPlayer> read(@PathVariable Long id) {
    return read(VLC_PLAYERS_ID, vlcPlayerService, id);
  }

  /**
   * Reads all VLC Players registered in the application.
   */
  @GetMapping(path = "/players")
  @ResponseBody
  public ResponseEntity<List<VlcPlayer>> readAll() {
    return readAll(VLC_PLAYERS, vlcPlayerService);
  }

  /**
   * Updates the VLC Player passed as a URL parameter.
   */
  @PutMapping(path = "/players/{id}")
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody VlcPlayerDto dto) {
    return update(VLC_PLAYERS_ID, vlcPlayerService, id, dto);
  }

  /**
   * Deletes the VLC Player passed as a URL parameter.
   */
  @DeleteMapping(path = "/players/{id}")
  @ResponseBody
  public ResponseEntity<VlcPlayer> delete(@PathVariable Long id) {
    return delete(VLC_PLAYERS_ID, vlcPlayerService, id);
  }

  /**
   * Gets the VLC Player passed as a URL parameter.
   */
  @GetMapping(path = "/players/hostname/{hostname}")
  @ResponseBody
  public ResponseEntity<VlcPlayer> getByHostname(@PathVariable String hostname) {
    logger.trace("/vlc-rc/players/hostname/{hostname} (GET)");
    VlcPlayer vlcPlayer = vlcPlayerService.getByHostname(hostname);
    return generateGetResponseEntity(vlcPlayer);
  }
  
  /**
   * Gets the status information of the VLC Player passed through the URL.
   */
  @GetMapping(path = "/players/{hostname}/status")
  @ResponseBody
  public ResponseEntity<VlcRcStatus> getVlcRcStatus(@PathVariable String hostname) {
    logger.trace("/vlc-rc/players/{hostname}/status (GET)");
    VlcRcStatus vlcRcStatus = vlcRcService.getVlcRcStatus(hostname);
    return generateGetResponseEntity(vlcRcStatus);
  }

  /**
   * Executes a command in the selected VLC Player.
   */
  @PostMapping(path = "/players/{hostname}/commands")
  @ResponseBody
  public ResponseEntity<VlcRcStatus> executeCommand(@RequestBody VlcRcCommand vlcRcCommand,
      @PathVariable String hostname) {
    logger.trace("/vlc-rc/players/{hostname}/commands (POST)");
    VlcRcStatus vlcRcStatus = vlcRcService.execute(vlcRcCommand, hostname);
    return generatePostResponseEntity(vlcRcStatus);
  }

  /**
   * Gets the current playlist from the selected VLC Player.
   */
  @GetMapping(path = "/players/{hostname}/playlist")
  @ResponseBody
  public ResponseEntity<List<Map<String, Object>>> getPlaylist(@PathVariable String hostname) {
    logger.trace("/vlc-rc/players/{hostname}/playlist (GET)");
    List<Map<String, Object>> vlcPlaylist = vlcRcService.getPlaylist(hostname);
    return generateGetResponseEntity(vlcPlaylist);
  }

  /**
   * Browse the VLC Player server's file system.
   */
  @GetMapping(path = "/players/{hostname}/browse")
  @ResponseBody
  public ResponseEntity<List<Map<String, Object>>> browse(@RequestParam(value = "uri",
      required = false) String uri, @PathVariable String hostname) {
    logger.trace("/vlc-rc/players/{hostname}/browse (GET)");
    List<Map<String, Object>> vlcRcFileList = vlcRcService.browse(uri, hostname);
    return generateGetResponseEntity(vlcRcFileList);
  }
}
