package com.nicobrest.kamehouse.admin.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.dao.KameHouseUserDao;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Unit tests for the KameHouseUserService class.
 *
 * @author nbrest
 */
class KameHouseUserServiceTest
    extends AbstractCrudServiceTest<KameHouseUser, KameHouseUserDto> {

  @InjectMocks
  private KameHouseUserService kameHouseUserService;

  @Mock
  private KameHouseUserDao kameHouseUserDaoMock;

  @Override
  public CrudService<KameHouseUser, KameHouseUserDto> getCrudService() {
    return kameHouseUserService;
  }

  @Override
  public CrudDao<KameHouseUser> getCrudDao() {
    return kameHouseUserDaoMock;
  }

  @Override
  public TestUtils<KameHouseUser, KameHouseUserDto> getTestUtils() {
    return new KameHouseUserTestUtils();
  }

  /**
   * Tests calling the service to get a single KameHouseUser in the repository by username.
   */
  @Test
  void loadUserByUsernameTest() {
    KameHouseUser kameHouseUser = testUtils.getSingleTestData();
    when(kameHouseUserDaoMock.loadUserByUsername(kameHouseUser.getUsername()))
        .thenReturn(kameHouseUser);

    KameHouseUser returnedUser =
        kameHouseUserService.loadUserByUsername(kameHouseUser.getUsername());

    testUtils.assertEqualsAllAttributes(kameHouseUser, returnedUser);
    verify(kameHouseUserDaoMock, times(1)).loadUserByUsername(kameHouseUser.getUsername());
  }
}
