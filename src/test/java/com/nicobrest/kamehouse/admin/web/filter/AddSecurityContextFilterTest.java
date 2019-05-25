package com.nicobrest.kamehouse.admin.web.filter;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Test class for the AddSecurityContextFilter.
 * 
 * @author nbrest
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(AddSecurityContextFilter.class)
@SuppressWarnings("deprecation")
public class AddSecurityContextFilterTest {

  @Mock
  private AddSecurityContextFilter addSecurityContextFilter;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Tests the filter to add the logged in username on each request as a parameter.
   */
  @Test
  public void test() {

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();
    try {
      AddSecurityContextFilter addSecurityContextFilterSpy = PowerMockito.spy(
          new AddSecurityContextFilter());
      PowerMockito.doReturn(new UsernamePasswordAuthenticationToken("goku", "gohan")).when(
          addSecurityContextFilterSpy, "getAuthentication");

      addSecurityContextFilterSpy.doFilter(request, response, chain);
      PowerMockito.verifyPrivate(addSecurityContextFilterSpy, Mockito.times(1)).invoke(
          "getAuthentication");
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
}
