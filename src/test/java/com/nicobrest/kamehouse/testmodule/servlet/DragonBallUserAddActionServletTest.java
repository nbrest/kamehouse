package com.nicobrest.kamehouse.testmodule.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.testmodule.service.DragonBallUserService;
import com.nicobrest.kamehouse.testmodule.servlet.DragonBallUserAddActionServlet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * Test class for the DragonBallUserAddActionServlet.
 * 
 * @author nbrest
 *
 */
public class DragonBallUserAddActionServletTest {

  @Mock
  private static DragonBallUserService dragonBallUserServiceMock;

  private MockHttpServletRequest request = new MockHttpServletRequest();
  private MockHttpServletResponse response = new MockHttpServletResponse();

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Tests the method to add a DragonBallUser from the system through the
   * servlet. 
   */
  @Test
  public void doPostTest() throws ServletException, IOException {
    DragonBallUserAddActionServlet dragonBallUserAddActionServlet =
        new DragonBallUserAddActionServlet();
    DragonBallUserAddActionServlet.setDragonBallUserService(dragonBallUserServiceMock);
    request.setParameter("username", "goku");
    request.setParameter("email", "goku@dbz.com");
    request.setParameter("age", "100");
    request.setParameter("stamina", "100");
    request.setParameter("powerLevel", "100");
    when(dragonBallUserServiceMock.create(any())).thenReturn(1L);

    dragonBallUserAddActionServlet.doPost(request, response);

    verify(dragonBallUserServiceMock, times(1)).create(any());
    assertEquals("users-list", response.getRedirectedUrl());
  }
}
