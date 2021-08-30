package com.nicobrest.kamehouse.commons.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Super class to all controller test classes to group common attributes and
 * functionality to all controller test classes.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractControllerTest<E, D> {

  protected MockMvc mockMvc;

  protected TestUtils<E, D> testUtils;

  /**
   * Executes a get request for the specified url on the mock server.
   */
  protected MockHttpServletResponse doGet(String url) throws Exception {
    return mockMvc.perform(get(url))
        .andDo(print())
        .andReturn()
        .getResponse();
  }

  /**
   * Executes a post request for the specified url and payload on the mock server.
   */
  protected MockHttpServletResponse doPost(String url, byte[] requestPayload)
      throws Exception {
    return mockMvc
        .perform(post(url).contentType(MediaType.APPLICATION_JSON).content(requestPayload))
        .andDo(print()).andReturn().getResponse();
  }

  /**
   * Executes a post request for the specified url with an empty payload.
   */
  protected MockHttpServletResponse doPost(String url) throws Exception {
    return mockMvc.perform(post(url)).andDo(print()).andReturn().getResponse();
  }

  /**
   * Executes a put request for the specified url and payload on the mock server.
   */
  protected MockHttpServletResponse doPut(String url, byte[] requestPayload) throws Exception {
    return mockMvc
        .perform(put(url).contentType(MediaType.APPLICATION_JSON).content(requestPayload))
        .andDo(print()).andReturn().getResponse();
  }

  /**
   * Executes a put request for the specified url with an empty payload.
   */
  protected MockHttpServletResponse doPut(String url) throws Exception {
    return mockMvc.perform(put(url)).andDo(print()).andReturn().getResponse();
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
  protected static void verifyResponseStatus(MockHttpServletResponse response,
      HttpStatus expectedStatus) {
    assertEquals(expectedStatus.value(), response.getStatus());
  }

  /**
   * Verifies that the response's content type matches the expected one.
   */
  protected static void verifyContentType(MockHttpServletResponse response,
      MediaType expectedContentType) {
    assertEquals(expectedContentType.toString(), response.getContentType());
  }

  /**
   * Gets the response body of the request as an object of the specified class.
   */
  protected static <T> T getResponseBody(MockHttpServletResponse response, Class<T> clazz)
      throws JsonParseException, JsonMappingException, UnsupportedEncodingException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    T responseBody = mapper.readValue(response.getContentAsString(),
        mapper.getTypeFactory().constructType(clazz));
    return responseBody;
  }

  /**
   * Gets the response body of the request as a list of objects of the specified
   * class.
   */
  protected static <T> List<T> getResponseBodyList(MockHttpServletResponse response, Class<T> clazz)
      throws JsonParseException, JsonMappingException, UnsupportedEncodingException, IOException,
      InstantiationException, IllegalAccessException {
    ObjectMapper mapper = new ObjectMapper();
    List<T> responseBody = mapper.readValue(response.getContentAsString(),
        mapper.getTypeFactory().constructCollectionType(List.class, clazz));
    return responseBody;
  }
}
