package com.nicobrest.kamehouse.commons.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import java.util.List;
import org.mockito.Mockito;

/**
 * Abstract class to group test functionality for all CRUD services.
 *
 * @author nbrest
 */
public abstract class AbstractCrudServiceTest<E, D> {

  protected TestUtils<E, D> testUtils;

  /** Creates entity test. */
  protected void createTest(CrudService<E, D> service, CrudDao<E> dao) {
    E entity = testUtils.getSingleTestData();
    D dto = testUtils.getTestDataDto();
    Identifiable identifiableEntity = (Identifiable) entity;
    Mockito.doReturn(identifiableEntity.getId()).when(dao).create(entity);

    Long returnedId = service.create(dto);

    assertEquals(identifiableEntity.getId(), returnedId);
    verify(dao, times(1)).create(entity);
  }

  /** Reads entity test. */
  protected void readTest(CrudService<E, D> service, CrudDao<E> dao) {
    E entity = testUtils.getSingleTestData();
    Identifiable identifiableEntity = (Identifiable) entity;
    when(dao.read(identifiableEntity.getId())).thenReturn(entity);

    E returnedEntity = service.read(identifiableEntity.getId());

    testUtils.assertEqualsAllAttributes(entity, returnedEntity);
    verify(dao, times(1)).read(identifiableEntity.getId());
  }

  /** Reads all entities test. */
  public void readAllTest(CrudService<E, D> service, CrudDao<E> dao) {
    List<E> entityList = testUtils.getTestDataList();
    when(dao.readAll()).thenReturn(entityList);

    List<E> returnedList = service.readAll();

    testUtils.assertEqualsAllAttributesList(entityList, returnedList);
    verify(dao, times(1)).readAll();
  }

  /** Updates entity test. */
  public void updateTest(CrudService<E, D> service, CrudDao<E> dao) {
    E entity = testUtils.getSingleTestData();
    D dto = testUtils.getTestDataDto();
    Mockito.doNothing().when(dao).update(entity);

    service.update(dto);

    verify(dao, times(1)).update(entity);
  }

  /** Deletes entity test. */
  public void deleteTest(CrudService<E, D> service, CrudDao<E> dao) {
    E entity = testUtils.getSingleTestData();
    Identifiable identifiableEntity = (Identifiable) entity;
    when(dao.delete(identifiableEntity.getId())).thenReturn(entity);

    E deletedEntity = service.delete(identifiableEntity.getId());

    testUtils.assertEqualsAllAttributes(entity, deletedEntity);
    verify(dao, times(1)).delete(identifiableEntity.getId());
  }
}
