package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingResponseTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the BookingResponseService class.
 *
 * @author nbrest
 */
public class BookingResponseServiceTest extends
    AbstractCrudServiceTest<BookingResponse, BookingResponseDto> {

  private BookingResponse bookingResponse;

  @InjectMocks
  private BookingResponseService bookingResponseService;

  @Mock(name = "bookingResponseDao")
  private CrudDao<BookingResponse> bookingResponseDaoMock;

  /**
   * Resets mock objects and initializes test repository.
   */
  @Before
  public void beforeTest() {
    testUtils = new BookingResponseTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    bookingResponse = testUtils.getSingleTestData();

    // Reset mock objects before each test
    MockitoAnnotations.initMocks(this);
    Mockito.reset(bookingResponseDaoMock);
  }

  /**
   * Tests calling the service to create a BookingResponse in the repository.
   */
  @Test
  public void createTest() {
    createTest(bookingResponseService, bookingResponseDaoMock);
  }

  /**
   * Tests calling the service to get a single BookingResponse in the
   * repository by id.
   */
  @Test
  public void readTest() {
    readTest(bookingResponseService, bookingResponseDaoMock);
  }

  /**
   * Tests calling the service to get all the BookingResponses in the
   * repository.
   */
  @Test
  public void readAllTest() {
    readAllTest(bookingResponseService, bookingResponseDaoMock);
  }

  /**
   * Tests calling the service to update an existing BookingResponse in the
   * repository.
   */
  @Test
  public void updateTest() {
    updateTest(bookingResponseService, bookingResponseDaoMock);
  }

  /**
   * Tests calling the service to delete an existing user in the repository.
   */
  @Test
  public void deleteEntityTest() {
    deleteTest(bookingResponseService, bookingResponseDaoMock);
  }
}
