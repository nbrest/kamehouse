package com.nicobrest.kamehouse.admin.service.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nicobrest.kamehouse.main.dao.Identifiable;
import com.nicobrest.kamehouse.utils.JsonUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * DTO for an ApplicationUser.
 * 
 * @author nbrest
 *
 */ 
public class ApplicationUserDto implements Identifiable, Serializable {
   
  private static final long serialVersionUID = 1L;
 
  private Long id; 
  private String username; 
  private String password; 
  private String email; 
  private String firstName; 
  private String lastName; 
  private Date lastLogin; 
  @JsonManagedReference
  private Set<ApplicationRoleDto> authorities; 
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

  /**
   * Get last login date.
   */
  public Date getLastLogin() {
    if (lastLogin != null) {
      return (Date) lastLogin.clone();
    } else {
      return null;
    }
  }

  /**
   * Set last login date.
   */
  public void setLastLogin(Date lastLogin) {
    if (lastLogin != null) {
      this.lastLogin = (Date) lastLogin.clone();
    }
  }
 
  public Set<ApplicationRoleDto> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(Set<ApplicationRoleDto> authorities) {
    this.authorities = authorities;
  }
 
 
  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  public void setAccountNonExpired(boolean accountNonExpired) {
    this.accountNonExpired = accountNonExpired;
  }

  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  public void setAccountNonLocked(boolean accountNonLocked) {
    this.accountNonLocked = accountNonLocked;
  }
 
  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  public void setCredentialsNonExpired(boolean credentialsNonExpired) {
    this.credentialsNonExpired = credentialsNonExpired;
  }
 
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
    if (obj instanceof ApplicationUserDto) {
      final ApplicationUserDto other = (ApplicationUserDto) obj;
      return new EqualsBuilder().append(id, other.getId()).append(username, other.getUsername())
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
