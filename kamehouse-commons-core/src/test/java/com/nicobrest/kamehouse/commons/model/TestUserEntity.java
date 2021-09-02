package com.nicobrest.kamehouse.commons.model;

import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.io.Serializable;
import java.util.Objects;

/** Test User Entity to test password utils. */
public class TestUserEntity implements IdentifiablePasswordEntity<String>, Serializable {

  private static final long serialVersionUID = 159367676076449689L;

  private Long id;
  private String name;
  @Masked
  private String password;

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

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    TestUserEntity that = (TestUserEntity) other;
    return Objects.equals(id, that.id)
        && Objects.equals(name, that.name)
        && Objects.equals(password, that.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, password);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString(), true);
  }
}
