package com.nicobrest.kamehouse.commons.model.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO for kamehouse roles.
 *
 * @author nbrest
 */
public class KameHouseRoleDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private String name;
  @JsonBackReference private KameHouseUserDto kameHouseUser;

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

  public KameHouseUserDto getKameHouseUser() {
    return kameHouseUser;
  }

  public void setKameHouseUser(KameHouseUserDto kameHouseUser) {
    this.kameHouseUser = kameHouseUser;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(name).toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof KameHouseRoleDto) {
      final KameHouseRoleDto other = (KameHouseRoleDto) obj;
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
