package ar.com.nicobrest.mobileinspections.dao;

import ar.com.nicobrest.mobileinspections.exception.DragonBallUserAlreadyExistsException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserNotFoundException;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

import java.util.List;

/**
 *        DragonBallUserDao interface
 *      
 * @since v0.02
 * @author nbrest
 */
public interface DragonBallUserDao {

  public void createDragonBallUser(DragonBallUser dragonBallUser) 
      throws DragonBallUserAlreadyExistsException;
  
  public DragonBallUser getDragonBallUser(String username) 
      throws DragonBallUserNotFoundException;
  
  public void updateDragonBallUser(DragonBallUser dragonBallUser) 
      throws DragonBallUserNotFoundException;
  
  public void deleteDragonBallUser(String username) 
      throws DragonBallUserNotFoundException;

  public List<DragonBallUser> getAllDragonBallUsers();

}
