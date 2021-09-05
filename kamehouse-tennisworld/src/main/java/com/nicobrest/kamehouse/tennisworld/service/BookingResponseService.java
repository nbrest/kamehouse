package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service layer to manage the TennisWorld BookingResponses.
 *
 * @author nbrest
 */
@Service
public class BookingResponseService
    extends AbstractCrudService<BookingResponse, BookingResponseDto> {

  @Autowired
  @Qualifier("bookingResponseDaoJpa")
  private CrudDao<BookingResponse> bookingResponseDao;

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public CrudDao<BookingResponse> getCrudDao() {
    return bookingResponseDao;
  }

  @Override
  protected void validate(BookingResponse entity) {
    // no validations needed here for now
  }
}
