package com.nicobrest.kamehouse.ui.controller;

import static org.junit.Assert.assertEquals;
import com.nicobrest.kamehouse.commons.controller.AbstractControllerTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for the SampleController class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class SampleControllerTest extends AbstractControllerTest {

  private SampleController sampleController = new SampleController();

  /**
   * Resets mock objects and test data.
   */
  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(sampleController).build();
  }

  /**
   * Tests the endpoint /dragonball/model-and-view
   * with the HTTP method GET. The service should respond with HTTP status 200 OK
   * and a view defined in dragonball/model-and-view.jsp.
   */
  @Test
  public void getModelAndViewTest() throws Exception {
    MockHttpServletResponse response = doGet("/ui/sample/dragonball/model-and-view");

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals("/test-module/jsp/dragonball/model-and-view", response.getForwardedUrl());
  }

}
