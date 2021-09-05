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
  protected <T> void updateEntityValues(T persistedEntity, T entity) {
    TennisWorldUser persistedObject = (TennisWorldUser) persistedEntity;
    TennisWorldUser updatedObject = (TennisWorldUser) entity;
    persistedObject.setEmail(updatedObject.getEmail());
    persistedObject.setPassword(updatedObject.getPassword());
  }

  @Override
  public TennisWorldUser getByEmail(String email) {
    logger.trace("Get TennisWorldUser: {}", email);
    TennisWorldUser tennisWorldUser = findByEmail(TennisWorldUser.class, email);
    logger.trace("Get TennisWorldUser: {} response {}", email, tennisWorldUser);
    return tennisWorldUser;
  }
}
