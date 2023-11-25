package com.nicobrest.kamehouse.commons.web.filter;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Test class for the AddSecurityContextFilter.
 *
 * @author nbrest
 */
class AddSecurityContextFilterTest {

  @Mock
  private AddSecurityContextFilter addSecurityContextFilter;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Tests the filter to add the logged in username on each request as a parameter.
   */
  @Test
  void doFilterTest() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();
    AddSecurityContextFilter addSecurityContextFilterSpy =
        Mockito.spy(new AddSecurityContextFilter());
    when(addSecurityContextFilterSpy.getAuthentication())
        .thenReturn(new UsernamePasswordAuthenticationToken("goku", "gohan"));

    addSecurityContextFilterSpy.doFilter(request, response, chain);

    // no exception thrown
  }
}
