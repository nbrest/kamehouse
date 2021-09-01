package com.nicobrest.kamehouse.tennisworld.testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.TennisWorldUserDto;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import org.apache.commons.codec.Charsets;

/**
 * Test data and common test methods to test TennisWorldUsers in all layers of the application.
 *
 * @author nbrest
 */
public class TennisWorldUserTestUtils extends AbstractTestUtils<TennisWorldUser, TennisWorldUserDto>
    implements TestUtils<TennisWorldUser, TennisWorldUserDto> {

  public static final String API_V1_TENNISWORLD_USERS = "/api/v1/tennis-world/users/";
  public static final String INVALID_EMAIL = "yukimura@dbz.com";

  @Override
  public void initTestData() {
    initSingleTestData();
    initTestDataList();
    initTestDataDto();
  }

  @Override
  public void assertEqualsAllAttributes(
      TennisWorldUser expectedEntity, TennisWorldUser returnedEntity) {
    assertEquals(expectedEntity, returnedEntity);
    assertEquals(expectedEntity.getId(), returnedEntity.getId());
    assertEquals(expectedEntity.getEmail(), returnedEntity.getEmail());
    assertEquals(
        new String(expectedEntity.getPassword(), StandardCharsets.UTF_8),
        new String(returnedEntity.getPassword(), StandardCharsets.UTF_8));
  }

  private void initSingleTestData() {
    singleTestData = new TennisWorldUser();
    singleTestData.setEmail("goku@dbz.com");
    singleTestData.setPassword("mada mada dane".getBytes(Charsets.UTF_8));
  }

  private void initTestDataDto() {
    testDataDto = new TennisWorldUserDto();
    testDataDto.setEmail("goku@dbz.com");
    testDataDto.setPassword("mada mada dane");
  }

  private void initTestDataList() {
    TennisWorldUser user2 = new TennisWorldUser();
    user2.setId(null);
    user2.setEmail("gohanTestMock@dbz.com");
    user2.setPassword("pegasus seiya".getBytes(Charsets.UTF_8));

    TennisWorldUser user3 = new TennisWorldUser();
    user3.setId(null);
    user3.setEmail("gotenTestMock@dbz.com");
    user3.setPassword("mada mada".getBytes(Charsets.UTF_8));

    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    testDataList.add(user2);
    testDataList.add(user3);
  }
}
