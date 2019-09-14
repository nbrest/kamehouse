package com.nicobrest.kamehouse.vlcrc.dao;

import com.nicobrest.kamehouse.main.dao.AbstractDaoJpa;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
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

  private static final String NOT_FOUND_IN_REPOSITORY = " was not found in the repository.";

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
    EntityManager em = getEntityManager();
    try {
      em.getTransaction().begin();
      VlcPlayer updatedVlcPlayer = em.find(VlcPlayer.class, vlcPlayer.getId());
      if (updatedVlcPlayer != null) {
        updatedVlcPlayer.setHostname(vlcPlayer.getHostname());
        updatedVlcPlayer.setPort(vlcPlayer.getPort());
        updatedVlcPlayer.setUsername(vlcPlayer.getUsername());
        updatedVlcPlayer.setPassword(vlcPlayer.getPassword());
      }
      em.getTransaction().commit();
      if (updatedVlcPlayer == null) {
        throw new KameHouseNotFoundException("VlcPlayer with id " + vlcPlayer.getId()
            + NOT_FOUND_IN_REPOSITORY);
      }
    } catch (PersistenceException pe) {
      handlePersistentException(pe);
    } finally {
      em.close();
    }
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
}
