package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import com.nicobrest.kamehouse.commons.controller.AbstractCrudControllerTest;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;
import com.nicobrest.kamehouse.tennisworld.service.BookingResponseService;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingResponseTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Unit tests for the BookingResponseController class.
 *
 * @author nbrest
 */
class BookingResponseControllerTest
    extends AbstractCrudControllerTest<BookingResponse, BookingResponseDto> {

  @InjectMocks
  private BookingResponseController bookingResponseController;

  @Mock(name = "bookingResponseService")
  private BookingResponseService bookingResponseServiceMock;

  @Override
  public String getCrudUrl() {
    return BookingResponseTestUtils.API_V1_TENNISWORLD_BOOKING_RESPONSES;
  }

  @Override
  public Class<BookingResponse> getEntityClass() {
    return BookingResponse.class;
  }

  @Override
  public CrudService<BookingResponse, BookingResponseDto> getCrudService() {
    return bookingResponseServiceMock;
  }

  @Override
  public TestUtils<BookingResponse, BookingResponseDto> getTestUtils() {
    return new BookingResponseTestUtils();
  }

  @Override
  public AbstractController getController() {
    return bookingResponseController;
  }
}
