package com.nicobrest.kamehouse.commons.dao;

import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import java.util.List;
import java.util.function.BiFunction;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class to group common functionality to Jpa DAOs.
 *
 * @author nbrest
 */
public abstract class AbstractDaoJpa {

  private static final String NO_RESULT_EXCEPTION =
      "NoResultException: Entity not found in the repository. Message: ";
  private static final String CONSTRAINT_VIOLATION_EXCEPTION =
      "ConstraintViolationException: Error inserting data. Message: ";
  private static final String PERSISTENCE_EXCEPTION = "PersistenceException thrown. Message: ";
  private static final String WITH_ID = " with id ";
  private static final String NOT_FOUND_IN_REPOSITORY = " was not found in the repository.";
  private static final String ILLEGAL_ARGUMENT =
      "IllegalArgumentException. There was an error in the input of the request. Message: ";
  protected static final Logger STATIC_LOGGER = LoggerFactory.getLogger(AbstractDaoJpa.class);
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired private EntityManagerFactory entityManagerFactory;

  public EntityManagerFactory getEntityManagerFactory() {
    return entityManagerFactory;
  }

  public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  public EntityManager getEntityManager() {
    return entityManagerFactory.createEntityManager();
  }

  /** Finds all objects of the specified class from the repository. */
  protected <T> List<T> findAll(Class<T> clazz) {
    EntityManager em = getEntityManager();
    List<T> entitiesList = null;
    try {
      logger.trace("findAll {}", clazz.getSimpleName());
      em.getTransaction().begin();
      entitiesList = em.createQuery("from " + clazz.getSimpleName(), clazz).getResultList();
      em.getTransaction().commit();
      logger.debug("findAll {} response {}", clazz.getSimpleName(), entitiesList);
    } catch (PersistenceException pe) {
      handlePersistentException(pe);
    } finally {
      em.close();
    }
    return entitiesList;
  }

  /** Finds the specified entity from the repository by id. */
  protected <T, V> T findById(Class<T> clazz, V id) {
    EntityManager em = getEntityManager();
    T entity = null;
    try {
      logger.trace("findById {}", id);
      em.getTransaction().begin();
      /*
       * find: returns the entity from the EntityManager if its already in memory.
       * Otherwise it goes to the database to find it. getReference: Returns a proxy
       * to the real entity. Useful if you need to access the primary key used to look
       * up the entity but not the other data of the object.
       */
      entity = em.find(clazz, id);
      em.getTransaction().commit();
      if (entity == null) {
        String warnMessage = clazz.getSimpleName() + WITH_ID + id + NOT_FOUND_IN_REPOSITORY;
        logger.warn(warnMessage);
        throw new KameHouseNotFoundException(warnMessage);
      }
      logger.debug("findById {} response {}", id, entity);
    } catch (PersistenceException pe) {
      handlePersistentException(pe);
    } catch (IllegalArgumentException e) {
      handleIllegalArgumentException(e);
    } finally {
      em.close();
    }
    return entity;
  }

  /** Finds the specified entity from the repository by username. */
  protected <T, Z> T findByUsername(Class<T> clazz, Z username) {
    return findByAttribute(clazz, "username", username);
  }

  /** Finds the specified entity from the repository by email. */
  protected <T, Z> T findByEmail(Class<T> clazz, Z email) {
    return findByAttribute(clazz, "email", email);
  }

  /** Finds the specified entity from the repository by the specified attribute. */
  protected <T, V, Z> T findByAttribute(Class<T> clazz, V attributeName, Z attributeValue) {
    EntityManager em = getEntityManager();
    T entity = null;
    try {
      logger.trace("findByAttribute {} {}", attributeName, attributeValue);
      em.getTransaction().begin();
      String parameterName = "p" + attributeName;
      Query query =
          em.createQuery(
              "SELECT entity from "
                  + clazz.getSimpleName()
                  + " entity where entity."
                  + attributeName
                  + "=:"
                  + parameterName);
      query.setParameter(parameterName, attributeValue);
      entity = (T) query.getSingleResult();
      em.getTransaction().commit();
      logger.debug("findByAttribute {} {} response {}", attributeName, attributeValue, entity);
    } catch (PersistenceException pe) {
      handlePersistentException(pe);
    } finally {
      em.close();
    }
    return entity;
  }

  /** Persists the specified entity in the repository. */
  protected <T> void persistEntityInRepository(T entity) {
    addEntityToRepository(entity, new PersistFunction<T>());
  }

  /** Merges the specified entity in the repository. */
  protected <T> T mergeEntityInRepository(T entity) {
    return addEntityToRepository(entity, new MergeFunction<T>());
  }

  /** Updates the specified entity in the repository. */
  protected <T> void updateEntityInRepository(Class<T> clazz, T entity, Long entityId) {
    EntityManager em = getEntityManager();
    try {
      logger.trace("updateEntityInRepository {}", entity);
      em.getTransaction().begin();
      T persistedEntity = em.find(clazz, entityId);
      if (persistedEntity != null) {
        updateEntityValues(persistedEntity, entity);
        em.merge(persistedEntity);
      }
      em.getTransaction().commit();
      if (persistedEntity == null) {
        String warnMessage = clazz.getSimpleName() + WITH_ID + entityId + NOT_FOUND_IN_REPOSITORY;
        logger.warn(warnMessage);
        throw new KameHouseNotFoundException(warnMessage);
      }
      logger.debug("updateEntityInRepository {} completed successfully", entity);
    } catch (PersistenceException pe) {
      handlePersistentException(pe);
    } catch (IllegalArgumentException e) {
      handleIllegalArgumentException(e);
    } finally {
      em.close();
    }
  }

  /** Updates the values of the persistedEntity with the object received as a second parameter. */
  protected abstract <T> void updateEntityValues(T persistedEntity, T entity);

  /** Deletes the entity of the specified class from the repository. */
  protected <T> T deleteEntityFromRepository(Class<T> clazz, Long entityId) {
    EntityManager em = getEntityManager();
    T entityToRemove = null;
    try {
      logger.trace("deleteEntityFromRepository {}", entityId);
      em.getTransaction().begin();
      entityToRemove = em.find(clazz, entityId);
      if (entityToRemove != null) {
        em.remove(entityToRemove);
      }
      em.getTransaction().commit();
      if (entityToRemove == null) {
        String warnMessage = clazz.getSimpleName() + WITH_ID + entityId + NOT_FOUND_IN_REPOSITORY;
        logger.warn(warnMessage);
        throw new KameHouseNotFoundException(warnMessage);
      }
      logger.debug("deleteEntityFromRepository {} response {}", entityId, entityToRemove);
    } catch (PersistenceException pe) {
      handlePersistentException(pe);
    } catch (IllegalArgumentException e) {
      handleIllegalArgumentException(e);
    } finally {
      em.close();
    }
    return entityToRemove;
  }

  /** Adds the specified entity in the repository. */
  private <T> T addEntityToRepository(T entity, BiFunction<EntityManager, T, T> addFunction) {
    T addedEntity = null;
    EntityManager em = getEntityManager();
    try {
      logger.trace("addEntityToRepository {}", entity);
      em.getTransaction().begin();
      addedEntity = addFunction.apply(em, entity);
      em.getTransaction().commit();
      logger.debug("addEntityToRepository {} response {}", entity, addedEntity);
    } catch (PersistenceException pe) {
      handlePersistentException(pe);
    } finally {
      em.close();
    }
    return addedEntity;
  }

  /** Persist() implementation of the BiFunction interface to add an entity to the repository. */
  private static class PersistFunction<T> implements BiFunction<EntityManager, T, T> {
    @Override
    public T apply(EntityManager em, T entity) {
      em.persist(entity);
      return null;
    }
  }

  /** Merge() implementation of the BiFunction interface to add an entity to the repository. */
  private static class MergeFunction<T> implements BiFunction<EntityManager, T, T> {
    @Override
    public T apply(EntityManager em, T entity) {
      return em.merge(entity);
    }
  }

  /** Processes the thrown persistent exception to throw the appropriate exception type. */
  private static void handlePersistentException(PersistenceException pe) {
    Throwable cause = pe;
    while (cause != null) {
      if (cause instanceof org.hibernate.exception.ConstraintViolationException) {
        String errorMessage = CONSTRAINT_VIOLATION_EXCEPTION + pe.getMessage();
        STATIC_LOGGER.error(errorMessage, pe);
        throw new KameHouseConflictException(errorMessage, pe);
      }
      if (cause instanceof javax.persistence.NoResultException) {
        String warnMessage = NO_RESULT_EXCEPTION + pe.getMessage();
        STATIC_LOGGER.warn(warnMessage);
        throw new KameHouseNotFoundException(warnMessage, pe);
      }
      cause = cause.getCause();
    }
    if (pe != null) {
      String errorMessage = PERSISTENCE_EXCEPTION + pe.getMessage();
      STATIC_LOGGER.error(errorMessage, pe);
      throw new KameHouseServerErrorException(errorMessage, pe);
    }
  }

  /** Returns a bad request response if the code throws an IllegalArgumentException. */
  private static void handleIllegalArgumentException(IllegalArgumentException ex) {
    String errorMessage = ILLEGAL_ARGUMENT + ex.getMessage();
    STATIC_LOGGER.error(errorMessage, ex);
    throw new KameHouseBadRequestException(errorMessage, ex);
  }
}
