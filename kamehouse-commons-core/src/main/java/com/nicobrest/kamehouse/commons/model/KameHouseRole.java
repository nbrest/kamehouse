package com.nicobrest.kamehouse.commons.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;

/**
 * Class that represents a user's ROLE in kamehouse. The current role definitions are:
 *
 * <p>ROLE_KAMISAMA: admin ROLE_SAIYAJIN: user ROLE_NAMEKIAN: guest ROLE_ANONYMOUS: unauthenticated
 * user (default by spring security)
 *
 * @author nbrest
 */
@Entity
@Table(name = "kamehouse_role")
public class KameHouseRole implements Identifiable, GrantedAuthority {

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

  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public KameHouseUser getKameHouseUser() {
    return kameHouseUser;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
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
    if (obj instanceof KameHouseRole other) {
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
