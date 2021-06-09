package com.nicobrest.kamehouse.commons.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import com.nicobrest.kamehouse.commons.model.ApplicationUser;
import com.nicobrest.kamehouse.commons.model.dto.ApplicationUserDto;
import com.nicobrest.kamehouse.commons.testutils.ApplicationUserTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Unit tests for the ApplicationUserAuthenticationDaoInMemory class.
 *
 * @author nbrest
 */
public class ApplicationUserAuthenticationDaoInMemoryTest {

  private TestUtils<ApplicationUser, ApplicationUserDto> testUtils;
  private ApplicationUser applicationUser;
  private ApplicationUserAuthenticationDaoInMemory applicationUserAuthenticationDao;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Initializes test repositories.
   */
  @Before
  public void init() {
    testUtils = new ApplicationUserTestUtils();
    testUtils.initTestData();
    applicationUser = testUtils.getSingleTestData();
    applicationUserAuthenticationDao = new ApplicationUserAuthenticationDaoInMemory();
  }

  /**
   * Tests getting a single ApplicationUser in the repository by its username.
   */
  @Test
  public void loadUserByUsernameTest() {
    ApplicationUser user = applicationUserAuthenticationDao.loadUserByUsername("admin");

    assertNotNull(user);
    assertEquals("admin", user.getUsername());
  }

  /**
   * Tests getting a single ApplicationUser in the repository Exception flows.
   */
  @Test
  public void loadUserByUsernameNotFoundExceptionTest() {
    thrown.expect(UsernameNotFoundException.class);
    thrown.expectMessage(
        "User with username " + ApplicationUserTestUtils.INVALID_USERNAME + " not found.");

    applicationUserAuthenticationDao.loadUserByUsername(ApplicationUserTestUtils.INVALID_USERNAME);
  }
}
