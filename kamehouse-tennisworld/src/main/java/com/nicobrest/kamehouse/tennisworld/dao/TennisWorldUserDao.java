package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;

/**
 * TennisWorldUserDao interface.
 *
 * @author nbrest
 */
public interface TennisWorldUserDao extends CrudDao<TennisWorldUser> {

  /**
   * Gets a TennisWorldUser from the repository by its email.
   */
  public TennisWorldUser getByEmail(String email);
}
