package com.nicobrest.kamehouse.testmodule.testutils;

import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.service.dto.DragonBallUserDto;

import java.util.LinkedList;
import java.util.List;

/**
 * Test data and common test methods to test DragonBallUsers in all layers of the application.
 * 
 * @author nicolas.brest
 *
 */
public class DragonBallUserTestUtils {
  
  public static final String API_V1_DRAGONBALL_USERS = "/api/v1/dragonball/users/";
  public static final Long INVALID_ID = 987987L;
  public static final String INVALID_USERNAME = "yukimura";
  public static final String INVALID_EMAIL = "yukimura@dbz.com";
  private static DragonBallUser singleTestData;
  private static List<DragonBallUser> testDataList;
  private static DragonBallUserDto testDataDto;
  
  static {
    initTestData();
  }
  
  public static DragonBallUser getSingleTestData() {
    return singleTestData;
  }

  public static List<DragonBallUser> getTestDataList() {
    return testDataList;
  }

  public static DragonBallUserDto getTestDataDto() {
    return testDataDto;
  }

  public static void initTestData() {
    initSingleTestData();
    initTestDataList();
    initTestDataDto();
  }
  
  public static void initSingleTestData() {
    singleTestData = new DragonBallUser(null, "gokuTestMock", "gokuTestMock@dbz.com", 49, 30,
        1000);
  }
  
  public static void initTestDataDto() {
    testDataDto = new DragonBallUserDto(null, "gokuTestMock", "gokuTestMock@dbz.com", 49, 30,
        1000);
  }
  
  public static void initTestDataList() { 
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
  
  public static void setIds() {
    if (testDataDto != null) {
      testDataDto.setId(100L);
    }
    if (testDataList != null && testDataList.get(0) != null) {
      testDataList.get(0).setId(100L);
    }
    if (testDataList != null && testDataList.get(1) != null) {
      testDataList.get(1).setId(101L);
    }
    if (testDataList != null && testDataList.get(2) != null) {
      testDataList.get(2).setId(102L);
    } 
  }
  
  public static void removeIds() {
    if (testDataDto != null) {
      testDataDto.setId(null);
    }
    if (testDataList != null && testDataList.get(0) != null) {
      testDataList.get(0).setId(null);
    }
    if (testDataList != null && testDataList.get(1) != null) {
      testDataList.get(1).setId(null);
    }
    if (testDataList != null && testDataList.get(2) != null) {
      testDataList.get(2).setId(null);
    } 
  }
}
