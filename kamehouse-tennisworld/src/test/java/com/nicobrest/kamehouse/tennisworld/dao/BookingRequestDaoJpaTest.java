package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingRequestTestUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit tests for the BookingRequestDaoJpa class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class BookingRequestDaoJpaTest
    extends AbstractCrudDaoJpaTest<BookingRequest, BookingRequestDto> {

  private BookingRequest bookingRequest;

  @Autowired
  private CrudDao<BookingRequest> bookingRequestDaoJpa;

  /**
   * Clears data from the repository before each test.
   */
  @Before
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
    BookingRequest updatedEntity = (BookingRequest) BeanUtils.cloneBean(bookingRequest);
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
    thrown.expect(KameHouseServerErrorException.class);
    thrown.expectMessage("PersistenceException");
    persistEntityInRepository(bookingRequest);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 70; i++) {
      sb.append("goku");
    }
    String duration = sb.toString();
    bookingRequest.setDuration(duration);

    bookingRequestDaoJpa.update(bookingRequest);
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
