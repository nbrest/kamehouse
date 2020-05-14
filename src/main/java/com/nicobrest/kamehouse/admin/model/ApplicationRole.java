package com.nicobrest.kamehouse.admin.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nicobrest.kamehouse.main.utils.JsonUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Class that represents a user's ROLE in the application.
 * 
 * @author nbrest
 *
 */
@Entity
@Table(name = "application_role")
public class ApplicationRole implements GrantedAuthority {
 
  private static final long serialVersionUID = 1L;
  
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  
  @Column(name = "name", nullable = false)
  private String name;
  
  @ManyToOne(optional = false)
  @JoinColumn(name = "application_user_id")
  @JsonBackReference
  private ApplicationUser applicationUser;
  
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
  
  public ApplicationUser getApplicationUser( ) {
    return applicationUser;
  }
  
  public void setApplicationUser(ApplicationUser applicationUser) {
    this.applicationUser = applicationUser;
  }

  @JsonIgnore
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
    return JsonUtils.toJsonString(this, super.toString());
  }
}
