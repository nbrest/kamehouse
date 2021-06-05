package com.nicobrest.kamehouse.testmodule.servlet;

import com.nicobrest.kamehouse.main.testutils.TestUtils;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.service.DragonBallUserService;
import com.nicobrest.kamehouse.testmodule.testutils.DragonBallUserTestUtils;

import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Abstract class to group dragonball user servlets test functionality.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractDragonBallUserServletTest {
  
  protected TestUtils<DragonBallUser, DragonBallUserDto> testUtils;
  protected DragonBallUser dragonBallUser;
  
  @Mock
  protected static DragonBallUserService dragonBallUserServiceMock;

  protected MockHttpServletRequest request = new MockHttpServletRequest();
  protected MockHttpServletResponse response = new MockHttpServletResponse();
  
  protected void initTestData() {
    testUtils = new DragonBallUserTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    dragonBallUser = testUtils.getSingleTestData();
  }
  
  /**
   * Set id request parameter.
   */
  protected void setIdRequestParameter() {
    request.setParameter("id", dragonBallUser.getId().toString());
  }
  
  /**
   * Set request parameters with dragonball user attributes.
   */
  protected void setDragonBallUserRequestParameters() {
    request.setParameter("username", dragonBallUser.getUsername());
    request.setParameter("email", dragonBallUser.getEmail());
    request.setParameter("age", String.valueOf(dragonBallUser.getAge()));
    request.setParameter("stamina", String.valueOf(dragonBallUser.getStamina()));
    request.setParameter("powerLevel", String.valueOf(dragonBallUser.getPowerLevel()));
  }
}
