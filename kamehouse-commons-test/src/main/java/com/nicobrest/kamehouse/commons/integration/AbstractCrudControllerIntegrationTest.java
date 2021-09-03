package com.nicobrest.kamehouse.commons.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

/**
 * Abstract crud controller to execute integration tests.
 */
public abstract class AbstractCrudControllerIntegrationTest<E, D>
    extends AbstractControllerIntegrationTest {

  private Class<E> entityClass;
  private Class<D> dtoClass;

  /**
   * Init abstract class.
   */
  public AbstractCrudControllerIntegrationTest() {
    entityClass = getEntityClass();
    dtoClass = getDtoClass();
    logger.debug("dtoClass {}", dtoClass);
  }

  public abstract String getCrudUrl();

  public abstract Class<E> getEntityClass();

  public abstract Class<D> getDtoClass();

  /**
   * Read all test.
   */
  public void readAllCrudTest() throws Exception {
    logger.info("Running readAllCrudTest");
    HttpGet get = HttpClientUtils.httpGet(getCrudUrl());

    HttpResponse response = getHttpClient().execute(get);

    assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    List<E> responseBody = getResponseBodyList(response, entityClass);
    assertNotNull(responseBody);
    assertTrue(responseBody.size() > 0);
    logger.info("Response body {}", responseBody);
  }
}
