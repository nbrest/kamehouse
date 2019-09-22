package com.nicobrest.kamehouse.vlcrc.service;

import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service layer to interact with the registered VLC Players in the application.
 * 
 * @author nbrest
 *
 */
@Service
public class VlcRcService {

  @Autowired
  private VlcPlayerService vlcPlayerService;

  public void setVlcPlayerService(VlcPlayerService vlcPlayerService) {
    this.vlcPlayerService = vlcPlayerService;
  }

  public VlcPlayerService getVlcPlayerService() {
    return vlcPlayerService;
  }

  /**
   * Gets the status information of the specified VLC Player.
   */
  public VlcRcStatus getVlcRcStatus(String hostname) { 
    return vlcPlayerService.getByHostname(hostname).getVlcRcStatus();
  }

  /**
   * Executes a command in the specified VLC Player.
   */
  public VlcRcStatus execute(VlcRcCommand vlcRcCommand, String hostname) { 
    return vlcPlayerService.getByHostname(hostname).execute(vlcRcCommand);
  }

  /**
   * Gets the current playlist for the selected VLC Player.
   */
  public List<Map<String, Object>> getPlaylist(String hostname) {
    return vlcPlayerService.getByHostname(hostname).getPlaylist();
  }

  /**
   * Browse the file system of the selected VLC Player.
   */
  public List<Map<String, Object>> browse(String uri, String hostname) {
    return vlcPlayerService.getByHostname(hostname).browse(uri);
  }
}
