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
  public VlcPlayer getByHostname(String hostname) { 
    return vlcPlayerDao.getByHostname(hostname);
  }
  
  /**
   * Create a VLC Player.
   */
  public Long create(VlcPlayerDto dto) {
    VlcPlayer vlcPlayer = getModel(dto); 
    return vlcPlayerDao.create(vlcPlayer);
  } 
  
  /**
   * Update a VLC Player.
   */
  public void update(VlcPlayerDto dto) {
    VlcPlayer vlcPlayer = getModel(dto);
    vlcPlayerDao.update(vlcPlayer);
  } 
  
  /**
   * Delete a VLC Player.
   */
  public VlcPlayer delete(Long id) {
    return vlcPlayerDao.delete(id);
  }
  
  /**
   * Get all VLC Players.
   */
  public List<VlcPlayer> getAll() {
    return vlcPlayerDao.getAll();
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
