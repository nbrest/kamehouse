package com.nicobrest.kamehouse.testmodule.dao;

import com.nicobrest.kamehouse.main.dao.CrudDao;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;

/**
 * DragonBallUserDao interface.
 *
 * @author nbrest
 */
public interface DragonBallUserDao extends CrudDao<DragonBallUser> {

  /**
   * Gets a DragonBallUser from the repository by its username.
   */
  public DragonBallUser getByUsername(String username);

  /**
   * Gets a DragonBallUser from the repository by its email.
   */
  public DragonBallUser getByEmail(String email);
}
