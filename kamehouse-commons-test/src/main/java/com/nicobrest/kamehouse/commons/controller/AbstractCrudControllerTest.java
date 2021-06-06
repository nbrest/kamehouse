package com.nicobrest.kamehouse.commons.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;

import org.hamcrest.core.IsInstanceOf;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.NestedServletException;

import java.io.IOException;
import java.util.List;

/**
 * Abstract class to group all CRUD controller common test functionality.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractCrudControllerTest<E, D> extends AbstractControllerTest<E, D> {

  private static final Long INVALID_ID = 987987L;

  /**
   * Creates entity test.
   */
  protected void createTest(String url, CrudService<E, D> service) throws Exception {
    E entity = testUtils.getSingleTestData();
    D dto = testUtils.getTestDataDto();
    Identifiable identifiableEntity = (Identifiable) entity;
    Mockito.doReturn(identifiableEntity.getId()).when(service).create(dto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(dto);

    MockHttpServletResponse response = doPost(url, requestPayload);
    Long responseBody = getResponseBody(response, Long.class);

    verifyResponseStatus(response, HttpStatus.CREATED);
    assertEquals(identifiableEntity.getId(), responseBody);
    verify(service, times(1)).create(dto);
  }

  /**
   * Creates entity ConflictException test.
   */
  protected void createConflictExceptionTest(String url, CrudService<E, D> service)
      throws Exception {
    D dto = testUtils.getTestDataDto();
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(KameHouseConflictException.class));
    Mockito.doThrow(new KameHouseConflictException("")).when(service).create(dto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(dto);

    doPost(url, requestPayload);
  }

  /**
   * Reads entity test.
   */
  protected void readTest(String url, CrudService<E, D> service, Class<E> clazz)
      throws Exception {
    E entity = testUtils.getSingleTestData(); 
    Identifiable identifiableEntity = (Identifiable) entity;
    when(service.read(identifiableEntity.getId())).thenReturn(entity);

    MockHttpServletResponse response = doGet(url + identifiableEntity.getId());
    E responseBody = getResponseBody(response, clazz);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributes(entity, responseBody);
  }

  /**
   * Reads all entities test.
   */
  protected void readAllTest(String url, CrudService<E, D> service, Class<E> clazz)
      throws Exception {
    List<E> entityList = testUtils.getTestDataList();
    when(service.readAll()).thenReturn(entityList);

    MockHttpServletResponse response = doGet(url);
    List<E> responseBody = getResponseBodyList(response, clazz);

    verifyResponseStatus(response, HttpStatus.OK);
    verifyContentType(response, MediaType.APPLICATION_JSON_UTF8);
    testUtils.assertEqualsAllAttributesList(entityList, responseBody);
    verify(service, times(1)).readAll();
    verifyNoMoreInteractions(service);
  }

  /**
   * Updates entity test.
   */
  protected void updateTest(String url, CrudService<E, D> service) throws Exception {
    D dto = testUtils.getTestDataDto();
    Identifiable identifiableDto = (Identifiable) dto;
    Mockito.doNothing().when(service).update(dto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(dto);

    MockHttpServletResponse response = doPut(url + identifiableDto.getId(), requestPayload);

    verifyResponseStatus(response, HttpStatus.OK);
    verify(service, times(1)).update(any());
  }

  /**
   * Updates entity invalid path id test.
   */
  protected void updateInvalidPathId(String url) throws IOException, Exception {
    D dto = testUtils.getTestDataDto();
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(KameHouseBadRequestException.class));
    byte[] requestPayload = JsonUtils.toJsonByteArray(dto);

    doPut(url + INVALID_ID, requestPayload);
  }

  /**
   * Updates entity not found test.
   */
  protected void updateNotFoundExceptionTest(String url, CrudService<E, D> service)
      throws Exception {
    D dto = testUtils.getTestDataDto();
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(KameHouseNotFoundException.class));
    Mockito.doThrow(new KameHouseNotFoundException("")).when(service).update(dto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(dto);
    Identifiable identifiableDto = (Identifiable) dto;
    doPut(url + identifiableDto.getId(), requestPayload);
  }

  /**
   * Deletes entity test.
   */
  protected void deleteTest(String url, CrudService<E, D> service, Class<E> clazz)
      throws Exception {
    E entity = testUtils.getSingleTestData();
    Identifiable identifiableEntity = (Identifiable) entity;
    when(service.delete(identifiableEntity.getId())).thenReturn(entity);

    MockHttpServletResponse response = doDelete(url + identifiableEntity.getId());
    E responseBody = getResponseBody(response, clazz);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributes(entity, responseBody);
    verify(service, times(1)).delete(identifiableEntity.getId());
  }

  /**
   * Deletes entity not found test.
   */
  protected void deleteNotFoundExceptionTest(String url, CrudService<E, D> service)
      throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(KameHouseNotFoundException.class));
    Mockito.doThrow(new KameHouseNotFoundException("")).when(service).delete(INVALID_ID);

    doDelete(url + INVALID_ID);
  }
}
