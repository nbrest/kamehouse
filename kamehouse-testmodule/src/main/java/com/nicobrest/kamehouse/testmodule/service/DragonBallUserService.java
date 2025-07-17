package com.nicobrest.kamehouse.testmodule.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import com.nicobrest.kamehouse.commons.validator.UserValidator;
import com.nicobrest.kamehouse.testmodule.dao.DragonBallUserDao;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDtoTranslator;
import com.nicobrest.kamehouse.testmodule.validator.DragonBallUserValidator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service layer to manage the DragonBallUsers.
 *
 * @author nbrest
 */
@Service
public class DragonBallUserService extends AbstractCrudService<DragonBallUser, DragonBallUserDto> {

  private static final DragonBallUserDtoTranslator TRANSLATOR = new DragonBallUserDtoTranslator();
  private static final String GET_DRAGONBALLUSER = "Get DragonBallUser: {}";
  private static final String GET_DRAGONBALLUSER_RESPONSE = "Get DragonBallUser: {} response {}";

  private DragonBallUserDao dragonBallUserDao;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public DragonBallUserService(
      @Qualifier("dragonBallUserDaoJpa") DragonBallUserDao dragonBallUserDao) {
    this.dragonBallUserDao = dragonBallUserDao;
  }

  @Override
  public CrudDao<DragonBallUser> getCrudDao() {
    return dragonBallUserDao;
  }

  @Override
  public KameHouseDtoTranslator<DragonBallUser, DragonBallUserDto> getDtoTranslator() {
    return TRANSLATOR;
  }

  @Override
  protected void validate(DragonBallUser dragonBallUser) {
    UserValidator.validateUsernameFormat(dragonBallUser.getUsername());
    UserValidator.validateEmailFormat(dragonBallUser.getEmail());
    InputValidator.validateStringLength(dragonBallUser.getUsername());
    InputValidator.validateStringLength(dragonBallUser.getEmail());
    DragonBallUserValidator.validatePositiveValue(dragonBallUser.getAge());
    DragonBallUserValidator.validatePositiveValue(dragonBallUser.getPowerLevel());
  }

  /**
   * Returns a single instance of a DragonBallUser looking up by username.
   */
  public DragonBallUser getByUsername(String username) {
    logger.trace(GET_DRAGONBALLUSER, username);
    DragonBallUser dragonBallUser = dragonBallUserDao.getByUsername(username);
    logger.trace(GET_DRAGONBALLUSER_RESPONSE, username, dragonBallUser);
    return dragonBallUser;
  }

  /**
   * Returns a single instance of a DragonBallUser looking up by email.
   */
  public DragonBallUser getByEmail(String email) {
    logger.trace(GET_DRAGONBALLUSER, email);
    DragonBallUser dragonBallUser = dragonBallUserDao.getByEmail(email);
    logger.trace(GET_DRAGONBALLUSER_RESPONSE, email, dragonBallUser);
    return dragonBallUser;
  }
}
