package com.nicobrest.kamehouse.commons.dao;

import com.nicobrest.kamehouse.commons.model.ApplicationUser;
import org.springframework.stereotype.Repository;

/**
 * JPA DAO for the ApplicationUser entities.
 *
 * @author nbrest
 */
@Repository
public class ApplicationUserAuthenticationDaoJpa extends AbstractCrudDaoJpa
    implements ApplicationUserAuthenticationDao {

  @Override
  public ApplicationUser loadUserByUsername(String username) {
    logger.trace("loadUserByUsername {}", username);
    ApplicationUser applicationUser = findByUsername(ApplicationUser.class, username);
    logger.trace("loadUserByUsername {} response {}", username, applicationUser);
    return applicationUser;
  }

  @Override
  protected <T> void updateEntityValues(T persistedEntity, T entity) {
    throw new UnsupportedOperationException("This method should not be called in this class");
  }
}
