package com.nicobrest.kamehouse.commons.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.model.TestEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for the AbstractCrudDaoJpa though a TestEntity dao.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
class AbstractCrudDaoJpaUnitTest {

  @Autowired
  private TestEntityCrudDaoJpa testEntityCrudDaoJpa;

  @Autowired
  private EntityManagerFactory entityManagerFactory;

  @Mock
  private EntityManagerFactory entityManagerFactoryMock;

  @Mock
  private EntityManager entityManagerMock;

  private Exception persistenceException = new PersistenceException("Test Exception");
  private Exception illegalArgumentException = new IllegalArgumentException("Test Exception");

  @BeforeEach
  public void setup() {
    setupTestData();
    MockitoAnnotations.openMocks(this);
    Mockito.reset(entityManagerFactoryMock, entityManagerMock);
  }

  /**
   * create entity test.
   */
  @Test
  void createTest() {
    TestEntity testEntity = new TestEntity();
    testEntity.setName("gohan");

    Long id = testEntityCrudDaoJpa.create(testEntity);
    assertNotNull(id);
  }

  /**
   * create entity conflict test.
   */
  @Test
  void createConflictExceptionTest() {
    TestEntity testEntity = new TestEntity();
    testEntity.setName("goku");
    assertThrows(
        KameHouseConflictException.class,
        () -> {
          testEntityCrudDaoJpa.create(testEntity);
        });
  }

  /**
   * find by attribute test.
   */
  @Test
  void findByAttributeTest() {
    TestEntity testEntity = testEntityCrudDaoJpa.findByAttribute(TestEntity.class, "name", "goku");
    assertNotNull(testEntity);
  }

  /**
   * read entity test.
   */
  @Test
  void readTest() {
    TestEntity testEntity = testEntityCrudDaoJpa.read(999999L);
    assertNotNull(testEntity);
  }

  /**
   * read not found test.
   */
  @Test
  void readNotFoundTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          testEntityCrudDaoJpa.read(888888L);
        });
  }

  /**
   * read all test.
   */
  @Test
  void readAllTest() {
    List<TestEntity> testEntities = testEntityCrudDaoJpa.readAll();
    assertEquals(1, testEntities.size());
  }

  /**
   * read all with filter params test.
   */
  @Test
  void readAllWithFilterParamsTest() {
    List<TestEntity> testEntities = testEntityCrudDaoJpa.readAll(30, "id", false);
    assertEquals(1, testEntities.size());
  }

  /**
   * update entity test.
   */
  @Test
  void updateTest() {
    TestEntity testEntity = new TestEntity();
    testEntity.setId(999999L);
    testEntity.setName("goku");

    Assertions.assertDoesNotThrow(() -> {
      testEntityCrudDaoJpa.update(testEntity);
    });
  }

  /**
   * update entity not found test.
   */
  @Test
  void updateNotFoundTest() {
    TestEntity testEntity = new TestEntity();
    testEntity.setId(888888L);
    testEntity.setName("goku");
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          testEntityCrudDaoJpa.update(testEntity);
        });
  }

  /**
   * delete entity test.
   */
  @Test
  void deleteTest() {
    TestEntity testEntity = testEntityCrudDaoJpa.delete(999999L);
    assertNotNull(testEntity);
  }

  /**
   * delete entity not found test.
   */
  @Test
  void deleteNotFoundTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          testEntityCrudDaoJpa.delete(888888L);
        });
  }

  /**
   * findAll PersistenceException test.
   */
  @Test
  void findAllPersistenceExceptionTest() {
    setupEntityManagerMock();
    persistenceException.initCause(
        new org.hibernate.exception.ConstraintViolationException("", new SQLException(), ""));
    when(entityManagerMock.getTransaction()).thenThrow(persistenceException);

    assertThrows(
        KameHouseConflictException.class,
        () -> {
          testEntityCrudDaoJpa.findAll(TestEntity.class);
        });
  }

  /**
   * findById PersistenceException test.
   */
  @Test
  void findByIdPersistenceExceptionTest() {
    setupEntityManagerMock();
    when(entityManagerMock.getTransaction()).thenThrow(persistenceException);

    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          testEntityCrudDaoJpa.findById(TestEntity.class, 1L);
        });
  }

  /**
   * findById IllegalArgumentException test.
   */
  @Test
  void findByIdIllegalArgumentExceptionTest() {
    setupEntityManagerMock();
    when(entityManagerMock.getTransaction()).thenThrow(illegalArgumentException);

    assertThrows(
        KameHouseBadRequestException.class,
        () -> {
          testEntityCrudDaoJpa.findById(TestEntity.class, 1L);
        });
  }

  /**
   * findByUsername IllegalArgumentException test. TestEntity doesn't have a username attribute.
   */
  @Test
  void findByUsernameIllegalArgumentExceptionTest() {
    assertThrows(
        KameHouseBadRequestException.class,
        () -> {
          testEntityCrudDaoJpa.findByUsername(TestEntity.class, "goku");
        });
  }

  /**
   * findByEmail IllegalArgumentException test. TestEntity doesn't have an email attribute.
   */
  @Test
  void findByEmailIllegalArgumentExceptionTest() {
    assertThrows(
        KameHouseBadRequestException.class,
        () -> {
          testEntityCrudDaoJpa.findByEmail(TestEntity.class, "goku@dbz.com");
        });
  }

  /**
   * findByAttribute PersistenceException test.
   */
  @Test
  void findByAttributePersistenceExceptionTest() {
    setupEntityManagerMock();
    when(entityManagerMock.getTransaction()).thenThrow(persistenceException);

    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          testEntityCrudDaoJpa.findByAttribute(TestEntity.class, "", "");
        });
  }

  /**
   * mergeEntityInRepository test.
   */
  @Test
  void mergeEntityInRepositoryTest() {
    TestEntity entity = testEntityCrudDaoJpa.mergeEntityInRepository(
        testEntityCrudDaoJpa.findAll(TestEntity.class).get(0));

    assertEquals(999999L, entity.getId());
    assertEquals("goku", entity.getName());
  }

  /**
   * updateEntityInRepository PersistenceException test.
   */
  @Test
  void updateEntityInRepositoryPersistenceExceptionTest() {
    setupEntityManagerMock();
    when(entityManagerMock.getTransaction()).thenThrow(persistenceException);
    TestEntity testEntity = new TestEntity();
    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          testEntityCrudDaoJpa.updateEntityInRepository(TestEntity.class, testEntity, 1L);
        });
  }

  /**
   * updateEntityInRepository IllegalArgumentException test.
   */
  @Test
  void updateEntityInRepositoryIllegalArgumentExceptionTest() {
    setupEntityManagerMock();
    when(entityManagerMock.getTransaction()).thenThrow(illegalArgumentException);
    TestEntity testEntity = new TestEntity();
    assertThrows(
        KameHouseBadRequestException.class,
        () -> {
          testEntityCrudDaoJpa.updateEntityInRepository(TestEntity.class, testEntity, 1L);
        });
  }

  /**
   * TODO: private methods tests: test them through other public/protected methods
   *  - addEntityToRepository handlePersistentException
   *  - handlePersistentException NoResultException and KameHouseServerErrorException
   *  - handleIllegalArgumentException
   *  after adding the previous tests, check the coverage and see if these are covered already
   */

  /**
   * Setup test data in the hsql db.
   */
  private void setupTestData() {
    testEntityCrudDaoJpa.setEntityManagerFactory(entityManagerFactory);
    String clearTableSql = "DELETE FROM TEST_ENTITY";
    String insertEntitySql = "INSERT INTO TEST_ENTITY (id, name) VALUES (999999, 'goku')";
    EntityManager em = testEntityCrudDaoJpa.getEntityManager();
    em.getTransaction().begin();
    em.createNativeQuery(clearTableSql).executeUpdate();
    em.createNativeQuery(insertEntitySql).executeUpdate();
    em.getTransaction().commit();
    em.close();
  }

  /**
   * Setup the entity manager mock for the tests.
   */
  private void setupEntityManagerMock() {
    testEntityCrudDaoJpa.setEntityManagerFactory(entityManagerFactoryMock);
    when(entityManagerFactoryMock.createEntityManager()).thenReturn(entityManagerMock);
  }
}
