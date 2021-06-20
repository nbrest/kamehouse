package com.nicobrest.kamehouse.commons.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.model.TestEntity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import javax.persistence.EntityManager;

/**
 * Unit tests for the AbstractCrudDaoJpa though a TestEntity dao.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class AbstractCrudDaoJpaUnitTest {

  @Autowired
  private TestEntityCrudDaoJpa testEntityCrudDaoJpa;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
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

    Long id = testEntityCrudDaoJpa.create(TestEntity.class, testEntity);
    assertNotNull(id);
  }

  /**
   * create entity conflict test.
   */
  @Test
  public void createConflictExceptionTest() {
    thrown.expect(KameHouseConflictException.class);
    TestEntity testEntity = new TestEntity();
    testEntity.setName("goku");

    testEntityCrudDaoJpa.create(TestEntity.class, testEntity);
  }

  /**
   * find by attribute test.
   */
  @Test
  public void findByAttributeTest() {
    TestEntity testEntity = testEntityCrudDaoJpa.findByAttribute(TestEntity.class, "name","goku");
    assertNotNull(testEntity);
  }

  /**
   * read entity test.
   */
  @Test
  public void readTest() {
    TestEntity testEntity = testEntityCrudDaoJpa.read(TestEntity.class, 999999L);
    assertNotNull(testEntity);
  }

  /**
   * read not found test.
   */
  @Test
  public void readNotFoundTest() {
    thrown.expect(KameHouseNotFoundException.class);

    testEntityCrudDaoJpa.read(TestEntity.class, 888888L);
  }

  /**
   * read all test.
   */
  @Test
  public void readAllTest() {
    List<TestEntity> testEntities = testEntityCrudDaoJpa.readAll(TestEntity.class);
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

    testEntityCrudDaoJpa.update(TestEntity.class, testEntity);
    // no exception expected
  }

  /**
   * update entity not found test.
   */
  @Test
  public void updateNotFoundTest() {
    thrown.expect(KameHouseNotFoundException.class);
    TestEntity testEntity = new TestEntity();
    testEntity.setId(888888L);
    testEntity.setName("goku");

    testEntityCrudDaoJpa.update(TestEntity.class, testEntity);
  }

  /**
   * delete entity test.
   */
  @Test
  public void deleteTest() {
    TestEntity testEntity = testEntityCrudDaoJpa.delete(TestEntity.class, 999999L);
    assertNotNull(testEntity);
  }

  /**
   * delete entity not found test.
   */
  @Test
  public void deleteNotFoundTest() {
    thrown.expect(KameHouseNotFoundException.class);

    testEntityCrudDaoJpa.delete(TestEntity.class, 888888L);
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
