package com.nicobrest.kamehouse.commons.model;

import com.nicobrest.kamehouse.commons.dao.Identifiable;
import java.io.Serializable;
import java.util.Objects;

/** Test Entity DTO to test the abstract classes. */
public class TestEntityDto implements Identifiable, Serializable {

  private Long id;
  private String name;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestEntityDto that = (TestEntityDto) o;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }
}
