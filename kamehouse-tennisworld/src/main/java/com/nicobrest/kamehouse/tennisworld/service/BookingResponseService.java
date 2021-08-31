package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service layer to manage the TennisWorld BookingResponses.
 *
 * @author nbrest
 */
@Service
public class BookingResponseService extends AbstractCrudService<BookingResponse, BookingResponseDto>
    implements CrudService<BookingResponse, BookingResponseDto> {

  @Autowired
  @Qualifier("bookingResponseDaoJpa")
  private CrudDao<BookingResponse> bookingResponseDao;

  @Override
  public Long create(BookingResponseDto dto) {
    return create(bookingResponseDao, dto);
  }

  @Override
  public BookingResponse read(Long id) {
    return read(bookingResponseDao, id);
  }

  @Override
  public List<BookingResponse> readAll() {
    return readAll(bookingResponseDao);
  }

  @Override
  public void update(BookingResponseDto dto) {
    update(bookingResponseDao, dto);
  }

  @Override
  public BookingResponse delete(Long id) {
    return delete(bookingResponseDao, id);
  }

  @Override
  protected BookingResponse getModel(BookingResponseDto dto) {
    BookingResponse entity = new BookingResponse();
    entity.setId(dto.getId());
    entity.setStatus(dto.getStatus());
    entity.setMessage(dto.getMessage());
    entity.setRequest(dto.getRequest());
    return entity;
  }

  @Override
  protected void validate(BookingResponse entity) {
    // no validations needed here for now
  }
}
