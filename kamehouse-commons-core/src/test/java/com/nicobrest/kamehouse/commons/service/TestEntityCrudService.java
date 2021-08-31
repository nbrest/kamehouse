package com.nicobrest.kamehouse.commons.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.model.TestEntity;
import com.nicobrest.kamehouse.commons.model.TestEntityDto;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/** Test service to test AbstractCrudService. */
@Component
public class TestEntityCrudService extends AbstractCrudService<TestEntity, TestEntityDto>
    implements CrudService<TestEntity, TestEntityDto> {

  private CrudDao<TestEntity> crudDao = new CrudDaoMock();

  @Override
  protected TestEntity getModel(TestEntityDto dto) {
    TestEntity testEntity = new TestEntity();
    testEntity.setId(dto.getId());
    testEntity.setName(dto.getName());
    return testEntity;
  }

  @Override
  protected void validate(TestEntity entity) {
    // Nothing to do
  }

  @Override
  public Long create(TestEntityDto dto) {
    return super.create(crudDao, dto);
  }

  @Override
  public TestEntity read(Long id) {
    return super.read(crudDao, id);
  }

  @Override
  public List<TestEntity> readAll() {
    return super.readAll(crudDao);
  }

  @Override
  public void update(TestEntityDto dto) {
    super.update(crudDao, dto);
  }

  @Override
  public TestEntity delete(Long id) {
    return super.delete(crudDao, id);
  }

  /** TestEntity CrudDao mock. */
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
      testEntity.setName("goku");
      return testEntity;
    }
  }
}
