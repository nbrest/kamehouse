package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.model.TestEntity;
import com.nicobrest.kamehouse.commons.model.TestEntityDto;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.service.TestEntityCrudService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Test controller to test AbstractCrudController and AbstractController.
 */
@Controller
@RequestMapping(value = "/api/v1/unit-tests")
public class TestEntityCrudController extends AbstractCrudController<TestEntity, TestEntityDto> {

  @Autowired
  private TestEntityCrudService testEntityCrudService;

  @Override
  public CrudService<TestEntity, TestEntityDto> getCrudService() {
    return testEntityCrudService;
  }

  /**
   * Create a TestEntity.
   */
  @PostMapping(path = "/test-entity")
  @ResponseBody
  public ResponseEntity<Long> create(@RequestBody TestEntityDto dto, HttpServletRequest request) {
    return super.create(dto);
  }

  /**
   * Read a TestEntity.
   */
  @GetMapping(path = "/test-entity/{id}")
  @ResponseBody
  public ResponseEntity<TestEntity> read(@PathVariable Long id) {
    return generatePasswordLessResponseEntity(super.read(id));
  }

  /**
   * Read all TestEntities.
   */
  @GetMapping(path = "/test-entity")
  @ResponseBody
  public ResponseEntity<List<TestEntity>> readAll() {
    return super.readAll();
  }

  /**
   * Update a TestEntity.
   */
  @PutMapping(path = "/test-entity/{id}")
  @ResponseBody
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody TestEntityDto dto) {
    return super.update(id, dto);
  }

  /**
   * Delete a TestEntity.
   */
  @DeleteMapping(path = "/test-entity/{id}")
  @ResponseBody
  public ResponseEntity<TestEntity> delete(@PathVariable Long id) {
    return super.delete(id);
  }

  /**
   * Expose generateGetResponseEntity in abstract class for unit testing.
   */
  public static ResponseEntity<TestEntity> generateGetResponseEntity(TestEntity entity,
      boolean logResponse) {
    return AbstractController.generateGetResponseEntity(entity,logResponse);
  }

  /**
   * Expose generatePutResponseEntity in abstract class for unit testing.
   */
  public static ResponseEntity<Void> generatePutResponseEntity() {
    return AbstractController.generatePutResponseEntity();
  }

  /**
   * Expose generatePutResponseEntity in abstract class for unit testing.
   */
  public static ResponseEntity<TestEntity> generatePutResponseEntity(TestEntity entity) {
    return AbstractController.generatePutResponseEntity(entity);
  }

  /**
   * Expose generatePostResponseEntity in abstract class for unit testing.
   */
  public static ResponseEntity<TestEntity> generatePostResponseEntity(TestEntity entity,
      boolean logResponse) {
    return AbstractController.generatePostResponseEntity(entity, logResponse);
  }

  /**
   * Expose generatePasswordLessResponseEntity in abstract class for unit testing.
   */
  public static ResponseEntity<TestEntity> generatePasswordLessResponseEntityWrapper(
      ResponseEntity<TestEntity> responseEntity) {
    return AbstractController.generatePasswordLessResponseEntity(responseEntity);
  }

  /**
   * Expose validatePathAndRequestBodyIds in abstract class for unit testing.
   */
  public static void validatePathAndRequestBodyIds(Long pathId, Long requestBodyId) {
    AbstractController.validatePathAndRequestBodyIds(pathId, requestBodyId);
  }
}
