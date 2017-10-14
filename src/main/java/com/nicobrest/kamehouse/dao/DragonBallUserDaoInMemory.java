package com.nicobrest.kamehouse.dao;

import com.nicobrest.kamehouse.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.model.DragonBallUser;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

/**
 * In-Memory DAO for the test endpoint dragonball.
 *
 * @author nbrest
 */
public class DragonBallUserDaoInMemory implements DragonBallUserDao {

  // TODO: Check concurrency issues when modifying the static maps
  // dragonBallUsers and dragonBallUsernamesById. Even though there's only one
  // instance of DragonBallUserDaoInMemory, since it's a singleton bean, but I
  // still think they should be synchronized
  private static Map<String, DragonBallUser> dragonBallUsers;
  private static Map<Long, String> dragonBallUsernamesById;

  @Autowired
  private DragonBallUser gohanDragonBallUser;

  // @AutoWired + @Qualifier("gotenDragonBallUser")
  @Resource(name = "gotenDragonBallUser")
  private DragonBallUser gotenDragonBallUser;

  /**
   * Constructors.
   *
   * @author nbrest
   */
  public DragonBallUserDaoInMemory() {

    initRepository();
  }

  /**
   * Getters and setters.
   *
   * @author nbrest
   */
  public void setGohanDragonBallUser(DragonBallUser gohanDragonBallUser) {

    this.gohanDragonBallUser = gohanDragonBallUser;
  }

  /**
   * Getters and setters.
   *
   * @author nbrest
   */
  public DragonBallUser getGohanDragonBallUser() {

    return this.gohanDragonBallUser;
  }

  /**
   * Getters and setters.
   *
   * @author nbrest
   */
  public void setGotenDragonBallUser(DragonBallUser gotenDragonBallUser) {

    this.gotenDragonBallUser = gotenDragonBallUser;
  }

  /**
   * Getters and setters.
   *
   * @author nbrest
   */
  public DragonBallUser getGotenDragonBallUser() {

    return this.gotenDragonBallUser;
  }

  /**
   * Initialize In-Memory repository.
   *
   * @author nbrest
   */
  private static void initRepository() {

    dragonBallUsers = new HashMap<String, DragonBallUser>();
    dragonBallUsernamesById = new HashMap<Long, String>();

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
      throw new KameHouseConflictException("DragonBallUser with username " + dragonBallUser
          .getUsername() + " already exists in the repository.");
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
      throw new KameHouseNotFoundException("DragonBallUser with id " + id
          + " was not found in the repository.");
    }
    return dragonBallUser;
  }

  @Override
  public DragonBallUser getDragonBallUser(String username) {

    DragonBallUser user = dragonBallUsers.get(username);

    if (user == null) {
      throw new KameHouseNotFoundException("DragonBallUser with username " + username
          + " was not found in the repository.");
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
      throw new KameHouseNotFoundException("DragonBallUser with id " + dragonBallUser.getId()
          + " was not found in the repository.");
    }

    // If the username changes, check that the new username doesn´t already
    // exist in the repo
    if (!dragonBallUser.getUsername().equals(dragonBallUsernamesById.get(dragonBallUser
        .getId()))) {
      if (dragonBallUsers.get(dragonBallUser.getUsername()) != null) {
        throw new KameHouseConflictException("DragonBallUser with username " + dragonBallUser
            .getUsername() + " already exists in the repository.");
      }
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
      throw new KameHouseNotFoundException("DragonBallUser with id " + id
          + " was not found in the repository.");
    }
    DragonBallUser removedUser = dragonBallUsers.remove(username);

    return removedUser;
  }

  @Override
  public List<DragonBallUser> getAllDragonBallUsers() {

    List<DragonBallUser> usersList = new ArrayList<DragonBallUser>(dragonBallUsers.values());

    return usersList;
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
