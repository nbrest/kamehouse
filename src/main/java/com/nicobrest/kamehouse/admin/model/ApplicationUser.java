package com.nicobrest.kamehouse.admin.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nicobrest.kamehouse.main.dao.Identifiable;
import com.nicobrest.kamehouse.main.utils.JsonUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Class that represents a user in the application.
 * 
 * @author nbrest
 *
 */
@Entity
@Table(name = "application_user")
public class ApplicationUser implements Identifiable, UserDetails {
 
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "username", unique = true, nullable = false)
  private String username;

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
  @OneToMany(mappedBy = "applicationUser", fetch = FetchType.EAGER, cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonManagedReference
  private Set<ApplicationRole> authorities;

  @Column(name = "account_non_expired")
  private boolean accountNonExpired = true;

  @Column(name = "account_non_locked")
  private boolean accountNonLocked = true;

  @Column(name = "credentials_non_expired")
  private boolean credentialsNonExpired = true;

  @Column(name = "enabled")
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

  @Override
  public Set<ApplicationRole> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(Set<ApplicationRole> authorities) {
    this.authorities = authorities;
  }

  /**
   * Add an application role to the user. Important to set the current
   * application user to that role for hibernate mappings.
   */
  public void addAuthority(ApplicationRole applicationRole) {
    authorities.add(applicationRole);
    applicationRole.setApplicationUser(this);
  }

  /**
   * Add an application role to the user. Important to set the application user
   * to null for that role for hibernate mappings.
   */
  public void removeAuthority(ApplicationRole applicationRole) {
    authorities.remove(applicationRole);
    applicationRole.setApplicationUser(null);
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
    String[] hiddenFields = { "password", "authorities" };
    return JsonUtils.toJsonString(this, super.toString(), hiddenFields);
  }
}
