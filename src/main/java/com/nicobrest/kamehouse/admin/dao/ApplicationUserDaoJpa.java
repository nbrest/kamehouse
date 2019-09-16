package com.nicobrest.kamehouse.admin.dao;

import com.nicobrest.kamehouse.admin.model.ApplicationRole;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.main.dao.AbstractDaoJpa;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * JPA DAO for the ApplicationUser entities.
 *
 * @author nbrest
 */
public class ApplicationUserDaoJpa extends AbstractDaoJpa implements ApplicationUserDao {

  @Override
  public List<ApplicationUser> getAllUsers() {
    logger.trace("Loading all ApplicationUsers");
    return findAll(ApplicationUser.class);
  }

  @Override
  public ApplicationUser getUser(Long id) {
    logger.trace("Loading ApplicationUser: {}", id);
    return findById(ApplicationUser.class, id);
  }
  
  @Override
  public ApplicationUser loadUserByUsername(String username) {
    logger.trace("Loading ApplicationUser: {}", username);
    return findByUsername(ApplicationUser.class, username);
  }

  @Override
  public Long createUser(ApplicationUser applicationUser) {
    logger.trace("Creating ApplicationUser: {}", applicationUser.getUsername());
    for (ApplicationRole role : applicationUser.getAuthorities()) {
      role.setApplicationUser(applicationUser);
    }
    // Use merge instead of persist so it doesn't throw the object
    // detached exception for ApplicationRoles
    ApplicationUser mergedApplicationUser = mergeEntityInRepository(applicationUser);
    return mergedApplicationUser.getId();
  }

  @Override
  public void updateUser(ApplicationUser applicationUser) {
    logger.trace("Updating ApplicationUser: {}", applicationUser.getUsername());
    updateEntityInRepository(ApplicationUser.class, applicationUser, applicationUser.getId());
  }

  @Override
  public ApplicationUser deleteUser(Long id) {
    logger.trace("Deleting ApplicationUser: {}", id);
    return deleteEntityFromRepository(ApplicationUser.class, id);
  }

  @Override
  protected <T> void updateEntityValues(T persistedEntity, T entity) {
    ApplicationUser persistedApplicationUser = (ApplicationUser) persistedEntity;
    ApplicationUser updatedApplicationUser = (ApplicationUser) entity;
    persistedApplicationUser.setAccountNonExpired(updatedApplicationUser.isAccountNonExpired());
    persistedApplicationUser.setAccountNonLocked(updatedApplicationUser.isAccountNonLocked());
    persistedApplicationUser.setCredentialsNonExpired(updatedApplicationUser
        .isCredentialsNonExpired());
    persistedApplicationUser.setEmail(updatedApplicationUser.getEmail());
    persistedApplicationUser.setEnabled(updatedApplicationUser.isEnabled());
    persistedApplicationUser.setFirstName(updatedApplicationUser.getFirstName());
    persistedApplicationUser.setLastLogin(updatedApplicationUser.getLastLogin());
    persistedApplicationUser.setLastName(updatedApplicationUser.getLastName());
    Set<ApplicationRole> persistedApplicationRoles = persistedApplicationUser.getAuthorities();
    Set<ApplicationRole> updatedApplicationRoles = updatedApplicationUser.getAuthorities();
    Iterator<ApplicationRole> persistedApplicationRolesIterator = persistedApplicationRoles
        .iterator();
    while (persistedApplicationRolesIterator.hasNext()) {
      ApplicationRole persistedRole = persistedApplicationRolesIterator.next();
      if (!updatedApplicationRoles.contains(persistedRole)) {
        persistedApplicationRolesIterator.remove();
      }
    }
    persistedApplicationRoles.addAll(updatedApplicationRoles);
  }
}
