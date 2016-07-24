package ar.com.nicobrest.mobileinspections.service;

import ar.com.nicobrest.mobileinspections.dao.DragonBallUserDao;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Service layer for the Example endpoints.
 * 
 * @author nbrest
 */
public class DragonBallUserService {

  @Autowired
  @Qualifier("dragonBallUserDaoJpa")
  private DragonBallUserDao dragonBallUserDao;

  /**
   * Getters and setters.
   * 
   * @author nbrest
   */
  public void setDragonBallUserDao(DragonBallUserDao dragonBallUserDao) {

    this.dragonBallUserDao = dragonBallUserDao;
  }

  /**
   * Getters and setters.
   * 
   * @author nbrest
   */
  public DragonBallUserDao getDragonBallUserDao() {

    return this.dragonBallUserDao;
  }

  /**
   * Create a new DragonBallUser in the repository.
   * 
   * @author nbrest
   */
  public Long createDragonBallUser(DragonBallUser dragonBallUser) {

    validateDragonBallUser(dragonBallUser);
    return dragonBallUserDao.createDragonBallUser(dragonBallUser);
  }

  /**
   * Returns a single instance of a DragonBallUser looking up by username.
   * 
   * @author nbrest
   */
  public DragonBallUser getDragonBallUser(String username) {

    return dragonBallUserDao.getDragonBallUser(username);
  }

  /**
   * Returns a single instance of a DragonBallUser looking up by email.
   * 
   * @author nbrest
   */
  public DragonBallUser getDragonBallUserByEmail(String email) {

    return dragonBallUserDao.getDragonBallUserByEmail(email);
  }

  /**
   * Updates an existing DragonBallUser in the repository.
   * 
   * @author nbrest
   */
  public void updateDragonBallUser(DragonBallUser dragonBallUser) {

    validateDragonBallUser(dragonBallUser);
    dragonBallUserDao.updateDragonBallUser(dragonBallUser);
  }

  /**
   * Deletes an existing DragonBallUser in the repository.
   * 
   * @author nbrest
   */
  public DragonBallUser deleteDragonBallUser(Long id) {

    return dragonBallUserDao.deleteDragonBallUser(id);
  }

  /**
   * Returns all the DragonBallUsers in the repository.
   * 
   * @author nbrest
   */
  public List<DragonBallUser> getAllDragonBallUsers() {

    return dragonBallUserDao.getAllDragonBallUsers();
  }
  
  /**
   * Performs all the input and logical validations on a DragonBallUser.
   * 
   * @author nbrest
   */
  private void validateDragonBallUser(DragonBallUser dragonBallUser) {
    
    /* - username must contain lettes, numbers or dots, and start with a letter or number
     * - check valid format in the email field: sth1@sth2.sth3
     * - age and powerlevel should be > 0
     * - strings shouldn´t be longer than the supported 255 characters of varchar in the database
     */
    validateUsernameFormat(dragonBallUser.getUsername());
    
    validateEmailFormat(dragonBallUser.getEmail());
    
    validatePositiveValue(dragonBallUser.getAge());
    validatePositiveValue(dragonBallUser.getPowerLevel());
    
    validateStringLength(dragonBallUser.getUsername());
    validateStringLength(dragonBallUser.getEmail());
  }
  
  /**
   * Validate that the username respects the established format.
   * 
   * @author nbrest
   */
  private void validateUsernameFormat(String username) {
    
  }
  
  /**
   * Validate that the email has a valid format.
   * 
   * @author nbrest
   */
  private void validateEmailFormat(String email) {
    
  }
  
  /**
   * Validate that the integer has a positive value.
   * 
   * @author nbrest
   */
  private void validatePositiveValue(int value) {
    
  }
  
  /**
   * Validate that the string lenght is accepted by the database.
   * 
   * @author nbrest
   */
  private void validateStringLength(String value) {
    
  }
}
