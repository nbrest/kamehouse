package com.nicobrest.kamehouse.service;

import com.nicobrest.kamehouse.dao.DragonBallUserDao;
import com.nicobrest.kamehouse.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.model.DragonBallUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Service layer to manage the DragonBallUsers.
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

    try {
      dragonBallUser.validateAllFields();
    } catch (KameHouseInvalidDataException e) {
      throw new KameHouseBadRequestException(e.getMessage(), e);
    }
    return dragonBallUserDao.createDragonBallUser(dragonBallUser);
  }

  /**
   * Returns a single instance of a DragonBallUser looking up by id.
   *
   * @author nbrest
   */
  public DragonBallUser getDragonBallUser(Long id) {

    return dragonBallUserDao.getDragonBallUser(id);
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

    try {
      dragonBallUser.validateAllFields();
    } catch (KameHouseInvalidDataException e) {
      throw new KameHouseBadRequestException(e.getMessage(), e);
    }
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
}
