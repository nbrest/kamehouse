package com.nicobrest.kamehouse.vlcrc.dao;

import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;

import java.util.List;

/**
 * DAO layer to manage registered VLC Players in the application.
 * 
 * @author nbrest
 *
 */
public interface VlcPlayerDao {

  /**
   * Creates a VLC player.
   */
  public Long createVlcPlayer(VlcPlayer vlcPlayer);
  
  /**
   * Updates a VLC Player.
   */
  public void updateVlcPlayer(VlcPlayer vlcPlayer);
  
  /**
   * Gets a VLC Player.
   */
  public VlcPlayer getVlcPlayer(String vlcPlayerName);
  
  /**
   * Deletes a VLC Player.
   */
  public VlcPlayer deleteVlcPlayer(Long vlcPlayerId);
  
  /**
   * Gets all VLC Players registered.
   */
  public List<VlcPlayer> getAllVlcPlayers();
}
