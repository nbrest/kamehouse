package com.nicobrest.kamehouse.vlcrc.dao;

import com.nicobrest.kamehouse.main.dao.AbstractCrudDaoJpa;
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
public class VlcPlayerDaoJpa extends AbstractCrudDaoJpa implements VlcPlayerDao {

  private static final String VLC_PLAYER_CACHE = "vlcPlayer";
  
  @Override
  @CacheEvict(value = { VLC_PLAYER_CACHE }, allEntries = true)
  public Long create(VlcPlayer entity) {
    return create(VlcPlayer.class, entity);
  }
  
  @Override
  public VlcPlayer read(Long id) {
    return read(VlcPlayer.class, id);
  }
  
  @Override
  public List<VlcPlayer> readAll() {
    return readAll(VlcPlayer.class);
  }

  @Override
  @CacheEvict(value = { VLC_PLAYER_CACHE }, allEntries = true)
  public void update(VlcPlayer entity) {
    update(VlcPlayer.class, entity);
  } 

  @Override
  @CacheEvict(value = { VLC_PLAYER_CACHE }, allEntries = true)
  public VlcPlayer delete(Long id) {
    return delete(VlcPlayer.class, id);
  }
  
  @Override
  @Cacheable(value = VLC_PLAYER_CACHE)
  public VlcPlayer getByHostname(String hostname) {
    logger.trace("Get VlcPlayer {}", hostname);
    VlcPlayer response = findByAttribute(VlcPlayer.class, "hostname", hostname);
    logger.trace("Get VlcPlayer {} response {}", hostname, response);
    return response;
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
