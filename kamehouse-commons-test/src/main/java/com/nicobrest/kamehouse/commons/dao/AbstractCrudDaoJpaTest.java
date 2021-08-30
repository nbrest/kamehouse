package com.nicobrest.kamehouse.commons.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;

import java.util.List;

/**
 * Abstract class to group common test functionality for CRUD Jpa DAOs.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractCrudDaoJpaTest<T, D> extends AbstractDaoJpaTest<T, D> {

  public static final Long INVALID_ID = 987987L;

  /**
   * Creates entity test.
   */
  protected void createTest(CrudDao<T> dao, Class<T> clazz) {
    T entity = testUtils.getSingleTestData();
    Long createdId = dao.create(entity);

    Identifiable identifiableEntity = (Identifiable) entity;
    identifiableEntity.setId(createdId);
    T createdEntity = findById(clazz, createdId);
    testUtils.assertEqualsAllAttributes(entity, createdEntity);
  }

  /**
   * Creates entity ConflictException test.
   */
  public void createConflictExceptionTest(CrudDao<T> dao) {
    assertThrows(KameHouseConflictException.class, () -> {
      dao.create(testUtils.getSingleTestData());
      testUtils.initTestData();

      dao.create(testUtils.getSingleTestData());
    });
  }

  /**
   * Reads entity test.
   */
  public void readTest(CrudDao<T> dao) {
    T entity = testUtils.getSingleTestData();
    persistEntityInRepository(entity);
    Identifiable identifiableEntity = (Identifiable) entity;

    T returnedEntity = dao.read(identifiableEntity.getId());

    testUtils.assertEqualsAllAttributes(entity, returnedEntity);
  }

  /**
   * Reads all entities test.
   */
  public void readAllTest(CrudDao<T> dao) {
    List<T> entitiesList = testUtils.getTestDataList();
    for (T entity : entitiesList) {
      persistEntityInRepository(entity);
    }

    List<T> returnedList = dao.readAll();

    testUtils.assertEqualsAllAttributesList(entitiesList, returnedList);
  }

  /**
   * Updates entity test.
   */
  public void updateTest(CrudDao<T> dao, Class<T> clazz, T updatedEntity) {
    T entity = testUtils.getSingleTestData();
    persistEntityInRepository(entity);
    Identifiable identifiableEntity = (Identifiable) entity;
    Identifiable identifiableUpdatedEntity = (Identifiable) updatedEntity;
    identifiableUpdatedEntity.setId(identifiableEntity.getId());

    dao.update(updatedEntity);

    T returnedEntity = findById(clazz, identifiableUpdatedEntity.getId());
    testUtils.assertEqualsAllAttributes(updatedEntity, returnedEntity);
  }

  /**
   * Updates entity NotFoundException test.
   */
  public void updateNotFoundExceptionTest(CrudDao<T> dao, Class<T> clazz) {
    assertThrows(KameHouseNotFoundException.class, () -> {
      T entity = testUtils.getSingleTestData();
      Identifiable identifiableEntity = (Identifiable) entity;
      identifiableEntity.setId(INVALID_ID);

      dao.update(entity);
    });
  }

  /**
   * Deletes entity test.
   */
  public void deleteTest(CrudDao<T> dao) {
    T entity = testUtils.getSingleTestData();
    persistEntityInRepository(entity);
    Identifiable identifiableEntity = (Identifiable) entity;

    T deletedEntity = dao.delete(identifiableEntity.getId());

    testUtils.assertEqualsAllAttributes(entity, deletedEntity);
  }

  /**
   * Deletes entity NotFoundException test.
   */
  public void deleteNotFoundExceptionTest(CrudDao<T> dao, Class<T> clazz) {
    assertThrows(KameHouseNotFoundException.class, () -> {
      dao.delete(INVALID_ID);
    });
  }
}
