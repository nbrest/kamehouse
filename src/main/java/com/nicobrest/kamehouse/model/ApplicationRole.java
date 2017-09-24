package com.nicobrest.kamehouse.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;

/**
 * Class that represents a user's ROLE in the application.
 * 
 * @author nbrest
 *
 */
public class ApplicationRole implements GrantedAuthority {

  private static final long serialVersionUID = 1L;
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getAuthority() {
    return this.name;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(name).toHashCode();
  }
 
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof ApplicationRole) {
      final ApplicationRole other = (ApplicationRole) obj;
      return new EqualsBuilder().append(name, other.getName()).isEquals();
    } else {
      return false;
    }
  }
 
  @Override
  public String toString() {

    try {
      return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      e.printStackTrace(); 
    }
    return "ApplicationRole: INVALID_STATE";
  }
}
