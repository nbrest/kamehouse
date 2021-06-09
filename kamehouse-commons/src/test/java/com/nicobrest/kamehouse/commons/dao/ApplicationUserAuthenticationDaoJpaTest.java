package com.nicobrest.kamehouse.commons.dao;

import com.nicobrest.kamehouse.commons.model.ApplicationUser;
import com.nicobrest.kamehouse.commons.model.dto.ApplicationUserDto;
import com.nicobrest.kamehouse.commons.testutils.ApplicationUserTestUtils;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit tests for the ApplicationUserAuthenticationDaoJpa class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class ApplicationUserAuthenticationDaoJpaTest
    extends AbstractCrudDaoJpaTest<ApplicationUser, ApplicationUserDto> {

  private ApplicationUser applicationUser;

  @Autowired
  private ApplicationUserAuthenticationDao applicationUserAuthenticationDaoJpa;

  /**
   * Clear data from the repository before each test.
   */
  @Before
  public void setUp() {
    testUtils = new ApplicationUserTestUtils();
    testUtils.initTestData();
    testUtils.removeIds();
    applicationUser = testUtils.getSingleTestData();

    clearTable("APPLICATION_ROLE");
    clearTable("APPLICATION_USER");
  }

  /**
   * Test for getting a single ApplicationUser in the repository by username.
   */
  @Test
  public void loadUserByUsernameTest() {
    mergeEntityInRepository(applicationUser);

    ApplicationUser returnedUser =
        applicationUserAuthenticationDaoJpa.loadUserByUsername(applicationUser.getUsername());

    applicationUser.setId(returnedUser.getId());
    testUtils.assertEqualsAllAttributes(applicationUser, returnedUser);
  }

  /**
   * Test for getting a single ApplicationUser in the repository Exception flows.
   */
  @Test
  public void loadUserByUsernameNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("Entity not found in the repository.");

    applicationUserAuthenticationDaoJpa.loadUserByUsername(ApplicationUserTestUtils.INVALID_USERNAME);
  }
}
