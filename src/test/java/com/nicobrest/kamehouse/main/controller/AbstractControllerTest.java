package com.nicobrest.kamehouse.main.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;

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

  protected static void verifyResponseStatus(MockHttpServletResponse response, int expectedStatus) {
    assertEquals(expectedStatus, response.getStatus());
  }

  protected static void verifyContentType(MockHttpServletResponse response,
      String expectedContentType) {
    assertEquals(expectedContentType, response.getContentType());
  }

  protected static List<ApplicationUser> getResponseBody(MockHttpServletResponse response)
      throws ClassNotFoundException, IOException {
    List<ApplicationUser> object = new ObjectMapper().readValue(response.getContentAsString(),
        new TypeReference<List<ApplicationUser>>() {
        });
    return (List<ApplicationUser>) object;
  }
}
