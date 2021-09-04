package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import com.nicobrest.kamehouse.commons.controller.AbstractCrudControllerTest;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingScheduleConfigDto;
import com.nicobrest.kamehouse.tennisworld.service.BookingScheduleConfigService;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingScheduleConfigTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Unit tests for the BookingScheduleConfigController class.
 *
 * @author nbrest
 */
public class BookingScheduleConfigControllerTest
    extends AbstractCrudControllerTest<BookingScheduleConfig, BookingScheduleConfigDto> {

  @InjectMocks
  private BookingScheduleConfigController bookingScheduleConfigController;

  @Mock(name = "bookingScheduleConfigService")
  private BookingScheduleConfigService bookingScheduleConfigServiceMock;

  @Override
  public String getCrudUrl() {
    return BookingScheduleConfigTestUtils.API_V1_TENNISWORLD_BOOKING_SCHEDULE_CONFIG;
  }

  @Override
  public Class<BookingScheduleConfig> getEntityClass() {
    return BookingScheduleConfig.class;
  }

  @Override
  public CrudService<BookingScheduleConfig, BookingScheduleConfigDto> getCrudService() {
    return bookingScheduleConfigServiceMock;
  }

  @Override
  public TestUtils<BookingScheduleConfig, BookingScheduleConfigDto> getTestUtils() {
    return new BookingScheduleConfigTestUtils();
  }

  @Override
  public AbstractController getController() {
    return bookingScheduleConfigController;
  }
}
