package com.nicobrest.kamehouse.admin.service.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.utils.JsonUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * DTO for application roles.
 * 
 * @author nbrest
 *
 */ 
public class ApplicationRoleDto implements Serializable {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationRoleDto.class);
  private static final long serialVersionUID = 1L;
   
  private Long id; 
  private String name; 
  @JsonBackReference
  private ApplicationUserDto applicationUser;
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public ApplicationUserDto getApplicationUser( ) {
    return applicationUser;
  }
  
  public void setApplicationUser(ApplicationUserDto applicationUser) {
    this.applicationUser = applicationUser;
  }
  
  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(name).toHashCode();
  }
 
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof ApplicationRoleDto) {
      final ApplicationRoleDto other = (ApplicationRoleDto) obj;
      return new EqualsBuilder().append(name, other.getName()).isEquals();
    } else {
      return false;
    }
  }
 
  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
