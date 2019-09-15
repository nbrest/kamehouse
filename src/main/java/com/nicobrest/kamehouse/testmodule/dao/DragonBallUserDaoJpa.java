package com.nicobrest.kamehouse.testmodule.dao;

import com.nicobrest.kamehouse.main.dao.AbstractDaoJpa;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * JPA DAO for the DragonBallUser test entities.
 *
 * @author nbrest
 */
public class DragonBallUserDaoJpa extends AbstractDaoJpa implements DragonBallUserDao {

  private static final String GET_DRAGONBALLUSER = "Get DragonBallUser: {}";
  private static final String GET_ALL_DRAGONBALL_USERS_CACHE = "getAllDragonBallUsersCache";
  private static final String GET_DRAGONBALL_USER_CACHE = "getDragonBallUserCache";
  private static final String GET_DRAGONBALL_USER_BY_USERNAME_CACHE =
      "getDragonBallUserByUsernameCache";
  private static final String GET_DRAGONBALL_USER_BY_EMAIL_CACHE = "getDragonBallUserByEmailCache";

  @Override
  @Cacheable(value = GET_ALL_DRAGONBALL_USERS_CACHE)
  public List<DragonBallUser> getAllDragonBallUsers() {
    logger.trace("Get all DragonBallUsers");
    return findAll(DragonBallUser.class);
  }

  @Override
  @Cacheable(value = GET_DRAGONBALL_USER_CACHE)
  public DragonBallUser getDragonBallUser(Long id) {
    logger.trace(GET_DRAGONBALLUSER, id);
    return findById(DragonBallUser.class, id);
  }

  @Override
  @Cacheable(value = GET_DRAGONBALL_USER_BY_USERNAME_CACHE)
  public DragonBallUser getDragonBallUser(String username) {
    logger.trace(GET_DRAGONBALLUSER, username);
    return findByUsername(DragonBallUser.class, username);
  }

  @Override
  @Cacheable(value = GET_DRAGONBALL_USER_BY_EMAIL_CACHE)
  public DragonBallUser getDragonBallUserByEmail(String email) {
    logger.trace(GET_DRAGONBALLUSER, email);
    return findByEmail(DragonBallUser.class, email);
  }

  @Override
  @CacheEvict(value = { GET_ALL_DRAGONBALL_USERS_CACHE, GET_DRAGONBALL_USER_CACHE,
      GET_DRAGONBALL_USER_BY_USERNAME_CACHE, GET_DRAGONBALL_USER_BY_EMAIL_CACHE },
      allEntries = true)
  public Long createDragonBallUser(DragonBallUser dragonBallUser) {
    logger.trace("Create DragonBallUser: {}", dragonBallUser);
    persistEntityInRepository(dragonBallUser);
    return dragonBallUser.getId();
  }

  @Override
  @CacheEvict(value = { GET_ALL_DRAGONBALL_USERS_CACHE, GET_DRAGONBALL_USER_CACHE,
      GET_DRAGONBALL_USER_BY_USERNAME_CACHE, GET_DRAGONBALL_USER_BY_EMAIL_CACHE },
      allEntries = true)
  public void updateDragonBallUser(DragonBallUser dragonBallUser) {
    logger.trace("Update DragonBallUser: {}", dragonBallUser);
    updateEntityInRepository(DragonBallUser.class, dragonBallUser, dragonBallUser.getId());
  }

  @Override
  @CacheEvict(value = { GET_ALL_DRAGONBALL_USERS_CACHE, GET_DRAGONBALL_USER_CACHE,
      GET_DRAGONBALL_USER_BY_USERNAME_CACHE, GET_DRAGONBALL_USER_BY_EMAIL_CACHE },
      allEntries = true)
  public DragonBallUser deleteDragonBallUser(Long id) {
    logger.trace("Delete DragonBallUser: {}", id);
    return deleteEntityFromRepository(DragonBallUser.class, id);
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
