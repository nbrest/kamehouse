package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import com.nicobrest.kamehouse.commons.controller.AbstractCrudControllerTest;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import com.nicobrest.kamehouse.tennisworld.service.BookingRequestService;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingRequestTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Unit tests for the BookingRequestController class.
 *
 * @author nbrest
 */
class BookingRequestControllerTest
    extends AbstractCrudControllerTest<BookingRequest, BookingRequestDto> {

  @InjectMocks
  private BookingRequestController bookingRequestController;

  @Mock(name = "bookingRequestService")
  private BookingRequestService bookingRequestServiceMock;

  @Override
  public String getCrudUrl() {
    return BookingRequestTestUtils.API_V1_TENNISWORLD_BOOKING_REQUESTS;
  }

  @Override
  public Class<BookingRequest> getEntityClass() {
    return BookingRequest.class;
  }

  @Override
  public CrudService<BookingRequest, BookingRequestDto> getCrudService() {
    return bookingRequestServiceMock;
  }

  @Override
  public TestUtils<BookingRequest, BookingRequestDto> getTestUtils() {
    return new BookingRequestTestUtils();
  }

  @Override
  public AbstractController getController() {
    return bookingRequestController;
  }
}
