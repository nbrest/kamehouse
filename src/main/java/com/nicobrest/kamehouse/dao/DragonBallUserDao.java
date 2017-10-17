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
   * generated DragonBallUser.
   */
  public Long createDragonBallUser(DragonBallUser dragonBallUser);

  /**
   * Gets a DragonBallUser from the repository by its id.
   */
  public DragonBallUser getDragonBallUser(Long id);

  /**
   * Gets a DragonBallUser from the repository by its username.
   */
  public DragonBallUser getDragonBallUser(String username);

  /**
   * Gets a DragonBallUser from the repository by its email.
   */
  public DragonBallUser getDragonBallUserByEmail(String email);

  /**
   * Updates a DragonBallUser on the repository.
   */
  public void updateDragonBallUser(DragonBallUser dragonBallUser);

  /**
   * Deletes a DragonBallUser from the repository.
   */
  public DragonBallUser deleteDragonBallUser(Long id);

  /**
   * Gets all the DragonBallUsers from the repository.
   */
  public List<DragonBallUser> getAllDragonBallUsers();
}
