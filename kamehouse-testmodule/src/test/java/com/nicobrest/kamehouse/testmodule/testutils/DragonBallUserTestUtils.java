package com.nicobrest.kamehouse.testmodule.testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import java.util.LinkedList;

/**
 * Test data and common test methods to test DragonBallUsers in all layers of the application.
 *
 * @author nbrest
 */
public class DragonBallUserTestUtils extends AbstractTestUtils<DragonBallUser, DragonBallUserDto>
    implements TestUtils<DragonBallUser, DragonBallUserDto> {

  public static final String API_V1_DRAGONBALL_USERS = "/api/v1/test-module/dragonball/users/";
  public static final Long INVALID_ID = 987987L;
  public static final String INVALID_USERNAME = "yukimura";
  public static final String INVALID_EMAIL = "yukimura@dbz.com";

  @Override
  public void initTestData() {
    initSingleTestData();
    initTestDataList();
    initTestDataDto();
  }

  @Override
  public void assertEqualsAllAttributes(
      DragonBallUser expectedEntity, DragonBallUser returnedEntity) {
    assertEquals(expectedEntity, returnedEntity);
    assertEquals(expectedEntity.getId(), returnedEntity.getId());
    assertEquals(expectedEntity.getUsername(), returnedEntity.getUsername());
    assertEquals(expectedEntity.getEmail(), returnedEntity.getEmail());
    assertEquals(expectedEntity.getAge(), returnedEntity.getAge());
    assertEquals(expectedEntity.getPowerLevel(), returnedEntity.getPowerLevel());
    assertEquals(expectedEntity.getStamina(), returnedEntity.getStamina());
  }

  private void initSingleTestData() {
    singleTestData = new DragonBallUser(null, "gokuTestMock", "gokuTestMock@dbz.com", 49, 30, 1000);
  }

  private void initTestDataDto() {
    testDataDto = new DragonBallUserDto(null, "gokuTestMock", "gokuTestMock@dbz.com", 49, 30, 1000);
  }

  private void initTestDataList() {
    DragonBallUser user2 = new DragonBallUser();
    user2.setId(null);
    user2.setAge(29);
    user2.setEmail("gohanTestMock@dbz.com");
    user2.setUsername("gohanTestMock");
    user2.setPowerLevel(20);
    user2.setStamina(1000);

    DragonBallUser user3 = new DragonBallUser();
    user3.setId(null);
    user3.setAge(19);
    user3.setEmail("gotenTestMock@dbz.com");
    user3.setUsername("gotenTestMock");
    user3.setPowerLevel(10);
    user3.setStamina(1000);

    testDataList = new LinkedList<DragonBallUser>();
    testDataList.add(singleTestData);
    testDataList.add(user2);
    testDataList.add(user3);
  }
}
