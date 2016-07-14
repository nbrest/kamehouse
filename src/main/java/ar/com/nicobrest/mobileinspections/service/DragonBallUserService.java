package ar.com.nicobrest.mobileinspections.service;

import ar.com.nicobrest.mobileinspections.dao.DragonBallUserDao;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 *        Service layer for the Example endpoints.
 *     
 * @author nbrest
 */
public class DragonBallUserService {

  @Autowired
  private DragonBallUserDao dragonBallUserDao;

  /**
   *      Getters and setters.
   *      
   * @author nbrest
   */
  public void setDragonBallUserDao(DragonBallUserDao dragonBallUserDao) {
    
    this.dragonBallUserDao = dragonBallUserDao;
  }

  /**
   *      Getters and setters.
   *      
   * @author nbrest
   */
  public DragonBallUserDao getDragonBallUserDao() {
    
    return this.dragonBallUserDao;
  }
  
  /**
   *      Create a new DragonBallUser in the repository.
   *    
   * @author nbrest
   */
  public Long createDragonBallUser(DragonBallUser dragonBallUser) {
    
    return dragonBallUserDao.createDragonBallUser(dragonBallUser);
  }
  
  /**
   *      Returns a single instance of a DragonBallUser.
   *      
   * @author nbrest 
   */
  public DragonBallUser getDragonBallUser(String username) {
    
    return dragonBallUserDao.getDragonBallUser(username);
  }
  
  /**
   *      Updates an existing DragonBallUser in the repository.
   *      
   * @author nbrest
   */
  public void updateDragonBallUser(DragonBallUser dragonBallUser) {
    
    dragonBallUserDao.updateDragonBallUser(dragonBallUser);
  }
  
  /**
   *      Deletes an existing DragonBallUser in the repository.
   *      
   * @author nbrest
   */
  public DragonBallUser deleteDragonBallUser(Long id) {
    
    return dragonBallUserDao.deleteDragonBallUser(id);
  }
  
  /**
   *      Returns all the DragonBallUsers in the repository.
   *      
   * @author nbrest
   */
  public List<DragonBallUser> getAllDragonBallUsers() {
    
    return dragonBallUserDao.getAllDragonBallUsers();
  }
}
