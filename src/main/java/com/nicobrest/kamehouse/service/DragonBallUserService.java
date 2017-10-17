package com.nicobrest.kamehouse.service;

import com.nicobrest.kamehouse.dao.DragonBallUserDao;
import com.nicobrest.kamehouse.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.model.DragonBallUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service layer to manage the DragonBallUsers.
 *
 * @author nbrest
 */
public class DragonBallUserService {

  private static final int MAX_STRING_LENGTH = 255;
  private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
      + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
  private static final String USERNAME_PATTERN = "^[A-Za-z0-9]+[\\._A-Za-z0-9-]*";

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
  public Long createDragonBallUser(DragonBallUser dragonBallUser) {

    try {
      validateAllFields(dragonBallUser);
    } catch (KameHouseInvalidDataException e) {
      // TODO: Maybe catch the exception in the controller and transform it to a
      // Network exception in the controller layer
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
  public void updateDragonBallUser(DragonBallUser dragonBallUser) {

    try {
      validateAllFields(dragonBallUser);
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
  private void validateAllFields(DragonBallUser dragonBallUser) {

    validateUsernameFormat(dragonBallUser.getUsername());
    validateStringLength(dragonBallUser.getUsername());
    validateEmailFormat(dragonBallUser.getEmail());
    validateStringLength(dragonBallUser.getEmail());
    validatePositiveValue(dragonBallUser.getAge());
    validatePositiveValue(dragonBallUser.getPowerLevel());
  }

  /**
   * Validate that the username respects the established format.
   */
  private void validateUsernameFormat(String username) {

    Pattern pattern = Pattern.compile(USERNAME_PATTERN);
    Matcher matcher = pattern.matcher(username);
    if (!matcher.matches()) {
      throw new KameHouseInvalidDataException("Invalid username format: " + username);
    }
  }

  /**
   * Validate that the email has a valid format.
   */
  private void validateEmailFormat(String email) {

    Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    Matcher matcher = pattern.matcher(email);
    if (!matcher.matches()) {
      throw new KameHouseInvalidDataException("Invalid email address: " + email);
    }
  }

  /**
   * Validate that the integer has a positive value.
   */
  private void validatePositiveValue(int value) {

    if (value < 0) {
      throw new KameHouseInvalidDataException(
          "The attribute should be a positive value. Current value: " + value);
    }
  }

  /**
   * Validate that the string length is accepted by the database.
   */
  private void validateStringLength(String value) {

    if (value.length() > MAX_STRING_LENGTH) {
      throw new KameHouseInvalidDataException("The string attribute excedes the maximum length of "
          + MAX_STRING_LENGTH + ". Current length: " + value.length());
    }
  }
}
