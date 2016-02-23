package ar.com.nicobrest.mobileinspections.dao;

import ar.com.nicobrest.mobileinspections.exception.DragonBallUserAlreadyExistsException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserNotFoundException;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;
import ar.com.nicobrest.mobileinspections.utils.IdGenerator;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 *        In-Memory DAO for the test endpoint dragonball
 *         
 * @since v0.03
 * @author nbrest
 */
public class DragonBallUserDaoInMemory implements DragonBallUserDao {

  private static Map<String, DragonBallUser> dragonBallUsers;

  @Autowired
  private DragonBallUser gohanDragonBallUser;

  // @AutoWired + @Qualifier("gotenDragonBallUser")
  @Resource(name = "gotenDragonBallUser")
  private DragonBallUser gotenDragonBallUser;

  /**
   * @since v0.03
   * @author nbrest
   */
  public DragonBallUserDaoInMemory() {

    initRepository();
  }

  /**
   *      Getters and setters
   *          
   * @since v0.03
   * @author nbrest
   * @param gohanDragonBallUser DragonBallUser
   */
  public void setGohanDragonBallUser(DragonBallUser gohanDragonBallUser) {

    this.gohanDragonBallUser = gohanDragonBallUser;
  }

  /**
   *      Getters and setters
   *          
   * @since v0.03
   * @author nbrest
   * @return DragonBallUser
   */
  public DragonBallUser getGohanDragonBallUser() {

    return this.gohanDragonBallUser;
  }

  /**
   *      Getters and setters
   *          
   * @since v0.03
   * @author nbrest
   * @param gotenDragonBallUser DragonBallUser
   */
  public void setGotenDragonBallUser(DragonBallUser gotenDragonBallUser) {

    this.gotenDragonBallUser = gotenDragonBallUser;
  }

  /**
   *      Getters and setters
   *         
   * @since v0.03
   * @author nbrest
   * @return DragonBallUser
   */
  public DragonBallUser getGotenDragonBallUser() {

    return this.gotenDragonBallUser;
  }

  /**
   *      Initialize In-Memory repository
   *         
   * @since v0.03
   * @author nbrest
   */
  private static void initRepository() {

    dragonBallUsers = new HashMap<String, DragonBallUser>();

    DragonBallUser user1 = new DragonBallUser(IdGenerator.getId(), "goku", "goku@dbz.com", 
        49, 30, 1000);
    dragonBallUsers.put(user1.getUsername(), user1);

    DragonBallUser user2 = new DragonBallUser();
    user2.setId(IdGenerator.getId());
    user2.setAge(29);
    user2.setEmail("gohan@dbz.com");
    user2.setUsername("gohan");
    user2.setPowerLevel(20);
    user2.setStamina(1000);
    dragonBallUsers.put(user2.getUsername(), user2);

    DragonBallUser user3 = new DragonBallUser(IdGenerator.getId(), "goten", "goten@dbz.com", 
        19, 10, 1000);
    dragonBallUsers.put(user3.getUsername(), user3);
  }
  
  /**
   *      Adds a new DragonBallUser to the repository
   *           
   * @since v0.03
   * @author nbrest
   * @throws DragonBallUserAlreadyExistsException User defined exception
   */
  public void createDragonBallUser(DragonBallUser dragonBallUser) 
      throws DragonBallUserAlreadyExistsException {

    if (dragonBallUsers.get(dragonBallUser.getUsername()) != null) {
      throw new DragonBallUserAlreadyExistsException("DragonBallUser with username " 
          + dragonBallUser.getUsername() + " already exists in the repository.");
    }
    dragonBallUser.setId(IdGenerator.getId());
    dragonBallUsers.put(dragonBallUser.getUsername(), dragonBallUser);
  }

  /**
   *      Returns a single instance of a DragonBallUser
   *           
   * @since v0.03
   * @author nbrest
   * @throws DragonBallUserNotFoundException User defined exception
   */
  public DragonBallUser getDragonBallUser(String username) throws DragonBallUserNotFoundException {

    DragonBallUser user = dragonBallUsers.get(username);

    if (user == null) {
      throw new DragonBallUserNotFoundException("DragonBallUser with username " 
          + username + " was not found in the repository.");
    }
    return user;
  }

  /**
   *      Updates an existing DragonBallUser in the repository
   *      
   * @since v0.03
   * @author nbrest
   * @throws DragonBallUserNotFoundException User defined exception
   */
  public void updateDragonBallUser(DragonBallUser dragonBallUser) 
      throws DragonBallUserNotFoundException {

    if (dragonBallUsers.get(dragonBallUser.getUsername()) == null) {
      throw new DragonBallUserNotFoundException("DragonBallUser with username " 
          + dragonBallUser.getUsername() + " was not found in the repository.");
    }
    Long storedId = dragonBallUsers.get(dragonBallUser.getUsername()).getId();
    dragonBallUser.setId(storedId);
    dragonBallUsers.put(dragonBallUser.getUsername(), dragonBallUser);
  }

  /**
   *      Deletes a DragonBallUser from the repository
   *      
   * @since v0.03
   * @author nbrest
   * @throws DragonBallUserNotFoundException User defined exception
   */
  public void deleteDragonBallUser(String username) throws DragonBallUserNotFoundException {

    DragonBallUser removedUser = dragonBallUsers.remove(username);

    if (removedUser == null) {
      throw new DragonBallUserNotFoundException("DragonBallUser with username " 
          + username + " was not found in the repository.");
    }
  }

  /**
   *      Returns all the DragonBallUsers in the repository
   *         
   * @since v0.03
   * @author nbrest
   */
  public List<DragonBallUser> getAllDragonBallUsers() {

    List<DragonBallUser> usersList = new ArrayList<DragonBallUser>(dragonBallUsers.values());

    return usersList;
  }
}
