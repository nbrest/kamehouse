package ar.com.nicobrest.mobileinspections.dao;

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
  public Long createDragonBallUser(DragonBallUser dragonBallUser);
  
  /**
   * Gets a DragonBallUser from the repository.
   * 
   * @author nbrest
   */
  public DragonBallUser getDragonBallUser(String username);
  
  /**
   * Updates a DragonBallUser on the repository.
   * 
   * @author nbrest
   */
  public void updateDragonBallUser(DragonBallUser dragonBallUser);
  
  /**
   * Deletes a DragonBallUser from the repository.
   * 
   * @author nbrest
   */
  public DragonBallUser deleteDragonBallUser(Long id);
  
  /**
   * Gets all the DragonBallUsers from the repository.
   * 
   * @author nbrest
   */
  public List<DragonBallUser> getAllDragonBallUsers();
}
