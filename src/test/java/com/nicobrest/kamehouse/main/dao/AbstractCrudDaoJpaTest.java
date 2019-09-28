package com.nicobrest.kamehouse.main.dao;

import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;

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
   * Create entity test.
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
   * Create entity ConflictException test.
   */
  public void createConflictExceptionTest(CrudDao<T> dao) {
    thrown.expect(KameHouseConflictException.class);
    thrown.expectMessage("ConstraintViolationException: Error inserting data");
    dao.create(testUtils.getSingleTestData());
    testUtils.initTestData();

    dao.create(testUtils.getSingleTestData());
  }

  /**
   * Read entity test.
   */
  public void readTest(CrudDao<T> dao) {
    T entity = testUtils.getSingleTestData();
    persistEntityInRepository(entity);
    Identifiable identifiableEntity = (Identifiable) entity;

    T returnedEntity = dao.read(identifiableEntity.getId());

    testUtils.assertEqualsAllAttributes(entity, returnedEntity);
  }

  /**
   * Read all entities test.
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
   * Update entity test.
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
   * Update entity NotFoundException test.
   */
  public void updateNotFoundExceptionTest(CrudDao<T> dao, Class<T> clazz) {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage(
        clazz.getSimpleName() + " with id " + INVALID_ID + " was not found in the repository.");
    T entity = testUtils.getSingleTestData();
    Identifiable identifiableEntity = (Identifiable) entity;
    identifiableEntity.setId(INVALID_ID);

    dao.update(entity);
  }

  /**
   * Delete entity test.
   */
  public void deleteTest(CrudDao<T> dao) {
    T entity = testUtils.getSingleTestData();
    persistEntityInRepository(entity);
    Identifiable identifiableEntity = (Identifiable) entity;

    T deletedEntity = dao.delete(identifiableEntity.getId());

    testUtils.assertEqualsAllAttributes(entity, deletedEntity);
  }

  /**
   * Delete entity NotFoundException test.
   */
  public void deleteNotFoundExceptionTest(CrudDao<T> dao, Class<T> clazz) {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage(
        clazz.getSimpleName() + " with id " + INVALID_ID + " was not found in the repository.");

    dao.delete(INVALID_ID);
  }
}
