package com.nicobrest.kamehouse.admin.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Unit tests for the KameHouseUserDaoJpa class.
 *
 * @author nbrest
 */
class KameHouseUserDaoJpaTest
    extends AbstractCrudDaoJpaTest<KameHouseUser, KameHouseUserDto> {

  private KameHouseUserDao kameHouseUserDaoJpa;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public KameHouseUserDaoJpaTest(KameHouseUserDao kameHouseUserDaoJpa,
      EntityManagerFactory entityManagerFactory) {
    super(entityManagerFactory);
    this.kameHouseUserDaoJpa = kameHouseUserDaoJpa;
  }

  @Override
  public Class<KameHouseUser> getEntityClass() {
    return KameHouseUser.class;
  }

  @Override
  public CrudDao<KameHouseUser> getCrudDao() {
    return kameHouseUserDaoJpa;
  }

  @Override
  public TestUtils<KameHouseUser, KameHouseUserDto> getTestUtils() {
    return new KameHouseUserTestUtils();
  }

  @Override
  public String[] getTablesToClear() {
    return new String[]{"KAMEHOUSE_ROLE", "KAMEHOUSE_USER"};
  }

  @Override
  public void updateEntity(KameHouseUser entity) {
    entity.setEmail("gokuUpdatedEmail@dbz.com");
  }

  @Override
  public void updateEntityServerError(KameHouseUser entity) {
    entity.setUsername(getInvalidString());
    entity.setPassword(getInvalidString());
  }

  /**
   * Test for getting a single KameHouseUser in the repository by username.
   */
  @Test
  void loadUserByUsernameTest() {
    KameHouseUser kameHouseUser = testUtils.getSingleTestData();
    mergeEntityInRepository(kameHouseUser);

    KameHouseUser returnedUser =
        kameHouseUserDaoJpa.loadUserByUsername(kameHouseUser.getUsername());

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
          kameHouseUserDaoJpa.loadUserByUsername(KameHouseUserTestUtils.INVALID_USERNAME);
        });
  }
}
