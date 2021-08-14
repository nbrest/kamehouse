package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.TennisWorldUserDto;
import com.nicobrest.kamehouse.tennisworld.testutils.TennisWorldUserTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests for the TennisWorldUserService class.
 *
 * @author nbrest
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ EncryptionUtils.class })
public class TennisWorldUserServiceTest extends
    AbstractCrudServiceTest<TennisWorldUser, TennisWorldUserDto> {

  private TennisWorldUser tennisWorldUser;

  @InjectMocks
  private TennisWorldUserService tennisWorldUserService;

  @Mock(name = "TennisWorldUserDao")
  private CrudDao<TennisWorldUser> tennisWorldUserDaoMock;

  /**
   * Resets mock objects and initializes test repository.
   */
  @Before
  public void beforeTest() {
    testUtils = new TennisWorldUserTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    tennisWorldUser = testUtils.getSingleTestData();

    // Reset mock objects before each test
    MockitoAnnotations.initMocks(this);
    PowerMockito.mockStatic(EncryptionUtils.class);
    Mockito.reset(tennisWorldUserDaoMock);
  }

  /**
   * Tests calling the service to create a TennisWorldUser in the repository.
   */
  @Test
  public void createEntityTest() {
    createTest(tennisWorldUserService, tennisWorldUserDaoMock);
  }

  /**
   * Tests calling the service to get a single TennisWorldUser in the
   * repository by id.
   */
  @Test
  public void readEntityTest() {
    readTest(tennisWorldUserService, tennisWorldUserDaoMock);
  }

  /**
   * Tests calling the service to get all the TennisWorldUsers in the
   * repository.
   */
  @Test
  public void readAllEntitiesTest() {
    readAllTest(tennisWorldUserService, tennisWorldUserDaoMock);
  }

  /**
   * Tests calling the service to update an existing TennisWorldUser in the
   * repository.
   */
  @Test
  public void updateEntityTest() {
    updateTest(tennisWorldUserService, tennisWorldUserDaoMock);
  }

  /**
   * Tests calling the service to delete an existing user in the repository.
   */
  @Test
  public void deleteEntityTest() {
    deleteTest(tennisWorldUserService, tennisWorldUserDaoMock);
  }
}
