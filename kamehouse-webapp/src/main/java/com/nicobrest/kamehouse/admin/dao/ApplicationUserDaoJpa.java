package com.nicobrest.kamehouse.admin.dao;

import com.nicobrest.kamehouse.admin.model.ApplicationRole;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.main.dao.AbstractCrudDaoJpa;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * JPA DAO for the ApplicationUser entities.
 *
 * @author nbrest
 */
public class ApplicationUserDaoJpa extends AbstractCrudDaoJpa implements ApplicationUserDao {

  @Override
  public Long create(ApplicationUser entity) {
    return create(ApplicationUser.class, entity);
  }

  @Override
  public ApplicationUser read(Long id) {
    return read(ApplicationUser.class, id);
  }
  
  @Override
  public List<ApplicationUser> readAll() {
    return readAll(ApplicationUser.class);
  }
  
  @Override
  public void update(ApplicationUser entity) {
    update(ApplicationUser.class, entity);
  }

  @Override
  public ApplicationUser delete(Long id) {
    return delete(ApplicationUser.class, id);
  }

  @Override
  public ApplicationUser loadUserByUsername(String username) {
    logger.trace("loadUserByUsername {}", username);
    ApplicationUser applicationUser = findByUsername(ApplicationUser.class, username);
    logger.trace("loadUserByUsername {} response {}", username, applicationUser);
    return applicationUser;
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
