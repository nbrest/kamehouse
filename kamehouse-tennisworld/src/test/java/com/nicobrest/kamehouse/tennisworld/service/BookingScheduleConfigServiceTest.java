package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingScheduleConfigDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingScheduleConfigTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the BookingScheduleConfigService class.
 *
 * @author nbrest
 */
public class BookingScheduleConfigServiceTest extends
    AbstractCrudServiceTest<BookingScheduleConfig, BookingScheduleConfigDto> {

  private BookingScheduleConfig bookingScheduleConfig;

  @InjectMocks
  private BookingScheduleConfigService bookingScheduleConfigService;

  @Mock(name = "bookingScheduleConfigDao")
  private CrudDao<BookingScheduleConfig> bookingScheduleConfigDaoMock;

  /**
   * Resets mock objects and initializes test repository.
   */
  @Before
  public void beforeTest() {
    testUtils = new BookingScheduleConfigTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    bookingScheduleConfig = testUtils.getSingleTestData();

    // Reset mock objects before each test
    MockitoAnnotations.initMocks(this);
    Mockito.reset(bookingScheduleConfigDaoMock);
  }

  /**
   * Tests calling the service to create a BookingScheduleConfig in the repository.
   */
  @Test
  public void createTest() {
    createTest(bookingScheduleConfigService, bookingScheduleConfigDaoMock);
  }

  /**
   * Tests calling the service to get a single BookingScheduleConfig in the
   * repository by id.
   */
  @Test
  public void readTest() {
    readTest(bookingScheduleConfigService, bookingScheduleConfigDaoMock);
  }

  /**
   * Tests calling the service to get all the BookingScheduleConfigs in the
   * repository.
   */
  @Test
  public void readAllTest() {
    readAllTest(bookingScheduleConfigService, bookingScheduleConfigDaoMock);
  }

  /**
   * Tests calling the service to update an existing BookingScheduleConfig in the
   * repository.
   */
  @Test
  public void updateTest() {
    updateTest(bookingScheduleConfigService, bookingScheduleConfigDaoMock);
  }

  /**
   * Tests calling the service to delete an existing user in the repository.
   */
  @Test
  public void deleteEntityTest() {
    deleteTest(bookingScheduleConfigService, bookingScheduleConfigDaoMock);
  }
}
