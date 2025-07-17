package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.admin.dao.KameHouseUserDao;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDtoTranslator;
import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import com.nicobrest.kamehouse.commons.validator.KameHouseUserValidator;
import com.nicobrest.kamehouse.commons.validator.UserValidator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service layer to manage the users in KameHouse.
 *
 * @author nbrest
 */
@Service
public class KameHouseUserService extends AbstractCrudService<KameHouseUser, KameHouseUserDto>
    implements UserDetailsService {

  private static final KameHouseUserDtoTranslator TRANSLATOR = new KameHouseUserDtoTranslator();
  private KameHouseUserDao kameHouseUserDao;
  private KameHouseUser anonymousUser;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public KameHouseUserService(@Qualifier("kameHouseUserDaoJpa") KameHouseUserDao kameHouseUserDao,
      @Qualifier("anonymousUser") KameHouseUser anonymousUser) {
    this.kameHouseUserDao = kameHouseUserDao;
    this.anonymousUser = anonymousUser;
  }

  @Override
  public CrudDao<KameHouseUser> getCrudDao() {
    return kameHouseUserDao;
  }

  @Override
  public KameHouseDtoTranslator<KameHouseUser, KameHouseUserDto> getDtoTranslator() {
    return TRANSLATOR;
  }

  @Override
  protected void validate(KameHouseUser kameHouseUser) {
    KameHouseUserValidator.validateFirstNameFormat(kameHouseUser.getFirstName());
    KameHouseUserValidator.validateLastNameFormat(kameHouseUser.getLastName());
    UserValidator.validateUsernameFormat(kameHouseUser.getUsername());
    UserValidator.validateEmailFormat(kameHouseUser.getEmail());
    InputValidator.validateStringLength(kameHouseUser.getFirstName());
    InputValidator.validateStringLength(kameHouseUser.getLastName());
    InputValidator.validateStringLength(kameHouseUser.getUsername());
    InputValidator.validateStringLength(kameHouseUser.getEmail());
    InputValidator.validateStringLength(kameHouseUser.getPassword());
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
      KameHouseUser kameHouseUser = kameHouseUserDao.loadUserByUsername(username);
      logger.trace("loadUserByUsername {} response {}", username, kameHouseUser);
      return kameHouseUser;
    } catch (KameHouseNotFoundException e) {
      throw new UsernameNotFoundException(e.getMessage(), e);
    }
  }
}
