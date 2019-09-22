package com.nicobrest.kamehouse.testmodule.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.service.DragonBallUserService;
import com.nicobrest.kamehouse.testmodule.servlet.DragonBallUserDeleteActionServlet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * Test class for the DragonBallUserDeleteActionServlet.
 * 
 * @author nbrest
 *
 */
public class DragonBallUserDeleteActionServletTest {

  @Mock
  private static DragonBallUserService dragonBallUserServiceMock;

  private MockHttpServletRequest request = new MockHttpServletRequest();
  private MockHttpServletResponse response = new MockHttpServletResponse();

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Tests the method to delete a DragonBallUser from the system through the
   * servlet. 
   */
  @Test
  public void doPostTest() throws ServletException, IOException {
    DragonBallUserDeleteActionServlet dragonBallUserDeleteActionServlet =
        new DragonBallUserDeleteActionServlet();
    DragonBallUserDeleteActionServlet.setDragonBallUserService(dragonBallUserServiceMock);
    request.setParameter("id", "100");
    DragonBallUser deletedDragonBallUser = new DragonBallUser(100L, "goku", "goku@dbz.com", 100,
        100, 100);
    when(dragonBallUserServiceMock.delete(100L)).thenReturn(deletedDragonBallUser);

    dragonBallUserDeleteActionServlet.doPost(request, response);

    verify(dragonBallUserServiceMock, times(1)).delete(100L);
    assertEquals("users-list", response.getRedirectedUrl());
  }
}
