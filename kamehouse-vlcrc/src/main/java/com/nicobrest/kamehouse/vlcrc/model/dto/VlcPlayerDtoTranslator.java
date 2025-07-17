package com.nicobrest.kamehouse.vlcrc.model.dto;

import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;

/**
 * Translator between entity and dto for VlcPlayer.
 *
 * @author nbrest
 */
public class VlcPlayerDtoTranslator implements KameHouseDtoTranslator<VlcPlayer, VlcPlayerDto> {

  @Override
  public VlcPlayer buildEntity(VlcPlayerDto dto) {
    VlcPlayer entity = new VlcPlayer();
    entity.setId(dto.getId());
    entity.setHostname(dto.getHostname());
    entity.setPort(dto.getPort());
    entity.setUsername(dto.getUsername());
    entity.setPassword(dto.getPassword());
    return entity;
  }

  @Override
  public VlcPlayerDto buildDto(VlcPlayer entity) {
    VlcPlayerDto dto = new VlcPlayerDto();
    dto.setId(entity.getId());
    dto.setHostname(entity.getHostname());
    dto.setPort(entity.getPort());
    dto.setUsername(entity.getUsername());
    dto.setPassword(entity.getPassword());
    return dto;
  }
}
