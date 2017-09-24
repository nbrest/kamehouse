package com.nicobrest.kamehouse.service;

import com.nicobrest.kamehouse.dao.ApplicationUserDao;
import com.nicobrest.kamehouse.model.ApplicationUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service layer to manage the users in the application.
 * 
 * @author nbrest
 *
 */
@Service
public class ApplicationUserService implements UserDetailsService {

  @Autowired
  private ApplicationUserDao applicationUserDao;

  public void setApplicationUserDao(ApplicationUserDao applicationUserDao) {
    this.applicationUserDao = applicationUserDao;
  }

  public ApplicationUserDao getApplicationUserDao() {
    return applicationUserDao;
  }

  @Override
  public ApplicationUser loadUserByUsername(String username) throws UsernameNotFoundException {
    return applicationUserDao.loadUserByUsername(username);
  }

  /**
   * Creates a new application user in the repository.
   */
  public Long createUser(ApplicationUser applicationUser) {
    Long id = applicationUserDao.createUser(applicationUser);
    return id;
  }

  /**
   * Updates an application user in the repository.
   */
  public void updateUser(ApplicationUser applicationUser) {
    applicationUserDao.updateUser(applicationUser);
  }

  /**
   * Deletes an application user from the repository.
   */
  public ApplicationUser deleteUser(Long id) {
    ApplicationUser deletedUser = applicationUserDao.deleteUser(id);
    return deletedUser;
  }
}