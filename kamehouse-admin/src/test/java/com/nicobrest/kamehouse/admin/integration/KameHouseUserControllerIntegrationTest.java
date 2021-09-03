package com.nicobrest.kamehouse.admin.integration;

import static org.junit.jupiter.api.Assertions.fail;

import com.nicobrest.kamehouse.commons.integration.AbstractCrudControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the KameHouseUserController class.
 *
 * @author nbrest
 */
public class KameHouseUserControllerIntegrationTest
    extends AbstractCrudControllerIntegrationTest<KameHouseUser, KameHouseUserDto> {

  private static final String WEBAPP = "/kame-house-admin";

  @Override
  public String getCrudUrl() {
    return getBaseUrl() + WEBAPP + KameHouseUserTestUtils.API_V1_ADMIN_KAMEHOUSE_USERS;
  }

  @Override
  public Class<KameHouseUser> getEntityClass() {
    return KameHouseUser.class;
  }

  @Override
  public Class<KameHouseUserDto> getDtoClass() {
    return KameHouseUserDto.class;
  }

  /**
   * Creates a user.
   */
  //@Test
  public void createTest() throws Exception {
    fail("fail");
  }

  /**
   * Creates an user conflict exception.
   */
  //@Test
  public void createConflictExceptionTest() throws Exception {
    fail("fail");
  }

  /**
   * Gets a specific user from the repository.
   */
  //@Test
  public void readTest() throws Exception {
    fail("fail");
  }

  /**
   * Gets all KameHouseUsers.
   */
  @Test
  public void readAllTest() throws Exception {
    readAllCrudTest();
  }

  /**
   * Updates an user.
   */
  //@Test
  public void updateTest() throws Exception {
    fail("fail");
  }

  /**
   * Updates an user with invalid path id. Exception expected.
   */
  //@Test
  public void updateInvalidPathId() throws Exception {
    fail("fail");
  }

  /**
   * Updates an user not found.
   */
  //@Test
  public void updateNotFoundExceptionTest() throws Exception {
    fail("fail");
  }

  /**
   * Deletes an user.
   */
  //@Test
  public void deleteTest() throws Exception {
    fail("fail");
  }

  /**
   * Deletes an user not found.
   */
  //@Test
  public void deleteNotFoundExceptionTest() throws Exception {
    fail("fail");
  }

  /**
   * Gets an kamehouse user.
   */
  //@Test
  public void loadUserByUsernameTest() throws Exception {
    fail("fail");
  }

  /**
   * Tests get user not found exception.
   */
  //@Test
  public void loadUserByUsernameNotFoundExceptionTest() throws Exception {
    fail("fail");
  }
}
