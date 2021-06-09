package com.nicobrest.kamehouse.commons.dao;

import com.nicobrest.kamehouse.commons.model.ApplicationUser;

/**
 * Interface for the ApplicationUserAuthenticationDao repositories.
 * 
 * @author nbrest
 *
 */
public interface ApplicationUserAuthenticationDao {

  /**
   * Gets an application user from the repository by it's username.
   */
  public ApplicationUser loadUserByUsername(String username);
}
