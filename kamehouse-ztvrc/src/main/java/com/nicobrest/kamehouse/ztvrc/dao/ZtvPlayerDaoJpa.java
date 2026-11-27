package com.nicobrest.kamehouse.ztvrc.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.ztvrc.model.VlcPlayer;
import com.nicobrest.kamehouse.ztvrc.model.ZtvPlayer;
import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

/**
 * DAO layer to manage registered ZTV Players in the application using JPA.
 *
 * @author nbrest
 */
@Repository
public class ZtvPlayerDaoJpa implements ZtvPlayerDao {

  private static final String ZTV_PLAYER_CACHE = "ztvPlayer";

  private final ZtvPlayerEntityCrudDao ztvPlayerCrudDao;

  public ZtvPlayerDaoJpa(EntityManagerFactory entityManagerFactory) {
    ztvPlayerCrudDao = new ZtvPlayerEntityCrudDao(entityManagerFactory);
  }

  @Override
  @CacheEvict(
      value = {ZTV_PLAYER_CACHE},
      allEntries = true)
  public Long create(ZtvPlayer entity) {
    return ztvPlayerCrudDao.create(toEntity(entity));
  }

  @Override
  public ZtvPlayer read(Long id) {
    return ztvPlayerCrudDao.read(id);
  }

  @Override
  public List<ZtvPlayer> readAll() {
    return new ArrayList<>(ztvPlayerCrudDao.readAll());
  }

  @Override
  public List<ZtvPlayer> readAll(Integer maxRows, String sortColumn, Boolean sortAscending) {
    return new ArrayList<>(ztvPlayerCrudDao.readAll(maxRows, sortColumn, sortAscending));
  }

  @Override
  @CacheEvict(
      value = {ZTV_PLAYER_CACHE},
      allEntries = true)
  public void update(ZtvPlayer entity) {
    ztvPlayerCrudDao.update(toEntity(entity));
  }

  @Override
  @CacheEvict(
      value = {ZTV_PLAYER_CACHE},
      allEntries = true)
  public ZtvPlayer delete(Long id) {
    return ztvPlayerCrudDao.delete(id);
  }

  @Override
  @Cacheable(value = ZTV_PLAYER_CACHE)
  public ZtvPlayer getByHostname(String hostname) {
    return ztvPlayerCrudDao.getByHostname(hostname);
  }

  private VlcPlayer toEntity(ZtvPlayer player) {
    if (player instanceof VlcPlayer vlcPlayer) {
      return vlcPlayer;
    }
    VlcPlayer vlcPlayer = new VlcPlayer();
    vlcPlayer.setId(player.getId());
    vlcPlayer.setHostname(player.getHostname());
    vlcPlayer.setPort(player.getPort());
    vlcPlayer.setUsername(player.getUsername());
    vlcPlayer.setPassword(player.getPassword());
    return vlcPlayer;
  }

  private static class ZtvPlayerEntityCrudDao extends AbstractCrudDaoJpa<VlcPlayer> {

    private ZtvPlayerEntityCrudDao(EntityManagerFactory entityManagerFactory) {
      super(entityManagerFactory);
    }

    @Override
    public Class<VlcPlayer> getEntityClass() {
      return VlcPlayer.class;
    }

    @Override
    protected void updateEntityValues(VlcPlayer persistedEntity, VlcPlayer entity) {
      persistedEntity.setHostname(entity.getHostname());
      persistedEntity.setPort(entity.getPort());
      persistedEntity.setUsername(entity.getUsername());
      persistedEntity.setPassword(entity.getPassword());
    }

    private VlcPlayer getByHostname(String hostname) {
      STATIC_LOGGER.trace("Get ZtvPlayer {}", hostname);
      VlcPlayer response = findByAttribute(VlcPlayer.class, "hostname", hostname);
      STATIC_LOGGER.trace("Get ZtvPlayer {} response {}", hostname, response);
      return response;
    }
  }
}
