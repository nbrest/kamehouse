package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.main.utils.JsonUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Represents the session status information that can be returned by the API.
 *
 * @author nbrest
 */
public class SessionStatus {

  private String username;
  private String firstName;
  private String lastName;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(username).append(firstName).append(lastName).toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof SessionStatus) {
      final SessionStatus other = (SessionStatus) obj;
      return new EqualsBuilder().append(username, other.getUsername()).append(firstName, other
          .getFirstName()).append(lastName, other.getLastName()).isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
