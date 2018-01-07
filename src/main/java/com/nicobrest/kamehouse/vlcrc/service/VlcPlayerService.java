package com.nicobrest.kamehouse.vlcrc.service;

import com.nicobrest.kamehouse.vlcrc.dao.VlcPlayerDao;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Service layer to manage the registered VLC Players in the application.
 * 
 * @author nbrest
 *
 */
public class VlcPlayerService {

  @Autowired
  @Qualifier("vlcPlayerDaoJpa")
  private VlcPlayerDao vlcPlayerDao;
  
  public VlcPlayerDao getVlcPlayerDao() {
    return vlcPlayerDao;
  }

  public void setVlcPlayerDao(VlcPlayerDao vlcPlayerDao) {
    this.vlcPlayerDao = vlcPlayerDao;
  }

  /**
   * Get a VLC Player.
   */
  public VlcPlayer getVlcPlayer(String vlcPlayerName) {
    VlcPlayer vlcPlayer = vlcPlayerDao.getVlcPlayer(vlcPlayerName);
    return vlcPlayer;
  }
  
  /**
   * Create a VLC Player.
   */
  public Long createVlcPlayer(VlcPlayer vlcPlayer) {
    Long vlcPlayerId = vlcPlayerDao.createVlcPlayer(vlcPlayer);
    return vlcPlayerId;
  } 
  
  /**
   * Update a VLC Player.
   */
  public void updateVlcPlayer(VlcPlayer vlcPlayer) {
    vlcPlayerDao.updateVlcPlayer(vlcPlayer);
  } 
  
  /**
   * Delete a VLC Player.
   */
  public VlcPlayer deleteVlcPlayer(Long vlcPlayerId) {
    VlcPlayer deletedVlcPlayer = vlcPlayerDao.deleteVlcPlayer(vlcPlayerId);
    return deletedVlcPlayer;
  }
  
  /**
   * Get all VLC Players.
   */
  public List<VlcPlayer> getAllVlcPlayers() {
    List<VlcPlayer> vlcPlayers = vlcPlayerDao.getAllVlcPlayers();
    return vlcPlayers;
  }
}
