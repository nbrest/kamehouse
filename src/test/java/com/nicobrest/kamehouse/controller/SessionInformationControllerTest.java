package com.nicobrest.kamehouse.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for the SessionInformationController.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class SessionInformationControllerTest {

  private MockMvc mockMvc;
  
  @Autowired
  private FilterChainProxy springSecurityFilterChain;
  
  @InjectMocks
  private SessionInformationController sessionInformationController;

  /**
   * Resets mock objects.
   */
  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(sessionInformationController).apply(
        SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain)).build();
  }

  @Test
  public void getSessionStatusTest() {

    List<String> roleAnonymous = new ArrayList<String>();
    roleAnonymous.add("ROLE_ANONYMOUS");
    try {
      mockMvc.perform(get("/api/v1/session/status")).andDo(print()).andExpect(status().isOk())
          .andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$.username", equalTo(
                  "anonymousUser"))).andExpect(jsonPath("$.session-id", equalTo(null)))
          .andExpect(jsonPath("$.roles", equalTo(roleAnonymous)));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
}
