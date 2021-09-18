package com.nicobrest.kamehouse.tennisworld.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractCrudControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingScheduleConfigDto;
import com.nicobrest.kamehouse.tennisworld.model.dto.TennisWorldUserDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingScheduleConfigTestUtils;
import java.io.IOException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the BookingScheduleConfigController class.
 *
 * @author nbrest
 */
public class BookingScheduleConfigControllerIntegrationTest
    extends AbstractCrudControllerIntegrationTest<BookingScheduleConfig, BookingScheduleConfigDto> {

  TennisWorldUserControllerIntegrationTest tennisWorldUserControllerIntegrationTest =
      new TennisWorldUserControllerIntegrationTest();

  @Override
  public String getWebapp() {
    return "kame-house-tennisworld";
  }

  @Override
  public String getCrudUrlSuffix() {
    return BookingScheduleConfigTestUtils.API_V1_TENNISWORLD_BOOKING_SCHEDULE_CONFIG;
  }

  @Override
  public Class<BookingScheduleConfig> getEntityClass() {
    return BookingScheduleConfig.class;
  }

  @Override
  public TestUtils<BookingScheduleConfig, BookingScheduleConfigDto> getTestUtils() {
    return new BookingScheduleConfigTestUtils();
  }

  @Override
  public BookingScheduleConfigDto buildDto(BookingScheduleConfigDto dto) {
    TennisWorldUserDto tennisWorldUserDto = tennisWorldUserControllerIntegrationTest.getDto();
    String email = tennisWorldUserDto.getEmail();
    TennisWorldUser tennisWorldUser = dto.getTennisWorldUser();
    tennisWorldUser.setEmail(email);
    return dto;
  }

  @Override
  public void updateDto(BookingScheduleConfigDto dto) {
    dto.setDuration("987");
  }

  /**
   * Creates an entity.
   */
  @Test
  @Order(1)
  @Override
  public void createTest() throws IOException {
    tennisWorldUserControllerIntegrationTest.createTest();
    super.createTest();
  }

  /**
   * Deletes an entity.
   */
  @Test
  @Order(8)
  @Override
  public void deleteTest() throws IOException {
    super.deleteTest();
    tennisWorldUserControllerIntegrationTest.deleteTest();
  }
}
