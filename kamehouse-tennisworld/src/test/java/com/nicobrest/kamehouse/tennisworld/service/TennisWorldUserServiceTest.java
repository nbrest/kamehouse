package com.nicobrest.kamehouse.tennisworld.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.tennisworld.dao.TennisWorldUserDao;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.TennisWorldUserDto;
import com.nicobrest.kamehouse.tennisworld.testutils.TennisWorldUserTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Unit tests for the TennisWorldUserService class.
 *
 * @author nbrest
 */
public class TennisWorldUserServiceTest
    extends AbstractCrudServiceTest<TennisWorldUser, TennisWorldUserDto> {

  @InjectMocks
  private TennisWorldUserService tennisWorldUserService;

  @Mock(name = "tennisWorldUserDao")
  private TennisWorldUserDao tennisWorldUserDaoMock;

  private MockedStatic<EncryptionUtils> encryptionUtilsMock;

  @Override
  public void initBeforeTest() {
    encryptionUtilsMock = Mockito.mockStatic(EncryptionUtils.class);
  }

  @Override
  public CrudService<TennisWorldUser, TennisWorldUserDto> getCrudService() {
    return tennisWorldUserService;
  }

  @Override
  public CrudDao<TennisWorldUser> getCrudDao() {
    return tennisWorldUserDaoMock;
  }

  @Override
  public TestUtils<TennisWorldUser, TennisWorldUserDto> getTestUtils() {
    return new TennisWorldUserTestUtils();
  }

  @AfterEach
  public void close() {
    encryptionUtilsMock.close();
  }

  /**
   * Tests calling the service to get a single TennisWorldUser in the repository by its email.
   */
  @Test
  void getByEmailTest() {
    TennisWorldUser tennisWorldUser = testUtils.getSingleTestData();
    when(tennisWorldUserDaoMock.getByEmail(tennisWorldUser.getEmail())).thenReturn(tennisWorldUser);

    TennisWorldUser returnedUser = tennisWorldUserService.getByEmail(tennisWorldUser.getEmail());

    testUtils.assertEqualsAllAttributes(tennisWorldUser, returnedUser);
    verify(tennisWorldUserDaoMock, times(1)).getByEmail(tennisWorldUser.getEmail());
  }
}
