package com.nicobrest.kamehouse.ztvrc.controller;

import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcPlaylistItem;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcStatus;
import com.nicobrest.kamehouse.ztvrc.service.ZtvRcService;
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
 * Controller for the Ztv Player WebSockets.
 *
 * @author nbrest
 */
@Controller
public class ZtvRcWebSocketController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private static final String LOCALHOST = "localhost";

  private final SimpMessagingTemplate messagingTemplate;
  private final ZtvRcService ztvRcService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public ZtvRcWebSocketController(ZtvRcService ztvRcService,
      SimpMessagingTemplate messagingTemplate) {
    this.ztvRcService = ztvRcService;
    this.messagingTemplate = messagingTemplate;
  }

  /**
   * Processes the websocket input request for ztv player status.
   */
  @MessageMapping("/ztv-player/status-in")
  @SendTo("/topic/ztv-player/status-out")
  public ZtvRcStatus getZtvRcStatus() {
    logger.trace("/ztv-player/status-in (WEBSOCKET)");
    ZtvRcStatus ztvRcStatus = null;
    try {
      ztvRcStatus = ztvRcService.getZtvRcStatus(LOCALHOST);
    } catch (KameHouseNotFoundException e) {
      logger.warn(e.getMessage());
    }
    if (ztvRcStatus == null) {
      // Return an empty object instead of null so the client receives a response and
      // updates the status view. Null doesn't even send a response to the channel.
      ztvRcStatus = new ZtvRcStatus();
    }
    return ztvRcStatus;
  }

  /**
   * Processes the websocket input request for ZTV player playlist.
   */
  @MessageMapping("/ztv-player/playlist-in")
  @SendTo("/topic/ztv-player/playlist-out")
  public List<ZtvRcPlaylistItem> getPlaylist() {
    logger.trace("/ztv-player/playlist-in (WEBSOCKET)");
    List<ZtvRcPlaylistItem> ztvPlaylist = null;
    try {
      ztvPlaylist = ztvRcService.getPlaylist(LOCALHOST);
    } catch (KameHouseNotFoundException e) {
      logger.warn(e.getMessage());
    }
    if (ztvPlaylist == null) {
      // Return an empty object instead of null so the client receives a response and
      // updates the status view. Null doesn't even send a response to the channel.
      ztvPlaylist = new ArrayList<>();
    }
    return ztvPlaylist;
  }

  /**
   * Push ZTV status periodically to topic to be consumed by websocket clients.
   */
  @Scheduled(fixedRate = 1000)
  public void pushPeriodicZtvStatus() {
    logger.trace("Pushing scheduled ZTV status to topic");
    ZtvRcStatus ztvRcStatus = ztvRcService.getZtvRcStatus(LOCALHOST);
    messagingTemplate.convertAndSend("/topic/ztv-player/status-out", ztvRcStatus);
  }

  /**
   * Push ZTV playlist periodically to topic to be consumed by websocket clients.
   */
  @Scheduled(fixedRate = 7000)
  public void pushPeriodicZtvPlaylist() {
    logger.trace("Pushing scheduled ZTV playlist to topic");
    List<ZtvRcPlaylistItem> playlist = ztvRcService.getPlaylist(LOCALHOST);
    messagingTemplate.convertAndSend("/topic/ztv-player/playlist-out", playlist);
  }
}
