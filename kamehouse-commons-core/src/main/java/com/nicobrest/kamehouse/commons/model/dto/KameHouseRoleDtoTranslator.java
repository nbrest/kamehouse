package com.nicobrest.kamehouse.commons.model.dto;

import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.commons.model.KameHouseRole;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;

/**
 * Translator between entity and dto for KameHouseRole.
 *
 * @author nbrest
 */
public class KameHouseRoleDtoTranslator implements
    KameHouseDtoTranslator<KameHouseRole, KameHouseRoleDto> {

  @Override
  public KameHouseRole buildEntity(KameHouseRoleDto kameHouseRoleDto) {
    KameHouseRole entity = new KameHouseRole();
    entity.setId(kameHouseRoleDto.getId());
    entity.setName(kameHouseRoleDto.getName());
    if (kameHouseRoleDto.getKameHouseUser() != null) {
      KameHouseUser kameHouseUserRef = new KameHouseUser();
      kameHouseUserRef.setId(kameHouseRoleDto.getKameHouseUser().getId());
      entity.setKameHouseUser(kameHouseUserRef);
    }
    return entity;
  }

  @Override
  public KameHouseRoleDto buildDto(KameHouseRole kameHouseRole) {
    KameHouseRoleDto dto = new KameHouseRoleDto();
    dto.setId(kameHouseRole.getId());
    dto.setName(kameHouseRole.getName());
    if (kameHouseRole.getKameHouseUser() != null) {
      KameHouseUserDto kameHouseUserDto = new KameHouseUserDto();
      kameHouseUserDto.setId(kameHouseRole.getKameHouseUser().getId());
      dto.setKameHouseUser(kameHouseUserDto);
    }
    return dto;
  }
}
