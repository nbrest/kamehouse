package com.nicobrest.kamehouse.ztvrc.model.dto;

import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.ztvrc.model.VlcPlayer;
import com.nicobrest.kamehouse.ztvrc.model.ZtvPlayer;

/**
 * Translator between entity and dto for ZTV Players.
 *
 * @author nbrest
 */
public class ZtvPlayerDtoTranslator implements KameHouseDtoTranslator<ZtvPlayer, ZtvPlayerDto> {

  @Override
  public ZtvPlayer buildEntity(ZtvPlayerDto dto) {
    ZtvPlayer entity = new VlcPlayer();
    entity.setId(dto.getId());
    entity.setHostname(dto.getHostname());
    entity.setPort(dto.getPort());
    entity.setUsername(dto.getUsername());
    entity.setPassword(dto.getPassword());
    return entity;
  }

  @Override
  public ZtvPlayerDto buildDto(ZtvPlayer entity) {
    ZtvPlayerDto dto = new ZtvPlayerDto();
    dto.setId(entity.getId());
    dto.setHostname(entity.getHostname());
    dto.setPort(entity.getPort());
    dto.setUsername(entity.getUsername());
    dto.setPassword(entity.getPassword());
    return dto;
  }
}
