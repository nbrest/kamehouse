package com.nicobrest.kamehouse.admin.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nicobrest.kamehouse.admin.controller.SessionStatusController;
import com.nicobrest.kamehouse.admin.service.SessionStatusService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for the SessionStatusController.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class SessionStatusControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private FilterChainProxy springSecurityFilterChain;

  @InjectMocks
  private SessionStatusController sessionStatusController;

  @Mock
  private SessionStatusService sessionStatusServiceMock;

  /**
   * Resets mock objects.
   */
  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(sessionStatusServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(sessionStatusController)
        .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain)).build();
  }
  
  /**
   * Tests getting the current session information.
   */
  @Test
  public void getSessionStatusTest() {

    List<String> roleAnonymous = new ArrayList<String>();
    roleAnonymous.add("ROLE_ANONYMOUS");
    Map<String, Object> sessionStatusMock = new HashMap<String, Object>();
    sessionStatusMock.put("username", "anonymousUser");
    sessionStatusMock.put("session-id", null);
    sessionStatusMock.put("firstName", null);
    sessionStatusMock.put("lastName", null);
    sessionStatusMock.put("email", null);
    sessionStatusMock.put("roles", roleAnonymous);

    when(sessionStatusServiceMock.get()).thenReturn(sessionStatusMock);
    try {
      ResultActions requestResult = mockMvc.perform(get("/api/v1/session/status")).andDo(print());
      requestResult.andExpect(status().isOk());
      requestResult.andExpect(content().contentType("application/json;charset=UTF-8"));
      requestResult.andExpect(jsonPath("$.username", equalTo("anonymousUser")));
      requestResult.andExpect(jsonPath("$.session-id", equalTo(null)));
      requestResult.andExpect(jsonPath("$.firstName", equalTo(null)));
      requestResult.andExpect(jsonPath("$.lastName", equalTo(null)));
      requestResult.andExpect(jsonPath("$.email", equalTo(null)));
      requestResult.andExpect(jsonPath("$.roles", equalTo(roleAnonymous)));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
}
