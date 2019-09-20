package com.nicobrest.kamehouse.main.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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
public class ViewResolverControllerTest {

  private MockMvc mockMvc;

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
    mockMvc = MockMvcBuilders.standaloneSetup(viewResolverController).setViewResolvers(
        viewResolver).build();
  }

  /**
   * Test all views handled by the ViewResolverController. 
   */
  @Test
  public void allViewsTest() throws Exception {
    ModelAndView returnedModelAndView = null;
    // Home
    when(request.getServletPath()).thenReturn("/");
    
    returnedModelAndView = viewResolverController.includeStaticHtml(request, response);
    
    assertEquals("/include-static-html", returnedModelAndView.getViewName());
    assertEquals("/index.html", returnedModelAndView.getModel().get("staticHtmlToLoad"));

    // About
    when(request.getServletPath()).thenReturn("/about");
    
    returnedModelAndView = viewResolverController.includeStaticHtml(request, response);
    
    assertEquals("/include-static-html", returnedModelAndView.getViewName());
    assertEquals("/about.html", returnedModelAndView.getModel().get("staticHtmlToLoad"));

    // Admin
    when(request.getServletPath()).thenReturn("/admin");
    
    returnedModelAndView = viewResolverController.includeStaticHtml(request, response);
    
    assertEquals("/include-static-html", returnedModelAndView.getViewName());
    assertEquals("/admin/index.html", returnedModelAndView.getModel().get("staticHtmlToLoad"));

    // Contact Us
    when(request.getServletPath()).thenReturn("/contact-us");
    
    returnedModelAndView = viewResolverController.includeStaticHtml(request, response);
    
    assertEquals("/include-static-html", returnedModelAndView.getViewName());
    assertEquals("/contact-us.html", returnedModelAndView.getModel().get("staticHtmlToLoad"));

    // Login and Logout
    ResultActions requestResult = mockMvc.perform(get("/login")).andDo(print());
    
    requestResult.andExpect(status().isOk());
    requestResult.andExpect(view().name("/login"));
    requestResult = mockMvc.perform(get("/logout")).andDo(print());
    requestResult.andExpect(status().is3xxRedirection());
    requestResult.andExpect(view().name("redirect:/login?logout"));

    // Test module
    when(request.getServletPath()).thenReturn("/test-module/");
    
    returnedModelAndView = viewResolverController.includeStaticHtml(request, response);
    
    assertEquals("/include-static-html", returnedModelAndView.getViewName());
    assertEquals("/test-module/index.html", returnedModelAndView.getModel().get(
        "staticHtmlToLoad"));

    // Test Module - JSP
    when(request.getServletPath()).thenReturn("/test-module/jsp/");
    String testModuleJspIndex = viewResolverController.testModuleJsp(request, response);
    assertEquals("/test-module/jsp/index", testModuleJspIndex);
    when(request.getServletPath()).thenReturn("/test-module/jsp/trunks");
    
    String testModuleJspSubpage = viewResolverController.testModuleJsp(request, response);
    
    assertEquals("/test-module/jsp/trunks", testModuleJspSubpage);

    // Vlc Player
    when(request.getServletPath()).thenReturn("/vlc-player");
    
    returnedModelAndView = viewResolverController.includeStaticHtml(request, response);
    
    assertEquals("/include-static-html", returnedModelAndView.getViewName());
    assertEquals("/vlc-player.html", returnedModelAndView.getModel().get("staticHtmlToLoad"));
  }
}
