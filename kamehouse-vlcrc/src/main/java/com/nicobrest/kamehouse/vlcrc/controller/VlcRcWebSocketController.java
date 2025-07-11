package com.nicobrest.kamehouse.vlcrc.controller;

import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcPlaylistItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import com.nicobrest.kamehouse.vlcrc.service.VlcRcService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

/**
 * Controller for the Vlc Player WebSockets.
 *
 * @author nbrest
 */
@Controller
public class VlcRcWebSocketController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private SimpMessagingTemplate messagingTemplate;
  private VlcRcService vlcRcService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public VlcRcWebSocketController(VlcRcService vlcRcService,
      SimpMessagingTemplate messagingTemplate) {
    this.vlcRcService = vlcRcService;
    this.messagingTemplate = messagingTemplate;
  }

  /**
   * Processes the websocket input request for vlc player status.
   */
  @MessageMapping("/vlc-player/status-in")
  @SendTo("/topic/vlc-player/status-out")
  public VlcRcStatus getVlcRcStatus() {
    logger.trace("/vlc-player/status-in (WEBSOCKET)");
    VlcRcStatus vlcRcStatus = null;
    try {
      vlcRcStatus = vlcRcService.getVlcRcStatus("localhost");
    } catch (KameHouseNotFoundException e) {
      logger.warn(e.getMessage());
    }
    if (vlcRcStatus == null) {
      // Return an empty object instead of null so the client receives a response and
      // updates the status view. Null doesn't even send a response to the channel.
      vlcRcStatus = new VlcRcStatus();
    }
    return vlcRcStatus;
  }

  /**
   * Processes the websocket input request for vlc player playlist.
   */
  @MessageMapping("/vlc-player/playlist-in")
  @SendTo("/topic/vlc-player/playlist-out")
  public List<VlcRcPlaylistItem> getPlaylist() {
    logger.trace("/vlc-player/playlist-in (WEBSOCKET)");
    List<VlcRcPlaylistItem> vlcPlaylist = null;
    try {
      vlcPlaylist = vlcRcService.getPlaylist("localhost");
    } catch (KameHouseNotFoundException e) {
      logger.warn(e.getMessage());
    }
    if (vlcPlaylist == null) {
      // Return an empty object instead of null so the client receives a response and
      // updates the status view. Null doesn't even send a response to the channel.
      vlcPlaylist = new ArrayList<>();
    }
    return vlcPlaylist;
  }

  /**
   * Push vlc status periodically to topic to be consumed by websocket clients.
   */
  @Scheduled(fixedRate = 1000)
  public void pushPeriodicVlcStatus() {
    logger.trace("Pushing scheduled vlc status to topic");
    VlcRcStatus vlcRcStatus = vlcRcService.getVlcRcStatus("localhost");
    messagingTemplate.convertAndSend("/topic/vlc-player/status-out", vlcRcStatus);
  }

  /**
   * Push vlc playlist periodically to topic to be consumed by websocket clients.
   */
  @Scheduled(fixedRate = 7000)
  public void pushPeriodicVlcPlaylist() {
    logger.trace("Pushing scheduled vlc playlist to topic");
    List<VlcRcPlaylistItem> playlist = vlcRcService.getPlaylist("localhost");
    messagingTemplate.convertAndSend("/topic/vlc-player/playlist-out", playlist);
  }
}
