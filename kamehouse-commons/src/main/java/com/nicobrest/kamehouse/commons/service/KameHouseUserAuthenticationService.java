package com.nicobrest.kamehouse.commons.service;

import com.nicobrest.kamehouse.commons.dao.KameHouseUserAuthenticationDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service layer to get read access to the KameHouse users.
 *
 * @author nbrest
 */
@Service
public class KameHouseUserAuthenticationService implements UserDetailsService {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private KameHouseUserAuthenticationDao kameHouseUserAuthenticationDao;
  private KameHouseUser anonymousUser;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public KameHouseUserAuthenticationService(
      @Qualifier("kameHouseUserAuthenticationDaoJpa") KameHouseUserAuthenticationDao
          kameHouseUserAuthenticationDao,
      @Qualifier("anonymousUser") KameHouseUser anonymousUser) {
    this.kameHouseUserAuthenticationDao = kameHouseUserAuthenticationDao;
    this.anonymousUser = anonymousUser;
  }

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public KameHouseUser loadUserByUsername(String username) {
    logger.trace("loadUserByUsername {}", username);
    if (username.equals("anonymousUser")) {
      logger.trace("loadUserByUsername {} response {}", username, anonymousUser);
      return anonymousUser;
    }
    try {
      KameHouseUser kameHouseUser = kameHouseUserAuthenticationDao.loadUserByUsername(username);
      logger.trace("loadUserByUsername {} response {}", username, kameHouseUser);
      return kameHouseUser;
    } catch (KameHouseNotFoundException e) {
      throw new UsernameNotFoundException(e.getMessage(), e);
    }
  }
}
