package com.nicobrest.kamehouse.commons.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.commons.model.TestEntity;
import com.nicobrest.kamehouse.commons.model.TestEntityDto;
import com.nicobrest.kamehouse.commons.model.TestEntityDtoTranslator;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Test service to test AbstractCrudService.
 */
@Service
public class TestEntityCrudService extends AbstractCrudService<TestEntity, TestEntityDto> {

  private CrudDao<TestEntity> crudDao = new CrudDaoMock();

  @Override
  public CrudDao<TestEntity> getCrudDao() {
    return crudDao;
  }

  @Override
  public KameHouseDtoTranslator<TestEntity, TestEntityDto> getDtoTranslator() {
    return new TestEntityDtoTranslator();
  }

  @Override
  protected void validate(TestEntity entity) {
    // Nothing to do
  }

  /**
   * TestEntity CrudDao mock.
   */
  public static class CrudDaoMock implements CrudDao<TestEntity> {

    @Override
    public Long create(TestEntity entity) {
      return 1L;
    }

    @Override
    public TestEntity read(Long id) {
      TestEntity testEntity = new TestEntity();
      testEntity.setId(id);
      testEntity.setName("goku");
      return testEntity;
    }

    @Override
    public List<TestEntity> readAll() {
      return readAll(0, null, true);
    }

    @Override
    public List<TestEntity> readAll(Integer maxRows, String sortColumn, Boolean sortAscending) {
      TestEntity testEntity = new TestEntity();
      testEntity.setId(1L);
      testEntity.setName("goku");
      return Collections.singletonList(testEntity);
    }

    @Override
    public void update(TestEntity entity) {
      // do nothing
    }

    @Override
    public TestEntity delete(Long id) {
      TestEntity testEntity = new TestEntity();
      testEntity.setId(id);
      testEntity.setName("goku-deleted");
      return testEntity;
    }
  }
}
