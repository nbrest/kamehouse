package com.nicobrest.kamehouse.commons.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

/**
 * Abstract class to group all CRUD controller common test functionality.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
public abstract class AbstractCrudControllerTest<E, D> extends AbstractControllerTest<E, D> {

  private static final Long INVALID_ID = 987987L;

  /**
   * Get crud url.
   */
  public abstract String getCrudUrl();

  /**
   * Get crud entity class.
   */
  public abstract Class<E> getEntityClass();

  /**
   * Get crud service.
   */
  public abstract CrudService<E, D> getCrudService();

  /**
   * Get test utils.
   */
  public abstract TestUtils<E, D> getTestUtils();

  /**
   * Get controller class.
   */
  public abstract AbstractController getController();

  /**
   * Override this method to execute custom init tasks before each test when required.
   */
  public void initBeforeTest() {

  }

  /**
   * Resets mock objects.
   */
  @BeforeEach
  public void beforeTest() {
    testUtils = getTestUtils();
    testUtils.initTestData();
    testUtils.setIds();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(getCrudService());
    initBeforeTest();
    mockMvc = MockMvcBuilders.standaloneSetup(getController()).build();
  }

  /**
   * Creates entity test.
   */
  @Test
  public void createTest() throws Exception {
    E entity = testUtils.getSingleTestData();
    D dto = testUtils.getTestDataDto();
    Identifiable identifiableEntity = (Identifiable) entity;
    Mockito.doReturn(identifiableEntity.getId()).when(getCrudService()).create(dto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(dto);

    MockHttpServletResponse response = doPost(getCrudUrl(), requestPayload);
    Long responseBody = getResponseBody(response, Long.class);

    verifyResponseStatus(response, HttpStatus.CREATED);
    assertEquals(identifiableEntity.getId(), responseBody);
    verify(getCrudService(), times(1)).create(dto);
  }

  /**
   * Creates entity ConflictException test.
   */
  @Test
  public void createConflictExceptionTest() throws Exception {
    D dto = testUtils.getTestDataDto();
    assertThrows(
        NestedServletException.class,
        () -> {
          Mockito.doThrow(new KameHouseConflictException("")).when(getCrudService()).create(dto);
          byte[] requestPayload = JsonUtils.toJsonByteArray(dto);

          doPost(getCrudUrl(), requestPayload);
        });
  }

  /**
   * Reads entity test.
   */
  @Test
  public void readTest() throws Exception {
    E entity = testUtils.getSingleTestData();
    Identifiable identifiableEntity = (Identifiable) entity;
    when(getCrudService().read(identifiableEntity.getId())).thenReturn(entity);

    MockHttpServletResponse response = doGet(getCrudUrl() + identifiableEntity.getId());
    E responseBody = getResponseBody(response, getEntityClass());

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributes(entity, responseBody);
  }

  /**
   * Reads all entities test.
   */
  @Test
  public void readAllTest() throws Exception {
    List<E> entityList = testUtils.getTestDataList();
    when(getCrudService().readAll()).thenReturn(entityList);

    MockHttpServletResponse response = doGet(getCrudUrl());
    List<E> responseBody = getResponseBodyList(response, getEntityClass());

    verifyResponseStatus(response, HttpStatus.OK);
    verifyContentType(response, MediaType.APPLICATION_JSON);
    testUtils.assertEqualsAllAttributesList(entityList, responseBody);
    verify(getCrudService(), times(1)).readAll();
    verifyNoMoreInteractions(getCrudService());
  }

  /**
   * Updates entity test.
   */
  @Test
  public void updateTest() throws Exception {
    D dto = testUtils.getTestDataDto();
    Identifiable identifiableDto = (Identifiable) dto;
    Mockito.doNothing().when(getCrudService()).update(dto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(dto);

    MockHttpServletResponse response = doPut(getCrudUrl() + identifiableDto.getId(),
        requestPayload);

    verifyResponseStatus(response, HttpStatus.OK);
    verify(getCrudService(), times(1)).update(any());
  }

  /**
   * Updates entity invalid path id test.
   */
  @Test
  public void updateInvalidPathId() throws Exception {
    D dto = testUtils.getTestDataDto();
    assertThrows(
        NestedServletException.class,
        () -> {
          byte[] requestPayload = JsonUtils.toJsonByteArray(dto);

          doPut(getCrudUrl() + INVALID_ID, requestPayload);
        });
  }

  /**
   * Updates entity not found test.
   */
  @Test
  public void updateNotFoundExceptionTest() throws Exception {
    D dto = testUtils.getTestDataDto();
    assertThrows(
        NestedServletException.class,
        () -> {
          Mockito.doThrow(new KameHouseNotFoundException("")).when(getCrudService()).update(dto);
          byte[] requestPayload = JsonUtils.toJsonByteArray(dto);
          Identifiable identifiableDto = (Identifiable) dto;
          doPut(getCrudUrl() + identifiableDto.getId(), requestPayload);
        });
  }

  /**
   * Deletes entity test.
   */
  @Test
  public void deleteTest() throws Exception {
    E entity = testUtils.getSingleTestData();
    Identifiable identifiableEntity = (Identifiable) entity;
    when(getCrudService().delete(identifiableEntity.getId())).thenReturn(entity);

    MockHttpServletResponse response = doDelete(getCrudUrl() + identifiableEntity.getId());
    E responseBody = getResponseBody(response, getEntityClass());

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributes(entity, responseBody);
    verify(getCrudService(), times(1)).delete(identifiableEntity.getId());
  }

  /**
   * Deletes entity not found test.
   */
  @Test
  public void deleteNotFoundExceptionTest() throws Exception {
    assertThrows(
        NestedServletException.class,
        () -> {
          Mockito.doThrow(new KameHouseNotFoundException("")).when(getCrudService())
              .delete(INVALID_ID);

          doDelete(getCrudUrl() + INVALID_ID);
        });
  }
}
