package com.nicobrest.kamehouse.main.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Super class to all controller test classes to group common attributes and
 * functionality to all controller test classes.
 * 
 * @author nicolas.brest
 *
 */
public abstract class AbstractControllerTest {

  protected MockMvc mockMvc;
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  protected MockHttpServletResponse executeGet(String url) throws Exception {
    return mockMvc.perform(get(url)).andDo(print()).andReturn().getResponse();
  }

  protected MockHttpServletResponse executePost(String url, byte[] requestPayload)
      throws Exception {
    return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(
        requestPayload)).andDo(print()).andReturn().getResponse();
  }

  protected MockHttpServletResponse executePut(String url, byte[] requestPayload)
      throws Exception {
    return mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(
        requestPayload)).andDo(print()).andReturn().getResponse();
  }
  
  protected MockHttpServletResponse executeDelete(String url) throws Exception {
    return mockMvc.perform(delete(url)).andDo(print()).andReturn().getResponse();
  }
  
  protected static void verifyResponseStatus(MockHttpServletResponse response,
      int expectedStatus) {
    assertEquals(expectedStatus, response.getStatus());
  }

  protected static void verifyContentType(MockHttpServletResponse response,
      String expectedContentType) {
    assertEquals(expectedContentType, response.getContentType());
  }

  protected static <T> List<T> getResponseBodyList(MockHttpServletResponse response,
      Class<T> clazz) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
    TypeReferenceListImpl<T> typeReference = new TypeReferenceListImpl<T>(clazz);
    List<T> responseBody = new ObjectMapper().readValue(response.getContentAsString(),
        typeReference);
    return (List<T>) responseBody;
  }

  protected static <T> T getResponseBody(MockHttpServletResponse response, Class<T> clazz)
      throws ClassNotFoundException, IOException {
    TypeReferenceImpl<T> typeReference = new TypeReferenceImpl<T>(clazz);
    T responseBody = new ObjectMapper().readValue(response.getContentAsString(), typeReference);
    return responseBody;
  }
  
  private static class TypeReferenceImpl<T> extends TypeReference<T> {
    protected final Type type;
    protected TypeReferenceImpl(Class<T> clazz) {
      type = clazz;
    }
    public Type getType() { return type; }
  }
  
  private static class TypeReferenceListImpl<T> extends TypeReference<T> {
    protected final Type type;
    protected TypeReferenceListImpl(Class<T> clazz) throws InstantiationException, IllegalAccessException {

      T object = clazz.newInstance();
      type = list(clazz).getClass();
    }
    public Type getType() { 
      return type; 
    }
  }
  
  public static final <T> List<Class<T>> list(Class<T> c) {
    final List<Class<T>> rv = new ArrayList<>();
    rv.add(c);
    return rv;
}
}
