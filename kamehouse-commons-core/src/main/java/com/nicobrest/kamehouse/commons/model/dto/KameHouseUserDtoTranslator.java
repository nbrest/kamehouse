package com.nicobrest.kamehouse.commons.model.dto;

import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.commons.model.KameHouseRole;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.utils.PasswordUtils;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Translator between entity and dto for KameHouseUser.
 *
 * @author nbrest
 */
public class KameHouseUserDtoTranslator implements
    KameHouseDtoTranslator<KameHouseUser, KameHouseUserDto> {

  private static final KameHouseRoleDtoTranslator ROLE_TRANSLATOR =
      new KameHouseRoleDtoTranslator();

  @Override
  public KameHouseUser buildEntity(KameHouseUserDto kameHouseUserDto) {
    KameHouseUser entity = new KameHouseUser();
    entity.setId(kameHouseUserDto.getId());
    entity.setUsername(kameHouseUserDto.getUsername());
    entity.setPassword(PasswordUtils.generateHashedPassword(kameHouseUserDto.getPassword()));
    entity.setEmail(kameHouseUserDto.getEmail());
    entity.setFirstName(kameHouseUserDto.getFirstName());
    entity.setLastName(kameHouseUserDto.getLastName());
    entity.setLastLogin(kameHouseUserDto.getLastLogin());
    if (kameHouseUserDto.getAuthorities() != null) {
      Set<KameHouseRole> authoritiesEntity = kameHouseUserDto.getAuthorities().stream()
          .map(dto -> {
            KameHouseRole role = ROLE_TRANSLATOR.buildEntity(dto);
            role.setKameHouseUser(entity);
            return role;
          })
          .collect(Collectors.toSet());
      entity.setAuthorities(authoritiesEntity);
    }
    entity.setAccountNonExpired(kameHouseUserDto.isAccountNonExpired());
    entity.setAccountNonLocked(kameHouseUserDto.isAccountNonLocked());
    entity.setCredentialsNonExpired(kameHouseUserDto.isCredentialsNonExpired());
    entity.setEnabled(kameHouseUserDto.isEnabled());
    return entity;
  }

  @Override
  public KameHouseUserDto buildDto(KameHouseUser kameHouseUser) {
    KameHouseUserDto dto = new KameHouseUserDto();
    dto.setId(kameHouseUser.getId());
    dto.setUsername(kameHouseUser.getUsername());
    dto.setPassword(kameHouseUser.getPassword());
    dto.setEmail(kameHouseUser.getEmail());
    dto.setFirstName(kameHouseUser.getFirstName());
    dto.setLastName(kameHouseUser.getLastName());
    dto.setLastLogin(kameHouseUser.getLastLogin());
    if (kameHouseUser.getAuthorities() != null) {
      Set<KameHouseRoleDto> authoritiesDto = kameHouseUser.getAuthorities().stream()
          .map(entity -> {
            KameHouseRoleDto roleDto = ROLE_TRANSLATOR.buildDto(entity);
            roleDto.setKameHouseUser(dto);
            return roleDto;
          })
          .collect(Collectors.toSet());
      dto.setAuthorities(authoritiesDto);
    }
    dto.setAccountNonExpired(kameHouseUser.isAccountNonExpired());
    dto.setAccountNonLocked(kameHouseUser.isAccountNonLocked());
    dto.setCredentialsNonExpired(kameHouseUser.isCredentialsNonExpired());
    dto.setEnabled(kameHouseUser.isEnabled());
    return dto;
  }
}
