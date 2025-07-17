package com.nicobrest.kamehouse.tennisworld.model.dto;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.Charsets;

/**
 * Translator between entity and dto for TennisWorldUser.
 *
 * @author nbrest
 */
public class TennisWorldUserDtoTranslator implements
    KameHouseDtoTranslator<TennisWorldUser, TennisWorldUserDto> {

  @Override
  public TennisWorldUser buildEntity(TennisWorldUserDto dto) {
    TennisWorldUser entity = new TennisWorldUser();
    entity.setId(dto.getId());
    entity.setEmail(dto.getEmail());
    if (dto.getPassword() != null) {
      byte[] encryptedPassword = EncryptionUtils.encrypt(dto.getPassword().getBytes(Charsets.UTF_8),
          EncryptionUtils.getKameHouseCertificate());
      entity.setPassword(encryptedPassword);
    } else {
      throw new KameHouseInvalidDataException("Received empty password for TennisWorldUser");
    }
    return entity;
  }

  @Override
  public TennisWorldUserDto buildDto(TennisWorldUser entity) {
    TennisWorldUserDto dto = new TennisWorldUserDto();
    dto.setId(entity.getId());
    dto.setEmail(entity.getEmail());
    if (entity.getPassword() != null) {
      dto.setPassword(new String(entity.getPassword(), StandardCharsets.UTF_8));
    }
    return dto;
  }
}
