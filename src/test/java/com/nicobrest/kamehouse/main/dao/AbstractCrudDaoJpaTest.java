package com.nicobrest.kamehouse.main.dao;

import static org.junit.Assert.assertEquals;

/**
 * Abstract class to group common test functionality for CRUD Jpa DAOs.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractCrudDaoJpaTest extends AbstractDaoJpaTest {

  /**
   * Create entity test.
   */
  protected <T> void createTest(CrudDao<T> dao, Class<T> clazz, T entity) {
    Long createdId = dao.create(entity);
    
    Identifiable identifiableEntity = (Identifiable) entity;
    identifiableEntity.setId(createdId);
    T createdEntity = findById(clazz, createdId);
    assertEquals(entity, createdEntity);
  }
}
