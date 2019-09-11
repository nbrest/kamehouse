package com.nicobrest.kamehouse.vlcrc.service;

import com.nicobrest.kamehouse.vlcrc.dao.VlcPlayerDao;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.service.dto.VlcPlayerDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer to manage the registered VLC Players in the application.
 * 
 * @author nbrest
 *
 */
@Service
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
    return vlcPlayerDao.getVlcPlayer(vlcPlayerName);
  }
  
  /**
   * Create a VLC Player.
   */
  public Long createVlcPlayer(VlcPlayerDto vlcPlayerDto) {
    VlcPlayer vlcPlayer = getModel(vlcPlayerDto); 
    return vlcPlayerDao.createVlcPlayer(vlcPlayer);
  } 
  
  /**
   * Update a VLC Player.
   */
  public void updateVlcPlayer(VlcPlayerDto vlcPlayerDto) {
    VlcPlayer vlcPlayer = getModel(vlcPlayerDto);
    vlcPlayerDao.updateVlcPlayer(vlcPlayer);
  } 
  
  /**
   * Delete a VLC Player.
   */
  public VlcPlayer deleteVlcPlayer(Long vlcPlayerId) {
    return vlcPlayerDao.deleteVlcPlayer(vlcPlayerId);
  }
  
  /**
   * Get all VLC Players.
   */
  public List<VlcPlayer> getAllVlcPlayers() {
    return vlcPlayerDao.getAllVlcPlayers();
  }
  
  /**
   * Get VlcPlayer model object from it's DTO.
   */
  private VlcPlayer getModel(VlcPlayerDto vlcPlayerDto) {
    VlcPlayer vlcPlayer = new VlcPlayer();
    vlcPlayer.setHostname(vlcPlayerDto.getHostname());
    vlcPlayer.setId(vlcPlayerDto.getId());
    vlcPlayer.setPassword(vlcPlayerDto.getPassword());
    vlcPlayer.setPort(vlcPlayerDto.getPort());
    vlcPlayer.setUsername(vlcPlayerDto.getUsername());
    return vlcPlayer;
  }
}
