package com.nicobrest.kamehouse.vlcrc.dao;

import com.nicobrest.kamehouse.main.dao.AbstractDaoJpa;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 * DAO layer to manage registered VLC Players in the application using JPA.
 * 
 * @author nbrest
 *
 */
public class VlcPlayerDaoJpa extends AbstractDaoJpa implements VlcPlayerDao {

  @Override
  @CacheEvict(value = { "getVlcPlayer" }, allEntries = true)
  public Long createVlcPlayer(VlcPlayer vlcPlayer) {
    logger.trace("Creating VlcPlayer: {}", vlcPlayer);
    createEntityInRepository(vlcPlayer);
    return vlcPlayer.getId();
  }

  @Override
  @CacheEvict(value = { "getVlcPlayer" }, allEntries = true)
  public void updateVlcPlayer(VlcPlayer vlcPlayer) {
    logger.trace("Updating VlcPlayer: {}", vlcPlayer);
    updateEntityInRepository(vlcPlayer.getId(), vlcPlayer, VlcPlayer.class);
  }

  @Override
  @Cacheable(value = "getVlcPlayer")
  public VlcPlayer getVlcPlayer(String vlcPlayerName) {
    logger.trace("Get VlcPlayer: {}", vlcPlayerName);
    EntityManager em = getEntityManager();
    VlcPlayer vlcPlayer = null;
    try {
      em.getTransaction().begin();
      Query query = em.createQuery(
          "SELECT vlcPlayer from VlcPlayer vlcPlayer where vlcPlayer.hostname=:pHostname");
      query.setParameter("pHostname", vlcPlayerName);
      vlcPlayer = (VlcPlayer) query.getSingleResult();
      em.getTransaction().commit();
    } catch (PersistenceException pe) {
      handlePersistentException(pe);
    } finally {
      em.close();
    }
    return vlcPlayer;
  }

  @Override
  @CacheEvict(value = { "getVlcPlayer" }, allEntries = true)
  public VlcPlayer deleteVlcPlayer(Long vlcPlayerId) {
    logger.trace("Deleting VlcPlayer: {}", vlcPlayerId);
    return deleteEntityFromRepository(vlcPlayerId, VlcPlayer.class);
  }

  @Override
  public List<VlcPlayer> getAllVlcPlayers() {
    logger.trace("Get all VlcPlayers");
    return getAllEntitiesFromRepository(VlcPlayer.class);
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
