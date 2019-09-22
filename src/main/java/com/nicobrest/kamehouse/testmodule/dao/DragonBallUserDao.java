package com.nicobrest.kamehouse.testmodule.dao;

import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;

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
  public Long create(DragonBallUser entity);

  /**
   * Gets a DragonBallUser from the repository by its id.
   */
  public DragonBallUser read(Long id);

  /**
   * Gets a DragonBallUser from the repository by its username.
   */
  public DragonBallUser getByUsername(String username);

  /**
   * Gets a DragonBallUser from the repository by its email.
   */
  public DragonBallUser getByEmail(String email);

  /**
   * Updates a DragonBallUser on the repository.
   */
  public void update(DragonBallUser entity);

  /**
   * Deletes a DragonBallUser from the repository.
   */
  public DragonBallUser delete(Long id);

  /**
   * Gets all the DragonBallUsers from the repository.
   */
  public List<DragonBallUser> getAll();
}
