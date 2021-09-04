package com.nicobrest.kamehouse.commons.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nicobrest.kamehouse.commons.dao.Identifiable;
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
public abstract class AbstractCrudControllerIntegrationTest<E, D>
    extends AbstractControllerIntegrationTest {

  protected TestUtils<E, D> testUtils;

  private Class<E> entityClass;
  private E entity;
  private Long createdId;

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
  public abstract String getCrudSuffix();

  /**
   * Crud entity class.
   */
  public abstract Class<E> getEntityClass();

  /**
   * Build entity to execute all crud operations using the testUtils entity as base.
   */
  public abstract E buildEntity(E entity);

  /**
   * Update the entity before executing the update request.
   */
  public abstract void updateEntity(E entity);

  public E getEntity() {
    return entity;
  }

  /**
   * Creates an entity.
   */
  @Test
  @Order(1)
  public void createTest() throws Exception {
    logger.info("Running createTest");
    entity = buildEntity(testUtils.getSingleTestData());
    HttpPost httpPost = new HttpPost(getCrudUrl());
    httpPost.setEntity(getRequestBody(entity));
    logger.info("Creating entity {}", entity);

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
    logger.info("Running createConflictExceptionTest createdId {}", createdId);
    HttpPost httpPost = new HttpPost(getCrudUrl());
    httpPost.setEntity(getRequestBody(entity));
    logger.info("Creating entity {}", entity);

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

    assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    E responseBody = getResponseBody(response, entityClass);
    assertNotNull(responseBody);
    logger.info("Response body {}", responseBody);
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
    logger.info("Running updateTest");
    updateEntity(entity);
    Identifiable identifiable = (Identifiable) entity;
    identifiable.setId(createdId);
    HttpPut httpPut = new HttpPut(getCrudUrl() + createdId);
    httpPut.setEntity(getRequestBody(entity));
    logger.info("Updating entity {}", entity);

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
    logger.info("Running updateInvalidPathId");
    HttpPut httpPut = new HttpPut(getCrudUrl() + createdId + createdId);
    httpPut.setEntity(getRequestBody(entity));
    logger.info("Updating entity {}", entity);

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
    logger.info("Running updateNotFoundExceptionTest");
    Long invalidId = createdId * 2;
    Identifiable identifiable = (Identifiable) entity;
    identifiable.setId(invalidId);
    HttpPut httpPut = new HttpPut(getCrudUrl() + invalidId);
    httpPut.setEntity(getRequestBody(entity));
    logger.info("Updating entity {}", entity);

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
    logger.info("Running deleteTest");
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
    logger.info("Running deleteNotFoundExceptionTest");
    HttpDelete httpDelete = new HttpDelete(getCrudUrl() + createdId);

    HttpResponse response = getHttpClient().execute(httpDelete);

    assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    logger.info("deleteNotFoundExceptionTest completed successfully");
  }

  /**
   * Crud url to execute operations.
   */
  protected String getCrudUrl() {
    return getBaseUrl() + getWebapp() + getCrudSuffix();
  }
}
