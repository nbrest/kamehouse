package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import com.nicobrest.kamehouse.commons.validator.UserValidator;
import com.nicobrest.kamehouse.tennisworld.dao.TennisWorldUserDao;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.TennisWorldUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service layer to manage the TennisWorldUsers.
 *
 * @author nbrest
 */
@Service
public class TennisWorldUserService
    extends AbstractCrudService<TennisWorldUser, TennisWorldUserDto> {

  @Autowired
  @Qualifier("tennisWorldUserDaoJpa")
  private TennisWorldUserDao tennisWorldUserDao;

  @Override
  public CrudDao<TennisWorldUser> getCrudDao() {
    return tennisWorldUserDao;
  }

  @Override
  protected void validate(TennisWorldUser entity) {
    UserValidator.validateEmailFormat(entity.getEmail());
    InputValidator.validateStringLength(entity.getEmail());
  }

  /**
   * Returns a single instance of a TennisWorldUser looking up by email.
   */
  public TennisWorldUser getByEmail(String email) {
    logger.trace("Get TennisWorldUser: {}", email);
    TennisWorldUser tennisWorldUser = tennisWorldUserDao.getByEmail(email);
    logger.trace("Get TennisWorldUser: {} response {}", email, tennisWorldUser);
    return tennisWorldUser;
  }
}
