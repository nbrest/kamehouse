package com.nicobrest.kamehouse.dao;

import com.nicobrest.kamehouse.model.ApplicationUser;

import java.util.List;

/**
 * Interfase for the ApplicationUserDao repositories.
 * 
 * @author nbrest
 *
 */
public interface ApplicationUserDao {

  /**
   * Get an application user from the repository by it's username.
   */
  public ApplicationUser loadUserByUsername(String username);
  
  /**
   * Creates a new application user in the repository. Returns it's new id.
   */
  public Long createUser(ApplicationUser applicationUser);
  
  /**
   * Updates an application user in the repository.
   */
  public void updateUser(ApplicationUser applicationUser); 
  
  /**
   * Deletes the application user with the passed id from the repository.
   */
  public ApplicationUser deleteUser(Long id);
  
  /**
   * Get all application users.
   */
  public List<ApplicationUser> getAllUsers();  
}
