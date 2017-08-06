package com.nicobrest.kamehouse.dao;

import com.nicobrest.kamehouse.model.DragonBallUser;

import java.util.List;

/**
 * DragonBallUserDao interface.
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
   * Gets a DragonBallUser from the repository by its id.
   *
   * @author nbrest
   */
  public DragonBallUser getDragonBallUser(Long id);

  /**
   * Gets a DragonBallUser from the repository by its username.
   *
   * @author nbrest
   */
  public DragonBallUser getDragonBallUser(String username);

  /**
   * Gets a DragonBallUser from the repository by its email.
   *
   * @author nbrest
   */
  public DragonBallUser getDragonBallUserByEmail(String email);

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
