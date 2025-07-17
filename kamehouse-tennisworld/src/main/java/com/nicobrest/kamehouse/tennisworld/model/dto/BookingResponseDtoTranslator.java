package com.nicobrest.kamehouse.tennisworld.model.dto;

import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;

/**
 * Translator between entity and dto for BookingResponse.
 *
 * @author nbrest
 */
public class BookingResponseDtoTranslator implements
    KameHouseDtoTranslator<BookingResponse, BookingResponseDto> {

  @Override
  public BookingResponse buildEntity(BookingResponseDto dto) {
    BookingResponse entity = new BookingResponse();
    entity.setId(dto.getId());
    entity.setStatus(dto.getStatus());
    entity.setMessage(dto.getMessage());
    entity.setRequest(dto.getRequest());
    return entity;
  }

  @Override
  public BookingResponseDto buildDto(BookingResponse entity) {
    BookingResponseDto dto = new BookingResponseDto();
    dto.setId(entity.getId());
    dto.setStatus(entity.getStatus());
    dto.setMessage(entity.getMessage());
    dto.setRequest(entity.getRequest());
    return dto;
  }
}
