package com.nicobrest.kamehouse.tennisworld.model.dto;

import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;

/**
 * Translator between entity and dto for BookingRequest.
 *
 * @author nbrest
 */
public class BookingRequestDtoTranslator implements
    KameHouseDtoTranslator<BookingRequest, BookingRequestDto> {

  @Override
  public BookingRequest buildEntity(BookingRequestDto dto) {
    BookingRequest entity = new BookingRequest();
    entity.setId(dto.getId());
    entity.setCreationDate(dto.getCreationDate());
    entity.setCardDetails(dto.getCardDetails());
    entity.setDate(dto.getDate());
    entity.setDuration(dto.getDuration());
    entity.setDryRun(dto.isDryRun());
    entity.setUsername(dto.getUsername());
    entity.setPassword(dto.getPassword());
    entity.setScheduled(dto.isScheduled());
    entity.setSessionType(dto.getSessionType());
    entity.setSite(dto.getSite());
    entity.setTime(dto.getTime());
    entity.setCourtNumber(dto.getCourtNumber());
    entity.setRetries(dto.getRetries());
    return entity;
  }

  @Override
  public BookingRequestDto buildDto(BookingRequest entity) {
    BookingRequestDto dto = new BookingRequestDto();
    dto.setId(entity.getId());
    dto.setCardDetails(entity.getCardDetails());
    dto.setCourtNumber(entity.getCourtNumber());
    dto.setCreationDate(entity.getCreationDate());
    dto.setDate(entity.getDate());
    dto.setDryRun(entity.isDryRun());
    dto.setDuration(entity.getDuration());
    dto.setPassword(entity.getPassword());
    dto.setRetries(entity.getRetries());
    dto.setScheduled(entity.isScheduled());
    dto.setSessionType(entity.getSessionType());
    dto.setSite(entity.getSite());
    dto.setTime(entity.getTime());
    dto.setUsername(entity.getUsername());
    return dto;
  }
}
