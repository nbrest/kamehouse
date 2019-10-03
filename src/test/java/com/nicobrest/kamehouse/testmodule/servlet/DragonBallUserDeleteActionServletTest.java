package com.nicobrest.kamehouse.testmodule.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * Test class for the DragonBallUserDeleteActionServlet.
 * 
 * @author nbrest
 *
 */
public class DragonBallUserDeleteActionServletTest extends AbstractDragonBallUserServletTest {

  @Before
  public void init() {
    initTestData();
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
    setIdRequestParameter();
    when(dragonBallUserServiceMock.delete(dragonBallUser.getId())).thenReturn(dragonBallUser);

    dragonBallUserDeleteActionServlet.doPost(request, response);

    verify(dragonBallUserServiceMock, times(1)).delete(dragonBallUser.getId());
    assertEquals("users-list", response.getRedirectedUrl());
  }
}
