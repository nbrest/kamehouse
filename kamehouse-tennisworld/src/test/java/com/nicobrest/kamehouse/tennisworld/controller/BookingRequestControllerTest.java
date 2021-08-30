package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractCrudControllerTest;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import com.nicobrest.kamehouse.tennisworld.service.BookingRequestService;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingRequestTestUtils;
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

import java.io.IOException;

/**
 * Unit tests for the BookingRequestController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class BookingRequestControllerTest
    extends AbstractCrudControllerTest<BookingRequest, BookingRequestDto> {

  private static final String API_V1_TENNISWORLD_BOOKING_REQUESTS =
      BookingRequestTestUtils.API_V1_TENNISWORLD_BOOKING_REQUESTS;

  private BookingRequest bookingRequest;

  @InjectMocks
  private BookingRequestController bookingRequestController;

  @Mock(name = "bookingRequestService")
  private BookingRequestService bookingRequestServiceMock;

  /**
   * Init test data.
   */
  @BeforeEach
  public void beforeTest() {
    testUtils = new BookingRequestTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    bookingRequest = testUtils.getSingleTestData();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(bookingRequestServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(bookingRequestController).build();
  }

  /**
   * Tests creating a new entity in the repository.
   */
  @Test
  public void createTest() throws Exception {
    createTest(API_V1_TENNISWORLD_BOOKING_REQUESTS, bookingRequestServiceMock);
  }

  /**
   * Tests getting a specific entity from the repository.
   */
  @Test
  public void readTest() throws Exception {
    readTest(API_V1_TENNISWORLD_BOOKING_REQUESTS, bookingRequestServiceMock, BookingRequest.class);
  }

  /**
   * Tests getting all the entities from the repository.
   */
  @Test
  public void readAllTest() throws Exception {
    readAllTest(API_V1_TENNISWORLD_BOOKING_REQUESTS, bookingRequestServiceMock, BookingRequest.class);
  }

  /**
   * Tests updating an existing entity in the repository.
   */
  @Test
  public void updateTest() throws Exception {
    updateTest(API_V1_TENNISWORLD_BOOKING_REQUESTS, bookingRequestServiceMock);
  }

  /**
   * Tests failing to update an existing entity in the repository with bad request.
   */
  @Test
  public void updateInvalidPathId() throws IOException, Exception {
    updateInvalidPathId(API_V1_TENNISWORLD_BOOKING_REQUESTS);
  }

  /**
   * Tests trying to update a non existing entity in the repository.
   */
  @Test
  public void updateNotFoundExceptionTest() throws Exception {
    updateNotFoundExceptionTest(API_V1_TENNISWORLD_BOOKING_REQUESTS, bookingRequestServiceMock);
  }

  /**
   * Tests for deleting an existing entity from the repository.
   */
  @Test
  public void deleteTest() throws Exception {
    deleteTest(API_V1_TENNISWORLD_BOOKING_REQUESTS, bookingRequestServiceMock, BookingRequest.class);
  }

  /**
   * Tests for deleting an entity not found in the repository.
   */
  @Test
  public void deleteNotFoundExceptionTest() throws Exception {
    deleteNotFoundExceptionTest(API_V1_TENNISWORLD_BOOKING_REQUESTS, bookingRequestServiceMock);
  }
}
