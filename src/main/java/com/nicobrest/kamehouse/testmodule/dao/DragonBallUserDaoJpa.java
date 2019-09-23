package com.nicobrest.kamehouse.testmodule.dao;

import com.nicobrest.kamehouse.main.dao.AbstractCrudDaoJpa;
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
  private static final String GET_ALL_DRAGONBALL_USERS_CACHE = "getAllDragonBallUsersCache";
  private static final String GET_DRAGONBALL_USER_CACHE = "getDragonBallUserCache";
  private static final String GET_DRAGONBALL_USER_BY_USERNAME_CACHE =
      "getDragonBallUserByUsernameCache";
  private static final String GET_DRAGONBALL_USER_BY_EMAIL_CACHE = "getDragonBallUserByEmailCache";

  @Override
  @CacheEvict(
      value = { GET_ALL_DRAGONBALL_USERS_CACHE, GET_DRAGONBALL_USER_CACHE,
          GET_DRAGONBALL_USER_BY_USERNAME_CACHE, GET_DRAGONBALL_USER_BY_EMAIL_CACHE },
      allEntries = true)
  public Long create(DragonBallUser entity) {
    return create(DragonBallUser.class, entity);
  }
  
  @Override
  @Cacheable(value = GET_DRAGONBALL_USER_CACHE)
  public DragonBallUser read(Long id) {
    return read(DragonBallUser.class, id);
  }

  @Override
  @CacheEvict(
      value = { GET_ALL_DRAGONBALL_USERS_CACHE, GET_DRAGONBALL_USER_CACHE,
          GET_DRAGONBALL_USER_BY_USERNAME_CACHE, GET_DRAGONBALL_USER_BY_EMAIL_CACHE },
      allEntries = true)
  public void update(DragonBallUser entity) {
    update(DragonBallUser.class, entity);
  }

  @Override
  @CacheEvict(
      value = { GET_ALL_DRAGONBALL_USERS_CACHE, GET_DRAGONBALL_USER_CACHE,
          GET_DRAGONBALL_USER_BY_USERNAME_CACHE, GET_DRAGONBALL_USER_BY_EMAIL_CACHE },
      allEntries = true)
  public DragonBallUser delete(Long id) {
    return delete(DragonBallUser.class, id);
  }

  @Override
  @Cacheable(value = GET_ALL_DRAGONBALL_USERS_CACHE)
  public List<DragonBallUser> getAll() {
    return getAll(DragonBallUser.class);
  }

  @Override
  @Cacheable(value = GET_DRAGONBALL_USER_BY_USERNAME_CACHE)
  public DragonBallUser getByUsername(String username) {
    logger.trace(GET_DRAGONBALLUSER, username);
    return findByUsername(DragonBallUser.class, username);
  }

  @Override
  @Cacheable(value = GET_DRAGONBALL_USER_BY_EMAIL_CACHE)
  public DragonBallUser getByEmail(String email) {
    logger.trace(GET_DRAGONBALLUSER, email);
    return findByEmail(DragonBallUser.class, email);
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
