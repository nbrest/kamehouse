package com.nicobrest.kamehouse.testmodule.service;

import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.main.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.testmodule.dao.DragonBallUserDao;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.service.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.validator.DragonBallUserValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer to manage the DragonBallUsers.
 *
 * @author nbrest
 */
@Service
public class DragonBallUserService {

  @Autowired
  @Qualifier("dragonBallUserDaoJpa")
  private DragonBallUserDao dragonBallUserDao;

  public void setDragonBallUserDao(DragonBallUserDao dragonBallUserDao) {
    this.dragonBallUserDao = dragonBallUserDao;
  }

  public DragonBallUserDao getDragonBallUserDao() {
    return this.dragonBallUserDao;
  }

  /**
   * Create a new DragonBallUser in the repository.
   */
  public Long createDragonBallUser(DragonBallUserDto dragonBallUserDto) {
    DragonBallUser dragonBallUser = getModel(dragonBallUserDto);
    try { 
      validateDragonBallUser(dragonBallUser);
    } catch (KameHouseInvalidDataException e) {
      // TODO: Catch the exception in the controller and transform it to a
      // Network exception in the controller layer. Also move network exceptions
      // from the Dao layer to the controller layer.
      throw new KameHouseBadRequestException(e.getMessage(), e);
    }
    return dragonBallUserDao.createDragonBallUser(dragonBallUser);
  }

  /**
   * Returns a single instance of a DragonBallUser looking up by id.
   */
  public DragonBallUser getDragonBallUser(Long id) {
    return dragonBallUserDao.getDragonBallUser(id);
  }

  /**
   * Returns a single instance of a DragonBallUser looking up by username.
   */
  public DragonBallUser getDragonBallUser(String username) {
    return dragonBallUserDao.getDragonBallUser(username);
  }

  /**
   * Returns a single instance of a DragonBallUser looking up by email.
   */
  public DragonBallUser getDragonBallUserByEmail(String email) {
    return dragonBallUserDao.getDragonBallUserByEmail(email);
  }

  /**
   * Updates an existing DragonBallUser in the repository.
   */
  public void updateDragonBallUser(DragonBallUserDto dragonBallUserDto) {
    DragonBallUser dragonBallUser = getModel(dragonBallUserDto);
    try {
      validateDragonBallUser(dragonBallUser);
    } catch (KameHouseInvalidDataException e) {
      throw new KameHouseBadRequestException(e.getMessage(), e);
    }
    dragonBallUserDao.updateDragonBallUser(dragonBallUser);
  }

  /**
   * Deletes an existing DragonBallUser in the repository.
   */
  public DragonBallUser deleteDragonBallUser(Long id) {
    return dragonBallUserDao.deleteDragonBallUser(id);
  }

  /**
   * Returns all the DragonBallUsers in the repository.
   */
  public List<DragonBallUser> getAllDragonBallUsers() {
    return dragonBallUserDao.getAllDragonBallUsers();
  }

  /**
   * Performs all the input and logical validations on a DragonBallUser and
   * throw an exception if a validation fails.
   */
  private void validateDragonBallUser(DragonBallUser dragonBallUser) {

    DragonBallUserValidator.validateUsernameFormat(dragonBallUser.getUsername());
    DragonBallUserValidator.validateEmailFormat(dragonBallUser.getEmail());
    DragonBallUserValidator.validateStringLength(dragonBallUser.getUsername());
    DragonBallUserValidator.validateStringLength(dragonBallUser.getEmail());
    DragonBallUserValidator.validatePositiveValue(dragonBallUser.getAge());
    DragonBallUserValidator.validatePositiveValue(dragonBallUser.getPowerLevel());
  }
  
  /**
   * Get a DragonBallUser model object from it's DTO.
   */
  private DragonBallUser getModel(DragonBallUserDto dragonBallUserDto) {
    DragonBallUser dragonBallUser = new DragonBallUser();
    dragonBallUser.setAge(dragonBallUserDto.getAge());
    dragonBallUser.setEmail(dragonBallUserDto.getEmail());
    dragonBallUser.setId(dragonBallUserDto.getId());
    dragonBallUser.setPowerLevel(dragonBallUser.getPowerLevel());
    dragonBallUser.setStamina(dragonBallUserDto.getStamina());
    dragonBallUser.setUsername(dragonBallUserDto.getUsername());
    return dragonBallUser;
  }
}
