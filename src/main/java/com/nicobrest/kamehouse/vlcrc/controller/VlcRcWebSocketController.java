package com.nicobrest.kamehouse.vlcrc.controller;
 
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import com.nicobrest.kamehouse.vlcrc.service.VlcRcService;

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
 
  @Autowired
  private VlcRcService vlcRcService;
  
  /**
   * Process the websocket input request for vlc player status.
   */
  @MessageMapping("/vlc-player/status-in")
  @SendTo("/topic/vlc-player/status-out")
  public VlcRcStatus getVlcRcStatus() throws Exception {
 
    VlcRcStatus vlcRcStatus = vlcRcService.getVlcRcStatus("localhost");
    return vlcRcStatus;
  }
}