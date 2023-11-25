package com.nicobrest.kamehouse.commons.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.model.TestEntity;
import com.nicobrest.kamehouse.commons.model.TestEntityDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for the AbstractCrudService through a TestEntity service.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
class AbstractCrudServiceUnitTest {

  @Autowired
  private TestEntityCrudService testEntityCrudService;
  private CrudDao<TestEntity> crudDao = new TestEntityCrudService.CrudDaoMock();

  /**
   * read entity test.
   */
  @Test
  void readTest() {
    TestEntity testEntity = testEntityCrudService.read(1L);
    assertNotNull(testEntity);
  }

  /**
   * read all entities test.
   */
  @Test
  void readAllTest() {
    List<TestEntity> testEntities = testEntityCrudService.readAll();
    assertNotNull(testEntities);
  }

  /**
   * create entity test.
   */
  @Test
  void createTest() {
    TestEntityDto testEntityDto = new TestEntityDto();
    testEntityDto.setName("goku");

    Long id = testEntityCrudService.create(testEntityDto);
    assertNotNull(id);
  }

  /**
   * update entity test.
   */
  @Test
  void updateTest() {
    TestEntityDto testEntityDto = new TestEntityDto();
    testEntityDto.setName("goku");

    testEntityCrudService.update(testEntityDto);
    // No exception thrown
  }

  /**
   * delete entity test.
   */
  @Test
  void deleteTest() {
    TestEntity testEntity = testEntityCrudService.delete(1L);
    assertNotNull(testEntity);
  }
}
