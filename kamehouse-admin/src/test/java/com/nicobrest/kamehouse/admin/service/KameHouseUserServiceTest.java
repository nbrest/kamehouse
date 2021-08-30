package com.nicobrest.kamehouse.admin.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.dao.KameHouseUserDao;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the KameHouseUserService class.
 *
 * @author nbrest
 */
public class KameHouseUserServiceTest
    extends AbstractCrudServiceTest<KameHouseUser, KameHouseUserDto> {

  private KameHouseUser kameHouseUser;

  @InjectMocks
  private KameHouseUserService kameHouseUserService;

  @Mock
  private KameHouseUserDao kameHouseUserDaoMock;

  /**
   * Resets mock objects and initializes test repository.
   */
  @BeforeEach
  public void beforeTest() {
    testUtils = new KameHouseUserTestUtils();
    testUtils.initTestData();
    kameHouseUser = testUtils.getSingleTestData();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(kameHouseUserDaoMock);
  }

  /**
   * Tests calling the service to create an KameHouseUser in the repository.
   */
  @Test
  public void createTest() {
    createTest(kameHouseUserService, kameHouseUserDaoMock);
  }

  /**
   * Tests calling the service to get a single KameHouseUser.
   */
  @Test
  public void readTest() {
    readTest(kameHouseUserService, kameHouseUserDaoMock);
  }

  /**
   * Tests getting all users of the application.
   */
  @Test
  public void readAllTest() {
    readAllTest(kameHouseUserService, kameHouseUserDaoMock);
  }

  /**
   * Tests calling the service to update an existing KameHouseUser in the
   * repository.
   */
  @Test
  public void updateTest() {
    updateTest(kameHouseUserService, kameHouseUserDaoMock);
  }

  /**
   * Tests calling the service to delete an existing user in the repository.
   */
  @Test
  public void deleteTest() {
    deleteTest(kameHouseUserService, kameHouseUserDaoMock);
  }

  /**
   * Tests calling the service to get a single KameHouseUser in the repository
   * by username.
   */
  @Test
  public void loadUserByUsernameTest() {
    when(kameHouseUserDaoMock.loadUserByUsername(kameHouseUser.getUsername()))
        .thenReturn(kameHouseUser);

    KameHouseUser returnedUser =
        kameHouseUserService.loadUserByUsername(kameHouseUser.getUsername());

    testUtils.assertEqualsAllAttributes(kameHouseUser, returnedUser);
    verify(kameHouseUserDaoMock, times(1)).loadUserByUsername(kameHouseUser.getUsername());
  }
}
