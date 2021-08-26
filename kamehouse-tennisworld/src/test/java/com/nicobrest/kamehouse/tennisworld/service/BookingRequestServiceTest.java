package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingRequestTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the BookingRequestService class.
 *
 * @author nbrest
 */
public class BookingRequestServiceTest extends
    AbstractCrudServiceTest<BookingRequest, BookingRequestDto> {

  private BookingRequest bookingRequest;

  @InjectMocks
  private BookingRequestService bookingRequestService;

  @Mock(name = "bookingRequestDao")
  private CrudDao<BookingRequest> bookingRequestDaoMock;

  /**
   * Resets mock objects and initializes test repository.
   */
  @Before
  public void beforeTest() {
    testUtils = new BookingRequestTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    bookingRequest = testUtils.getSingleTestData();

    // Reset mock objects before each test
    MockitoAnnotations.initMocks(this);
    Mockito.reset(bookingRequestDaoMock);
  }

  /**
   * Tests calling the service to create a BookingRequest in the repository.
   */
  @Test
  public void createTest() {
    createTest(bookingRequestService, bookingRequestDaoMock);
  }

  /**
   * Tests calling the service to get a single BookingRequest in the
   * repository by id.
   */
  @Test
  public void readTest() {
    readTest(bookingRequestService, bookingRequestDaoMock);
  }

  /**
   * Tests calling the service to get all the BookingRequests in the
   * repository.
   */
  @Test
  public void readAllTest() {
    readAllTest(bookingRequestService, bookingRequestDaoMock);
  }

  /**
   * Tests calling the service to update an existing BookingRequest in the
   * repository.
   */
  @Test
  public void updateTest() {
    updateTest(bookingRequestService, bookingRequestDaoMock);
  }

  /**
   * Tests calling the service to delete an existing user in the repository.
   */
  @Test
  public void deleteEntityTest() {
    deleteTest(bookingRequestService, bookingRequestDaoMock);
  }
}
