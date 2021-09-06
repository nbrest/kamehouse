package com.nicobrest.kamehouse.tennisworld.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractCrudControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.TennisWorldUserDto;
import com.nicobrest.kamehouse.tennisworld.testutils.TennisWorldUserTestUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Integration tests for the TennisWorldUserController class.
 *
 * @author nbrest
 */
public class TennisWorldUserControllerIntegrationTest
    extends AbstractCrudControllerIntegrationTest<TennisWorldUser, TennisWorldUserDto> {

  @Override
  public String getWebapp() {
    return "kame-house-tennisworld";
  }

  @Override
  public String getCrudUrlSuffix() {
    return TennisWorldUserTestUtils.API_V1_TENNISWORLD_USERS;
  }

  @Override
  public Class<TennisWorldUser> getEntityClass() {
    return TennisWorldUser.class;
  }

  @Override
  public TestUtils<TennisWorldUser, TennisWorldUserDto> getTestUtils() {
    return new TennisWorldUserTestUtils();
  }

  @Override
  public TennisWorldUserDto buildDto(TennisWorldUserDto dto) {
    String randomUsername = RandomStringUtils.randomAlphabetic(12);
    dto.setEmail(randomUsername + "@dbz.com");
    return dto;
  }

  @Override
  public void updateDto(TennisWorldUserDto dto) {
    dto.setPassword(RandomStringUtils.randomAlphabetic(12));
  }
}
