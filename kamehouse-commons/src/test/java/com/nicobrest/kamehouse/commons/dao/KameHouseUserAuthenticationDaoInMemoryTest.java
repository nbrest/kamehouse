package com.nicobrest.kamehouse.commons.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

  /**
   * Initializes test repositories.
   */
  @BeforeEach
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
    assertThrows(UsernameNotFoundException.class, () -> {
      kameHouseUserAuthenticationDao.loadUserByUsername(KameHouseUserTestUtils.INVALID_USERNAME);
    });
  }
}
