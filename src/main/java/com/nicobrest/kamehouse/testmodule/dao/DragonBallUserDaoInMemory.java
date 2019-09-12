package com.nicobrest.kamehouse.testmodule.dao;

import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
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

  private static Map<String, DragonBallUser> dragonBallUsers;
  private static Map<Long, String> dragonBallUsernamesById;
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
   * Initialize In-Memory repository.
   */
  private static void initRepository() {

    dragonBallUsers = new HashMap<>();
    dragonBallUsernamesById = new HashMap<>();

    DragonBallUser user1 = new DragonBallUser(IdGenerator.getId(), "goku", "goku@dbz.com", 49, 30,
        1000);
    dragonBallUsers.put(user1.getUsername(), user1);
    dragonBallUsernamesById.put(user1.getId(), user1.getUsername());

    DragonBallUser user2 = new DragonBallUser();
    user2.setId(IdGenerator.getId());
    user2.setAge(29);
    user2.setEmail("gohan@dbz.com");
    user2.setUsername("gohan");
    user2.setPowerLevel(20);
    user2.setStamina(1000);
    dragonBallUsers.put(user2.getUsername(), user2);
    dragonBallUsernamesById.put(user2.getId(), user2.getUsername());

    DragonBallUser user3 = new DragonBallUser(IdGenerator.getId(), "goten", "goten@dbz.com", 19,
        10, 1000);
    dragonBallUsers.put(user3.getUsername(), user3);
    dragonBallUsernamesById.put(user3.getId(), user3.getUsername());
  }

  @Override
  public Long createDragonBallUser(DragonBallUser dragonBallUser) {

    if (dragonBallUsers.get(dragonBallUser.getUsername()) != null) {
      throw new KameHouseConflictException(DBUSER_WITH_USERNAME + dragonBallUser.getUsername()
          + ALREADY_IN_REPOSITORY);
    }
    dragonBallUser.setId(IdGenerator.getId());
    dragonBallUsers.put(dragonBallUser.getUsername(), dragonBallUser);
    dragonBallUsernamesById.put(dragonBallUser.getId(), dragonBallUser.getUsername());
    return dragonBallUser.getId();
  }

  @Override
  public DragonBallUser getDragonBallUser(Long id) {

    String username = dragonBallUsernamesById.get(id);
    DragonBallUser dragonBallUser = dragonBallUsers.get(username);

    if (username == null) {
      throw new KameHouseNotFoundException(DBUSER_WITH_ID + id + NOT_FOUND_IN_REPOSITORY);
    }
    return dragonBallUser;
  }

  @Override
  public DragonBallUser getDragonBallUser(String username) {

    DragonBallUser user = dragonBallUsers.get(username);

    if (user == null) {
      throw new KameHouseNotFoundException(DBUSER_WITH_USERNAME + username
          + NOT_FOUND_IN_REPOSITORY);
    }
    return user;
  }

  @Override
  public DragonBallUser getDragonBallUserByEmail(String email) {

    throw new UnsupportedOperationException(
        "This functionality is not implemented for the DragonBallUserInMemory repository.");
  }

  @Override
  public void updateDragonBallUser(DragonBallUser dragonBallUser) {

    // Check that the user being updated exists in the repo
    if (dragonBallUsernamesById.get(dragonBallUser.getId()) == null) {
      throw new KameHouseNotFoundException(DBUSER_WITH_ID + dragonBallUser.getId()
          + NOT_FOUND_IN_REPOSITORY);
    }

    // If the username changes, check that the new username doesn´t already
    // exist in the repo
    if (!dragonBallUser.getUsername().equals(dragonBallUsernamesById.get(dragonBallUser.getId()))
        && (dragonBallUsers.get(dragonBallUser.getUsername()) != null)) { 
      throw new KameHouseConflictException(DBUSER_WITH_USERNAME + dragonBallUser.getUsername()
          + ALREADY_IN_REPOSITORY); 
    }

    // Remove old entry for the updated user
    dragonBallUsers.remove(dragonBallUsernamesById.get(dragonBallUser.getId()));
    dragonBallUsernamesById.remove(dragonBallUser.getId());

    // Insert the new entry for the updated user
    dragonBallUsers.put(dragonBallUser.getUsername(), dragonBallUser);
    dragonBallUsernamesById.put(dragonBallUser.getId(), dragonBallUser.getUsername());
  }

  @Override
  public DragonBallUser deleteDragonBallUser(Long id) {

    String username = dragonBallUsernamesById.remove(id);
    if (username == null) {
      throw new KameHouseNotFoundException(DBUSER_WITH_ID + id + NOT_FOUND_IN_REPOSITORY);
    }
    return dragonBallUsers.remove(username);
  }

  @Override
  public List<DragonBallUser> getAllDragonBallUsers() {
    return new ArrayList<>(dragonBallUsers.values());
  }

  /**
   * Static inner class that generates Ids.
   */
  private static class IdGenerator {

    private static final AtomicInteger sequence = new AtomicInteger(1);

    private IdGenerator() {
    }

    /**
     * Return next number in the sequence.
     */
    public static Long getId() {
      return Long.valueOf(sequence.getAndIncrement());
    }
  }
}
