package com.nicobrest.kamehouse.tennisworld.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;
import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingRequestTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit tests for the BookingRequestDaoJpa class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class BookingRequestDaoJpaTest
    extends AbstractCrudDaoJpaTest<BookingRequest, BookingRequestDto> {

  private BookingRequest bookingRequest;

  @Autowired
  private CrudDao<BookingRequest> bookingRequestDaoJpa;

  /**
   * Clears data from the repository before each test.
   */
  @BeforeEach
  public void setUp() {
    testUtils = new BookingRequestTestUtils();
    testUtils.initTestData();
    testUtils.removeIds();
    bookingRequest = testUtils.getSingleTestData();
    ((BookingRequestTestUtils) testUtils).unsetTransientData();

    clearTable("BOOKING_REQUEST");
  }

  /**
   * Tests creating a BookingRequest in the repository.
   */
  @Test
  public void createTest() {
    createTest(bookingRequestDaoJpa, BookingRequest.class);
  }

  /**
   * Tests getting a single BookingRequest from the repository by id.
   */
  @Test
  public void readTest() {
    readTest(bookingRequestDaoJpa);
  }

  /**
   * Tests getting all the BookingRequests in the repository.
   */
  @Test
  public void readAllTest() {
    readAllTest(bookingRequestDaoJpa);
  }

  /**
   * Tests updating an existing user in the repository.
   */
  @Test
  public void updateTest() throws IllegalAccessException, InstantiationException,
      InvocationTargetException, NoSuchMethodException {
    BookingRequest updatedEntity = bookingRequest;
    updatedEntity.setDuration("99");

    updateTest(bookingRequestDaoJpa, BookingRequest.class, updatedEntity);
  }

  /**
   * Tests updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateNotFoundExceptionTest() {
    updateNotFoundExceptionTest(bookingRequestDaoJpa, BookingRequest.class);
  }

  /**
   * Tests updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateServerErrorExceptionTest() {
    assertThrows(KameHouseServerErrorException.class, () -> {
      persistEntityInRepository(bookingRequest);
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < 70; i++) {
        sb.append("goku");
      }
      String duration = sb.toString();
      bookingRequest.setDuration(duration);

      bookingRequestDaoJpa.update(bookingRequest);
    });
  }

  /**
   * Tests deleting an existing user from the repository.
   */
  @Test
  public void deleteTest() {
    deleteTest(bookingRequestDaoJpa);
  }

  /**
   * Tests deleting an existing user from the repository Exception flows.
   */
  @Test
  public void deleteNotFoundExceptionTest() {
    deleteNotFoundExceptionTest(bookingRequestDaoJpa, BookingRequest.class);
  }
}
