package com.nicobrest.kamehouse.vlcrc.controller;

import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import com.nicobrest.kamehouse.vlcrc.service.VlcRcService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Controller for the Vlc Player WebSockets.
 * 
 * @author nbrest
 *
 */
@Controller
public class VlcRcWebSocketController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private VlcRcService vlcRcService;

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
}