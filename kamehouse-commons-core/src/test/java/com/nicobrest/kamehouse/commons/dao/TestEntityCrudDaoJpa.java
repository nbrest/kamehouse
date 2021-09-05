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
  protected void updateEntityValues(TestEntity persistedEntity, TestEntity entity) {
    persistedEntity.setName(entity.getName());
  }
}
