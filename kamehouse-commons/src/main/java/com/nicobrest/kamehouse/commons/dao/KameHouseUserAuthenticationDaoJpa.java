package com.nicobrest.kamehouse.commons.dao;

import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import org.springframework.stereotype.Repository;

/**
 * JPA DAO for the KameHouseUser entities.
 *
 * @author nbrest
 */
@Repository
public class KameHouseUserAuthenticationDaoJpa extends AbstractCrudDaoJpa<KameHouseUser>
    implements KameHouseUserAuthenticationDao {

  @Override
  public Class<KameHouseUser> getEntityClass() {
    return KameHouseUser.class;
  }

  @Override
  protected void updateEntityValues(KameHouseUser persistedEntity, KameHouseUser entity) {
    throw new UnsupportedOperationException("This method should not be called in this class");
  }

  @Override
  public KameHouseUser loadUserByUsername(String username) {
    logger.trace("loadUserByUsername {}", username);
    KameHouseUser kameHouseUser = findByUsername(KameHouseUser.class, username);
    logger.trace("loadUserByUsername {} response {}", username, kameHouseUser);
    return kameHouseUser;
  }
}
