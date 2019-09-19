package com.nicobrest.kamehouse.main.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

/**
 * Super class to all controller test classes to group common attributes and
 * functionality to all controller test classes.
 * 
 * @author nicolas.brest
 *
 */
public abstract class AbstractControllerTest {

  protected MockMvc mockMvc;

  protected MockHttpServletResponse executeGet(String url) throws Exception {
    return mockMvc.perform(get(url)).andDo(print()).andReturn().getResponse();
  }

  protected MockHttpServletResponse executePost(String url, byte[] requestPayload)
      throws Exception {
    return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(
        requestPayload)).andDo(print()).andReturn().getResponse();
  }

  protected static void verifyResponseStatus(MockHttpServletResponse response,
      int expectedStatus) {
    assertEquals(expectedStatus, response.getStatus());
  }

  protected static void verifyContentType(MockHttpServletResponse response,
      String expectedContentType) {
    assertEquals(expectedContentType, response.getContentType());
  }

  protected static <T> List<T> getResponseBodyAsList(MockHttpServletResponse response,
      Class<T> clazz) throws ClassNotFoundException, IOException {
    TypeReference<T> typeReference = getTypeReference(clazz, true);
    List<T> responseBody = new ObjectMapper().readValue(response.getContentAsString(),
        typeReference);
    return (List<T>) responseBody;
  }

  protected static <T> T getResponseBody(MockHttpServletResponse response, Class<T> clazz)
      throws ClassNotFoundException, IOException {
    TypeReference<T> typeReference = getTypeReference(clazz, false);
    T responseBody = new ObjectMapper().readValue(response.getContentAsString(), typeReference);
    return responseBody;
  }

  private static <T> TypeReference<T> getTypeReference(Class<T> clazz, boolean asList)
      throws ClassNotFoundException {
    String className = clazz.getSimpleName();
    TypeReference<T> typeReference;
    switch (className) {
      case "ApplicationUser":
        if (asList) {
          typeReference = (TypeReference<T>) new TypeReference<List<ApplicationUser>>() {
          };
        } else {
          typeReference = (TypeReference<T>) new TypeReference<ApplicationUser>() {
          };
        }
        return typeReference;
      case "Long":
        if (asList) {
          typeReference = (TypeReference<T>) new TypeReference<List<Long>>() {
          };
        } else {
          typeReference = (TypeReference<T>) new TypeReference<Long>() {
          };
        }
        return typeReference;
      default:
        throw new ClassNotFoundException(className + " not found");
    }
  }

}
