package com.nicobrest.kamehouse.testmodule.service;

import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import com.nicobrest.kamehouse.commons.validator.UserValidator;
import com.nicobrest.kamehouse.testmodule.dao.DragonBallUserDao;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.validator.DragonBallUserValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service layer to manage the DragonBallUsers.
 *
 * @author nbrest
 */
@Service
public class DragonBallUserService extends AbstractCrudService<DragonBallUser, DragonBallUserDto>
    implements CrudService<DragonBallUser, DragonBallUserDto> {

  private static final String GET_DRAGONBALLUSER = "Get DragonBallUser: {}";
  private static final String GET_DRAGONBALLUSER_RESPONSE = "Get DragonBallUser: {} response {}";

  @Autowired
  @Qualifier("dragonBallUserDaoJpa")
  private DragonBallUserDao dragonBallUserDao;

  public void setDragonBallUserDao(DragonBallUserDao dragonBallUserDao) {
    this.dragonBallUserDao = dragonBallUserDao;
  }

  public DragonBallUserDao getDragonBallUserDao() {
    return this.dragonBallUserDao;
  }

  @Override
  public Long create(DragonBallUserDto dto) {
    return create(dragonBallUserDao, dto);
  }

  @Override
  public DragonBallUser read(Long id) {
    return read(dragonBallUserDao, id);
  }

  @Override
  public List<DragonBallUser> readAll() {
    return readAll(dragonBallUserDao);
  }

  @Override
  public void update(DragonBallUserDto dto) {
    update(dragonBallUserDao, dto);
  }

  @Override
  public DragonBallUser delete(Long id) {
    return delete(dragonBallUserDao, id);
  }

  /** Returns a single instance of a DragonBallUser looking up by username. */
  public DragonBallUser getByUsername(String username) {
    logger.trace(GET_DRAGONBALLUSER, username);
    DragonBallUser dragonBallUser = dragonBallUserDao.getByUsername(username);
    logger.trace(GET_DRAGONBALLUSER_RESPONSE, username, dragonBallUser);
    return dragonBallUser;
  }

  /** Returns a single instance of a DragonBallUser looking up by email. */
  public DragonBallUser getByEmail(String email) {
    logger.trace(GET_DRAGONBALLUSER, email);
    DragonBallUser dragonBallUser = dragonBallUserDao.getByEmail(email);
    logger.trace(GET_DRAGONBALLUSER_RESPONSE, email, dragonBallUser);
    return dragonBallUser;
  }

  @Override
  protected DragonBallUser getModel(DragonBallUserDto dragonBallUserDto) {
    DragonBallUser dragonBallUser = new DragonBallUser();
    dragonBallUser.setAge(dragonBallUserDto.getAge());
    dragonBallUser.setEmail(dragonBallUserDto.getEmail());
    dragonBallUser.setId(dragonBallUserDto.getId());
    dragonBallUser.setPowerLevel(dragonBallUserDto.getPowerLevel());
    dragonBallUser.setStamina(dragonBallUserDto.getStamina());
    dragonBallUser.setUsername(dragonBallUserDto.getUsername());
    return dragonBallUser;
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
}
