package com.nicobrest.kamehouse.vlcrc.service;

import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
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
  private VlcPlayer vlcPlayer;
  
  public void setVlcPlayer(VlcPlayer vlcPlayer) {
    this.vlcPlayer = vlcPlayer;
  }
  
  public VlcPlayer getVlcPlayer() {
    return vlcPlayer;
  }
  
  /**
   * Gets the status information of the specified VLC Player.
   */
  public VlcRcStatus getVlcRcStatus(String vlcPlayerName) {
    VlcRcStatus vlcRcStatus = vlcPlayer.getVlcRcStatus();
    return vlcRcStatus;
  }
  
  /**
   * Executes a command in the specified VLC Player.
   */
  public VlcRcStatus execute(VlcRcCommand vlcRcCommand, String vlcPlayerName) {
    //TODO: Add a vlcPlayer DAO and search by vlcPlayerName (hostname)
    VlcRcStatus vlcRcStatus = vlcPlayer.execute(vlcRcCommand);
    return vlcRcStatus;
  }
  
  /**
   * Gets the current playlist for the selected VLC Player.
   */
  public List<Map<String,Object>> getPlaylist(String vlcPlayerName) {
    List<Map<String,Object>> vlcPlaylist = vlcPlayer.getPlaylist();
    return vlcPlaylist;
  }
}
