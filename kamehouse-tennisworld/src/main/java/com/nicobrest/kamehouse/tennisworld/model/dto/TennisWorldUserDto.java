package com.nicobrest.kamehouse.tennisworld.model.dto;

import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseDto;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.codec.Charsets;

/**
 * TennisWorldUser DTO.
 *
 * @author nbrest
 */
public class TennisWorldUserDto implements KameHouseDto<TennisWorldUser>, Serializable {

  private static final long serialVersionUID = 159367676076449689L;

  private Long id;
  private String email;
  @Masked
  private String password;

  @Override
  public TennisWorldUser buildEntity() {
    TennisWorldUser entity = new TennisWorldUser();
    entity.setId(getId());
    entity.setEmail(getEmail());
    if (password != null) {
      byte[] encryptedPassword = EncryptionUtils.encrypt(getPassword().getBytes(Charsets.UTF_8),
          EncryptionUtils.getKameHouseCertificate());
      entity.setPassword(encryptedPassword);
    } else {
      throw new KameHouseInvalidDataException("Received empty password for TennisWorldUser");
    }
    return entity;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    TennisWorldUserDto that = (TennisWorldUserDto) other;
    return Objects.equals(id, that.id)
        && Objects.equals(email, that.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
