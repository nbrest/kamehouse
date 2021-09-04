package com.nicobrest.kamehouse.vlcrc.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseDto;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO for a VLC Player.
 *
 * @author nbrest
 */
public class VlcPlayerDto implements KameHouseDto<VlcPlayer>, Serializable {

  @JsonIgnore
  private static final long serialVersionUID = 1L;

  private Long id;
  private String hostname;
  private int port;
  private String username;
  @Masked
  private String password;

  @Override
  public VlcPlayer buildEntity() {
    VlcPlayer entity = new VlcPlayer();
    entity.setId(getId());
    entity.setHostname(getHostname());
    entity.setPort(getPort());
    entity.setUsername(getUsername());
    entity.setPassword(getPassword());
    return entity;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public String getHostname() {
    return hostname;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getPort() {
    return port;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(id).append(hostname).append(port).toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof VlcPlayerDto) {
      final VlcPlayerDto other = (VlcPlayerDto) obj;
      return new EqualsBuilder()
          .append(id, other.getId())
          .append(hostname, other.getHostname())
          .append(port, other.getPort())
          .isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
