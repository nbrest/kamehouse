package com.nicobrest.kamehouse.main.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.main.dao.Identifiable;
import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.main.service.CrudService;
import com.nicobrest.kamehouse.utils.JsonUtils;

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
   * Create entity test.
   */
  protected void createTest(String url, CrudService<E, D> service) throws Exception {
    E entity = testUtils.getSingleTestData();
    D dto = testUtils.getTestDataDto();
    Identifiable identifiableEntity = (Identifiable) entity;
    Mockito.doReturn(identifiableEntity.getId()).when(service).create(dto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(dto);

    MockHttpServletResponse response = executePost(url, requestPayload);
    Long responseBody = getResponseBody(response, Long.class);

    verifyResponseStatus(response, HttpStatus.CREATED);
    assertEquals(identifiableEntity.getId(), responseBody);
    verify(service, times(1)).create(dto);
  }

  /**
   * Create entity ConflictException test.
   */
  protected void createConflictExceptionTest(String url, CrudService<E, D> service)
      throws Exception {
    D dto = testUtils.getTestDataDto();
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseConflictException.class));
    Mockito.doThrow(new KameHouseConflictException("")).when(service).create(dto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(dto);

    executePost(url, requestPayload);
  }

  /**
   * Read entity test.
   */
  protected void readTest(String url, CrudService<E, D> service, Class<E> clazz)
      throws Exception {
    E entity = testUtils.getSingleTestData(); 
    Identifiable identifiableEntity = (Identifiable) entity;
    when(service.read(identifiableEntity.getId())).thenReturn(entity);

    MockHttpServletResponse response = executeGet(url + identifiableEntity.getId());
    E responseBody = getResponseBody(response, clazz);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals(entity, responseBody);
    testUtils.assertEqualsAllAttributes(entity, responseBody);
  }

  /**
   * Read entity test.
   */
  protected void readAllTest(String url, CrudService<E, D> service, Class<E> clazz)
      throws Exception {
    List<E> entityList = testUtils.getTestDataList();
    when(service.readAll()).thenReturn(entityList);

    MockHttpServletResponse response = executeGet(url);
    List<E> responseBody = getResponseBodyList(response, clazz);

    verifyResponseStatus(response, HttpStatus.OK);
    verifyContentType(response, MediaType.APPLICATION_JSON_UTF8);
    assertEquals(entityList, responseBody);
    testUtils.assertEqualsAllAttributesList(entityList, responseBody);
    verify(service, times(1)).readAll();
    verifyNoMoreInteractions(service);
  }

  /**
   * Update entity test.
   */
  protected void updateTest(String url, CrudService<E, D> service) throws Exception {
    D dto = testUtils.getTestDataDto();
    Identifiable identifiableDto = (Identifiable) dto;
    Mockito.doNothing().when(service).update(dto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(dto);

    MockHttpServletResponse response = executePut(url + identifiableDto.getId(), requestPayload);

    verifyResponseStatus(response, HttpStatus.OK);
    verify(service, times(1)).update(any());
  }

  /**
   * Update entity invalid path id test.
   */
  protected void updateInvalidPathId(String url) throws IOException, Exception {
    D dto = testUtils.getTestDataDto();
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseBadRequestException.class));
    byte[] requestPayload = JsonUtils.toJsonByteArray(dto);

    executePut(url + INVALID_ID, requestPayload);
  }

  /**
   * Update entity not found test.
   */
  protected void updateNotFoundExceptionTest(String url, CrudService<E, D> service)
      throws Exception {
    D dto = testUtils.getTestDataDto();
    Identifiable identifiableDto = (Identifiable) dto;
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseNotFoundException.class));
    Mockito.doThrow(new KameHouseNotFoundException("")).when(service).update(dto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(dto);

    executePut(url + identifiableDto.getId(), requestPayload);
  }

  /**
   * Delete entity test.
   */
  protected void deleteTest(String url, CrudService<E, D> service, Class<E> clazz)
      throws Exception {
    E entity = testUtils.getSingleTestData();
    Identifiable identifiableEntity = (Identifiable) entity;
    when(service.delete(identifiableEntity.getId())).thenReturn(entity);

    MockHttpServletResponse response = executeDelete(url + identifiableEntity.getId());
    E responseBody = getResponseBody(response, clazz);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals(entity, responseBody);
    testUtils.assertEqualsAllAttributes(entity, responseBody);
    verify(service, times(1)).delete(identifiableEntity.getId());
  }

  /**
   * Delete entity not found test.
   */
  protected void deleteNotFoundExceptionTest(String url, CrudService<E, D> service)
      throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseNotFoundException.class));
    Mockito.doThrow(new KameHouseNotFoundException("")).when(service).delete(INVALID_ID);

    executeDelete(url + INVALID_ID);
  }
}
