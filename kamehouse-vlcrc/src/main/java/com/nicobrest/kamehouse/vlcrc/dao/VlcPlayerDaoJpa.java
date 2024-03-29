package com.nicobrest.kamehouse.vlcrc.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import jakarta.persistence.EntityManagerFactory;
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

  public VlcPlayerDaoJpa(EntityManagerFactory entityManagerFactory) {
    super(entityManagerFactory);
  }

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
  protected void updateEntityValues(VlcPlayer persistedEntity, VlcPlayer entity) {
    persistedEntity.setHostname(entity.getHostname());
    persistedEntity.setPort(entity.getPort());
    persistedEntity.setUsername(entity.getUsername());
    persistedEntity.setPassword(entity.getPassword());
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
