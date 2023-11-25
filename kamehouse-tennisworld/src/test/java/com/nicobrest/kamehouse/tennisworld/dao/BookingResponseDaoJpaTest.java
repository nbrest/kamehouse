package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingResponseTestUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Unit tests for the BookingResponseDaoJpa class.
 *
 * @author nbrest
 */
class BookingResponseDaoJpaTest
    extends AbstractCrudDaoJpaTest<BookingResponse, BookingResponseDto> {

  @Autowired
  private CrudDao<BookingResponse> bookingResponseDaoJpa;

  @Override
  public void initBeforeTest() {
    // Using a normal insert made some tests hang when executed in parallel.
    String table = "BOOKING_REQUEST";
    String columns =
        "id, username, date, time, site, session_type, duration, dry_run, "
            + "creation_date, scheduled, court_number, retries";
    String[] rows = {
        "1, 'goku@dbz.com', '2020-07-28', '18:45', 'MELBOURNE_PARK', 'ROD_LAVER_OUTDOOR_WESTERN'"
            + ", '60', 'false', '2020-07-28 20:08:08.235', 'false', 0, 0",
        "2, 'goku@dbz.com', '2021-07-30', '06:30', 'MELBOURNE_PARK', 'CARDIO'"
            + ", '45', 'false', '2020-07-28 20:08:08.235', 'false', 0, 0",
        "3, 'goku@dbz.com', '2021-07-30', '06:30', 'MELBOURNE_PARK', 'ROD_LAVER_SHOW_COURTS'"
            + ", '45', 'false', '2020-07-28 20:08:08.235', 'false', 0, 0"
    };

    for (String row : rows) {
      insertData(getInsertQuery(table, columns, row));
    }
  }

  @Override
  public Class<BookingResponse> getEntityClass() {
    return BookingResponse.class;
  }

  @Override
  public CrudDao<BookingResponse> getCrudDao() {
    return bookingResponseDaoJpa;
  }

  @Override
  public TestUtils<BookingResponse, BookingResponseDto> getTestUtils() {
    return new BookingResponseTestUtils();
  }

  @Override
  public String[] getTablesToClear() {
    return new String[]{"BOOKING_RESPONSE"};
  }

  @Override
  public void updateEntity(BookingResponse entity) {
    entity.setMessage("mada mada dane pegasus");
  }

  @Override
  public void updateEntityServerError(BookingResponse entity) {
    entity.setMessage(getInvalidString());
  }
}
