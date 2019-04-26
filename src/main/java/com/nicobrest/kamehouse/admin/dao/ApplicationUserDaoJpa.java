package com.nicobrest.kamehouse.admin.dao;

import com.nicobrest.kamehouse.admin.model.ApplicationRole;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.main.exception.KameHouseServerErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 * JPA DAO for the ApplicationUser entities.
 *
 * @author nbrest
 */
public class ApplicationUserDaoJpa implements ApplicationUserDao {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationUserDaoJpa.class);

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

  @Override
  @CacheEvict(value = { "getApplicationUsers" }, allEntries = true)
  public Long createUser(ApplicationUser applicationUser) {

    logger.trace("Creating ApplicationUser: " + applicationUser.getUsername());
    EntityManager em = getEntityManager();
    try {
      em.getTransaction().begin();
      for (ApplicationRole role : applicationUser.getAuthorities()) {
        role.setApplicationUser(applicationUser);
      }
      // Use em.merge instead of em.persist so it doesn't throw the object
      // detached exception for ApplicationRoles
      em.merge(applicationUser);
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
      throw new KameHouseServerErrorException("PersistenceException in createUser", pe);
    } finally {
      em.close();
    }
    // TODO: this method is returning always null, so maybe with em.merge()
    // instead of em.persist() it doesn't update the id in the object. I need to
    // investigate it more.
    return applicationUser.getId();
  }

  @Override
  @Cacheable(value = "getApplicationUsers")
  public ApplicationUser loadUserByUsername(String username) {

    logger.trace("Loading ApplicationUser: " + username);
    EntityManager em = getEntityManager();
    ApplicationUser applicationUser = null;
    try {
      em.getTransaction().begin();
      Query queryAppUser = em.createQuery(
          "SELECT appuser from ApplicationUser appuser where appuser.username=:pUsername");
      queryAppUser.setParameter("pUsername", username);
      applicationUser = (ApplicationUser) queryAppUser.getSingleResult();
      em.getTransaction().commit();
    } catch (PersistenceException pe) {
      pe.printStackTrace();
      // Iterate through the causes of the PersistenceException to identify and
      // return the correct exception.
      Throwable cause = pe;
      while (cause != null) {
        if (cause instanceof javax.persistence.NoResultException) {
          throw new UsernameNotFoundException("User with username " + username + " not found.");
        }
        cause = cause.getCause();
      }
      throw new KameHouseServerErrorException("PersistenceException in loadUserByUsername", pe);
    } finally {
      em.close();
    }
    return applicationUser;
  }

  @Override
  @CacheEvict(value = { "getApplicationUsers" }, allEntries = true)
  public void updateUser(ApplicationUser applicationUser) {

    logger.trace("Updating ApplicationUser: " + applicationUser.getUsername());
    EntityManager em = getEntityManager();
    try {
      em.getTransaction().begin();
      ApplicationUser updatedAppUser = em.find(ApplicationUser.class, applicationUser.getId());
      if (updatedAppUser != null) {
        updatedAppUser.setAccountNonExpired(applicationUser.isAccountNonExpired());
        updatedAppUser.setAccountNonLocked(applicationUser.isAccountNonLocked());
        updatedAppUser.setAuthorities(applicationUser.getAuthorities());
        updatedAppUser.setCredentialsNonExpired(applicationUser.isCredentialsNonExpired());
        updatedAppUser.setEmail(applicationUser.getEmail());
        updatedAppUser.setEnabled(applicationUser.isEnabled());
        updatedAppUser.setFirstName(applicationUser.getFirstName());
        updatedAppUser.setLastLogin(applicationUser.getLastLogin());
        updatedAppUser.setLastName(applicationUser.getLastName());
        // Do not update the password here. Create a separate method just to
        // update the password.
        // updatedAppUser.setPassword(applicationUser.getPassword());
        // Do not update the username here. Create a separate method just to
        // update the username.
        // updatedAppUser.setUsername(applicationUser.getUsername());

        for (ApplicationRole role : updatedAppUser.getAuthorities()) {
          role.setApplicationUser(updatedAppUser);
        }
        em.merge(updatedAppUser);
      }
      em.getTransaction().commit();
      if (updatedAppUser == null) {
        throw new UsernameNotFoundException("ApplicationUser with id " + applicationUser.getId()
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
      throw new KameHouseServerErrorException("PersistenceException in updateUser", pe);
    } finally {
      em.close();
    }
  }

  @Override
  @CacheEvict(value = { "getApplicationUsers" }, allEntries = true)
  public ApplicationUser deleteUser(Long id) {

    logger.trace("Deleting ApplicationUser: " + id);
    EntityManager em = getEntityManager();
    ApplicationUser appUserToRemove = null;
    try {
      em.getTransaction().begin();
      appUserToRemove = em.find(ApplicationUser.class, id);
      if (appUserToRemove != null) {
        em.remove(appUserToRemove);
      }
      em.getTransaction().commit();
      if (appUserToRemove == null) {
        throw new UsernameNotFoundException("ApplicationUser with id " + id
            + " was not found in the repository.");
      }
    } catch (PersistenceException pe) {
      pe.printStackTrace();
      throw new KameHouseServerErrorException("PersistenceException in deleteUser", pe);
    } finally {
      em.close();
    }
    return appUserToRemove;
  }

  @Override
  public List<ApplicationUser> getAllUsers() {

    logger.trace("Loading all ApplicationUsers");
    EntityManager em = getEntityManager();
    List<ApplicationUser> applicationUsers = null;
    try {
      em.getTransaction().begin();
      applicationUsers = em.createQuery("from ApplicationUser", ApplicationUser.class)
          .getResultList();
      em.getTransaction().commit();
    } catch (PersistenceException pe) {
      pe.printStackTrace();
      throw new KameHouseServerErrorException("PersistenceException in getAllUsers", pe);
    } finally {
      em.close();
    }
    return applicationUsers;
  }
}
