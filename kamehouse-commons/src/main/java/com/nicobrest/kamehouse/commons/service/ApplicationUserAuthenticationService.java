package com.nicobrest.kamehouse.commons.service;

import com.nicobrest.kamehouse.commons.dao.ApplicationUserAuthenticationDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.model.ApplicationUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service layer to get read access to the application users.
 * 
 * @author nbrest
 *
 */
@Service
public class ApplicationUserAuthenticationService implements UserDetailsService {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  @Qualifier("applicationUserAuthenticationDaoJpa")
  private ApplicationUserAuthenticationDao applicationUserAuthenticationDao;

  @Autowired
  @Qualifier("anonymousUser")
  private ApplicationUser anonymousUser;

  public ApplicationUserAuthenticationDao getApplicationUserAuthenticationDao() {
    return applicationUserAuthenticationDao;
  }

  public void setApplicationUserAuthenticationDao(ApplicationUserAuthenticationDao
                                                      applicationUserAuthenticationDao) {
    this.applicationUserAuthenticationDao = applicationUserAuthenticationDao;
  }

  public ApplicationUser getAnonymousUser() {
    return anonymousUser;
  }

  public void setAnonymousUser(ApplicationUser anonymousUser) {
    this.anonymousUser = anonymousUser;
  }

  @Override
  public ApplicationUser loadUserByUsername(String username) {
    logger.trace("loadUserByUsername {}", username);
    if (username.equals("anonymousUser")) {
      logger.trace("loadUserByUsername {} response {}", username, anonymousUser);
      return anonymousUser;
    }
    try {
      ApplicationUser applicationUser = applicationUserAuthenticationDao
          .loadUserByUsername(username);
      logger.trace("loadUserByUsername {} response {}", username, applicationUser);
      return applicationUser;
    } catch (KameHouseNotFoundException e) {
      throw new UsernameNotFoundException(e.getMessage(), e);
    }
  }
}