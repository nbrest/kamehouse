package com.nicobrest.kamehouse.admin.dao;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;

/**
 * Interface for the KameHouseUserDao repositories.
 * 
 * @author nbrest
 *
 */
public interface KameHouseUserDao extends CrudDao<KameHouseUser> {

  /**
   * Gets a kamehouse user from the repository by it's username.
   */
  public KameHouseUser loadUserByUsername(String username);
}
