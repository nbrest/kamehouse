package com.nicobrest.kamehouse.commons.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Unit tests for the KameHouseUserAuthenticationDaoJpa class.
 *
 * @author nbrest
 */
class KameHouseUserAuthenticationDaoJpaTest
    extends AbstractDaoJpaTest<KameHouseUser, KameHouseUserDto> {

  private KameHouseUser kameHouseUser;
  private KameHouseUserAuthenticationDao kameHouseUserAuthenticationDaoJpa;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public KameHouseUserAuthenticationDaoJpaTest(
      EntityManagerFactory entityManagerFactory,
      KameHouseUserAuthenticationDao kameHouseUserAuthenticationDaoJpa) {
    super(entityManagerFactory);
    this.kameHouseUserAuthenticationDaoJpa = kameHouseUserAuthenticationDaoJpa;
  }

  /**
   * Clear data from the repository before each test.
   */
  @BeforeEach
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
  void loadUserByUsernameTest() {
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
  void loadUserByUsernameNotFoundExceptionTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          kameHouseUserAuthenticationDaoJpa.loadUserByUsername(
              KameHouseUserTestUtils.INVALID_USERNAME);
        });
  }
}
