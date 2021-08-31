package com.nicobrest.kamehouse.tennisworld.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingResponseTestUtils;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for the BookingResponseDaoJpa class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class BookingResponseDaoJpaTest
    extends AbstractCrudDaoJpaTest<BookingResponse, BookingResponseDto> {

  private BookingResponse bookingResponse;

  @Autowired private CrudDao<BookingResponse> bookingResponseDaoJpa;

  /** Clears data from the repository before each test. */
  @BeforeEach
  public void setUp() {
    testUtils = new BookingResponseTestUtils();
    testUtils.initTestData();
    testUtils.removeIds();
    bookingResponse = testUtils.getSingleTestData();

    clearTable("BOOKING_RESPONSE");

    // Using a normal insert made some tests hang when executed in parallel.
    String table = "BOOKING_REQUEST";
    String columns =
        "id, username, date, time, site, session_type, duration, dry_run, "
            + "creation_date, scheduled";
    String[] rows = {
      "1, 'goku@dbz.com', '2020-07-28', '18:45', 'MELBOURNE_PARK', 'ROD_LAVER_OUTDOOR'"
          + ", '60', 'false', '2020-07-28 20:08:08.235', 'false'",
      "2, 'goku@dbz.com', '2021-07-30', '06:30', 'MELBOURNE_PARK', 'CARDIO'"
          + ", '45', 'false', '2020-07-28 20:08:08.235', 'false'",
      "3, 'goku@dbz.com', '2021-07-30', '06:30', 'MELBOURNE_PARK', 'CARDIO'"
          + ", '45', 'false', '2020-07-28 20:08:08.235', 'false'"
    };

    for (String row : rows) {
      insertData(getInsertQuery(table, columns, row));
    }
  }

  /** Tests creating a BookingResponse in the repository. */
  @Test
  public void createTest() {
    createTest(bookingResponseDaoJpa, BookingResponse.class);
  }

  /** Tests getting a single BookingResponse from the repository by id. */
  @Test
  public void readTest() {
    readTest(bookingResponseDaoJpa);
  }

  /** Tests getting all the BookingResponses in the repository. */
  @Test
  public void readAllTest() {
    readAllTest(bookingResponseDaoJpa);
  }

  /** Tests updating an existing user in the repository. */
  @Test
  public void updateTest()
      throws IllegalAccessException, InstantiationException, InvocationTargetException,
          NoSuchMethodException {
    BookingResponse updatedEntity = bookingResponse;
    updatedEntity.setMessage("mada mada dane pegasus");

    updateTest(bookingResponseDaoJpa, BookingResponse.class, updatedEntity);
  }

  /** Tests updating an existing user in the repository Exception flows. */
  @Test
  public void updateNotFoundExceptionTest() {
    updateNotFoundExceptionTest(bookingResponseDaoJpa, BookingResponse.class);
  }

  /** Tests updating an existing user in the repository Exception flows. */
  @Test
  public void updateServerErrorExceptionTest() {
    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          persistEntityInRepository(bookingResponse);
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < 70; i++) {
            sb.append("goku");
          }
          String duration = sb.toString();
          bookingResponse.setMessage(duration);

          bookingResponseDaoJpa.update(bookingResponse);
        });
  }

  /** Tests deleting an existing user from the repository. */
  @Test
  public void deleteTest() {
    deleteTest(bookingResponseDaoJpa);
  }

  /** Tests deleting an existing user from the repository Exception flows. */
  @Test
  public void deleteNotFoundExceptionTest() {
    deleteNotFoundExceptionTest(bookingResponseDaoJpa, BookingResponse.class);
  }
}
