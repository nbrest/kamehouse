package com.nicobrest.kamehouse.commons.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
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
 * Class that represents a user's ROLE in kamehouse. The current role definitions are:
 *
 * <p>
 * ROLE_KAMISAMA: admin
 * ROLE_SAIYAJIN: user
 * ROLE_NAMEKIAN: guest
 * ROLE_ANONYMOUS: unauthenticated user (default by spring security)
 * </p>
 *
 * @author nbrest
 *
 */
@Entity
@Table(name = "kamehouse_role")
public class KameHouseRole implements GrantedAuthority {
 
  private static final long serialVersionUID = 1L;
  
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  
  @Column(name = "name", nullable = false)
  private String name;
  
  @ManyToOne(optional = false)
  @JoinColumn(name = "kamehouse_user_id")
  @JsonBackReference
  private KameHouseUser kameHouseUser;
  
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
  
  public KameHouseUser getKameHouseUser( ) {
    return kameHouseUser;
  }
  
  public void setKameHouseUser(KameHouseUser kameHouseUser) {
    this.kameHouseUser = kameHouseUser;
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
    if (obj instanceof KameHouseRole) {
      final KameHouseRole other = (KameHouseRole) obj;
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
