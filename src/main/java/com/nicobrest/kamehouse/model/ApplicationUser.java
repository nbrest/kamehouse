package com.nicobrest.kamehouse.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Class that represents a user in the application.
 * 
 * @author nbrest
 *
 */
public class ApplicationUser implements UserDetails {

  private static final long serialVersionUID = 1L;
  private Long id;
  private String username;
  private String password;
  private String email;
  private String firstName;
  private String lastName;

  /* Spring Security related fields */
  private List<ApplicationRole> authorities;
  private boolean accountNonExpired = true;
  private boolean accountNonLocked = true;
  private boolean credentialsNonExpired = true;
  private boolean enabled = true;

  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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
  public List<ApplicationRole> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(List<ApplicationRole> authorities) {
    this.authorities = authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  public void setAccountNonExpired(boolean accountNonExpired) {
    this.accountNonExpired = accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  public void setAccountNonLocked(boolean accountNonLocked) {
    this.accountNonLocked = accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  public void setCredentialsNonExpired(boolean credentialsNonExpired) {
    this.credentialsNonExpired = credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
   
  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(id).append(username).toHashCode();
  }
 
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof ApplicationUser) {
      final ApplicationUser other = (ApplicationUser) obj;
      return new EqualsBuilder().append(id, other.getId()).append(username, other.getUsername())
          .isEquals();
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
    return "ApplicationUser: INVALID_STATE";
  }
}
