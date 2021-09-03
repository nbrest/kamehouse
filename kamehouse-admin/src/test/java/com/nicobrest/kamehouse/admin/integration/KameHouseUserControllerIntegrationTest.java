package com.nicobrest.kamehouse.admin.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractCrudControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Order;
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

  @Override
  public KameHouseUser createEntity() {
    KameHouseUser kameHouseUser = testUtils.getSingleTestData();
    kameHouseUser.setId(null);
    String randomUsername = RandomStringUtils.randomAlphabetic(12);
    kameHouseUser.setUsername(randomUsername);
    kameHouseUser.setEmail(randomUsername + "@dbz.com");
    return kameHouseUser;
  }

  public KameHouseUserControllerIntegrationTest() {
    testUtils = new KameHouseUserTestUtils();
    testUtils.initTestData();
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
