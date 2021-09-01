package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractCrudControllerTest;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingScheduleConfigDto;
import com.nicobrest.kamehouse.tennisworld.service.BookingScheduleConfigService;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingScheduleConfigTestUtils;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for the BookingScheduleConfigController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
public class BookingScheduleConfigControllerTest
    extends AbstractCrudControllerTest<BookingScheduleConfig, BookingScheduleConfigDto> {

  private static final String API_V1_TENNISWORLD_BOOKING_SCHEDULE_CONFIG =
      BookingScheduleConfigTestUtils.API_V1_TENNISWORLD_BOOKING_SCHEDULE_CONFIG;

  @InjectMocks
  private BookingScheduleConfigController bookingScheduleConfigController;

  @Mock(name = "bookingScheduleConfigService")
  private BookingScheduleConfigService bookingScheduleConfigServiceMock;

  /**
   * Init test data.
   */
  @BeforeEach
  public void beforeTest() {
    testUtils = new BookingScheduleConfigTestUtils();
    testUtils.initTestData();
    testUtils.setIds();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(bookingScheduleConfigServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(bookingScheduleConfigController).build();
  }

  /**
   * Tests creating a new entity in the repository.
   */
  @Test
  public void createTest() throws Exception {
    createTest(API_V1_TENNISWORLD_BOOKING_SCHEDULE_CONFIG, bookingScheduleConfigServiceMock);
  }

  /**
   * Tests creating a new entity in the repository that already exists.
   */
  @Test
  public void createConflictExceptionTest() throws Exception {
    createConflictExceptionTest(
        API_V1_TENNISWORLD_BOOKING_SCHEDULE_CONFIG, bookingScheduleConfigServiceMock);
  }

  /**
   * Tests getting a specific entity from the repository.
   */
  @Test
  public void readTest() throws Exception {
    readTest(
        API_V1_TENNISWORLD_BOOKING_SCHEDULE_CONFIG,
        bookingScheduleConfigServiceMock,
        BookingScheduleConfig.class);
  }

  /**
   * Tests getting all the entities from the repository.
   */
  @Test
  public void readAllTest() throws Exception {
    readAllTest(
        API_V1_TENNISWORLD_BOOKING_SCHEDULE_CONFIG,
        bookingScheduleConfigServiceMock,
        BookingScheduleConfig.class);
  }

  /**
   * Tests updating an existing entity in the repository.
   */
  @Test
  public void updateTest() throws Exception {
    updateTest(API_V1_TENNISWORLD_BOOKING_SCHEDULE_CONFIG, bookingScheduleConfigServiceMock);
  }

  /**
   * Tests failing to update an existing entity in the repository with bad request.
   */
  @Test
  public void updateInvalidPathId() throws IOException, Exception {
    updateInvalidPathId(API_V1_TENNISWORLD_BOOKING_SCHEDULE_CONFIG);
  }

  /**
   * Tests trying to update a non existing entity in the repository.
   */
  @Test
  public void updateNotFoundExceptionTest() throws Exception {
    updateNotFoundExceptionTest(
        API_V1_TENNISWORLD_BOOKING_SCHEDULE_CONFIG, bookingScheduleConfigServiceMock);
  }

  /**
   * Tests for deleting an existing entity from the repository.
   */
  @Test
  public void deleteTest() throws Exception {
    deleteTest(
        API_V1_TENNISWORLD_BOOKING_SCHEDULE_CONFIG,
        bookingScheduleConfigServiceMock,
        BookingScheduleConfig.class);
  }

  /**
   * Tests for deleting an entity not found in the repository.
   */
  @Test
  public void deleteNotFoundExceptionTest() throws Exception {
    deleteNotFoundExceptionTest(
        API_V1_TENNISWORLD_BOOKING_SCHEDULE_CONFIG, bookingScheduleConfigServiceMock);
  }
}
