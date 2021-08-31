package com.nicobrest.kamehouse.tennisworld.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingScheduleConfigDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingScheduleConfigTestUtils;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for the BookingScheduleConfigDaoJpa class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class BookingScheduleConfigDaoJpaTest
    extends AbstractCrudDaoJpaTest<BookingScheduleConfig, BookingScheduleConfigDto> {

  private BookingScheduleConfig bookingScheduleConfig;

  @Autowired private CrudDao<BookingScheduleConfig> bookingScheduleConfigDaoJpa;

  /** Clears data from the repository before each test. */
  @BeforeEach
  public void setUp() {
    testUtils = new BookingScheduleConfigTestUtils();
    testUtils.initTestData();
    testUtils.removeIds();
    bookingScheduleConfig = testUtils.getSingleTestData();

    clearTable("BOOKING_SCHEDULE_CONFIG");

    // Adding a clearTable("TENNISWORLD_USER") made tests hang.
    String table = "TENNISWORLD_USER";
    String columns = "id, email, password";
    String values = "1, 'goku@dbz.com', X'FFD8FFE000104A4640'";
    insertData(getInsertQuery(table, columns, values));
  }

  /** Tests creating a BookingScheduleConfig in the repository. */
  @Test
  public void createTest() {
    createTest(bookingScheduleConfigDaoJpa, BookingScheduleConfig.class);
  }

  /** Tests creating a BookingScheduleConfig in the repository Exception flows. */
  @Test
  public void createConflictExceptionTest() {
    createConflictExceptionTest(bookingScheduleConfigDaoJpa);
  }

  /** Tests getting a single BookingScheduleConfig from the repository by id. */
  @Test
  public void readTest() {
    readTest(bookingScheduleConfigDaoJpa);
  }

  /** Tests getting all the BookingScheduleConfigs in the repository. */
  @Test
  public void readAllTest() {
    readAllTest(bookingScheduleConfigDaoJpa);
  }

  /** Tests updating an existing user in the repository. */
  @Test
  public void updateTest()
      throws IllegalAccessException, InstantiationException, InvocationTargetException,
          NoSuchMethodException {
    BookingScheduleConfig updatedEntity = bookingScheduleConfig;
    updatedEntity.setBookAheadDays(10);

    updateTest(bookingScheduleConfigDaoJpa, BookingScheduleConfig.class, updatedEntity);
  }

  /** Tests updating an existing user in the repository Exception flows. */
  @Test
  public void updateNotFoundExceptionTest() {
    updateNotFoundExceptionTest(bookingScheduleConfigDaoJpa, BookingScheduleConfig.class);
  }

  /** Tests updating an existing user in the repository Exception flows. */
  @Test
  public void updateServerErrorExceptionTest() {
    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          persistEntityInRepository(bookingScheduleConfig);
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < 70; i++) {
            sb.append("goku");
          }
          String duration = sb.toString();
          bookingScheduleConfig.setDuration(duration);

          bookingScheduleConfigDaoJpa.update(bookingScheduleConfig);
        });
  }

  /** Tests deleting an existing user from the repository. */
  @Test
  public void deleteTest() {
    deleteTest(bookingScheduleConfigDaoJpa);
  }

  /** Tests deleting an existing user from the repository Exception flows. */
  @Test
  public void deleteNotFoundExceptionTest() {
    deleteNotFoundExceptionTest(bookingScheduleConfigDaoJpa, BookingScheduleConfig.class);
  }
}
