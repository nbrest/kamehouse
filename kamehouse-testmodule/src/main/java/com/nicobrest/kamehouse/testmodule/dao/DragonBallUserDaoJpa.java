package com.nicobrest.kamehouse.testmodule.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

/**
 * JPA DAO for the DragonBallUser test entities.
 *
 * @author nbrest
 */
@Repository
public class DragonBallUserDaoJpa extends AbstractCrudDaoJpa<DragonBallUser>
    implements DragonBallUserDao {

  private static final String GET_DRAGONBALLUSER = "Get DragonBallUser: {}";
  private static final String GET_DRAGONBALLUSER_RESPONSE = "Get DragonBallUser: {} response {}";
  private static final String DRAGONBALL_USERS_CACHE = "dragonBallUsers";
  private static final String DRAGONBALL_USER_CACHE = "dragonBallUser";
  private static final String DRAGONBALL_USER_BY_USERNAME_CACHE = "dragonBallUserByUsername";
  private static final String DRAGONBALL_USER_BY_EMAIL_CACHE = "dragonBallUserByEmail";

  @Override
  public Class<DragonBallUser> getEntityClass() {
    return DragonBallUser.class;
  }

  @Override
  @CacheEvict(
      value = {
          DRAGONBALL_USERS_CACHE,
          DRAGONBALL_USER_CACHE,
          DRAGONBALL_USER_BY_USERNAME_CACHE,
          DRAGONBALL_USER_BY_EMAIL_CACHE
      },
      allEntries = true)
  public Long create(DragonBallUser entity) {
    return super.create(entity);
  }

  @Override
  @Cacheable(value = DRAGONBALL_USER_CACHE)
  public DragonBallUser read(Long id) {
    return super.read(id);
  }

  @Override
  @Cacheable(value = DRAGONBALL_USERS_CACHE)
  public List<DragonBallUser> readAll() {
    return super.readAll();
  }

  @Override
  @CacheEvict(
      value = {
          DRAGONBALL_USERS_CACHE,
          DRAGONBALL_USER_CACHE,
          DRAGONBALL_USER_BY_USERNAME_CACHE,
          DRAGONBALL_USER_BY_EMAIL_CACHE
      },
      allEntries = true)
  public void update(DragonBallUser entity) {
    super.update(entity);
  }

  @Override
  @CacheEvict(
      value = {
          DRAGONBALL_USERS_CACHE,
          DRAGONBALL_USER_CACHE,
          DRAGONBALL_USER_BY_USERNAME_CACHE,
          DRAGONBALL_USER_BY_EMAIL_CACHE
      },
      allEntries = true)
  public DragonBallUser delete(Long id) {
    return super.delete(id);
  }

  @Override
  protected void updateEntityValues(DragonBallUser persistedEntity, DragonBallUser entity) {
    persistedEntity.setAge(entity.getAge());
    persistedEntity.setEmail(entity.getEmail());
    persistedEntity.setPowerLevel(entity.getPowerLevel());
    persistedEntity.setStamina(entity.getStamina());
    persistedEntity.setUsername(entity.getUsername());
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
}
