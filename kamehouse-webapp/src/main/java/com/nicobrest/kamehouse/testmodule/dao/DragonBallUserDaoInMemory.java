package com.nicobrest.kamehouse.testmodule.dao;

import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-Memory DAO for the test endpoint dragonball.
 *
 * @author nbrest
 */
public class DragonBallUserDaoInMemory implements DragonBallUserDao {

  private static Map<String, DragonBallUser> repository;
  private static Map<Long, String> usernamesById;
  private static final String ALREADY_IN_REPOSITORY = " already exists in the repository.";
  private static final String DBUSER_WITH_ID = "DragonBallUser with id ";
  private static final String DBUSER_WITH_USERNAME = "DragonBallUser with username ";
  private static final String NOT_FOUND_IN_REPOSITORY = " was not found in the repository.";

  @Autowired
  private DragonBallUser gohanDragonBallUser;

  @Autowired
  @Qualifier("gotenDragonBallUser")
  // @Resource(name = "gotenDragonBallUser")
  private DragonBallUser gotenDragonBallUser;

  public DragonBallUserDaoInMemory() {
    initRepository();
  }

  public void setGohanDragonBallUser(DragonBallUser gohanDragonBallUser) {
    this.gohanDragonBallUser = gohanDragonBallUser;
  }

  public DragonBallUser getGohanDragonBallUser() {
    return this.gohanDragonBallUser;
  }

  public void setGotenDragonBallUser(DragonBallUser gotenDragonBallUser) {
    this.gotenDragonBallUser = gotenDragonBallUser;
  }

  public DragonBallUser getGotenDragonBallUser() {
    return this.gotenDragonBallUser;
  }

  /**
   * Initializes In-Memory repository.
   */
  public static void initRepository() {
    repository = new HashMap<>();
    usernamesById = new HashMap<>();

    DragonBallUser user1 = new DragonBallUser(IdGenerator.getId(), "goku", "goku@dbz.com", 49, 30,
        1000);
    repository.put(user1.getUsername(), user1);
    usernamesById.put(user1.getId(), user1.getUsername());

    DragonBallUser user2 = new DragonBallUser();
    user2.setId(IdGenerator.getId());
    user2.setAge(29);
    user2.setEmail("gohan@dbz.com");
    user2.setUsername("gohan");
    user2.setPowerLevel(20);
    user2.setStamina(1000);
    repository.put(user2.getUsername(), user2);
    usernamesById.put(user2.getId(), user2.getUsername());

    DragonBallUser user3 = new DragonBallUser(IdGenerator.getId(), "goten", "goten@dbz.com", 19,
        10, 1000);
    repository.put(user3.getUsername(), user3);
    usernamesById.put(user3.getId(), user3.getUsername());
  }

  @Override
  public Long create(DragonBallUser entity) {
    if (repository.get(entity.getUsername()) != null) {
      throw new KameHouseConflictException(DBUSER_WITH_USERNAME + entity.getUsername()
          + ALREADY_IN_REPOSITORY);
    }
    entity.setId(IdGenerator.getId());
    repository.put(entity.getUsername(), entity);
    usernamesById.put(entity.getId(), entity.getUsername());
    return entity.getId();
  }

  @Override
  public DragonBallUser read(Long id) {
    String username = usernamesById.get(id);
    DragonBallUser dragonBallUser = repository.get(username);
    if (username == null) {
      throw new KameHouseNotFoundException(DBUSER_WITH_ID + id + NOT_FOUND_IN_REPOSITORY);
    }
    return dragonBallUser;
  }

  @Override
  public List<DragonBallUser> readAll() {
    return new ArrayList<>(repository.values());
  }

  @Override
  public void update(DragonBallUser entity) {
    // Check that the user being updated exists in the repo
    if (usernamesById.get(entity.getId()) == null) {
      throw new KameHouseNotFoundException(DBUSER_WITH_ID + entity.getId()
          + NOT_FOUND_IN_REPOSITORY);
    }

    // If the username changes, check that the new username doesn´t already
    // exist in the repo
    if (!entity.getUsername().equals(usernamesById.get(entity.getId()))
        && (repository.get(entity.getUsername()) != null)) {
      throw new KameHouseConflictException(DBUSER_WITH_USERNAME + entity.getUsername()
          + ALREADY_IN_REPOSITORY);
    }

    // Remove old entry for the updated user
    repository.remove(usernamesById.get(entity.getId()));
    usernamesById.remove(entity.getId());

    // Insert the new entry for the updated user
    repository.put(entity.getUsername(), entity);
    usernamesById.put(entity.getId(), entity.getUsername());
  }

  @Override
  public DragonBallUser delete(Long id) {
    String username = usernamesById.remove(id);
    if (username == null) {
      throw new KameHouseNotFoundException(DBUSER_WITH_ID + id + NOT_FOUND_IN_REPOSITORY);
    }
    return repository.remove(username);
  }

  @Override
  public DragonBallUser getByUsername(String username) {
    DragonBallUser user = repository.get(username);
    if (user == null) {
      throw new KameHouseNotFoundException(DBUSER_WITH_USERNAME + username
          + NOT_FOUND_IN_REPOSITORY);
    }
    return user;
  }

  @Override
  public DragonBallUser getByEmail(String email) {
    throw new UnsupportedOperationException(
        "This functionality is not implemented for the DragonBallUserInMemory repository.");
  }
  
  /**
   * Static inner class that generates Ids.
   */
  private static class IdGenerator {

    private static final AtomicInteger sequence = new AtomicInteger(1);

    private IdGenerator() {
    }

    /**
     * Returns next number in the sequence.
     */
    public static Long getId() {
      return Long.valueOf(sequence.getAndIncrement());
    }
  }
}
