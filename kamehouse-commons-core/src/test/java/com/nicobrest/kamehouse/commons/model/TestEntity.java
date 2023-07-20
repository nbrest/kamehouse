package com.nicobrest.kamehouse.commons.model;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Test Entity to test all the abstract classes.
 */
@Entity
@Table(name = "test_entity")
public class TestEntity implements KameHouseEntity<TestEntityDto>, Serializable {

  private static final long serialVersionUID = 159367676076449689L;

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "name", unique = true, nullable = false)
  private String name;

  @Override
  public TestEntityDto buildDto() {
    TestEntityDto dto = new TestEntityDto();
    dto.setId(getId());
    dto.setName(getName());
    return dto;
  }

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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestEntity that = (TestEntity) o;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
