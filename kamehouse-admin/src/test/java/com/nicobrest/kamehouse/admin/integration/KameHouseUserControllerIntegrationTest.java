package com.nicobrest.kamehouse.admin.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractCrudControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Integration tests for the KameHouseUserController class.
 *
 * @author nbrest
 */
@TestMethodOrder(OrderAnnotation.class)
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
  @Test
  @Order(1)
  public void createTest() throws Exception {
    logger.info("Running createTest");
  }

  /**
   * Creates an user conflict exception.
   */
  @Test
  @Order(2)
  public void createConflictExceptionTest() throws Exception {
    logger.info("Running createConflictExceptionTest");
  }

  /**
   * Gets a specific user from the repository.
   */
  @Test
  @Order(3)
  public void readTest() throws Exception {
    logger.info("Running readTest");
  }

  /**
   * Gets all KameHouseUsers.
   */
  @Test
  @Order(4)
  public void readAllTest() throws Exception {
    readAllCrudTest();
  }

  /**
   * Updates an user.
   */
  @Test
  @Order(5)
  public void updateTest() throws Exception {
    logger.info("Running updateTest");
  }

  /**
   * Updates an user with invalid path id. Exception expected.
   */
  @Test
  @Order(6)
  public void updateInvalidPathId() throws Exception {
    logger.info("Running updateInvalidPathId");
  }

  /**
   * Updates an user not found.
   */
  @Test
  @Order(7)
  public void updateNotFoundExceptionTest() throws Exception {
    logger.info("Running updateNotFoundExceptionTest");
  }

  /**
   * Deletes an user.
   */
  @Test
  @Order(8)
  public void deleteTest() throws Exception {
    logger.info("Running deleteTest");
  }

  /**
   * Deletes an user not found.
   */
  @Test
  @Order(9)
  public void deleteNotFoundExceptionTest() throws Exception {
    logger.info("Running deleteNotFoundExceptionTest");
  }

  /**
   * Gets an kamehouse user.
   */
  @Test
  @Order(10)
  public void loadUserByUsernameTest() throws Exception {
    logger.info("Running loadUserByUsernameTest");
  }

  /**
   * Tests get user not found exception.
   */
  @Test
  @Order(11)
  public void loadUserByUsernameNotFoundExceptionTest() throws Exception {
    logger.info("Running loadUserByUsernameTest");
  }
}
