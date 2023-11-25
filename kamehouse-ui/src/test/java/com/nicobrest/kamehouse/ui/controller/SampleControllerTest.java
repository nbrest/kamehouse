package com.nicobrest.kamehouse.ui.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.controller.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for the SampleController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
class SampleControllerTest extends AbstractControllerTest {

  private SampleController sampleController = new SampleController();

  /** Resets mock objects and test data. */
  @BeforeEach
  void beforeTest() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(sampleController).build();
  }

  /**
   * Tests the endpoint /dragonball/model-and-view with the HTTP method GET. The service should
   * respond with HTTP status 200 OK and a view defined in dragonball/model-and-view.jsp.
   */
  @Test
  void getModelAndViewTest() throws Exception {
    MockHttpServletResponse response = doGet("/api/v1/ui/sample/dragonball/model-and-view");

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals("/jsp/test-module/dragonball/model-and-view", response.getForwardedUrl());
  }
}
