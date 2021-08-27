package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingScheduleConfigDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingScheduleConfigTestUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit tests for the BookingScheduleConfigDaoJpa class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class BookingScheduleConfigDaoJpaTest
    extends AbstractCrudDaoJpaTest<BookingScheduleConfig, BookingScheduleConfigDto> {

  private BookingScheduleConfig bookingScheduleConfig;

  @Autowired
  private CrudDao<BookingScheduleConfig> bookingScheduleConfigDaoJpa;

  /**
   * Clears data from the repository before each test.
   */
  @Before
  public void setUp() {
    testUtils = new BookingScheduleConfigTestUtils();
    testUtils.initTestData();
    testUtils.removeIds();
    bookingScheduleConfig = testUtils.getSingleTestData();

    clearTable("BOOKING_SCHEDULE_CONFIG");

    // Adding a clearTable("TENNISWORLD_USER") made tests hang.
    String table = "TENNISWORLD_USER";
    String columns = "id, email, password";
    String values = "1, 'goku@dbz.com', ''";
    insertData(getInsertQuery(table, columns, values));
  }

  /**
   * Tests creating a BookingScheduleConfig in the repository.
   */
  @Test
  public void createTest() {
    createTest(bookingScheduleConfigDaoJpa, BookingScheduleConfig.class);
  }

  /**
   * Tests creating a BookingScheduleConfig in the repository Exception flows.
   */
  @Test
  public void createConflictExceptionTest() {
    createConflictExceptionTest(bookingScheduleConfigDaoJpa);
  }

  /**
   * Tests getting a single BookingScheduleConfig from the repository by id.
   */
  @Test
  public void readTest() {
    readTest(bookingScheduleConfigDaoJpa);
  }

  /**
   * Tests getting all the BookingScheduleConfigs in the repository.
   */
  @Test
  public void readAllTest() {
    readAllTest(bookingScheduleConfigDaoJpa);
  }

  /**
   * Tests updating an existing user in the repository.
   */
  @Test
  public void updateTest() throws IllegalAccessException, InstantiationException,
      InvocationTargetException, NoSuchMethodException {
    BookingScheduleConfig updatedEntity = (BookingScheduleConfig) BeanUtils.cloneBean(bookingScheduleConfig);
    updatedEntity.setBookAheadDays(10);

    updateTest(bookingScheduleConfigDaoJpa, BookingScheduleConfig.class, updatedEntity);
  }

  /**
   * Tests updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateNotFoundExceptionTest() {
    updateNotFoundExceptionTest(bookingScheduleConfigDaoJpa, BookingScheduleConfig.class);
  }

  /**
   * Tests updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateServerErrorExceptionTest() {
    thrown.expect(KameHouseServerErrorException.class);
    thrown.expectMessage("PersistenceException");
    persistEntityInRepository(bookingScheduleConfig);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 70; i++) {
      sb.append("goku");
    }
    String duration = sb.toString();
    bookingScheduleConfig.setDuration(duration);

    bookingScheduleConfigDaoJpa.update(bookingScheduleConfig);
  }

  /**
   * Tests deleting an existing user from the repository.
   */
  @Test
  public void deleteTest() {
    deleteTest(bookingScheduleConfigDaoJpa);
  }

  /**
   * Tests deleting an existing user from the repository Exception flows.
   */
  @Test
  public void deleteNotFoundExceptionTest() {
    deleteNotFoundExceptionTest(bookingScheduleConfigDaoJpa, BookingScheduleConfig.class);
  }
}
