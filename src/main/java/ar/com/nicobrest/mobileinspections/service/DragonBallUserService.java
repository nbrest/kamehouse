package ar.com.nicobrest.mobileinspections.service;

import ar.com.nicobrest.mobileinspections.dao.DragonBallUserDao;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @since v0.03
 * @author nbrest
 * 
 *      Service layer for the Example endpoints
 */
public class DragonBallUserService {

  @Autowired
  private DragonBallUserDao dragonBallUserDao;

  /**
   * @since v0.03
   * @author nbrest
   * @param dragonBallUserDao
   * 
   *      Getters and setters
   */
  public void setDragonBallUserDao(DragonBallUserDao dragonBallUserDao) {
    
    this.dragonBallUserDao = dragonBallUserDao;
  }
 
  /**
   * @since v0.03
   * @author nbrest
   * @return DragonBallUserDao
   * 
   *      Getters and setters
   */
  public DragonBallUserDao getDragonBallUserDao() {
    
    return this.dragonBallUserDao;
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Returns a single instance of a DragonBallUser
   */
  public DragonBallUser getDragonBallUser(String username) {
    
    return dragonBallUserDao.getDragonBallUser(username);
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Returns all the DragonBallUsers in the repository
   */
  public List<DragonBallUser> getAllDragonBallUsers() {
    
    return dragonBallUserDao.getAllDragonBallUsers();
  }
}
