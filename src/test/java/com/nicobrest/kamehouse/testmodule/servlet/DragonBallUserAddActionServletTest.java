package com.nicobrest.kamehouse.testmodule.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * Test class for the DragonBallUserAddActionServlet.
 * 
 * @author nbrest
 *
 */
public class DragonBallUserAddActionServletTest extends AbstractDragonBallUserServletTest {

  @Before
  public void init() {
    initTestData();
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Tests the method to add a DragonBallUser from the system through the servlet.
   */
  @Test
  public void doPostTest() throws ServletException, IOException {
    DragonBallUserAddActionServlet dragonBallUserAddActionServlet =
        new DragonBallUserAddActionServlet();
    DragonBallUserAddActionServlet.setDragonBallUserService(dragonBallUserServiceMock);
    setDragonBallUserRequestParameters();
    when(dragonBallUserServiceMock.create(any())).thenReturn(dragonBallUser.getId());

    dragonBallUserAddActionServlet.doPost(request, response);

    verify(dragonBallUserServiceMock, times(1)).create(any());
    assertEquals("users-list", response.getRedirectedUrl());
  }
}
