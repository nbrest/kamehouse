package com.nicobrest.kamehouse.vlcrc.dao;

import com.nicobrest.kamehouse.main.dao.AbstractDaoJpa;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * DAO layer to manage registered VLC Players in the application using JPA.
 * 
 * @author nbrest
 *
 */
public class VlcPlayerDaoJpa extends AbstractDaoJpa implements VlcPlayerDao {

  private static final String GET_VLC_PLAYER_CACHE = "getVlcPlayer";
  
  @Override
  public List<VlcPlayer> getAllVlcPlayers() {
    logger.trace("Get all VlcPlayers");
    return findAll(VlcPlayer.class);
  }
  
  @Override
  @Cacheable(value = GET_VLC_PLAYER_CACHE)
  public VlcPlayer getVlcPlayer(String vlcPlayerName) {
    logger.trace("Get VlcPlayer: {}", vlcPlayerName);
    return findByAttribute(VlcPlayer.class, "hostname", vlcPlayerName);
  }
  
  @Override
  @CacheEvict(value = { GET_VLC_PLAYER_CACHE }, allEntries = true)
  public Long createVlcPlayer(VlcPlayer vlcPlayer) {
    logger.trace("Creating VlcPlayer: {}", vlcPlayer);
    persistEntityInRepository(vlcPlayer);
    return vlcPlayer.getId();
  }

  @Override
  @CacheEvict(value = { GET_VLC_PLAYER_CACHE }, allEntries = true)
  public void updateVlcPlayer(VlcPlayer vlcPlayer) {
    logger.trace("Updating VlcPlayer: {}", vlcPlayer);
    updateEntityInRepository(VlcPlayer.class, vlcPlayer, vlcPlayer.getId());
  } 

  @Override
  @CacheEvict(value = { GET_VLC_PLAYER_CACHE }, allEntries = true)
  public VlcPlayer deleteVlcPlayer(Long vlcPlayerId) {
    logger.trace("Deleting VlcPlayer: {}", vlcPlayerId);
    return deleteEntityFromRepository(VlcPlayer.class, vlcPlayerId);
  }

  @Override
  protected <T> void updateEntityValues(T persistedEntity, T entity) {
    VlcPlayer persistedVlcPlayer = (VlcPlayer) persistedEntity;
    VlcPlayer vlcPlayer = (VlcPlayer) entity;
    persistedVlcPlayer.setHostname(vlcPlayer.getHostname());
    persistedVlcPlayer.setPort(vlcPlayer.getPort());
    persistedVlcPlayer.setUsername(vlcPlayer.getUsername());
    persistedVlcPlayer.setPassword(vlcPlayer.getPassword());
  }
}
