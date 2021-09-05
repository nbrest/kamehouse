package com.nicobrest.kamehouse.commons.dao;

import com.nicobrest.kamehouse.commons.model.TestEntity;
import org.springframework.stereotype.Repository;

/**
 * Test CrudDaoJpa to test the AbstractCrudDaoJpa.
 */
@Repository
public class TestEntityCrudDaoJpa extends AbstractCrudDaoJpa<TestEntity> {

  @Override
  public Class<TestEntity> getEntityClass() {
    return TestEntity.class;
  }

  @Override
  protected <T> void updateEntityValues(T persistedEntity, T entity) {
    TestEntity persistedTestEntity = (TestEntity) persistedEntity;
    TestEntity entityTestEntity = (TestEntity) entity;
    persistedTestEntity.setName(entityTestEntity.getName());
  }
}
