package com.nicobrest.kamehouse.testmodule.service;

import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.main.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.main.validator.UserValidator;
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
  public Long create(DragonBallUserDto dto) {
    DragonBallUser dragonBallUser = getModel(dto);
    try { 
      validate(dragonBallUser);
    } catch (KameHouseInvalidDataException e) {
      throw new KameHouseBadRequestException(e.getMessage(), e);
    }
    return dragonBallUserDao.create(dragonBallUser);
  }

  /**
   * Reads a single instance of a DragonBallUser looking up by id.
   */
  public DragonBallUser read(Long id) {
    return dragonBallUserDao.read(id);
  }

  /**
   * Reads all the DragonBallUsers in the repository.
   */
  public List<DragonBallUser> readAll() {
    return dragonBallUserDao.readAll();
  }

  /**
   * Updates an existing DragonBallUser in the repository.
   */
  public void update(DragonBallUserDto dto) {
    DragonBallUser dragonBallUser = getModel(dto);
    try {
      validate(dragonBallUser);
    } catch (KameHouseInvalidDataException e) {
      throw new KameHouseBadRequestException(e.getMessage(), e);
    }
    dragonBallUserDao.update(dragonBallUser);
  }

  /**
   * Deletes an existing DragonBallUser in the repository.
   */
  public DragonBallUser delete(Long id) {
    return dragonBallUserDao.delete(id);
  }

  /**
   * Returns a single instance of a DragonBallUser looking up by username.
   */
  public DragonBallUser getByUsername(String username) {
    return dragonBallUserDao.getByUsername(username);
  }

  /**
   * Returns a single instance of a DragonBallUser looking up by email.
   */
  public DragonBallUser getByEmail(String email) {
    return dragonBallUserDao.getByEmail(email);
  }

  /**
   * Performs all the input and logical validations on a DragonBallUser and
   * throw an exception if a validation fails.
   */
  private void validate(DragonBallUser dragonBallUser) {
    UserValidator.validateUsernameFormat(dragonBallUser.getUsername());
    UserValidator.validateEmailFormat(dragonBallUser.getEmail());
    UserValidator.validateStringLength(dragonBallUser.getUsername());
    UserValidator.validateStringLength(dragonBallUser.getEmail());
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
    dragonBallUser.setPowerLevel(dragonBallUserDto.getPowerLevel());
    dragonBallUser.setStamina(dragonBallUserDto.getStamina());
    dragonBallUser.setUsername(dragonBallUserDto.getUsername());
    return dragonBallUser;
  }
}
