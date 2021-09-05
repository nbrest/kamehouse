package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import org.springframework.stereotype.Repository;

/**
 * JPA DAO for TennisWorldUser entity.
 *
 * @author nbrest
 */
@Repository
public class TennisWorldUserDaoJpa extends AbstractCrudDaoJpa<TennisWorldUser>
    implements TennisWorldUserDao {

  @Override
  public Class<TennisWorldUser> getEntityClass() {
    return TennisWorldUser.class;
  }

  @Override
  protected void updateEntityValues(TennisWorldUser persistedEntity, TennisWorldUser entity) {
    persistedEntity.setEmail(entity.getEmail());
    persistedEntity.setPassword(entity.getPassword());
  }

  @Override
  public TennisWorldUser getByEmail(String email) {
    logger.trace("Get TennisWorldUser: {}", email);
    TennisWorldUser tennisWorldUser = findByEmail(TennisWorldUser.class, email);
    logger.trace("Get TennisWorldUser: {} response {}", email, tennisWorldUser);
    return tennisWorldUser;
  }
}
