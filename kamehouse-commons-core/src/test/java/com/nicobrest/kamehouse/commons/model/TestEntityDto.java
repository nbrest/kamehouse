package com.nicobrest.kamehouse.commons.model;

import com.nicobrest.kamehouse.commons.model.dto.KameHouseDto;
import java.io.Serializable;
import java.util.Objects;

/**
 * Test Entity DTO to test the abstract classes.
 */
public class TestEntityDto implements KameHouseDto<TestEntity>, Serializable {

  private Long id;
  private String name;

  @Override
  public TestEntity buildEntity() {
    TestEntity entity = new TestEntity();
    entity.setId(getId());
    entity.setName(getName());
    return entity;
  }

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
