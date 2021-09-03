package com.nicobrest.kamehouse.commons.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
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
  private Class<D> dtoClass;
  private E entity;
  private Long createdId;

  /**
   * Init abstract class.
   */
  public AbstractCrudControllerIntegrationTest() {
    entityClass = getEntityClass();
    dtoClass = getDtoClass();
    logger.info("dtoClass {}", dtoClass);
  }

  /**
   * Crud url to execute operations.
   */
  public abstract String getCrudUrl();

  /**
   * Crud entity class.
   */
  public abstract Class<E> getEntityClass();

  /**
   * Crud dto class.
   */
  public abstract Class<D> getDtoClass();

  /**
   * Create entity to execute all crud operations.
   */
  public abstract E createEntity();

  public E getEntity() {
    return entity;
  }

  public void setEntity(E entity) {
    this.entity = entity;
  }

  public Long getCreatedId() {
    return createdId;
  }

  /**
   * Creates a user.
   */
  @Test
  @Order(1)
  public void createTest() throws Exception {
    logger.info("Running createTest");
    setEntity(createEntity());
    HttpPost createRequest = new HttpPost(getCrudUrl());
    byte[] requestBody = JsonUtils.toJsonByteArray(getEntity());
    HttpEntity entity = new ByteArrayEntity(requestBody, ContentType.APPLICATION_JSON);
    createRequest.setEntity(entity);
    createRequest.setHeader(X_REQUESTED_WITH, XML_HTTP_REQUEST);

    HttpResponse response = getHttpClient().execute(createRequest);

    assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
    Long responseBody = getResponseBody(response, Long.class);
    assertNotNull(responseBody);
    createdId = responseBody;
    logger.info("Created id {}", createdId);
  }

  /**
   * Creates an user conflict exception.
   */
  @Test
  @Order(2)
  public void createConflictExceptionTest() throws Exception {
    logger.info("Running createConflictExceptionTest createdId " + getCreatedId());
  }

  /**
   * Gets a specific user from the repository.
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
   * Gets all users.
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
   * Updates an user.
   */
  @Test
  @Order(5)
  public void updateTest() throws Exception {
    logger.info("Running updateTest");
  }

  /**
   * Updates an user with invalid path id. Exception expected.
   */
  @Test
  @Order(6)
  public void updateInvalidPathId() throws Exception {
    logger.info("Running updateInvalidPathId");
  }

  /**
   * Updates an user not found.
   */
  @Test
  @Order(7)
  public void updateNotFoundExceptionTest() throws Exception {
    logger.info("Running updateNotFoundExceptionTest");
  }

  /**
   * Deletes an user.
   */
  @Test
  @Order(8)
  public void deleteTest() throws Exception {
    logger.info("Running deleteTest");
  }

  /**
   * Deletes an user not found.
   */
  @Test
  @Order(9)
  public void deleteNotFoundExceptionTest() throws Exception {
    logger.info("Running deleteNotFoundExceptionTest");
  }
}
