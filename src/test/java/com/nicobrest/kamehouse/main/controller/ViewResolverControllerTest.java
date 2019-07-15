package com.nicobrest.kamehouse.main.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.nicobrest.kamehouse.main.controller.ViewResolverController;

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
    mockMvc = MockMvcBuilders.standaloneSetup(viewResolverController).setViewResolvers(viewResolver)
        .build();
  }

  /**
   * Test all views handled by the ViewResolverController.
   */
  @Test
  public void allViewsTest() {

    try {
      ResultActions requestResult = mockMvc.perform(get("/")).andDo(print());
      requestResult.andExpect(status().isOk());
      requestResult.andExpect(view().name("/index"));

      requestResult = mockMvc.perform(get("/about")).andDo(print());
      requestResult.andExpect(status().isOk());
      requestResult.andExpect(view().name("/about"));

      when(request.getServletPath()).thenReturn("/admin/");
      String adminIndex = viewResolverController.adminPage(request, response);
      assertEquals("/admin/index", adminIndex);
      when(request.getServletPath()).thenReturn("/admin/goku");
      String adminSubpage = viewResolverController.adminPage(request, response);
      assertEquals("/admin/goku", adminSubpage);

      when(request.getServletPath()).thenReturn("/app/");
      String appIndex = viewResolverController.appPage(request, response);
      assertEquals("/app/index", appIndex);

      when(request.getServletPath()).thenReturn("/app/gohan");
      String appSubpage = viewResolverController.appPage(request, response);
      assertEquals("/app/gohan", appSubpage);

      requestResult = mockMvc.perform(get("/contact-us")).andDo(print());
      requestResult.andExpect(status().isOk());
      requestResult.andExpect(view().name("/contact-us"));

      when(request.getServletPath()).thenReturn("/jsp/");
      String jspIndex = viewResolverController.jspPage(request, response);
      assertEquals("/jsp/index", jspIndex);
      when(request.getServletPath()).thenReturn("/jsp/trunks");
      String jspSubpage = viewResolverController.jspPage(request, response);
      assertEquals("/jsp/trunks", jspSubpage);

      requestResult = mockMvc.perform(get("/login")).andDo(print());
      requestResult.andExpect(status().isOk());
      requestResult.andExpect(view().name("/login"));
      
      requestResult = mockMvc.perform(get("/logout")).andDo(print());
      requestResult.andExpect(status().is3xxRedirection());
      requestResult.andExpect(view().name("redirect:/login?logout"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
}
