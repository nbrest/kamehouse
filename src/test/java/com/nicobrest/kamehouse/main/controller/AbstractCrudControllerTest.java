package com.nicobrest.kamehouse.main.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nicobrest.kamehouse.main.dao.Identifiable;
import com.nicobrest.kamehouse.main.service.CrudService;
import com.nicobrest.kamehouse.utils.JsonUtils;

import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Abstract class to group all CRUD controller common test functionality.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractCrudControllerTest extends AbstractControllerTest {

  /**
   * Create entity test.
   */
  protected <E, D> void createTest(String url, CrudService<E, D> service, E entity, D dto)
      throws Exception {
    Identifiable identifiableEntity = (Identifiable) entity;
    Mockito.doReturn(identifiableEntity.getId()).when(service).create(dto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(dto);

    MockHttpServletResponse response = executePost(url, requestPayload);
    Long responseBody = getResponseBody(response, Long.class);

    verifyResponseStatus(response, HttpStatus.CREATED);
    assertEquals(identifiableEntity.getId(), responseBody);
    verify(service, times(1)).create(dto);
  }
  
  
}
