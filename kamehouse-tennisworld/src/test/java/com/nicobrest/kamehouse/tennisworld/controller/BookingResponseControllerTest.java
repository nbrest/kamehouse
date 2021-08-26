package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractCrudControllerTest;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;
import com.nicobrest.kamehouse.tennisworld.service.BookingResponseService;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingResponseTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

/**
 * Unit tests for the BookingResponseController class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class BookingResponseControllerTest
    extends AbstractCrudControllerTest<BookingResponse, BookingResponseDto> {

  private static final String API_V1_TENNISWORLD_BOOKING_RESPONSES =
      BookingResponseTestUtils.API_V1_TENNISWORLD_BOOKING_RESPONSES;

  private BookingResponse bookingResponse;

  @InjectMocks
  private BookingResponseController bookingResponseController;

  @Mock(name = "bookingResponseService")
  private BookingResponseService bookingResponseServiceMock;

  /**
   * Init test data.
   */
  @Before
  public void beforeTest() {
    testUtils = new BookingResponseTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    bookingResponse = testUtils.getSingleTestData();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(bookingResponseServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(bookingResponseController).build();
  }

  /**
   * Tests creating a new entity in the repository.
   */
  @Test
  public void createTest() throws Exception {
    createTest(API_V1_TENNISWORLD_BOOKING_RESPONSES, bookingResponseServiceMock);
  }

  /**
   * Tests getting a specific entity from the repository.
   */
  @Test
  public void readTest() throws Exception {
    readTest(API_V1_TENNISWORLD_BOOKING_RESPONSES, bookingResponseServiceMock, BookingResponse.class);
  }

  /**
   * Tests getting all the entities from the repository.
   */
  @Test
  public void readAllTest() throws Exception {
    readAllTest(API_V1_TENNISWORLD_BOOKING_RESPONSES, bookingResponseServiceMock, BookingResponse.class);
  }

  /**
   * Tests updating an existing entity in the repository.
   */
  @Test
  public void updateTest() throws Exception {
    updateTest(API_V1_TENNISWORLD_BOOKING_RESPONSES, bookingResponseServiceMock);
  }

  /**
   * Tests failing to update an existing entity in the repository with bad request.
   */
  @Test
  public void updateInvalidPathId() throws Exception {
    updateInvalidPathId(API_V1_TENNISWORLD_BOOKING_RESPONSES);
  }

  /**
   * Tests trying to update a non existing entity in the repository.
   */
  @Test
  public void updateNotFoundExceptionTest() throws Exception {
    updateNotFoundExceptionTest(API_V1_TENNISWORLD_BOOKING_RESPONSES, bookingResponseServiceMock);
  }

  /**
   * Tests for deleting an existing entity from the repository.
   */
  @Test
  public void deleteTest() throws Exception {
    deleteTest(API_V1_TENNISWORLD_BOOKING_RESPONSES, bookingResponseServiceMock, BookingResponse.class);
  }

  /**
   * Tests for deleting an entity not found in the repository.
   */
  @Test
  public void deleteNotFoundExceptionTest() throws Exception {
    deleteNotFoundExceptionTest(API_V1_TENNISWORLD_BOOKING_RESPONSES, bookingResponseServiceMock);
  }
}
