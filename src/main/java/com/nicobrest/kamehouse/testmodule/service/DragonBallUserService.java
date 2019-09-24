package com.nicobrest.kamehouse.testmodule.service;

import com.nicobrest.kamehouse.main.service.AbstractCrudService;
import com.nicobrest.kamehouse.main.service.CrudService;
import com.nicobrest.kamehouse.main.validator.UserValidator;
import com.nicobrest.kamehouse.testmodule.dao.DragonBallUserDao;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.service.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.validator.DragonBallUserValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer to manage the DragonBallUsers.
 *
 * @author nbrest
 */
@Service
public class DragonBallUserService extends AbstractCrudService
    implements CrudService<DragonBallUser, DragonBallUserDto> {

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

  /**
   * Returns a single instance of a DragonBallUser looking up by username.
   */
  public DragonBallUser getByUsername(String username) {
    return dragonBallUserDao.getByUsername(username);
  }

  /**
   * Returns a single instance of a DragonBallUser looking up by email.
   */
  public DragonBallUser getByEmail(String email) {
    return dragonBallUserDao.getByEmail(email);
  }

  @Override
  protected <E, D> E getModel(D dto) {
    DragonBallUserDto dragonBallUserDto = (DragonBallUserDto) dto;
    DragonBallUser dragonBallUser = new DragonBallUser();
    dragonBallUser.setAge(dragonBallUserDto.getAge());
    dragonBallUser.setEmail(dragonBallUserDto.getEmail());
    dragonBallUser.setId(dragonBallUserDto.getId());
    dragonBallUser.setPowerLevel(dragonBallUserDto.getPowerLevel());
    dragonBallUser.setStamina(dragonBallUserDto.getStamina());
    dragonBallUser.setUsername(dragonBallUserDto.getUsername());
    return (E) dragonBallUser;
  }

  @Override
  protected <E> void validate(E entity) {
    DragonBallUser dragonBallUser = (DragonBallUser) entity;
    UserValidator.validateUsernameFormat(dragonBallUser.getUsername());
    UserValidator.validateEmailFormat(dragonBallUser.getEmail());
    UserValidator.validateStringLength(dragonBallUser.getUsername());
    UserValidator.validateStringLength(dragonBallUser.getEmail());
    DragonBallUserValidator.validatePositiveValue(dragonBallUser.getAge());
    DragonBallUserValidator.validatePositiveValue(dragonBallUser.getPowerLevel());
  }
}
