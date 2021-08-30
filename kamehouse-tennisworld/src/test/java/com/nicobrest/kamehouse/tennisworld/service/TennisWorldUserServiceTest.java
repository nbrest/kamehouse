package com.nicobrest.kamehouse.tennisworld.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.tennisworld.dao.TennisWorldUserDao;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.TennisWorldUserDto;
import com.nicobrest.kamehouse.tennisworld.testutils.TennisWorldUserTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the TennisWorldUserService class.
 *
 * @author nbrest
 */
public class TennisWorldUserServiceTest
    extends AbstractCrudServiceTest<TennisWorldUser, TennisWorldUserDto> {

  private TennisWorldUser tennisWorldUser;

  @InjectMocks
  private TennisWorldUserService tennisWorldUserService;

  @Mock(name = "tennisWorldUserDao")
  private TennisWorldUserDao tennisWorldUserDaoMock;

  private MockedStatic<EncryptionUtils> encryptionUtilsMock;

  /**
   * Resets mock objects and initializes test repository.
   */
  @BeforeEach
  public void beforeTest() {
    testUtils = new TennisWorldUserTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    tennisWorldUser = testUtils.getSingleTestData();

    // Reset mock objects before each test
    MockitoAnnotations.openMocks(this);
    encryptionUtilsMock = Mockito.mockStatic(EncryptionUtils.class);
    Mockito.reset(tennisWorldUserDaoMock);
  }

  @AfterEach
  public void close() {
    encryptionUtilsMock.close();
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

  /**
   * Tests calling the service to get a single TennisWorldUser in the
   * repository by its email.
   */
  @Test
  public void getByEmailTest() {
    when(tennisWorldUserDaoMock.getByEmail(tennisWorldUser.getEmail())).thenReturn(tennisWorldUser);

    TennisWorldUser returnedUser = tennisWorldUserService.getByEmail(tennisWorldUser.getEmail());

    testUtils.assertEqualsAllAttributes(tennisWorldUser, returnedUser);
    verify(tennisWorldUserDaoMock, times(1)).getByEmail(tennisWorldUser.getEmail());
  }
}
