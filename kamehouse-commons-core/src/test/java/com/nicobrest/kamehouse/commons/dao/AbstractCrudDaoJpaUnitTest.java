package com.nicobrest.kamehouse.commons.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.model.TestEntity;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for the AbstractCrudDaoJpa though a TestEntity dao.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class AbstractCrudDaoJpaUnitTest {

  @Autowired
  private TestEntityCrudDaoJpa testEntityCrudDaoJpa;

  @BeforeEach
  public void setup() {
    setupTestData();
  }

  /**
   * create entity test.
   */
  @Test
  public void createTest() {
    TestEntity testEntity = new TestEntity();
    testEntity.setName("gohan");

    Long id = testEntityCrudDaoJpa.create(testEntity);
    assertNotNull(id);
  }

  /**
   * create entity conflict test.
   */
  @Test
  public void createConflictExceptionTest() {
    assertThrows(
        KameHouseConflictException.class,
        () -> {
          TestEntity testEntity = new TestEntity();
          testEntity.setName("goku");

          testEntityCrudDaoJpa.create(testEntity);
        });
  }

  /**
   * find by attribute test.
   */
  @Test
  public void findByAttributeTest() {
    TestEntity testEntity = testEntityCrudDaoJpa.findByAttribute(TestEntity.class, "name", "goku");
    assertNotNull(testEntity);
  }

  /**
   * read entity test.
   */
  @Test
  public void readTest() {
    TestEntity testEntity = testEntityCrudDaoJpa.read(999999L);
    assertNotNull(testEntity);
  }

  /**
   * read not found test.
   */
  @Test
  public void readNotFoundTest() {
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
  public void readAllTest() {
    List<TestEntity> testEntities = testEntityCrudDaoJpa.readAll();
    assertEquals(1, testEntities.size());
  }

  /**
   * update entity test.
   */
  @Test
  public void updateTest() {
    TestEntity testEntity = new TestEntity();
    testEntity.setId(999999L);
    testEntity.setName("goku");

    testEntityCrudDaoJpa.update(testEntity);
    // no exception expected
  }

  /**
   * update entity not found test.
   */
  @Test
  public void updateNotFoundTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          TestEntity testEntity = new TestEntity();
          testEntity.setId(888888L);
          testEntity.setName("goku");

          testEntityCrudDaoJpa.update(testEntity);
        });
  }

  /**
   * delete entity test.
   */
  @Test
  public void deleteTest() {
    TestEntity testEntity = testEntityCrudDaoJpa.delete(999999L);
    assertNotNull(testEntity);
  }

  /**
   * delete entity not found test.
   */
  @Test
  public void deleteNotFoundTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          testEntityCrudDaoJpa.delete(888888L);
        });
  }

  /**
   * Setup test data in the hsql db.
   */
  private void setupTestData() {
    String clearTableSql = "DELETE FROM TEST_ENTITY";
    String insertEntitySql = "INSERT INTO TEST_ENTITY (id, name) VALUES (999999, 'goku')";
    EntityManager em = testEntityCrudDaoJpa.getEntityManager();
    em.getTransaction().begin();
    em.createNativeQuery(clearTableSql).executeUpdate();
    em.createNativeQuery(insertEntitySql).executeUpdate();
    em.getTransaction().commit();
    em.close();
  }
}
