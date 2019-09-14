package com.nicobrest.kamehouse.main.dao;

import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.main.exception.KameHouseServerErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

/**
 * Abstract class to group common functionality to Jpa Daos.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractDaoJpa {

  protected final Logger logger = LoggerFactory.getLogger(getClass());
  private static final String NOT_FOUND_IN_REPOSITORY =
      "NoResultException: Entity not found in the repository.";
  private static final String CONSTRAINT_VIOLATION_EXCEPTION =
      "ConstraintViolationException: Error inserting data.";
  private static final String PERSISTENCE_EXCEPTION = "PersistenceException thrown.";
  
  @Autowired
  private EntityManagerFactory entityManagerFactory;

  public EntityManagerFactory getEntityManagerFactory() {
    return entityManagerFactory;
  }

  public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  public EntityManager getEntityManager() {
    return entityManagerFactory.createEntityManager();
  }

  /**
   * Persist the specified entity in the repository.
   */
  protected <T> void createEntityInRepository(T entity) {
    EntityManager em = getEntityManager();
    try {
      em.getTransaction().begin();
      em.persist(entity);
      em.getTransaction().commit();
    } catch (PersistenceException pe) {
      handlePersistentException(pe);
    } finally {
      em.close();
    }
  }

  /**
   * Delete the entity of the specified class from the repository.
   */
  protected <T> T deleteEntityFromRepository(Long entityId, Class<T> clazz) {
    EntityManager em = getEntityManager();
    T entityToRemove = null;
    try {
      em.getTransaction().begin();
      entityToRemove = em.find(clazz, entityId);
      if (entityToRemove != null) {
        em.remove(entityToRemove);
      }
      em.getTransaction().commit();
      if (entityToRemove == null) {
        throw new KameHouseNotFoundException(clazz.getSimpleName() + " with id " + entityId
            + " was not found in the repository.");
      }
    } catch (PersistenceException pe) {
      throw new KameHouseServerErrorException(PERSISTENCE_EXCEPTION, pe);
    } finally {
      em.close();
    }
    return entityToRemove;
  }

  /**
   * Get all objects of the specified entity from the repository.
   */
  protected <T> List<T> getAllEntitiesFromRepository(Class<T> clazz) {

    EntityManager em = getEntityManager();
    List<T> listOfEntities = null;
    try {
      em.getTransaction().begin();
      listOfEntities = em.createQuery("from " + clazz.getSimpleName(), clazz).getResultList();
      em.getTransaction().commit();
    } catch (PersistenceException pe) {
      throw new KameHouseServerErrorException(PERSISTENCE_EXCEPTION, pe);
    } finally {
      em.close();
    }
    return listOfEntities;
  }

  /**
   * Process the thrown persistent exception to throw the appropriate type.
   */
  protected void handlePersistentException(PersistenceException pe) {
    Throwable cause = pe;
    while (cause != null) {
      if (cause instanceof org.hibernate.exception.ConstraintViolationException) {
        throw new KameHouseConflictException(CONSTRAINT_VIOLATION_EXCEPTION, pe);
      }
      if (cause instanceof javax.persistence.NoResultException) {
        throw new KameHouseNotFoundException(NOT_FOUND_IN_REPOSITORY, pe);
      }
      cause = cause.getCause();
    }
    throw new KameHouseServerErrorException(PERSISTENCE_EXCEPTION, pe);
  }
}
