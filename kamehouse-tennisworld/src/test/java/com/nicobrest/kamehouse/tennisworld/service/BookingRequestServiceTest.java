package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingRequestTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Unit tests for the BookingRequestService class.
 *
 * @author nbrest
 */
public class BookingRequestServiceTest extends
    AbstractCrudServiceTest<BookingRequest, BookingRequestDto> {

  @InjectMocks
  private BookingRequestService bookingRequestService;

  @Mock(name = "bookingRequestDao")
  private CrudDao<BookingRequest> bookingRequestDaoMock;

  @Override
  public CrudService<BookingRequest, BookingRequestDto> getCrudService() {
    return bookingRequestService;
  }

  @Override
  public CrudDao<BookingRequest> getCrudDao() {
    return bookingRequestDaoMock;
  }

  @Override
  public TestUtils<BookingRequest, BookingRequestDto> getTestUtils() {
    return new BookingRequestTestUtils();
  }
}
