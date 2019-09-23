package com.nicobrest.kamehouse.admin.dao;

import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.main.dao.CrudDao;

/**
 * Interface for the ApplicationUserDao repositories.
 * 
 * @author nbrest
 *
 */
public interface ApplicationUserDao extends CrudDao<ApplicationUser> {

  /**
   * Get an application user from the repository by it's username.
   */
  public ApplicationUser loadUserByUsername(String username);
}
