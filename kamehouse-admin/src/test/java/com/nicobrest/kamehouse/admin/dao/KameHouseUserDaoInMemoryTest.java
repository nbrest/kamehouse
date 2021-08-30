package com.nicobrest.kamehouse.admin.dao;

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
 * Unit tests for the KameHouseUserInMemoryDao class.
 *
 * @author nbrest
 */
public class KameHouseUserDaoInMemoryTest {

  private TestUtils<KameHouseUser, KameHouseUserDto> testUtils;
  private KameHouseUser kameHouseUser;
  private KameHouseUserDaoInMemory kameHouseUserDao;


  /**
   * Initializes test repositories.
   */
  @BeforeEach
  public void init() {
    testUtils = new KameHouseUserTestUtils();
    testUtils.initTestData();
    kameHouseUser = testUtils.getSingleTestData();
    kameHouseUserDao = new KameHouseUserDaoInMemory();
  }

  /**
   * Tests creating a KameHouseUser in the repository.
   */
  @Test
  public void createTest() {
    kameHouseUserDao.create(kameHouseUser);

    KameHouseUser createdUser =
        kameHouseUserDao.loadUserByUsername(kameHouseUser.getUsername());
    
    testUtils.assertEqualsAllAttributes(kameHouseUser, createdUser);
  }

  /**
   * Tests getting all the KameHouseUser in the repository.
   */
  @Test
  public void readAllTest() {
    assertEquals(4, kameHouseUserDao.readAll().size());
  }

  /**
   * Tests updating an existing user in the repository.
   */
  @Test
  public void updateTest() {
    KameHouseUser originalUser = kameHouseUserDao.loadUserByUsername("admin");
    kameHouseUser.setId(originalUser.getId());
    kameHouseUser.setUsername(originalUser.getUsername());

    kameHouseUserDao.update(kameHouseUser);

    KameHouseUser updatedUser = kameHouseUserDao.loadUserByUsername("admin");
    testUtils.assertEqualsAllAttributes(kameHouseUser, updatedUser);
  }

  /**
   * Tests updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateNotFoundExceptionTest() {
    kameHouseUser.setUsername(KameHouseUserTestUtils.INVALID_USERNAME);
    assertThrows(UsernameNotFoundException.class, () -> {
      kameHouseUserDao.update(kameHouseUser);
    });
  }

  /**
   * Tests deleting an existing user from the repository.
   */
  @Test
  public void deleteTest() {
    KameHouseUser userToDelete = kameHouseUserDao.loadUserByUsername("admin");

    KameHouseUser deletedUser = kameHouseUserDao.delete(userToDelete.getId());

    testUtils.assertEqualsAllAttributes(userToDelete, deletedUser);
  }

  /**
   * Tests deleting an existing user from the repository Exception flows.
   */
  @Test
  public void deleteNotFoundExceptionTest() {
    assertThrows(UsernameNotFoundException.class, () -> {
      kameHouseUserDao.delete(KameHouseUserTestUtils.INVALID_ID);
    });
  }

  /**
   * Tests getting a single KameHouseUser in the repository by its username.
   */
  @Test
  public void loadUserByUsernameTest() {
    KameHouseUser user = kameHouseUserDao.loadUserByUsername("admin");

    assertNotNull(user);
    assertEquals("admin", user.getUsername());
  }

  /**
   * Tests getting a single KameHouseUser in the repository Exception flows.
   */
  @Test
  public void loadUserByUsernameNotFoundExceptionTest() {
    assertThrows(UsernameNotFoundException.class, () -> {
      kameHouseUserDao.loadUserByUsername(KameHouseUserTestUtils.INVALID_USERNAME);
    });
  }
}
