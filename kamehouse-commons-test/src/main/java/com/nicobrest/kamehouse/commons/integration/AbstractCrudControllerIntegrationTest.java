package com.nicobrest.kamehouse.commons.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nicobrest.kamehouse.commons.model.KameHouseEntity;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseDto;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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

  protected TestUtils<E, D> testUtils;

  private Class<E> entityClass;
  private D dto;
  private long createdId = -1;

  /**
   * Init abstract class.
   */
  public AbstractCrudControllerIntegrationTest() {
    entityClass = getEntityClass();
    testUtils = getTestUtils();
    testUtils.initTestData();
  }

  /**
   * Get test utils.
   */
  public abstract TestUtils<E, D> getTestUtils();

  /**
   * Webapp to connect to.
   */
  public abstract String getWebapp();

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
   * Creates an entity.
   */
  @Test
  @Order(1)
  public void createTest() throws Exception {
    logger.info("Running createTest");
    dto = buildDto(testUtils.getTestDataDto());
    HttpPost httpPost = new HttpPost(getCrudUrl());
    httpPost.setEntity(getRequestBody(dto));
    logger.info("Creating entity {}", dto);

    HttpResponse response = getHttpClient().execute(httpPost);

    assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
    Long responseBody = getResponseBody(response, Long.class);
    assertNotNull(responseBody);
    createdId = responseBody;
    logger.info("Created id {}", createdId);
  }

  /**
   * Creates an entity conflict exception.
   */
  @Test
  @Order(2)
  public void createConflictExceptionTest() throws Exception {
    if (!hasUniqueConstraints()) {
      logger.info("Skipping createConflictExceptionTest");
      return;
    }
    logger.info("Running createConflictExceptionTest createdId {}", createdId);
    HttpPost httpPost = new HttpPost(getCrudUrl());
    httpPost.setEntity(getRequestBody(dto));
    logger.info("Creating entity {}", dto);

    HttpResponse response = getHttpClient().execute(httpPost);

    assertEquals(HttpStatus.SC_CONFLICT, response.getStatusLine().getStatusCode());
    logger.info("createConflictExceptionTest completed successfully");
  }

  /**
   * Gets a specific entity from the repository.
   */
  @Test
  @Order(3)
  public void readTest() throws Exception {
    logger.info("Running readTest with id {}", createdId);
    HttpGet get = HttpClientUtils.httpGet(getCrudUrl() + createdId);

    HttpResponse response = getHttpClient().execute(get);

    verifySuccessfulResponse(response);
  }

  /**
   * Gets all entities.
   */
  @Test
  @Order(4)
  public void readAllTest() throws Exception {
    logger.info("Running readAllTest");
    HttpGet get = HttpClientUtils.httpGet(getCrudUrl());

    HttpResponse response = getHttpClient().execute(get);

    assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    List<E> responseBody = getResponseBodyList(response, entityClass);
    assertNotNull(responseBody);
    assertTrue(responseBody.size() > 0);
    logger.info("Response body {}", responseBody);
  }

  /**
   * Updates an entity.
   */
  @Test
  @Order(5)
  public void updateTest() throws Exception {
    logger.info("Running updateTest with id {}", createdId);
    updateDto(dto);
    dto.setId(createdId);
    HttpPut httpPut = new HttpPut(getCrudUrl() + createdId);
    httpPut.setEntity(getRequestBody(dto));
    logger.info("Updating entity {}", dto);

    HttpResponse response = getHttpClient().execute(httpPut);

    assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    logger.info("updateTest completed successfully");
  }

  /**
   * Updates an entity with invalid path id. Exception expected.
   */
  @Test
  @Order(6)
  public void updateInvalidPathId() throws Exception {
    logger.info("Running updateInvalidPathId with id {}", createdId + createdId);
    HttpPut httpPut = new HttpPut(getCrudUrl() + createdId + createdId);
    httpPut.setEntity(getRequestBody(dto));
    logger.info("Updating entity {}", dto);

    HttpResponse response = getHttpClient().execute(httpPut);

    assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    logger.info("updateInvalidPathId completed successfully");
  }

  /**
   * Updates an entity not found.
   */
  @Test
  @Order(7)
  public void updateNotFoundExceptionTest() throws Exception {
    Long invalidId = createdId * 2;
    logger.info("Running updateNotFoundExceptionTest with id {}", invalidId);
    dto.setId(invalidId);
    HttpPut httpPut = new HttpPut(getCrudUrl() + invalidId);
    httpPut.setEntity(getRequestBody(dto));
    logger.info("Updating entity {}", dto);

    HttpResponse response = getHttpClient().execute(httpPut);

    assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    logger.info("updateNotFoundExceptionTest completed successfully");
  }

  /**
   * Deletes an entity.
   */
  @Test
  @Order(8)
  public void deleteTest() throws Exception {
    logger.info("Running deleteTest with id {}", createdId);
    HttpDelete httpDelete = new HttpDelete(getCrudUrl() + createdId);

    HttpResponse response = getHttpClient().execute(httpDelete);

    assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    logger.info("deleteTest completed successfully");
  }

  /**
   * Deletes an entity not found.
   */
  @Test
  @Order(9)
  public void deleteNotFoundExceptionTest() throws Exception {
    logger.info("Running deleteNotFoundExceptionTest with id {}", createdId);
    HttpDelete httpDelete = new HttpDelete(getCrudUrl() + createdId);

    HttpResponse response = getHttpClient().execute(httpDelete);

    assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    logger.info("deleteNotFoundExceptionTest completed successfully");
  }

  /**
   * Crud url to execute operations.
   */
  protected String getCrudUrl() {
    return getBaseUrl() + getWebapp() + getCrudUrlSuffix();
  }
}
