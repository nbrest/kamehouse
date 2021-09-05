package com.nicobrest.kamehouse.commons.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Abstract class to group test functionality for all CRUD services.
 *
 * @author nbrest
 */
public abstract class AbstractCrudServiceTest<E, D> {

  protected TestUtils<E, D> testUtils;

  /**
   * Get crud service.
   */
  public abstract CrudService<E, D> getCrudService();

  /**
   * Get crud DAO.
   */
  public abstract CrudDao<E> getCrudDao();

  /**
   * Get test utils.
   */
  public abstract TestUtils<E, D> getTestUtils();

  /**
   * Override in concrete classes when custom init before tests is required.
   */
  public void initBeforeTest() {

  }

  /**
   * Resets mock objects and initializes test repository.
   */
  @BeforeEach
  public void beforeTest() {
    testUtils = getTestUtils();
    testUtils.initTestData();

    MockitoAnnotations.openMocks(this);
    initBeforeTest();
    Mockito.reset(getCrudDao());
  }

  /**
   * Creates entity test.
   */
  @Test
  public void createTest() {
    E entity = testUtils.getSingleTestData();
    D dto = testUtils.getTestDataDto();
    Identifiable identifiableEntity = (Identifiable) entity;
    Mockito.doReturn(identifiableEntity.getId()).when(getCrudDao()).create(entity);

    Long returnedId = getCrudService().create(dto);

    assertEquals(identifiableEntity.getId(), returnedId);
    verify(getCrudDao(), times(1)).create(entity);
  }

  /**
   * Reads entity test.
   */
  @Test
  public void readTest() {
    E entity = testUtils.getSingleTestData();
    Identifiable identifiableEntity = (Identifiable) entity;
    when(getCrudDao().read(identifiableEntity.getId())).thenReturn(entity);

    E returnedEntity = getCrudService().read(identifiableEntity.getId());

    testUtils.assertEqualsAllAttributes(entity, returnedEntity);
    verify(getCrudDao(), times(1)).read(identifiableEntity.getId());
  }

  /**
   * Reads all entities test.
   */
  @Test
  public void readAllTest() {
    List<E> entityList = testUtils.getTestDataList();
    when(getCrudDao().readAll()).thenReturn(entityList);

    List<E> returnedList = getCrudService().readAll();

    testUtils.assertEqualsAllAttributesList(entityList, returnedList);
    verify(getCrudDao(), times(1)).readAll();
  }

  /**
   * Updates entity test.
   */
  @Test
  public void updateTest() {
    E entity = testUtils.getSingleTestData();
    D dto = testUtils.getTestDataDto();
    Mockito.doNothing().when(getCrudDao()).update(entity);

    getCrudService().update(dto);

    verify(getCrudDao(), times(1)).update(entity);
  }

  /**
   * Deletes entity test.
   */
  @Test
  public void deleteTest() {
    E entity = testUtils.getSingleTestData();
    Identifiable identifiableEntity = (Identifiable) entity;
    when(getCrudDao().delete(identifiableEntity.getId())).thenReturn(entity);

    E deletedEntity = getCrudService().delete(identifiableEntity.getId());

    testUtils.assertEqualsAllAttributes(entity, deletedEntity);
    verify(getCrudDao(), times(1)).delete(identifiableEntity.getId());
  }
}
