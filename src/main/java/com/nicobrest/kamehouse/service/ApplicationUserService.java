package com.nicobrest.kamehouse.service;

import com.nicobrest.kamehouse.dao.ApplicationUserDao;
import com.nicobrest.kamehouse.model.ApplicationUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer to manage the users in the application.
 * 
 * @author nbrest
 *
 */
@Service
public class ApplicationUserService implements UserDetailsService {
 
  @Autowired
  @Qualifier("applicationUserDaoJpa")
  private ApplicationUserDao applicationUserDao;

  @Autowired
  @Qualifier("anonymousUser")
  private ApplicationUser anonymousUser;
  
  public void setApplicationUserDao(ApplicationUserDao applicationUserDao) {
    this.applicationUserDao = applicationUserDao;
  }

  public ApplicationUserDao getApplicationUserDao() {
    return applicationUserDao;
  }
  
  public void setAnonymousUser(ApplicationUser anonymousUser) {
    this.anonymousUser = anonymousUser;
  }
  
  public ApplicationUser getAnonymousUser() {
    return anonymousUser;
  }

  @Override
  public ApplicationUser loadUserByUsername(String username) throws UsernameNotFoundException {
    if (username.equals("anonymousUser")) {
      return anonymousUser;
    }
    ApplicationUser user = applicationUserDao.loadUserByUsername(username);
    return user;
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
    //Don't return the passwords through the API.
    deletedUser.setPassword(null);
    return deletedUser;
  }

  /**
   * Get all application users.
   */
  public List<ApplicationUser> getAllUsers() {
    List<ApplicationUser> applicationUsers = applicationUserDao.getAllUsers();
    return applicationUsers;
  }
}