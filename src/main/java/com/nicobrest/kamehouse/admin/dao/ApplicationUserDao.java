package com.nicobrest.kamehouse.admin.dao;

import com.nicobrest.kamehouse.admin.model.ApplicationUser;

import java.util.List;

/**
 * Interface for the ApplicationUserDao repositories.
 * 
 * @author nbrest
 *
 */
public interface ApplicationUserDao {

  /**
   * Get an application user from the repository by it's id.
   */
  public ApplicationUser read(Long id);  
  
  /**
   * Get an application user from the repository by it's username.
   */
  public ApplicationUser loadUserByUsername(String username);
  
  /**
   * Creates a new application user in the repository. Returns it's new id.
   */
  public Long create(ApplicationUser entity);
  
  /**
   * Updates an application user in the repository.
   */
  public void update(ApplicationUser entity); 
  
  /**
   * Deletes the application user with the passed id from the repository.
   */
  public ApplicationUser delete(Long id);
  
  /**
   * Get all application users.
   */
  public List<ApplicationUser> getAll();
}
