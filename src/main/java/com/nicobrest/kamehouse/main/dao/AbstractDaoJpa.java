package com.nicobrest.kamehouse.main.dao;

import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.main.exception.KameHouseServerErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
  
  protected void handleOnCreateOrUpdatePersistentException(PersistenceException pe) {
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
    throw new KameHouseServerErrorException("PersistenceException", pe);
  }
}
