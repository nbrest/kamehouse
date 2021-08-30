package com.nicobrest.kamehouse.tennisworld.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingScheduleConfigDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingScheduleConfigTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

  @Mock(name = "tennisWorldUserService")
  private TennisWorldUserService tennisWorldUserServiceMock;

  /**
   * Resets mock objects and initializes test repository.
   */
  @BeforeEach
  public void beforeTest() {
    testUtils = new BookingScheduleConfigTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    bookingScheduleConfig = testUtils.getSingleTestData();

    // Reset mock objects before each test
    MockitoAnnotations.openMocks(this);
    Mockito.reset(bookingScheduleConfigDaoMock);
    when(tennisWorldUserServiceMock.getByEmail((any())))
        .thenReturn(bookingScheduleConfig.getTennisWorldUser());
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
