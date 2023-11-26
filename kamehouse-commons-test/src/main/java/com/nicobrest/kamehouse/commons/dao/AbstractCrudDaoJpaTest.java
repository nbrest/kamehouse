package com.nicobrest.kamehouse.commons.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.model.KameHouseEntity;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseDto;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Abstract class to group common test functionality for CRUD Jpa DAOs.
 *
 * @author nbrest
 */
public abstract class AbstractCrudDaoJpaTest
    <E extends KameHouseEntity<D>, D extends KameHouseDto<E>> extends AbstractDaoJpaTest<E, D> {

  public static final Long INVALID_ID = 987987L;

  protected AbstractCrudDaoJpaTest(EntityManagerFactory entityManagerFactory) {
    super(entityManagerFactory);
  }

  /**
   * Get the entity class.
   */
  public abstract Class<E> getEntityClass();

  /**
   * Get crud DAO.
   */
  public abstract CrudDao<E> getCrudDao();

  /**
   * Get test utils.
   */
  public abstract TestUtils<E, D> getTestUtils();

  /**
   * Get crud DAO.
   */
  public abstract String[] getTablesToClear();

  /**
   * Update the entity properties to execute the update test.
   */
  public abstract void updateEntity(E entity);

  /**
   * Update the entity properties to generate an error executing the update.
   */
  public abstract void updateEntityServerError(E entity);

  /**
   * Override in concrete classes when custom init before tests is required.
   */
  public void initBeforeTest() {

  }

  /**
   * Check if the entity has unique constraints. By default true, it can be overridden to false in
   * the concrete subclasses.
   */
  public boolean hasUniqueConstraints() {
    return true;
  }

  /**
   * Clears data from the repository before each test.
   */
  @BeforeEach
  void beforeTest() {
    testUtils = getTestUtils();
    testUtils.initTestData();
    testUtils.removeIds();

    for (String table : getTablesToClear()) {
      clearTable(table);
    }
    initBeforeTest();
  }

  /**
   * Creates entity test.
   */
  @Test
  void createTest() {
    E entity = testUtils.getSingleTestData();

    Long createdId = getCrudDao().create(entity);

    entity.setId(createdId);
    E createdEntity = findById(getEntityClass(), createdId);
    testUtils.assertEqualsAllAttributes(entity, createdEntity);
  }

  /**
   * Creates entity ConflictException test.
   */
  @Test
  void createConflictExceptionTest() {
    if (!hasUniqueConstraints()) {
      logger.info("Skipping createConflictExceptionTest");
      return;
    }
    assertThrows(
        KameHouseConflictException.class,
        () -> {
          getCrudDao().create(testUtils.getSingleTestData());

          testUtils.initTestData();

          getCrudDao().create(testUtils.getSingleTestData());
        });
  }

  /**
   * Reads entity test.
   */
  @Test
  void readTest() {
    E entity = testUtils.getSingleTestData();
    persistEntityInRepository(entity);

    E returnedEntity = getCrudDao().read(entity.getId());

    testUtils.assertEqualsAllAttributes(entity, returnedEntity);
  }

  /**
   * Reads all entities test.
   */
  @Test
  void readAllTest() {
    List<E> entitiesList = testUtils.getTestDataList();
    for (E entity : entitiesList) {
      persistEntityInRepository(entity);
    }

    List<E> returnedList = getCrudDao().readAll();

    testUtils.assertEqualsAllAttributesList(entitiesList, returnedList);
  }

  /**
   * Updates entity test.
   */
  @Test
  void updateTest() {
    E entity = testUtils.getSingleTestData();
    persistEntityInRepository(entity);
    updateEntity(entity);

    getCrudDao().update(entity);

    E returnedEntity = findById(getEntityClass(), entity.getId());
    testUtils.assertEqualsAllAttributes(entity, returnedEntity);
  }

  /**
   * Updates entity NotFoundException test.
   */
  @Test
  void updateNotFoundExceptionTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          E entity = testUtils.getSingleTestData();
          entity.setId(INVALID_ID);

          getCrudDao().update(entity);
        });
  }

  /**
   * Tests updating an existing user in the repository Exception flows.
   */
  @Test
  void updateServerErrorExceptionTest() {
    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          E entity = testUtils.getSingleTestData();
          persistEntityInRepository(entity);
          updateEntityServerError(entity);

          getCrudDao().update(entity);
        });
  }

  /**
   * Deletes entity test.
   */
  @Test
  void deleteTest() {
    E entity = testUtils.getSingleTestData();
    persistEntityInRepository(entity);

    E deletedEntity = getCrudDao().delete(entity.getId());

    testUtils.assertEqualsAllAttributes(entity, deletedEntity);
  }

  /**
   * Deletes entity NotFoundException test.
   */
  @Test
  void deleteNotFoundExceptionTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          getCrudDao().delete(INVALID_ID);
        });
  }

  /**
   * Returns a string that's too long and with invalid characters to fail the persistence.
   */
  protected String getInvalidString() {
    return RandomStringUtils.random(10000);
  }
}
