package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingResponseTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Unit tests for the BookingResponseService class.
 *
 * @author nbrest
 */
public class BookingResponseServiceTest extends
    AbstractCrudServiceTest<BookingResponse, BookingResponseDto> {

  @InjectMocks
  private BookingResponseService bookingResponseService;

  @Mock(name = "bookingResponseDao")
  private CrudDao<BookingResponse> bookingResponseDaoMock;

  @Override
  public CrudService<BookingResponse, BookingResponseDto> getCrudService() {
    return bookingResponseService;
  }

  @Override
  public CrudDao<BookingResponse> getCrudDao() {
    return bookingResponseDaoMock;
  }

  @Override
  public TestUtils<BookingResponse, BookingResponseDto> getTestUtils() {
    return new BookingResponseTestUtils();
  }
}
