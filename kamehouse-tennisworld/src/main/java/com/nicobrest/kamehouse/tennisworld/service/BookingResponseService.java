package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDtoTranslator;
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

  private CrudDao<BookingResponse> bookingResponseDao;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public BookingResponseService(
      @Qualifier("bookingResponseDaoJpa") CrudDao<BookingResponse> bookingResponseDao) {
    this.bookingResponseDao = bookingResponseDao;
  }

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public CrudDao<BookingResponse> getCrudDao() {
    return bookingResponseDao;
  }

  @Override
  public KameHouseDtoTranslator<BookingResponse, BookingResponseDto> getDtoTranslator() {
    return new BookingResponseDtoTranslator();
  }

  @Override
  protected void validate(BookingResponse entity) {
    // no validations needed here for now
  }
}
