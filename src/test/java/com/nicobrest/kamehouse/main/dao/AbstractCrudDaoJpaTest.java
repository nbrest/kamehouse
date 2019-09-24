package com.nicobrest.kamehouse.main.dao;

import static org.junit.Assert.assertEquals;

public abstract class AbstractCrudDaoJpaTest extends AbstractDaoJpaTest {

  protected <T> void createTest(CrudDao<T> dao, Class<T> clazz, T entity) {
    Long createdId = dao.create(entity);
    Identifiable identifiableEntity = (Identifiable) entity;
    identifiableEntity.setId(createdId);
    
    T createdEntity = findById(clazz, createdId);
    assertEquals(entity, createdEntity);
  }
}
