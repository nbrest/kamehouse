package com.nicobrest.kamehouse.main.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Unit tests for the ViewResolverController class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class ViewResolverControllerTest extends AbstractControllerTest<ModelAndView, Object> {

  @Mock
  private MockHttpServletRequest request;

  @Mock
  private MockHttpServletResponse response;

  @InjectMocks
  private ViewResolverController viewResolverController;

  /**
   * Resets mock objects.
   */
  @Before
  public void beforeTest() {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/WEB-INF/jsp");
    viewResolver.setSuffix(".jsp");

    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(viewResolverController).setViewResolvers(viewResolver)
        .build();
  }

  /**
   * Tests urls handled by include-static-html.
   */
  @Test
  public void includeStaticUrlsTest() throws Exception {
    testIncludeStaticHtml("/", "/index.html");
    testIncludeStaticHtml("/about", "/about.html");
    testIncludeStaticHtml("/admin", "/admin/index.html");
    testIncludeStaticHtml("/contact-us", "/contact-us.html");
    testIncludeStaticHtml("/test-module/", "/test-module/index.html");
    testIncludeStaticHtml("/vlc-player", "/vlc-player.html");
  }

  /**
   * Tests jsp test module urls.
   */
  @Test
  public void testModuleUrlsTest() throws Exception {
    testTestModuleJsp("/test-module/jsp/", "/test-module/jsp/index");
    testTestModuleJsp("/test-module/jsp/trunks", "/test-module/jsp/trunks");
  }

  /**
   * Tests login.
   */
  @Test
  public void loginTest() throws Exception {
    MockHttpServletResponse response = doGet("/login");

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals("/WEB-INF/jsp/login.jsp", response.getForwardedUrl());
  }

  /**
   * Tests logout.
   */
  @Test
  public void logoutTest() throws Exception {
    MockHttpServletResponse response = doGet("/logout");

    verifyResponseStatus(response, HttpStatus.FOUND);
    assertEquals("/login?logout", response.getRedirectedUrl());
  }

  /**
   * Tests include-static-html functionality to load static html based on the
   * specified source url.
   */
  private void testIncludeStaticHtml(String sourceUrl, String expectedHtml) {
    ModelAndView returnedModelAndView = null;
    when(request.getServletPath()).thenReturn(sourceUrl);

    returnedModelAndView = viewResolverController.includeStaticHtml(request, response);

    assertEquals("/include-static-html", returnedModelAndView.getViewName());
    assertEquals(expectedHtml, returnedModelAndView.getModel().get("staticHtmlToLoad"));
  }

  /**
   * Tests test module jsp view generated from the source url.
   */
  private void testTestModuleJsp(String sourceUrl, String expectedView) {
    when(request.getServletPath()).thenReturn(sourceUrl);

    String testModuleJspView = viewResolverController.testModuleJsp(request, response);

    assertEquals(expectedView, testModuleJspView);
  }
}
