package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingScheduleConfigDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingScheduleConfigTestUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Unit tests for the BookingScheduleConfigDaoJpa class.
 *
 * @author nbrest
 */
class BookingScheduleConfigDaoJpaTest
    extends AbstractCrudDaoJpaTest<BookingScheduleConfig, BookingScheduleConfigDto> {

  @Autowired
  private CrudDao<BookingScheduleConfig> bookingScheduleConfigDaoJpa;

  @Override
  public void initBeforeTest() {
    // Adding a clearTable("TENNISWORLD_USER") made tests hang.
    String table = "TENNISWORLD_USER";
    String columns = "id, email, password";
    String values = "1, 'goku@dbz.com', X'FFD8FFE000104A4640'";
    insertData(getInsertQuery(table, columns, values));
  }

  @Override
  public Class<BookingScheduleConfig> getEntityClass() {
    return BookingScheduleConfig.class;
  }

  @Override
  public CrudDao<BookingScheduleConfig> getCrudDao() {
    return bookingScheduleConfigDaoJpa;
  }

  @Override
  public TestUtils<BookingScheduleConfig, BookingScheduleConfigDto> getTestUtils() {
    return new BookingScheduleConfigTestUtils();
  }

  @Override
  public String[] getTablesToClear() {
    return new String[]{"BOOKING_SCHEDULE_CONFIG"};
  }

  @Override
  public void updateEntity(BookingScheduleConfig entity) {
    entity.setBookAheadDays(10);
  }

  @Override
  public void updateEntityServerError(BookingScheduleConfig entity) {
    entity.setBookAheadDays(1000000000);
    entity.setDuration(getInvalidString());
  }
}
