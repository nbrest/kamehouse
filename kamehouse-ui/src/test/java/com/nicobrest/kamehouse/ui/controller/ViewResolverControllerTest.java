package com.nicobrest.kamehouse.ui.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.controller.AbstractControllerTest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Unit tests for the ViewResolverController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
public class ViewResolverControllerTest extends AbstractControllerTest<ModelAndView, Object> {

  private MockHttpServletResponse response = new MockHttpServletResponse();

  @InjectMocks private ViewResolverController viewResolverController;

  /** Resets mock objects. */
  @BeforeEach
  public void beforeTest() {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/WEB-INF/jsp");
    viewResolver.setSuffix(".jsp");

    MockitoAnnotations.openMocks(this);
    mockMvc =
        MockMvcBuilders.standaloneSetup(viewResolverController)
            .setViewResolvers(viewResolver)
            .build();
  }

  /** Tests jsp test module urls. */
  @Test
  public void testModuleUrlsTest() throws Exception {
    testTestModuleJsp("/jsp/test-module/", "/jsp/test-module/index");
    testTestModuleJsp("/jsp/test-module/trunks", "/jsp/test-module/trunks");
  }

  /** Tests logout. */
  @Test
  public void logoutTest() throws Exception {
    MockHttpServletResponse response = doGet("/logout");

    verifyResponseStatus(response, HttpStatus.FOUND);
    assertEquals("/login.html?logout", response.getRedirectedUrl());
  }

  /** Tests test module jsp view generated from the source url. */
  private void testTestModuleJsp(String sourceUrl, String expectedView) {
    HttpServletRequestWrapper request =
        Mockito.spy(new HttpServletRequestWrapper(new MockHttpServletRequest()));
    when(request.getServletPath()).thenReturn(sourceUrl);

    String testModuleJspView = viewResolverController.testModuleJsp(request, response);

    assertEquals(expectedView, testModuleJspView);
  }
}
