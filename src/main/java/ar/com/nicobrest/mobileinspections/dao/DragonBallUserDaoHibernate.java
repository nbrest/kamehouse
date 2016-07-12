package ar.com.nicobrest.mobileinspections.dao;

import ar.com.nicobrest.mobileinspections.exception.DragonBallUserAlreadyExistsException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserNotFoundException;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 *      Hibernate DAO for the DragonBallUser test entities.
 * 
 * @author nbrest 
 */
public class DragonBallUserDaoHibernate implements DragonBallUserDao {

  @Autowired
  private SessionFactory sessionFactory;
  
  /**
   * Inserts a DragonBallUser to the repository.
   * 
   * @author nbrest
   */
  public void createDragonBallUser(DragonBallUser dragonBallUser) 
      throws DragonBallUserAlreadyExistsException {

  }
  
  /**
   * Gets a DragonBallUser from the repository.
   * 
   * @author nbrest
   */
  public DragonBallUser getDragonBallUser(String username) 
      throws DragonBallUserNotFoundException {
    
    return null;
  }
  
  /**
   * Updates a DragonBallUser on the repository.
   * 
   * @author nbrest
   */
  public void updateDragonBallUser(DragonBallUser dragonBallUser) 
      throws DragonBallUserNotFoundException {
    
  }
  
  /**
   * Deletes a DragonBallUser from the repository.
   * 
   * @author nbrest
   * @return DragonBallUser
   */
  public DragonBallUser deleteDragonBallUser(String username) 
      throws DragonBallUserNotFoundException {
    
    return null;
  } 
  
  /**
   * Gets all the DragonBallUsers from the repository.
   * 
   * @author nbrest
   */
  public List<DragonBallUser> getAllDragonBallUsers() {
    
    return null;
  } 
}
