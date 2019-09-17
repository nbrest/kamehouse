package com.nicobrest.kamehouse.vlcrc.controller;

import com.nicobrest.kamehouse.main.controller.AbstractController;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import com.nicobrest.kamehouse.vlcrc.service.VlcPlayerService;
import com.nicobrest.kamehouse.vlcrc.service.VlcRcService;
import com.nicobrest.kamehouse.vlcrc.service.dto.VlcPlayerDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
public class VlcRcController extends AbstractController {

  @Autowired
  private VlcRcService vlcRcService;

  @Autowired
  private VlcPlayerService vlcPlayerService;

  /**
   * Creates a VLC Player.
   */
  @PostMapping(path = "/players")
  @ResponseBody
  public ResponseEntity<Long> createVlcPlayer(@RequestBody VlcPlayerDto vlcPlayerDto) {
    logger.trace("In controller /vlc-rc/players (POST)");
    Long vlcPlayerId = vlcPlayerService.createVlcPlayer(vlcPlayerDto);
    return new ResponseEntity<>(vlcPlayerId, HttpStatus.CREATED);
  }

  /**
   * Gets all VLC Player registered in the application.
   */
  @GetMapping(path = "/players")
  @ResponseBody
  public ResponseEntity<List<VlcPlayer>> getAllVlcPlayers() {
    logger.trace("In controller /vlc-rc/players/ (GET)");
    List<VlcPlayer> vlcPlayers = vlcPlayerService.getAllVlcPlayers();
    return generateGetResponseEntity(vlcPlayers);
  }

  /**
   * Gets the VLC Player passed as a URL parameter.
   */
  @GetMapping(path = "/players/{vlcPlayerName}")
  @ResponseBody
  public ResponseEntity<VlcPlayer> getVlcPlayer(@PathVariable String vlcPlayerName) {
    logger.trace("In controller /vlc-rc/players/{vlcPlayerName} (GET)");
    VlcPlayer vlcPlayer = vlcPlayerService.getVlcPlayer(vlcPlayerName);
    return generateGetResponseEntity(vlcPlayer);
  }

  /**
   * Updates the VLC Player passed as a URL parameter.
   */
  @PutMapping(path = "/players/{vlcPlayerId}")
  public ResponseEntity<Void> updateVlcPlayer(@PathVariable Long vlcPlayerId,
      @RequestBody VlcPlayerDto vlcPlayerDto) {
    logger.trace("In controller /vlc-rc/players/{vlcPlayerId} (PUT)");
    validatePathAndRequestBodyIds(vlcPlayerId, vlcPlayerDto.getId());
    vlcPlayerService.updateVlcPlayer(vlcPlayerDto);
    return generatePutResponseEntity();
  }

  /**
   * Deletes the VLC Player passed as a URL parameter.
   */
  @DeleteMapping(path = "/players/{vlcPlayerId}")
  @ResponseBody
  public ResponseEntity<VlcPlayer> deleteVlcPlayer(@PathVariable Long vlcPlayerId) {
    logger.trace("In controller /vlc-rc/players/{vlcPlayerId} (DELETE)");
    VlcPlayer vlcPlayer = vlcPlayerService.deleteVlcPlayer(vlcPlayerId);
    return generateDeleteResponseEntity(vlcPlayer);
  }

  /**
   * Gets the status information of the VLC Player passed through the URL.
   */
  @GetMapping(path = "/players/{vlcPlayerName}/status")
  @ResponseBody
  public ResponseEntity<VlcRcStatus> getVlcRcStatus(@PathVariable String vlcPlayerName) {
    logger.trace("In controller /vlc-rc/players/{vlcPlayerName}/status (GET)");
    VlcRcStatus vlcRcStatus = vlcRcService.getVlcRcStatus(vlcPlayerName);
    return generateGetResponseEntity(vlcRcStatus);
  }

  /**
   * Executes a command in the selected VLC Player.
   */
  @PostMapping(path = "/players/{vlcPlayerName}/commands")
  @ResponseBody
  public ResponseEntity<VlcRcStatus> executeCommand(@RequestBody VlcRcCommand vlcRcCommand,
      @PathVariable String vlcPlayerName) {

    logger.trace("In controller /vlc-rc/players/{vlcPlayerName}/commands (POST)");
    VlcRcStatus vlcRcStatus = vlcRcService.execute(vlcRcCommand, vlcPlayerName);
    return generatePostResponseEntity(vlcRcStatus);
  }

  /**
   * Gets the current playlist from the selected VLC Player.
   */
  @GetMapping(path = "/players/{vlcPlayerName}/playlist")
  @ResponseBody
  public ResponseEntity<List<Map<String, Object>>> getPlaylist(@PathVariable String vlcPlayerName) {
    logger.trace("In controller /vlc-rc/players/{vlcPlayerName}/playlist (GET)");
    List<Map<String, Object>> vlcPlaylist = vlcRcService.getPlaylist(vlcPlayerName);
    return generateGetResponseEntity(vlcPlaylist);
  }

  /**
   * Browse the VLC Player server's file system.
   */
  @GetMapping(path = "/players/{vlcPlayerName}/browse")
  @ResponseBody
  public ResponseEntity<List<Map<String, Object>>> browse(
      @RequestParam(value = "uri", required = false) String uri,
      @PathVariable String vlcPlayerName) {
    logger.trace("In controller /vlc-rc/players/{vlcPlayerName}/browse (GET)");
    List<Map<String, Object>> vlcRcFileList = vlcRcService.browse(uri, vlcPlayerName);
    return generateGetResponseEntity(vlcRcFileList);
  }
}
