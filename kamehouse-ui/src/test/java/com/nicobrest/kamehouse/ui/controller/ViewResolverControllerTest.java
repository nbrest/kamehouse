package com.nicobrest.kamehouse.ui.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.controller.AbstractControllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Unit tests for the ViewResolverController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class ViewResolverControllerTest extends AbstractControllerTest<ModelAndView, Object> {

  private MockHttpServletRequest request;

  private MockHttpServletResponse response;

  @InjectMocks
  private ViewResolverController viewResolverController;

  /**
   * Resets mock objects.
   */
  @BeforeEach
  public void beforeTest() {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/WEB-INF/jsp");
    viewResolver.setSuffix(".jsp");

    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(viewResolverController)
        .setViewResolvers(viewResolver)
        .build();
  }

  /**
   * Tests urls handled by include-static-html.
   */
  @Test
  public void includeStaticUrlsTest() throws Exception {
    testIncludeStaticHtml("", "/index.html");
    testIncludeStaticHtml("/", "/index.html");
    testIncludeStaticHtml("/about", "/about.html");
    testIncludeStaticHtml("/admin", "/admin/index.html");
    testIncludeStaticHtml("/contact-us", "/contact-us.html");
    testIncludeStaticHtml("/login", "/login.html");
    testIncludeStaticHtml("/tennisworld/", "/tennisworld/index.html");
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
    HttpServletRequestWrapper request =
        Mockito.spy(new HttpServletRequestWrapper(new MockHttpServletRequest()));
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
    HttpServletRequestWrapper request =
        Mockito.spy(new HttpServletRequestWrapper(new MockHttpServletRequest()));
    when(request.getServletPath()).thenReturn(sourceUrl);

    String testModuleJspView = viewResolverController.testModuleJsp(request, response);

    assertEquals(expectedView, testModuleJspView);
  }
}
