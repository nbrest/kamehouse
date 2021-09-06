package com.nicobrest.kamehouse.tennisworld.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractCrudControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingScheduleConfigDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingScheduleConfigTestUtils;

/**
 * Integration tests for the BookingScheduleConfigController class. These integration tests require
 * a TennisWorldUser with email 'goku@dbz.com' to exist in the local database. If it doesn't exist,
 * the tests will fail.
 *
 * @author nbrest
 */
public class BookingScheduleConfigControllerIntegrationTest
    extends AbstractCrudControllerIntegrationTest<BookingScheduleConfig, BookingScheduleConfigDto> {

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
    return dto;
  }

  @Override
  public void updateDto(BookingScheduleConfigDto dto) {
    dto.setDuration("987");
  }
}
