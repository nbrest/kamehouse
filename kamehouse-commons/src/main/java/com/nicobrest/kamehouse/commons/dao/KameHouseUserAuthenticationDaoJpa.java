package com.nicobrest.kamehouse.commons.dao;

import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import org.springframework.stereotype.Repository;

/**
 * JPA DAO for the KameHouseUser entities.
 *
 * @author nbrest
 */
@Repository
public class KameHouseUserAuthenticationDaoJpa extends AbstractCrudDaoJpa
    implements KameHouseUserAuthenticationDao {

  @Override
  public KameHouseUser loadUserByUsername(String username) {
    logger.trace("loadUserByUsername {}", username);
    KameHouseUser kameHouseUser = findByUsername(KameHouseUser.class, username);
    logger.trace("loadUserByUsername {} response {}", username, kameHouseUser);
    return kameHouseUser;
  }

  @Override
  protected <T> void updateEntityValues(T persistedEntity, T entity) {
    throw new UnsupportedOperationException("This method should not be called in this class");
  }
}
