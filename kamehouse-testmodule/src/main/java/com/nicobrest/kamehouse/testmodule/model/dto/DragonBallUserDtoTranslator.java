package com.nicobrest.kamehouse.testmodule.model.dto;

import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;

/**
 * Translator between entity and dto for DragonBallUser.
 *
 * @author nbrest
 */
public class DragonBallUserDtoTranslator implements
    KameHouseDtoTranslator<DragonBallUser, DragonBallUserDto> {

  @Override
  public DragonBallUser buildEntity(DragonBallUserDto dto) {
    DragonBallUser entity = new DragonBallUser();
    entity.setId(dto.getId());
    entity.setUsername(dto.getUsername());
    entity.setEmail(dto.getEmail());
    entity.setAge(dto.getAge());
    entity.setPowerLevel(dto.getPowerLevel());
    entity.setStamina(dto.getStamina());
    return entity;
  }

  @Override
  public DragonBallUserDto buildDto(DragonBallUser entity) {
    DragonBallUserDto dto = new DragonBallUserDto();
    dto.setId(entity.getId());
    dto.setUsername(entity.getUsername());
    dto.setEmail(entity.getEmail());
    dto.setAge(entity.getAge());
    dto.setPowerLevel(entity.getPowerLevel());
    dto.setStamina(entity.getStamina());
    return dto;
  }
}
