package com.nicobrest.kamehouse.testmodule.servlet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.service.DragonBallUserService;
import com.nicobrest.kamehouse.testmodule.testutils.DragonBallUserTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;

/**
 * Tests for DragonBallUser servlet.
 * 
 * @author nbrest
 *
 */
public class DragonBallUserServletTest {
  
  private TestUtils<DragonBallUser, DragonBallUserDto> testUtils;
  private DragonBallUser dragonBallUser;
  
  @Mock
  private static DragonBallUserService dragonBallUserServiceMock;

  private MockHttpServletRequest request = new MockHttpServletRequest();
  private MockHttpServletResponse response = new MockHttpServletResponse();

  private void initTestData() {
    testUtils = new DragonBallUserTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    dragonBallUser = testUtils.getSingleTestData();
  }

  @Before
  public void init() {
    initTestData();
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Tests the method to get a DragonBallUser from the system through a servlet.
   */
  @Test
  public void doGetTest() throws ServletException {
    DragonBallUserServlet dragonBallUserServlet = new DragonBallUserServlet();
    DragonBallUserServlet.setDragonBallUserService(dragonBallUserServiceMock);
    List<DragonBallUser> dragonBallUsers = new ArrayList<>();
    dragonBallUsers.add(dragonBallUser);
    when(dragonBallUserServiceMock.readAll()).thenReturn(dragonBallUsers);

    dragonBallUserServlet.doGet(request, response);

    verify(dragonBallUserServiceMock, times(1)).readAll();
  }

  /**
   * Tests the method to add a DragonBallUser from the system through the servlet.
   */
  @Test
  public void doPostTest() throws ServletException {
    DragonBallUserServlet dragonBallUserServlet = new DragonBallUserServlet();
    DragonBallUserServlet.setDragonBallUserService(dragonBallUserServiceMock);
    setDragonBallUserRequestParameters();
    when(dragonBallUserServiceMock.create(any())).thenReturn(dragonBallUser.getId());

    dragonBallUserServlet.doPost(request, response);

    verify(dragonBallUserServiceMock, times(1)).create(any());
  }

  /**
   * Tests the method to edit a DragonBallUser from the system through the
   * servlet.
   */
  @Test
  public void doPutTest() throws ServletException {
    DragonBallUserServlet dragonBallUserServlet = new DragonBallUserServlet();
    DragonBallUserServlet.setDragonBallUserService(dragonBallUserServiceMock);
    setIdRequestParameter();
    setDragonBallUserRequestParameters();
    doNothing().when(dragonBallUserServiceMock).update(any());

    dragonBallUserServlet.doPut(request, response);

    verify(dragonBallUserServiceMock, times(1)).update(any());
  }

  /**
   * Tests the method to delete a DragonBallUser from the system through the
   * servlet.
   */
  @Test
  public void doDeleteTest() throws ServletException {
    DragonBallUserServlet dragonBallUserServlet = new DragonBallUserServlet();
    DragonBallUserServlet.setDragonBallUserService(dragonBallUserServiceMock);
    setIdRequestParameter();
    when(dragonBallUserServiceMock.delete(dragonBallUser.getId())).thenReturn(dragonBallUser);

    dragonBallUserServlet.doDelete(request, response);

    verify(dragonBallUserServiceMock, times(1)).delete(dragonBallUser.getId());
  }

  /**
   * Set id request parameter.
   */
  private void setIdRequestParameter() {
    request.setParameter("id", dragonBallUser.getId().toString());
  }

  /**
   * Set request parameters with dragonball user attributes.
   */
  private void setDragonBallUserRequestParameters() {
    request.setParameter("username", dragonBallUser.getUsername());
    request.setParameter("email", dragonBallUser.getEmail());
    request.setParameter("age", String.valueOf(dragonBallUser.getAge()));
    request.setParameter("stamina", String.valueOf(dragonBallUser.getStamina()));
    request.setParameter("powerLevel", String.valueOf(dragonBallUser.getPowerLevel()));
  }
}
