package com.nicobrest.kamehouse.commons.dao;

import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit tests for the KameHouseUserAuthenticationDaoJpa class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class KameHouseUserAuthenticationDaoJpaTest
    extends AbstractCrudDaoJpaTest<KameHouseUser, KameHouseUserDto> {

  private KameHouseUser kameHouseUser;

  @Autowired
  private KameHouseUserAuthenticationDao kameHouseUserAuthenticationDaoJpa;

  /**
   * Clear data from the repository before each test.
   */
  @Before
  public void setUp() {
    testUtils = new KameHouseUserTestUtils();
    testUtils.initTestData();
    testUtils.removeIds();
    kameHouseUser = testUtils.getSingleTestData();

    clearTable("KAMEHOUSE_ROLE");
    clearTable("KAMEHOUSE_USER");
  }

  /**
   * Test for getting a single KameHouseUser in the repository by username.
   */
  @Test
  public void loadUserByUsernameTest() {
    mergeEntityInRepository(kameHouseUser);

    KameHouseUser returnedUser =
        kameHouseUserAuthenticationDaoJpa.loadUserByUsername(kameHouseUser.getUsername());

    kameHouseUser.setId(returnedUser.getId());
    testUtils.assertEqualsAllAttributes(kameHouseUser, returnedUser);
  }

  /**
   * Test for getting a single KameHouseUser in the repository Exception flows.
   */
  @Test
  public void loadUserByUsernameNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("Entity not found in the repository.");

    kameHouseUserAuthenticationDaoJpa.loadUserByUsername(KameHouseUserTestUtils.INVALID_USERNAME);
  }
}
