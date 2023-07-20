package com.nicobrest.kamehouse.commons.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseRoleDto;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Class that represents a user in kamehouse.
 *
 * @author nbrest
 */
@Entity
@Table(name = "kamehouse_user")
public class KameHouseUser implements PasswordEntity<String>, KameHouseEntity<KameHouseUserDto>,
    UserDetails {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "username", unique = true, nullable = false)
  private String username;

  @Masked
  @Column(name = "password")
  private String password;

  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "last_login")
  private Date lastLogin;

  /* Spring Security related fields
   *
   * In this case, because I know the amount of roles to retrieve is always going to be limited,
   * it's ok to use FetchType.EAGER, but in a dataset that I know will grow to thousands of
   * records or more, I should use FetchType.LAZY.
   * */
  @Masked
  @OneToMany(
      mappedBy = "kameHouseUser",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonManagedReference
  private Set<KameHouseRole> authorities;

  @Column(name = "account_non_expired")
  private boolean accountNonExpired = true;

  @Column(name = "account_non_locked")
  private boolean accountNonLocked = true;

  @Column(name = "credentials_non_expired")
  private boolean credentialsNonExpired = true;

  @Column(name = "enabled")
  private boolean enabled = true;

  @Override
  public KameHouseUserDto buildDto() {
    KameHouseUserDto dto = new KameHouseUserDto();
    dto.setId(getId());
    dto.setUsername(getUsername());
    dto.setPassword(getPassword());
    dto.setEmail(getEmail());
    dto.setFirstName(getFirstName());
    dto.setLastName(getLastName());
    dto.setLastLogin(getLastLogin());
    if (authorities != null) {
      Set<KameHouseRoleDto> authoritiesDto = authorities.stream()
          .map(entity -> {
            KameHouseRoleDto roleDto = entity.buildDto();
            roleDto.setKameHouseUser(dto);
            return roleDto;
          })
          .collect(Collectors.toSet());
      dto.setAuthorities(authoritiesDto);
    }
    dto.setAccountNonExpired(isAccountNonExpired());
    dto.setAccountNonLocked(isAccountNonLocked());
    dto.setCredentialsNonExpired(isCredentialsNonExpired());
    dto.setEnabled(isEnabled());
    return dto;
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
    } else {
      this.lastLogin = null;
    }
  }

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public Set<KameHouseRole> getAuthorities() {
    return authorities;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public void setAuthorities(Set<KameHouseRole> authorities) {
    this.authorities = authorities;
  }

  /**
   * Add an application role to the user. Important to set the current kamehouse user to that role
   * for hibernate mappings.
   */
  public void addAuthority(KameHouseRole kameHouseRole) {
    authorities.add(kameHouseRole);
    kameHouseRole.setKameHouseUser(this);
  }

  /**
   * Add an application role to the user. Important to set the kamehouse user to null for that role
   * for hibernate mappings.
   */
  public void removeAuthority(KameHouseRole kameHouseRole) {
    authorities.remove(kameHouseRole);
    kameHouseRole.setKameHouseUser(null);
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
    if (obj instanceof KameHouseUser) {
      final KameHouseUser other = (KameHouseUser) obj;
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
