package com.nicobrest.kamehouse.commons.dao;

import com.nicobrest.kamehouse.commons.model.TestEntity;
import org.springframework.stereotype.Service;

/**
 * Test CrudDaoJpa to test the AbstractCrudDaoJpa.
 */
@Service
public class TestEntityCrudDaoJpa extends AbstractCrudDaoJpa {

  @Override
  protected <T> void updateEntityValues(T persistedEntity, T entity) {
    TestEntity persistedTestEntity = (TestEntity) persistedEntity;
    TestEntity entityTestEntity = (TestEntity) entity;
    persistedTestEntity.setName(entityTestEntity.getName());
  }
}
