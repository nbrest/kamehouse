package com.nicobrest.kamehouse.commons.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.model.KameHouseEntity;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseDto;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Abstract crud controller to execute integration tests.
 */
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractCrudControllerIntegrationTest<E extends KameHouseEntity<D>,
    D extends KameHouseDto<E>> extends AbstractControllerIntegrationTest {

  private static final String UPDATE_ENTITY = "Updating entity {}";

  protected TestUtils<E, D> testUtils;

  private D dto;
  private long createdId = -1;

  /**
   * Init abstract class.
   */
  protected AbstractCrudControllerIntegrationTest() {
    testUtils = getTestUtils();
    testUtils.initTestData();
  }

  /**
   * Get test utils.
   */
  public abstract TestUtils<E, D> getTestUtils();

  /**
   * Get crud suffix for the url.
   */
  public abstract String getCrudUrlSuffix();

  /**
   * Crud entity class.
   */
  public abstract Class<E> getEntityClass();

  /**
   * Build dto to execute all crud operations using the testUtils entity as base.
   */
  public abstract D buildDto(D dto);

  /**
   * Update the dto before executing the update request.
   */
  public abstract void updateDto(D dto);

  public D getDto() {
    return dto;
  }

  public void setDto(D dto) {
    this.dto = dto;
  }

  public Long getCreatedId() {
    return createdId;
  }

  public void setCreatedId(Long createdId) {
    this.createdId = createdId;
  }

  /**
   * Check if the entity has unique constraints. By default yes, it can be overriden to false in the
   * concrete integration test subclasses.
   */
  public boolean hasUniqueConstraints() {
    return true;
  }

  /**
   * Crud url to execute operations.
   */
  protected String getCrudUrl() {
    return getWebappUrl() + getCrudUrlSuffix();
  }

  /**
   * Creates an entity.
   */
  @Test
  @Order(1)
  public void createTest() throws IOException {
    logger.info("Running createTest");
    dto = buildDto(testUtils.getTestDataDto());
    logger.info("Creating entity {}", dto);

    HttpResponse response = post(getCrudUrl(), dto);

    createdId = verifySuccessfulCreatedResponse(response, Long.class);
    logger.info("Created id {}", createdId);
  }

  /**
   * Creates an entity conflict exception.
   */
  @Test
  @Order(2)
  public void createConflictExceptionTest() throws IOException {
    if (!hasUniqueConstraints()) {
      logger.info("Skipping createConflictExceptionTest");
      return;
    }
    logger.info("Running createConflictExceptionTest createdId {}", createdId);
    logger.info("Creating entity {}", dto);

    HttpResponse response = post(getCrudUrl(), dto);

    assertEquals(HttpStatus.SC_CONFLICT, response.getStatusLine().getStatusCode());
    logger.info("createConflictExceptionTest completed successfully");
  }

  /**
   * Gets a specific entity from the repository.
   */
  @Test
  @Order(3)
  public void readTest() throws IOException {
    logger.info("Running readTest with id {}", createdId);

    HttpResponse response = get(getCrudUrl() + createdId);

    verifySuccessfulResponse(response, getEntityClass());
  }

  /**
   * Gets all entities.
   */
  @Test
  @Order(4)
  public void readAllTest() throws IOException {
    logger.info("Running readAllTest");

    HttpResponse response = get(getCrudUrl());

    verifySuccessfulResponseList(response, getEntityClass());
  }

  /**
   * Updates an entity.
   */
  @Test
  @Order(5)
  public void updateTest() throws IOException {
    logger.info("Running updateTest with id {}", createdId);
    updateDto(dto);
    dto.setId(createdId);
    logger.info(UPDATE_ENTITY, dto);

    HttpResponse response = put(getCrudUrl() + createdId, dto);

    assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    logger.info("updateTest completed successfully");
  }

  /**
   * Updates an entity with invalid path id. Exception expected.
   */
  @Test
  @Order(6)
  public void updateInvalidPathId() throws IOException {
    logger.info("Running updateInvalidPathId with id {}", createdId + createdId);
    logger.info(UPDATE_ENTITY, dto);

    HttpResponse response = put(getCrudUrl() + createdId + createdId, dto);

    assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    logger.info("updateInvalidPathId completed successfully");
  }

  /**
   * Updates an entity not found.
   */
  @Test
  @Order(7)
  public void updateNotFoundExceptionTest() throws IOException {
    Long invalidId = createdId * 2;
    logger.info("Running updateNotFoundExceptionTest with id {}", invalidId);
    dto.setId(invalidId);
    logger.info(UPDATE_ENTITY, dto);

    HttpResponse response = put(getCrudUrl() + invalidId, dto);

    assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    logger.info("updateNotFoundExceptionTest completed successfully");
  }

  /**
   * Deletes an entity.
   */
  @Test
  @Order(8)
  public void deleteTest() throws IOException {
    logger.info("Running deleteTest with id {}", createdId);

    HttpResponse response = delete(getCrudUrl() + createdId);

    assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    logger.info("deleteTest completed successfully");
  }

  /**
   * Deletes an entity not found.
   */
  @Test
  @Order(9)
  public void deleteNotFoundExceptionTest() throws IOException {
    logger.info("Running deleteNotFoundExceptionTest with id {}", createdId);

    HttpResponse response = delete(getCrudUrl() + createdId);

    assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    logger.info("deleteNotFoundExceptionTest completed successfully");
  }
}
