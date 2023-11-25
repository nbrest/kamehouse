package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import com.nicobrest.kamehouse.commons.controller.AbstractCrudControllerTest;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.TennisWorldUserDto;
import com.nicobrest.kamehouse.tennisworld.service.TennisWorldUserService;
import com.nicobrest.kamehouse.tennisworld.testutils.TennisWorldUserTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Unit tests for the TennisWorldUserController class.
 *
 * @author nbrest
 */
class TennisWorldUserControllerTest
    extends AbstractCrudControllerTest<TennisWorldUser, TennisWorldUserDto> {

  @InjectMocks
  private TennisWorldUserController tennisWorldUserController;

  @Mock(name = "tennisWorldUserService")
  private TennisWorldUserService tennisWorldUserServiceMock;

  @Override
  public String getCrudUrl() {
    return TennisWorldUserTestUtils.API_V1_TENNISWORLD_USERS;
  }

  @Override
  public Class<TennisWorldUser> getEntityClass() {
    return TennisWorldUser.class;
  }

  @Override
  public CrudService<TennisWorldUser, TennisWorldUserDto> getCrudService() {
    return tennisWorldUserServiceMock;
  }

  @Override
  public TestUtils<TennisWorldUser, TennisWorldUserDto> getTestUtils() {
    return new TennisWorldUserTestUtils();
  }

  @Override
  public AbstractController getController() {
    return tennisWorldUserController;
  }
}
