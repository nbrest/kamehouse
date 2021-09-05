package com.nicobrest.kamehouse.vlcrc.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

/**
 * DAO layer to manage registered VLC Players in the application using JPA.
 *
 * @author nbrest
 */
@Repository
public class VlcPlayerDaoJpa extends AbstractCrudDaoJpa<VlcPlayer> implements VlcPlayerDao {

  private static final String VLC_PLAYER_CACHE = "vlcPlayer";

  @Override
  public Class<VlcPlayer> getEntityClass() {
    return VlcPlayer.class;
  }

  @Override
  @CacheEvict(
      value = {VLC_PLAYER_CACHE},
      allEntries = true)
  public Long create(VlcPlayer entity) {
    return super.create(entity);
  }

  @Override
  @CacheEvict(
      value = {VLC_PLAYER_CACHE},
      allEntries = true)
  public void update(VlcPlayer entity) {
    super.update(entity);
  }

  @Override
  @CacheEvict(
      value = {VLC_PLAYER_CACHE},
      allEntries = true)
  public VlcPlayer delete(Long id) {
    return super.delete(id);
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

  @Override
  @Cacheable(value = VLC_PLAYER_CACHE)
  public VlcPlayer getByHostname(String hostname) {
    logger.trace("Get VlcPlayer {}", hostname);
    VlcPlayer response = findByAttribute(VlcPlayer.class, "hostname", hostname);
    logger.trace("Get VlcPlayer {} response {}", hostname, response);
    return response;
  }
}
