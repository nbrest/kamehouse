package com.nicobrest.kamehouse.commons.model.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO for kamehouse roles.
 *
 * @author nbrest
 */
public class KameHouseRoleDto implements Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private String name;
  @JsonBackReference
  private KameHouseUserDto kameHouseUser;

  @Override
  public Long getId() {
    return id;
  }

  @Override
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
  public KameHouseUserDto getKameHouseUser() {
    return kameHouseUser;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public void setKameHouseUser(KameHouseUserDto kameHouseUser) {
    this.kameHouseUser = kameHouseUser;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(name).toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof KameHouseRoleDto other) {
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
