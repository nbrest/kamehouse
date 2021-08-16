package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import com.nicobrest.kamehouse.commons.validator.UserValidator;
import com.nicobrest.kamehouse.tennisworld.dao.TennisWorldUserDao;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.TennisWorldUserDto;
import org.apache.commons.codec.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer to manage the TennisWorldUsers.
 *
 * @author nbrest
 */
@Service
public class TennisWorldUserService extends AbstractCrudService<TennisWorldUser, TennisWorldUserDto>
    implements CrudService<TennisWorldUser, TennisWorldUserDto> {

  @Autowired
  @Qualifier("tennisWorldUserDaoJpa")
  private TennisWorldUserDao tennisWorldUserDao;

  @Override
  public Long create(TennisWorldUserDto dto) {
    return create(tennisWorldUserDao, dto);
  }

  @Override
  public TennisWorldUser read(Long id) {
    return read(tennisWorldUserDao, id);
  }

  @Override
  public List<TennisWorldUser> readAll() {
    return readAll(tennisWorldUserDao);
  }

  @Override
  public void update(TennisWorldUserDto dto) {
    update(tennisWorldUserDao, dto);
  }

  @Override
  public TennisWorldUser delete(Long id) {
    return delete(tennisWorldUserDao, id);
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

  @Override
  protected TennisWorldUser getModel(TennisWorldUserDto dto) {
    TennisWorldUser entity = new TennisWorldUser();
    entity.setId(dto.getId());
    entity.setEmail(dto.getEmail());
    byte[] encryptedPassword = EncryptionUtils.encrypt(dto.getPassword().getBytes(Charsets.UTF_8),
        EncryptionUtils.getKameHouseCertificate());
    entity.setPassword(encryptedPassword);
    return entity;
  }

  @Override
  protected void validate(TennisWorldUser entity) {
    UserValidator.validateEmailFormat(entity.getEmail());
    InputValidator.validateStringLength(entity.getEmail());
  }
}
