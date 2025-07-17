package com.nicobrest.kamehouse.commons.model;

/**
 * Translator between entity and dto for TestEntity.
 *
 * @author nbrest
 */
public class TestEntityDtoTranslator implements KameHouseDtoTranslator<TestEntity, TestEntityDto> {

  @Override
  public TestEntity buildEntity(TestEntityDto testEntityDto) {
    TestEntity entity = new TestEntity();
    entity.setId(testEntityDto.getId());
    entity.setName(testEntityDto.getName());
    return entity;
  }

  @Override
  public TestEntityDto buildDto(TestEntity testEntity) {
    TestEntityDto dto = new TestEntityDto();
    dto.setId(testEntity.getId());
    dto.setName(testEntity.getName());
    return dto;
  }
}
