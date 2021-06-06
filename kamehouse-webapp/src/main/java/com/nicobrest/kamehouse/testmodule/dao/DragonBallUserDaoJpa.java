package com.nicobrest.kamehouse.testmodule.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * JPA DAO for the DragonBallUser test entities.
 *
 * @author nbrest
 */
public class DragonBallUserDaoJpa extends AbstractCrudDaoJpa implements DragonBallUserDao {

  private static final String GET_DRAGONBALLUSER = "Get DragonBallUser: {}";
  private static final String GET_DRAGONBALLUSER_RESPONSE = "Get DragonBallUser: {} response {}";
  private static final String DRAGONBALL_USERS_CACHE = "dragonBallUsers";
  private static final String DRAGONBALL_USER_CACHE = "dragonBallUser";
  private static final String DRAGONBALL_USER_BY_USERNAME_CACHE = "dragonBallUserByUsername";
  private static final String DRAGONBALL_USER_BY_EMAIL_CACHE = "dragonBallUserByEmail";

  @Override
  @CacheEvict(value = { DRAGONBALL_USERS_CACHE, DRAGONBALL_USER_CACHE,
      DRAGONBALL_USER_BY_USERNAME_CACHE, DRAGONBALL_USER_BY_EMAIL_CACHE }, allEntries = true)
  public Long create(DragonBallUser entity) {
    return create(DragonBallUser.class, entity);
  }

  @Override
  @Cacheable(value = DRAGONBALL_USER_CACHE)
  public DragonBallUser read(Long id) {
    return read(DragonBallUser.class, id);
  }

  @Override
  @Cacheable(value = DRAGONBALL_USERS_CACHE)
  public List<DragonBallUser> readAll() {
    return readAll(DragonBallUser.class);
  }

  @Override
  @CacheEvict(value = { DRAGONBALL_USERS_CACHE, DRAGONBALL_USER_CACHE,
      DRAGONBALL_USER_BY_USERNAME_CACHE, DRAGONBALL_USER_BY_EMAIL_CACHE }, allEntries = true)
  public void update(DragonBallUser entity) {
    update(DragonBallUser.class, entity);
  }

  @Override
  @CacheEvict(value = { DRAGONBALL_USERS_CACHE, DRAGONBALL_USER_CACHE,
      DRAGONBALL_USER_BY_USERNAME_CACHE, DRAGONBALL_USER_BY_EMAIL_CACHE }, allEntries = true)
  public DragonBallUser delete(Long id) {
    return delete(DragonBallUser.class, id);
  }

  @Override
  @Cacheable(value = DRAGONBALL_USER_BY_USERNAME_CACHE)
  public DragonBallUser getByUsername(String username) {
    logger.trace(GET_DRAGONBALLUSER, username);
    DragonBallUser dragonBallUser = findByUsername(DragonBallUser.class, username);
    logger.trace(GET_DRAGONBALLUSER_RESPONSE, username, dragonBallUser);
    return dragonBallUser;
  }

  @Override
  @Cacheable(value = DRAGONBALL_USER_BY_EMAIL_CACHE)
  public DragonBallUser getByEmail(String email) {
    logger.trace(GET_DRAGONBALLUSER, email);
    DragonBallUser dragonBallUser = findByEmail(DragonBallUser.class, email);
    logger.trace(GET_DRAGONBALLUSER_RESPONSE, email, dragonBallUser);
    return dragonBallUser;
  }

  @Override
  protected <T> void updateEntityValues(T persistedEntity, T entity) {
    DragonBallUser persistedDragonBallUser = (DragonBallUser) persistedEntity;
    DragonBallUser dragonBallUser = (DragonBallUser) entity;
    persistedDragonBallUser.setAge(dragonBallUser.getAge());
    persistedDragonBallUser.setEmail(dragonBallUser.getEmail());
    persistedDragonBallUser.setPowerLevel(dragonBallUser.getPowerLevel());
    persistedDragonBallUser.setStamina(dragonBallUser.getStamina());
    persistedDragonBallUser.setUsername(dragonBallUser.getUsername());
  }
}
