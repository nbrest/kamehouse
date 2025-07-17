package com.nicobrest.kamehouse.tennisworld.model.dto;

import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import org.apache.commons.lang3.BooleanUtils;

/**
 * Translator between entity and dto for BookingScheduleConfig.
 *
 * @author nbrest
 */
public class BookingScheduleConfigDtoTranslator implements
    KameHouseDtoTranslator<BookingScheduleConfig, BookingScheduleConfigDto> {

  @Override
  public BookingScheduleConfig buildEntity(BookingScheduleConfigDto dto) {
    BookingScheduleConfig entity = new BookingScheduleConfig();
    entity.setId(dto.getId());
    entity.setTennisWorldUser(dto.getTennisWorldUser());
    entity.setSessionType(dto.getSessionType());
    entity.setSite(dto.getSite());
    entity.setDay(dto.getDay());
    entity.setTime(dto.getTime());
    entity.setBookingDate(dto.getBookingDate());
    entity.setBookAheadDays(dto.getBookAheadDays());
    entity.setEnabled(BooleanUtils.isTrue(dto.isEnabled()));
    entity.setDuration(dto.getDuration());
    entity.setCourtNumber(dto.getCourtNumber());
    return entity;
  }

  @Override
  public BookingScheduleConfigDto buildDto(BookingScheduleConfig entity) {
    BookingScheduleConfigDto dto = new BookingScheduleConfigDto();
    dto.setId(entity.getId());
    dto.setTennisWorldUser(entity.getTennisWorldUser());
    dto.setSessionType(entity.getSessionType());
    dto.setSite(entity.getSite());
    dto.setDay(entity.getDay());
    dto.setTime(entity.getTime());
    dto.setBookingDate(entity.getBookingDate());
    dto.setBookAheadDays(entity.getBookAheadDays());
    dto.setEnabled(Boolean.valueOf(entity.isEnabled()));
    dto.setDuration(entity.getDuration());
    dto.setCourtNumber(entity.getCourtNumber());
    return dto;
  }
}
