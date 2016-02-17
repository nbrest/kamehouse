package ar.com.nicobrest.mobileinspections.dao;

import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @since v0.03
 * @author nbrest
 * 
 *      In-Memory DAO for the test endpoint dragonball
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
   * @since v0.03
   * @author nbrest
   * @param gohanDragonBallUser
   * 
   *      Getters and setters
   */
  public void setGohanDragonBallUser(DragonBallUser gohanDragonBallUser) {
    
    this.gohanDragonBallUser = gohanDragonBallUser;
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * @return DragonBallUser
   * 
   *      Getters and setters
   */
  public DragonBallUser getGohanDragonBallUser() {
    
    return this.gohanDragonBallUser;
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Initialize In-Memory repository
   */
  private static void initRepository() {
    
    DragonBallUser user1 = new DragonBallUser();
    user1.setId(1000L);
    user1.setAge(49);
    user1.setEmail("goku@dbz.com");
    user1.setUsername("goku");
    user1.setPowerLevel(30);
    user1.setStamina(1000);
    
    DragonBallUser user2 = new DragonBallUser();
    user2.setId(1001L);
    user2.setAge(29);
    user2.setEmail("gohan@dbz.com");
    user2.setUsername("gohan");
    user2.setPowerLevel(20);
    user2.setStamina(1000);
    
    DragonBallUser user3 = new DragonBallUser();
    user3.setId(1002L);
    user3.setAge(19);
    user3.setEmail("goten@dbz.com");
    user3.setUsername("goten");
    user3.setPowerLevel(10);
    user3.setStamina(1000);
    
    dragonBallUsers = new HashMap<String, DragonBallUser>();
    dragonBallUsers.put(user1.getUsername(), user1);
    dragonBallUsers.put(user2.getUsername(), user2);
    dragonBallUsers.put(user3.getUsername(), user3);
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * @param gotenDragonBallUser
   * 
   *      Getters and setters
   */
  public void setGotenDragonBallUser(DragonBallUser gotenDragonBallUser) {
    
    this.gotenDragonBallUser = gotenDragonBallUser;
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * @return DragonBallUser
   * 
   *      Getters and setters
   */
  public DragonBallUser getGotenDragonBallUser() {
    
    return this.gotenDragonBallUser;
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Returns a single instance of a DragonBallUser
   */
  public DragonBallUser getDragonBallUser(String username) {
    
    return dragonBallUsers.get(username);
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Returns all the DragonBallUsers in the repository
   */
  public List<DragonBallUser> getAllDragonBallUsers() {
    
    List<DragonBallUser> usersList = new ArrayList<DragonBallUser>(dragonBallUsers.values());
    
    return usersList;
  }
}
