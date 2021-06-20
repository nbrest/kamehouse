package com.nicobrest.kamehouse.commons.service;

import static org.junit.Assert.assertNotNull;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.model.TestEntity;
import com.nicobrest.kamehouse.commons.model.TestEntityDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Unit tests for the AbstractCrudService through a TestEntity service.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class AbstractCrudServiceUnitTest {

  @Autowired
  private TestEntityCrudService testEntityCrudService;
  private CrudDao<TestEntity> crudDao = new TestEntityCrudService.CrudDaoMock();

  /**
   * read entity test.
   */
  @Test
  public void readTest() {
    TestEntity testEntity = testEntityCrudService.read(crudDao, 1L);
    assertNotNull(testEntity);
  }

  /**
   * read all entities test.
   */
  @Test
  public void readAllTest() {
    List<TestEntity> testEntities = testEntityCrudService.readAll(crudDao);
    assertNotNull(testEntities);
  }

  /**
   * create entity test.
   */
  @Test
  public void createTest() {
    TestEntityDto testEntityDto = new TestEntityDto();
    testEntityDto.setName("goku");

    Long id = testEntityCrudService.create(crudDao, testEntityDto);
    assertNotNull(id);
  }

  /**
   * update entity test.
   */
  @Test
  public void updateTest() {
    TestEntityDto testEntityDto = new TestEntityDto();
    testEntityDto.setName("goku");

    testEntityCrudService.update(crudDao, testEntityDto);
    // No exception thrown
  }

  /**
   * delete entity test.
   */
  @Test
  public void deleteTest() {
    TestEntity testEntity = testEntityCrudService.delete(crudDao, 1L);
    assertNotNull(testEntity);
  }
}
