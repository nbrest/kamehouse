package com.nicobrest.kamehouse.tennisworld.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingScheduleConfigDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingScheduleConfigTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Unit tests for the BookingScheduleConfigService class.
 *
 * @author nbrest
 */
class BookingScheduleConfigServiceTest
    extends AbstractCrudServiceTest<BookingScheduleConfig, BookingScheduleConfigDto> {

  @InjectMocks
  private BookingScheduleConfigService bookingScheduleConfigService;

  @Mock(name = "bookingScheduleConfigDao")
  private CrudDao<BookingScheduleConfig> bookingScheduleConfigDaoMock;

  @Mock(name = "tennisWorldUserService")
  private TennisWorldUserService tennisWorldUserServiceMock;

  @Override
  public void initBeforeTest() {
    when(tennisWorldUserServiceMock.getByEmail((any())))
        .thenReturn(testUtils.getSingleTestData().getTennisWorldUser());
  }

  @Override
  public CrudService<BookingScheduleConfig, BookingScheduleConfigDto> getCrudService() {
    return bookingScheduleConfigService;
  }

  @Override
  public CrudDao<BookingScheduleConfig> getCrudDao() {
    return bookingScheduleConfigDaoMock;
  }

  @Override
  public TestUtils<BookingScheduleConfig, BookingScheduleConfigDto> getTestUtils() {
    return new BookingScheduleConfigTestUtils();
  }
}
