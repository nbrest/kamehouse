package ar.com.nicobrest.mobileinspections.service;

import ar.com.nicobrest.mobileinspections.dao.DragonBallUserDao;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserAlreadyExistsException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserNotFoundException;
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
   * @param dragonBallUserDao DragonBallUserDao
   */
  public void setDragonBallUserDao(DragonBallUserDao dragonBallUserDao) {
    
    this.dragonBallUserDao = dragonBallUserDao;
  }

  /**
   *      Getters and setters.
   *      
   * @author nbrest
   * @return DragonBallUserDao
   */
  public DragonBallUserDao getDragonBallUserDao() {
    
    return this.dragonBallUserDao;
  }
  
  /**
   *      Create a new DragonBallUser in the repository.
   *    
   * @author nbrest
   * @param dragonBallUser DragonBallUser
   * @throws DragonBallUserAlreadyExistsException User defined exception
   */
  public void createDragonBallUser(DragonBallUser dragonBallUser) 
      throws DragonBallUserAlreadyExistsException {
    
    dragonBallUserDao.createDragonBallUser(dragonBallUser);
  }
  
  /**
   *      Returns a single instance of a DragonBallUser.
   *      
   * @author nbrest
   * @throws DragonBallUserNotFoundException User defined exception
   */
  public DragonBallUser getDragonBallUser(String username) throws DragonBallUserNotFoundException {
    
    return dragonBallUserDao.getDragonBallUser(username);
  }
  
  /**
   *      Updates an existing DragonBallUser in the repository.
   *      
   * @author nbrest
   * @param dragonBallUser DragonBallUser
   * @throws DragonBallUserNotFoundException User defined exception
   */
  public void updateDragonBallUser(DragonBallUser dragonBallUser) 
      throws DragonBallUserNotFoundException {
    
    dragonBallUserDao.updateDragonBallUser(dragonBallUser);
  }
  
  /**
   *      Deletes an existing DragonBallUser in the repository.
   *      
   * @author nbrest
   * @param username : User name
   * @throws DragonBallUserNotFoundException User defined exception
   */
  public DragonBallUser deleteDragonBallUser(String username) 
      throws DragonBallUserNotFoundException {
    
    return dragonBallUserDao.deleteDragonBallUser(username);
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
