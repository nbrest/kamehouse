package com.nicobrest.kamehouse.commons.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.nicobrest.kamehouse.commons.model.TestEntity;
import com.nicobrest.kamehouse.commons.model.TestEntityDto;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

/**
 * Unit tests for the AbstractCrudController and AbstractController through a TestEntity
 * controller.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
public class AbstractCrudControllerUnitTest {

  private static final String API_TEST_ENTITY = "/api/v1/unit-tests/test-entity";
  private MockMvc mockMvc;
  private TestEntity testEntity;
  private TestEntityDto testEntityDto;

  @Autowired
  private TestEntityCrudController testEntityCrudController;

  /**
   * Tests setup.
   */
  @BeforeEach
  public void beforeTest() {
    testEntity = new TestEntity();
    testEntity.setId(1L);
    testEntity.setName("goku");

    testEntityDto = new TestEntityDto();
    testEntityDto.setId(1L);
    testEntityDto.setName("goku");

    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(testEntityCrudController).build();
  }

  /**
   * create entity test.
   */
  @Test
  public void createTest() throws Exception {
    byte[] requestPayload = JsonUtils.toJsonByteArray(testEntityDto);
    MockHttpServletResponse response = doPost(API_TEST_ENTITY, requestPayload);

    verifyResponseStatus(response, HttpStatus.CREATED);
  }

  /**
   * read entity test.
   */
  @Test
  public void readTest() throws Exception {
    MockHttpServletResponse response = doGet(API_TEST_ENTITY + "/1");

    verifyResponseStatus(response, HttpStatus.OK);
  }

  /**
   * read all entities test.
   */
  @Test
  public void readAllTest() throws Exception {
    MockHttpServletResponse response = doGet(API_TEST_ENTITY);

    verifyResponseStatus(response, HttpStatus.OK);
  }

  /**
   * update entity test.
   */
  @Test
  public void updateTest() throws Exception {
    byte[] requestPayload = JsonUtils.toJsonByteArray(testEntityDto);
    MockHttpServletResponse response = doPut(API_TEST_ENTITY + "/1", requestPayload);

    verifyResponseStatus(response, HttpStatus.OK);
  }

  /**
   * update entity error test.
   */
  @Test
  public void updatePathIdNotValidTest() throws Exception {
    assertThrows(
        NestedServletException.class,
        () -> {
          byte[] requestPayload = JsonUtils.toJsonByteArray(testEntityDto);
          doPut(API_TEST_ENTITY + "/2", requestPayload);
        },
        "KameHouseBadRequestException");
  }

  /**
   * delete entity test.
   */
  @Test
  public void deleteTest() throws Exception {
    MockHttpServletResponse response = doDelete(API_TEST_ENTITY + "/1");

    verifyResponseStatus(response, HttpStatus.OK);
  }

  /**
   * Executes a get request for the specified url on the mock server.
   */
  protected MockHttpServletResponse doGet(String url) throws Exception {
    return mockMvc.perform(get(url)).andDo(print()).andReturn().getResponse();
  }

  /**
   * Executes a post request for the specified url and payload on the mock server.
   */
  protected MockHttpServletResponse doPost(String url, byte[] requestPayload) throws Exception {
    return mockMvc
        .perform(post(url).contentType(MediaType.APPLICATION_JSON).content(requestPayload))
        .andDo(print())
        .andReturn()
        .getResponse();
  }

  /**
   * Executes a put request for the specified url and payload on the mock server.
   */
  protected MockHttpServletResponse doPut(String url, byte[] requestPayload) throws Exception {
    return mockMvc
        .perform(put(url).contentType(MediaType.APPLICATION_JSON).content(requestPayload))
        .andDo(print())
        .andReturn()
        .getResponse();
  }

  /**
   * Executes a delete request for the specified url on the mock server.
   */
  protected MockHttpServletResponse doDelete(String url) throws Exception {
    return mockMvc.perform(delete(url)).andDo(print()).andReturn().getResponse();
  }

  /**
   * Verifies that the response's status code matches the expected one.
   */
  protected static void verifyResponseStatus(
      MockHttpServletResponse response, HttpStatus expectedStatus) {
    assertEquals(expectedStatus.value(), response.getStatus());
  }
}
