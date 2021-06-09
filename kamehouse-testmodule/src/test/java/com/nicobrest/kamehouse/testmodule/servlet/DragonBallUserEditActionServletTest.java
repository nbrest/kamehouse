package com.nicobrest.kamehouse.testmodule.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * Test class for the DragonBallUserEditActionServlet.
 * 
 * @author nbrest
 *
 */
public class DragonBallUserEditActionServletTest extends AbstractDragonBallUserServletTest {

  @Before
  public void init() {
    initTestData();
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Tests the method to edit a DragonBallUser from the system through the
   * servlet.
   */
  @Test
  public void doPostTest() throws ServletException, IOException {
    DragonBallUserEditActionServlet dragonBallUserEditActionServlet =
        new DragonBallUserEditActionServlet();
    DragonBallUserEditActionServlet.setDragonBallUserService(dragonBallUserServiceMock);
    setIdRequestParameter();
    setDragonBallUserRequestParameters();
    doNothing().when(dragonBallUserServiceMock).update(any());

    dragonBallUserEditActionServlet.doPost(request, response);

    verify(dragonBallUserServiceMock, times(1)).update(any());
    assertEquals("users-list", response.getRedirectedUrl());
  }
}
