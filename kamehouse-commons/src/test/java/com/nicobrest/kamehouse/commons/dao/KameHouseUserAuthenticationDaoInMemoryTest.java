package com.nicobrest.kamehouse.commons.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Unit tests for the KameHouseUserAuthenticationDaoInMemory class.
 *
 * @author nbrest
 */
public class KameHouseUserAuthenticationDaoInMemoryTest {

  private TestUtils<KameHouseUser, KameHouseUserDto> testUtils;
  private KameHouseUser kameHouseUser;
  private KameHouseUserAuthenticationDaoInMemory kameHouseUserAuthenticationDao;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Initializes test repositories.
   */
  @Before
  public void init() {
    testUtils = new KameHouseUserTestUtils();
    testUtils.initTestData();
    kameHouseUser = testUtils.getSingleTestData();
    kameHouseUserAuthenticationDao = new KameHouseUserAuthenticationDaoInMemory();
  }

  /**
   * Tests getting a single KameHouseUser in the repository by its username.
   */
  @Test
  public void loadUserByUsernameTest() {
    KameHouseUser user = kameHouseUserAuthenticationDao.loadUserByUsername("admin");

    assertNotNull(user);
    assertEquals("admin", user.getUsername());
  }

  /**
   * Tests getting a single KameHouseUser in the repository Exception flows.
   */
  @Test
  public void loadUserByUsernameNotFoundExceptionTest() {
    thrown.expect(UsernameNotFoundException.class);
    thrown.expectMessage(
        "User with username " + KameHouseUserTestUtils.INVALID_USERNAME + " not found.");

    kameHouseUserAuthenticationDao.loadUserByUsername(KameHouseUserTestUtils.INVALID_USERNAME);
  }
}
