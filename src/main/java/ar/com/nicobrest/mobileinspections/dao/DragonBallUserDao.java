package ar.com.nicobrest.mobileinspections.dao;

import ar.com.nicobrest.mobileinspections.exception.DragonBallUserAlreadyExistsException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserNotFoundException;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

import java.util.List;

/**
 *        DragonBallUserDao interface.
 *      
 * @author nbrest
 */
public interface DragonBallUserDao {

  /**
   * Creates a DragonBallUser in the repository. Returns the ID of the newly 
   * generated DragonBallUser
   * 
   * @author nbrest
   */
  public Long createDragonBallUser(DragonBallUser dragonBallUser) 
      throws DragonBallUserAlreadyExistsException;
  
  /**
   * Gets a DragonBallUser from the repository.
   * 
   * @author nbrest
   * @return DragonBallUser
   */
  public DragonBallUser getDragonBallUser(String username) 
      throws DragonBallUserNotFoundException;
  
  /**
   * Updates a DragonBallUser on the repository.
   * 
   * @author nbrest
   */
  public void updateDragonBallUser(DragonBallUser dragonBallUser) 
      throws DragonBallUserNotFoundException, DragonBallUserAlreadyExistsException;
  
  /**
   * Deletes a DragonBallUser from the repository.
   * 
   * @author nbrest
   * @return DragonBallUser
   */
  public DragonBallUser deleteDragonBallUser(Long id) 
      throws DragonBallUserNotFoundException;
  
  /**
   * Gets all the DragonBallUsers from the repository.
   * 
   * @author nbrest
   * @return List of DragonBallUser
   */
  public List<DragonBallUser> getAllDragonBallUsers();
}
