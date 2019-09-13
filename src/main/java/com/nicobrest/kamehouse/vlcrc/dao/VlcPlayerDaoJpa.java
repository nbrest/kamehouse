package com.nicobrest.kamehouse.vlcrc.dao;

import com.nicobrest.kamehouse.main.dao.AbstractDaoJpa;
import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.main.exception.KameHouseServerErrorException;
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
    EntityManager em = getEntityManager();
    try {
      em.getTransaction().begin();
      em.persist(vlcPlayer);
      em.getTransaction().commit();
    } catch (PersistenceException pe) {
      handleOnCreatePersistentException(pe);
    } finally {
      em.close();
    }
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
        throw new KameHouseNotFoundException("VLC Player with id " + vlcPlayer.getId()
            + NOT_FOUND_IN_REPOSITORY);
      }
    } catch (PersistenceException pe) {
      // Iterate through the causes of the PersistenceException to identify and
      // return the correct exception.
      Throwable cause = pe;
      while (cause != null) {
        if (cause instanceof org.hibernate.exception.ConstraintViolationException) {
          throw new KameHouseConflictException("ConstraintViolationException: Error updating data",
              pe);
        }
        cause = cause.getCause();
      }
      throw new KameHouseServerErrorException("PersistenceException in updateVlcPlayer", pe);
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
      // Iterate through the causes of the PersistenceException to identify and
      // return the correct exception.
      Throwable cause = pe;
      while (cause != null) {
        if (cause instanceof javax.persistence.NoResultException) {
          throw new KameHouseNotFoundException("VLC Player with hostname " + vlcPlayerName
              + NOT_FOUND_IN_REPOSITORY);
        }
        cause = cause.getCause();
      }
      throw new KameHouseServerErrorException("PersistenceException in getVlcPlayer", pe);
    } finally {
      em.close();
    }
    return vlcPlayer;
  }

  @Override
  @CacheEvict(value = { "getVlcPlayer" }, allEntries = true)
  public VlcPlayer deleteVlcPlayer(Long vlcPlayerId) {
    logger.trace("Deleting VlcPlayer: {}", vlcPlayerId);
    EntityManager em = getEntityManager();
    VlcPlayer vlcPlayerToRemove = null;
    try {
      em.getTransaction().begin();
      vlcPlayerToRemove = em.find(VlcPlayer.class, vlcPlayerId);
      if (vlcPlayerToRemove != null) {
        em.remove(vlcPlayerToRemove);
      }
      em.getTransaction().commit();
      if (vlcPlayerToRemove == null) {
        throw new KameHouseNotFoundException("VLC Player with id " + vlcPlayerId
            + NOT_FOUND_IN_REPOSITORY);
      }
    } catch (PersistenceException pe) {
      throw new KameHouseServerErrorException("PersistenceException in deleteDragonBallUser", pe);
    } finally {
      em.close();
    }
    return vlcPlayerToRemove;
  }

  @Override
  public List<VlcPlayer> getAllVlcPlayers() {
    logger.trace("Get all VlcPlayers");
    EntityManager em = getEntityManager();
    List<VlcPlayer> vlcPlayers = null;
    try {
      em.getTransaction().begin();
      vlcPlayers = em.createQuery("from VlcPlayer", VlcPlayer.class).getResultList();
      em.getTransaction().commit();
    } catch (PersistenceException pe) {
      throw new KameHouseServerErrorException("PersistenceException in getAllVlcPlayers", pe);
    } finally {
      em.close();
    }
    return vlcPlayers;
  }
}
