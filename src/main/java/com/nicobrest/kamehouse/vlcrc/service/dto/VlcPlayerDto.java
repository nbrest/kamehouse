package com.nicobrest.kamehouse.vlcrc.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * DTO for a VLC Player.
 * 
 * @author nbrest
 *
 */ 
public class VlcPlayerDto implements Serializable {

  @JsonIgnore
  private static final long serialVersionUID = 1L;
  @JsonIgnore
  private static final Logger logger = LoggerFactory.getLogger(VlcPlayerDto.class);
  
  private Long id; 
  private String hostname; 
  private int port; 
  private String username; 
  private String password;
  
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
      return new EqualsBuilder().append(id, other.getId()).append(hostname, other.getHostname())
          .append(port, other.getPort()).isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {

    try {
      return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      logger.error("Error formatting json", e);
    }
    return "VlcPlayer: INVALID_STATE";
  }
}
