package com.nicobrest.kamehouse.vlcrc.service;

import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Service layer to interact with the registered VLC Players in the application.
 * 
 * @author nbrest
 *
 */
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
  public VlcRcStatus getVlcRcStatus(String vlcPlayerName) {
    VlcRcStatus vlcRcStatus = vlcPlayerService.getVlcPlayer(vlcPlayerName).getVlcRcStatus();
    return vlcRcStatus;
  }

  /**
   * Executes a command in the specified VLC Player.
   */
  public VlcRcStatus execute(VlcRcCommand vlcRcCommand, String vlcPlayerName) { 
    VlcRcStatus vlcRcStatus = vlcPlayerService.getVlcPlayer(vlcPlayerName).execute(vlcRcCommand);
    return vlcRcStatus;
  }

  /**
   * Gets the current playlist for the selected VLC Player.
   */
  public List<Map<String, Object>> getPlaylist(String vlcPlayerName) {
    List<Map<String, Object>> vlcPlaylist = vlcPlayerService.getVlcPlayer(vlcPlayerName)
        .getPlaylist();
    return vlcPlaylist;
  }

  /**
   * Browse the file system of the selected VLC Player.
   */
  public List<Map<String, Object>> browse(String uri, String vlcPlayerName) {
    List<Map<String, Object>> vlcRcFileList = vlcPlayerService.getVlcPlayer(vlcPlayerName).browse(
        uri);
    return vlcRcFileList;
  }
}
