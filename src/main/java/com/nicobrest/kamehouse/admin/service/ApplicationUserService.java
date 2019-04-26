package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.admin.dao.ApplicationUserDao;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.security.PasswordUtils;
import com.nicobrest.kamehouse.admin.validator.ApplicationUserValidator;

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
    validateApplicationUser(applicationUser);
    applicationUser.setPassword(PasswordUtils.generateHashedPassword(applicationUser
        .getPassword()));
    Long id = applicationUserDao.createUser(applicationUser);
    return id;
  }

  /**
   * Updates an application user in the repository.
   */
  public void updateUser(ApplicationUser applicationUser) {
    validateApplicationUser(applicationUser);
    applicationUserDao.updateUser(applicationUser);
  }

  /**
   * Deletes an application user from the repository.
   */
  public ApplicationUser deleteUser(Long id) {
    ApplicationUser deletedUser = applicationUserDao.deleteUser(id);
    return deletedUser;
  }

  /**
   * Get all application users.
   */
  public List<ApplicationUser> getAllUsers() {
    List<ApplicationUser> applicationUsers = applicationUserDao.getAllUsers();
    return applicationUsers;
  }
  
  /**
   * Validates the application user attributes.
   */
  private void validateApplicationUser(ApplicationUser applicationUser) {
    
    ApplicationUserValidator.validateFirstNameFormat(applicationUser.getFirstName());
    ApplicationUserValidator.validateLastNameFormat(applicationUser.getLastName());
    ApplicationUserValidator.validateUsernameFormat(applicationUser.getUsername());
    ApplicationUserValidator.validateEmailFormat(applicationUser.getEmail());
    ApplicationUserValidator.validateStringLength(applicationUser.getFirstName());
    ApplicationUserValidator.validateStringLength(applicationUser.getLastName());
    ApplicationUserValidator.validateStringLength(applicationUser.getUsername());
    ApplicationUserValidator.validateStringLength(applicationUser.getEmail());
    ApplicationUserValidator.validateStringLength(applicationUser.getPassword());
  }
}