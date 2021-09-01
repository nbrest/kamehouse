package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.admin.dao.KameHouseUserDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.model.KameHouseRole;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseRoleDto;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.utils.PasswordUtils;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import com.nicobrest.kamehouse.commons.validator.KameHouseUserValidator;
import com.nicobrest.kamehouse.commons.validator.UserValidator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    implements CrudService<KameHouseUser, KameHouseUserDto>, UserDetailsService {

  @Autowired
  @Qualifier("kameHouseUserDaoJpa")
  private KameHouseUserDao kameHouseUserDao;

  @Autowired
  @Qualifier("anonymousUser")
  private KameHouseUser anonymousUser;

  @Override
  public Long create(KameHouseUserDto dto) {
    dto.setPassword(PasswordUtils.generateHashedPassword(dto.getPassword()));
    return create(kameHouseUserDao, dto);
  }

  @Override
  public KameHouseUser read(Long id) {
    return read(kameHouseUserDao, id);
  }

  @Override
  public List<KameHouseUser> readAll() {
    return readAll(kameHouseUserDao);
  }

  @Override
  public void update(KameHouseUserDto dto) {
    dto.setPassword(PasswordUtils.generateHashedPassword(dto.getPassword()));
    update(kameHouseUserDao, dto);
  }

  @Override
  public KameHouseUser delete(Long id) {
    return delete(kameHouseUserDao, id);
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

  @Override
  protected KameHouseUser getModel(KameHouseUserDto kameHouseUserDto) {
    KameHouseUser kameHouseUser = new KameHouseUser();
    kameHouseUser.setAccountNonExpired(kameHouseUserDto.isAccountNonExpired());
    kameHouseUser.setAccountNonLocked(kameHouseUserDto.isAccountNonLocked());
    Set<KameHouseRole> kameHouseRoles = new HashSet<>();
    Set<KameHouseRoleDto> kameHouseRoleDtos = kameHouseUserDto.getAuthorities();
    if (kameHouseRoleDtos != null) {
      for (KameHouseRoleDto kameHouseRoleDto : kameHouseRoleDtos) {
        KameHouseRole kameHouseRole = new KameHouseRole();
        kameHouseRole.setId(kameHouseRoleDto.getId());
        kameHouseRole.setName(kameHouseRoleDto.getName());
        kameHouseRole.setKameHouseUser(kameHouseUser);
        kameHouseRoles.add(kameHouseRole);
      }
    }
    kameHouseUser.setAuthorities(kameHouseRoles);
    kameHouseUser.setCredentialsNonExpired(kameHouseUserDto.isCredentialsNonExpired());
    kameHouseUser.setEmail(kameHouseUserDto.getEmail());
    kameHouseUser.setEnabled(kameHouseUserDto.isEnabled());
    kameHouseUser.setFirstName(kameHouseUserDto.getFirstName());
    kameHouseUser.setId(kameHouseUserDto.getId());
    kameHouseUser.setLastLogin(kameHouseUserDto.getLastLogin());
    kameHouseUser.setLastName(kameHouseUserDto.getLastName());
    kameHouseUser.setPassword(kameHouseUserDto.getPassword());
    kameHouseUser.setUsername(kameHouseUserDto.getUsername());
    return kameHouseUser;
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
}
