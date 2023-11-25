package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingRequestTestUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Unit tests for the BookingRequestDaoJpa class.
 *
 * @author nbrest
 */
class BookingRequestDaoJpaTest
    extends AbstractCrudDaoJpaTest<BookingRequest, BookingRequestDto> {

  private CrudDao<BookingRequest> bookingRequestDaoJpa;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public BookingRequestDaoJpaTest(CrudDao<BookingRequest> bookingRequestDaoJpa,
      EntityManagerFactory entityManagerFactory) {
    super(entityManagerFactory);
    this.bookingRequestDaoJpa = bookingRequestDaoJpa;
  }

  @Override
  public boolean hasUniqueConstraints() {
    return false;
  }

  @Override
  public void initBeforeTest() {
    ((BookingRequestTestUtils) testUtils).unsetTransientData();
  }

  @Override
  public Class<BookingRequest> getEntityClass() {
    return BookingRequest.class;
  }

  @Override
  public CrudDao<BookingRequest> getCrudDao() {
    return bookingRequestDaoJpa;
  }

  @Override
  public TestUtils<BookingRequest, BookingRequestDto> getTestUtils() {
    return new BookingRequestTestUtils();
  }

  @Override
  public String[] getTablesToClear() {
    return new String[]{"BOOKING_REQUEST"};
  }

  @Override
  public void updateEntity(BookingRequest entity) {
    entity.setDuration("99");
  }

  @Override
  public void updateEntityServerError(BookingRequest entity) {
    entity.setDuration(getInvalidString());
  }
}
