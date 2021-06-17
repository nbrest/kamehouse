package com.nicobrest.kamehouse.commons.dao;

import com.nicobrest.kamehouse.commons.model.KameHouseUser;

/**
 * Interface for the KameHouseUserAuthenticationDao repositories.
 * 
 * @author nbrest
 *
 */
public interface KameHouseUserAuthenticationDao {

  /**
   * Gets an KameHouse user from the repository by it's username.
   */
  public KameHouseUser loadUserByUsername(String username);
}
