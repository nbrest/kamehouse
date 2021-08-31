package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * JPA DAO for TennisWorldUser entity.
 *
 * @author nbrest
 */
@Repository
public class TennisWorldUserDaoJpa extends AbstractCrudDaoJpa implements TennisWorldUserDao {

  @Override
  public Long create(TennisWorldUser entity) {
    return create(TennisWorldUser.class, entity);
  }

  @Override
  public TennisWorldUser read(Long id) {
    return read(TennisWorldUser.class, id);
  }

  @Override
  public List<TennisWorldUser> readAll() {
    return readAll(TennisWorldUser.class);
  }

  @Override
  public void update(TennisWorldUser entity) {
    update(TennisWorldUser.class, entity);
  }

  @Override
  public TennisWorldUser delete(Long id) {
    return delete(TennisWorldUser.class, id);
  }

  @Override
  public TennisWorldUser getByEmail(String email) {
    logger.trace("Get TennisWorldUser: {}", email);
    TennisWorldUser tennisWorldUser = findByEmail(TennisWorldUser.class, email);
    logger.trace("Get TennisWorldUser: {} response {}", email, tennisWorldUser);
    return tennisWorldUser;
  }

  @Override
  protected <T> void updateEntityValues(T persistedEntity, T entity) {
    TennisWorldUser persistedObject = (TennisWorldUser) persistedEntity;
    TennisWorldUser updatedObject = (TennisWorldUser) entity;
    persistedObject.setEmail(updatedObject.getEmail());
    persistedObject.setPassword(updatedObject.getPassword());
  }
}
