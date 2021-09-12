package com.nicobrest.kamehouse.commons.model.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.model.KameHouseRole;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.PasswordUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO for an KameHouseUser.
 *
 * @author nbrest
 */
public class KameHouseUserDto implements KameHouseDto<KameHouseUser>, Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private String username;
  @Masked
  private String password;
  private String email;
  private String firstName;
  private String lastName;
  private Date lastLogin;
  @Masked
  @JsonManagedReference
  private Set<KameHouseRoleDto> authorities;
  private boolean accountNonExpired = true;
  private boolean accountNonLocked = true;
  private boolean credentialsNonExpired = true;
  private boolean enabled = true;

  @Override
  public KameHouseUser buildEntity() {
    KameHouseUser entity = new KameHouseUser();
    entity.setId(getId());
    entity.setUsername(getUsername());
    entity.setPassword(PasswordUtils.generateHashedPassword(getPassword()));
    entity.setEmail(getEmail());
    entity.setFirstName(getFirstName());
    entity.setLastName(getLastName());
    entity.setLastLogin(getLastLogin());
    if (authorities != null) {
      Set<KameHouseRole> authoritiesEntity = authorities.stream()
          .map(dto -> {
            KameHouseRole role = dto.buildEntity();
            role.setKameHouseUser(entity);
            return role;
          })
          .collect(Collectors.toSet());
      entity.setAuthorities(authoritiesEntity);
    }
    entity.setAccountNonExpired(isAccountNonExpired());
    entity.setAccountNonLocked(isAccountNonLocked());
    entity.setCredentialsNonExpired(isCredentialsNonExpired());
    entity.setEnabled(isEnabled());
    return entity;
  }

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

  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public Set<KameHouseRoleDto> getAuthorities() {
    return authorities;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public void setAuthorities(Set<KameHouseRoleDto> authorities) {
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
    if (obj instanceof KameHouseUserDto) {
      final KameHouseUserDto other = (KameHouseUserDto) obj;
      return new EqualsBuilder()
          .append(id, other.getId())
          .append(username, other.getUsername())
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
