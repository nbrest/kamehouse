package com.nicobrest.kamehouse.testmodule.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.controller.AbstractControllerTest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Unit tests for the JspViewResolverController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
class JspViewResolverControllerTest extends AbstractControllerTest<ModelAndView, Object> {

  private MockHttpServletResponse response = new MockHttpServletResponse();

  @InjectMocks private JspViewResolverController jspViewResolverController;

  /** Resets mock objects. */
  @BeforeEach
  void beforeTest() {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/WEB-INF/jsp");
    viewResolver.setSuffix(".jsp");

    MockitoAnnotations.openMocks(this);
    mockMvc =
        MockMvcBuilders.standaloneSetup(jspViewResolverController)
            .setViewResolvers(viewResolver)
            .build();
  }

  /** Tests jsp urls. */
  @Test
  void jspTest() {
    jspUrlTest("/jsp/test-module/", "/jsp/test-module/index");
    jspUrlTest("/jsp/test-module/trunks", "/jsp/test-module/trunks");
  }

  /** Tests test module jsp view generated from the source url. */
  private void jspUrlTest(String sourceUrl, String expectedView) {
    HttpServletRequestWrapper request =
        Mockito.spy(new HttpServletRequestWrapper(new MockHttpServletRequest()));
    when(request.getServletPath()).thenReturn(sourceUrl);

    String testModuleJspView = jspViewResolverController.jsp(request, response);

    assertEquals(expectedView, testModuleJspView);
  }
}
