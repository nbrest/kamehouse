package com.nicobrest.kamehouse.vlcrc.dao;

import com.nicobrest.kamehouse.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 * DAO layer to manage registered VLC Players in the application using JPA.
 * 
 * @author nbrest
 *
 */
public class VlcPlayerDaoJpa implements VlcPlayerDao {

  @Autowired
  private EntityManagerFactory entityManagerFactory;

  public EntityManagerFactory getEntityManagerFactory() {
    return entityManagerFactory;
  }

  public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  @Override
  public Long createVlcPlayer(VlcPlayer vlcPlayer) {
    EntityManager em = getEntityManager();
    try {
      em.getTransaction().begin();
      em.persist(vlcPlayer);
      em.getTransaction().commit();
    } catch (PersistenceException pe) {
      pe.printStackTrace();
      // Iterate through the causes of the PersistenceException to identify and
      // return the correct exception.
      Throwable cause = pe;
      while (cause != null) {
        if (cause instanceof org.hibernate.exception.ConstraintViolationException) {
          throw new KameHouseConflictException(
              "ConstraintViolationException: Error inserting data", pe);
        }
        cause = cause.getCause();
      }
      throw new KameHouseServerErrorException("PersistenceException in createVlcPlayer", pe);
    } finally {
      em.close();
    }
    return vlcPlayer.getId();
  }

  @Override
  public void updateVlcPlayer(VlcPlayer vlcPlayer) {

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
            + " was not found in the repository.");
      }
    } catch (PersistenceException pe) {
      pe.printStackTrace();
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
  public VlcPlayer getVlcPlayer(String vlcPlayerName) {
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
      pe.printStackTrace();
      // Iterate through the causes of the PersistenceException to identify and
      // return the correct exception.
      Throwable cause = pe;
      while (cause != null) {
        if (cause instanceof javax.persistence.NoResultException) {
          throw new KameHouseNotFoundException("VLC Player with hostname " + vlcPlayerName
              + " was not found in the repository.");
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
  public VlcPlayer deleteVlcPlayer(Long vlcPlayerId) {
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
            + " was not found in the repository.");
      }
    } catch (PersistenceException pe) {
      pe.printStackTrace();
      throw new KameHouseServerErrorException("PersistenceException in deleteDragonBallUser", pe);
    } finally {
      em.close();
    }
    return vlcPlayerToRemove;
  }

  @Override
  public List<VlcPlayer> getAllVlcPlayers() {
    EntityManager em = getEntityManager();
    List<VlcPlayer> vlcPlayers = null;
    try {
      em.getTransaction().begin();
      vlcPlayers = em.createQuery("from VlcPlayer", VlcPlayer.class).getResultList();
      em.getTransaction().commit();
    } catch (PersistenceException pe) {
      pe.printStackTrace();
      throw new KameHouseServerErrorException("PersistenceException in getAllVlcPlayers", pe);
    } finally {
      em.close();
    }
    return vlcPlayers;
  }

  /**
   * Get the EntityManager.
   */
  public EntityManager getEntityManager() {
    return entityManagerFactory.createEntityManager();
  }
}
