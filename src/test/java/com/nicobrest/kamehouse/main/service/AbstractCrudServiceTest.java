package com.nicobrest.kamehouse.main.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nicobrest.kamehouse.main.dao.CrudDao;
import com.nicobrest.kamehouse.main.dao.Identifiable;

import org.mockito.Mockito;

/**
 * Abstract class to group test functionality for all CRUD services.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractCrudServiceTest {

  /**
   * Create entity test.
   */
  protected <E, D> void createTest(CrudService<E, D> service, CrudDao<E> dao, E entity, D dto) {
    Identifiable identifiableEntity = (Identifiable) entity;
    Mockito.doReturn(identifiableEntity.getId()).when(dao).create(entity);

    Long returnedId = service.create(dto);

    assertEquals(identifiableEntity.getId(), returnedId);
    verify(dao, times(1)).create(entity);
  }
}
