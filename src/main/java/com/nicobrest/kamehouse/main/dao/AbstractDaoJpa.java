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
import javax.persistence.Query;

/**
 * Abstract class to group common functionality to Jpa Daos.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractDaoJpa {

  protected final Logger logger = LoggerFactory.getLogger(getClass());
  private static final String NO_RESULT_EXCEPTION =
      "NoResultException: Entity not found in the repository.";
  private static final String CONSTRAINT_VIOLATION_EXCEPTION =
      "ConstraintViolationException: Error inserting data.";
  private static final String PERSISTENCE_EXCEPTION = "PersistenceException thrown.";
  private static final String WITH_ID = " with id ";
  private static final String NOT_FOUND_IN_REPOSITORY = " was not found in the repository.";

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
   * Update the specified entity in the repository.
   */
  protected <T> void updateEntityInRepository(Long entityId, T entity, Class<T> clazz) {
    EntityManager em = getEntityManager();
    try {
      em.getTransaction().begin();
      T persistedEntity = em.find(clazz, entityId);
      if (persistedEntity != null) {
        updateEntityValues(persistedEntity, entity);
        em.merge(persistedEntity);
      }
      em.getTransaction().commit();
      if (persistedEntity == null) {
        throw new KameHouseNotFoundException(clazz.getSimpleName() + WITH_ID + entityId
            + NOT_FOUND_IN_REPOSITORY);
      }
    } catch (PersistenceException pe) {
      handlePersistentException(pe);
    } finally {
      em.close();
    }
  }

  /**
   * Abstract method to update the values of the persistedEntity with the object
   * received as a second parameter.
   */
  protected abstract <T> void updateEntityValues(T persistedEntity, T entity);

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
        throw new KameHouseNotFoundException(clazz.getSimpleName() + WITH_ID + entityId
            + NOT_FOUND_IN_REPOSITORY);
      }
    } catch (PersistenceException pe) {
      throw new KameHouseServerErrorException(PERSISTENCE_EXCEPTION, pe);
    } finally {
      em.close();
    }
    return entityToRemove;
  }

  /**
   * Get the specified entity from the repository.
   */
  protected <T, V> T getEntityFromRepository(V searchParameter) { 
    EntityManager em = getEntityManager();
    T entity = null;
    try {
      em.getTransaction().begin();
      Query query = prepareQueryForGetEntity(em, searchParameter);
      entity = (T) query.getSingleResult();
      em.getTransaction().commit();
    } catch (PersistenceException pe) {
      handlePersistentException(pe);
    } finally {
      em.close();
    }
    return entity;
  }
  
  /**
   * Prepare the select query to get an entity from the repository.
   */
  protected abstract <T> Query prepareQueryForGetEntity(EntityManager em, T searchParameter);
  
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
        throw new KameHouseNotFoundException(NO_RESULT_EXCEPTION, pe);
      }
      cause = cause.getCause();
    }
    throw new KameHouseServerErrorException(PERSISTENCE_EXCEPTION, pe);
  }
}
