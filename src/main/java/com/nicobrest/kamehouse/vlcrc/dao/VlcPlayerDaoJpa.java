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
  public List<VlcPlayer> getAll() {
    logger.trace("Get all VlcPlayers");
    return findAll(VlcPlayer.class);
  }
  
  @Override
  @Cacheable(value = GET_VLC_PLAYER_CACHE)
  public VlcPlayer getByHostname(String hostname) {
    logger.trace("Get VlcPlayer: {}", hostname);
    return findByAttribute(VlcPlayer.class, "hostname", hostname);
  }
  
  @Override
  @CacheEvict(value = { GET_VLC_PLAYER_CACHE }, allEntries = true)
  public Long create(VlcPlayer entity) {
    logger.trace("Creating VlcPlayer: {}", entity);
    persistEntityInRepository(entity);
    return entity.getId();
  }

  @Override
  @CacheEvict(value = { GET_VLC_PLAYER_CACHE }, allEntries = true)
  public void update(VlcPlayer entity) {
    logger.trace("Updating VlcPlayer: {}", entity);
    updateEntityInRepository(VlcPlayer.class, entity, entity.getId());
  } 

  @Override
  @CacheEvict(value = { GET_VLC_PLAYER_CACHE }, allEntries = true)
  public VlcPlayer delete(Long id) {
    logger.trace("Deleting VlcPlayer: {}", id);
    return deleteEntityFromRepository(VlcPlayer.class, id);
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
