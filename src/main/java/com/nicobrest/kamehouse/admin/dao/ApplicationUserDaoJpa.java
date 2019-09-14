package com.nicobrest.kamehouse.admin.dao;

import com.nicobrest.kamehouse.admin.model.ApplicationRole;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.main.dao.AbstractDaoJpa;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 * JPA DAO for the ApplicationUser entities.
 *
 * @author nbrest
 */
public class ApplicationUserDaoJpa extends AbstractDaoJpa implements ApplicationUserDao {

  @Override
  @CacheEvict(value = { "getApplicationUsers" }, allEntries = true)
  public Long createUser(ApplicationUser applicationUser) {
    logger.trace("Creating ApplicationUser: {}", applicationUser.getUsername());
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
      handlePersistentException(pe);
    } finally {
      em.close();
    }
    return applicationUser.getId();
  }

  @Override
  @Cacheable(value = "getApplicationUsers")
  public ApplicationUser loadUserByUsername(String username) {
    logger.trace("Loading ApplicationUser: {}", username);
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
      handlePersistentException(pe);
    } finally {
      em.close();
    }
    return applicationUser;
  }

  @Override
  @CacheEvict(value = { "getApplicationUsers" }, allEntries = true)
  public void updateUser(ApplicationUser applicationUser) {
    logger.trace("Updating ApplicationUser: {}", applicationUser.getUsername());
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
      handlePersistentException(pe);
    } finally {
      em.close();
    }
  }

  @Override
  @CacheEvict(value = { "getApplicationUsers" }, allEntries = true)
  public ApplicationUser deleteUser(Long id) {
    logger.trace("Deleting ApplicationUser: {}", id);
    return deleteEntityFromRepository(id, ApplicationUser.class);
  }

  @Override
  public List<ApplicationUser> getAllUsers() {
    logger.trace("Loading all ApplicationUsers");
    return getAllEntitiesFromRepository(ApplicationUser.class);
  }
}
