package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingScheduleConfigDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer to manage the TennisWorldBookingScheduleConfigs.
 *
 * @author nbrest
 */
@Service
public class BookingScheduleConfigService
    extends AbstractCrudService<BookingScheduleConfig, BookingScheduleConfigDto>
    implements CrudService<BookingScheduleConfig, BookingScheduleConfigDto> {

  @Autowired
  @Qualifier("bookingScheduleConfigDaoJpa")
  private CrudDao<BookingScheduleConfig> bookingScheduleConfigDao;

  @Override
  public Long create(BookingScheduleConfigDto dto) {
    return create(bookingScheduleConfigDao, dto);
  }

  @Override
  public BookingScheduleConfig read(Long id) {
    return read(bookingScheduleConfigDao, id);
  }

  @Override
  public List<BookingScheduleConfig> readAll() {
    return readAll(bookingScheduleConfigDao);
  }

  @Override
  public void update(BookingScheduleConfigDto dto) {
    update(bookingScheduleConfigDao, dto);
  }

  @Override
  public BookingScheduleConfig delete(Long id) {
    return delete(bookingScheduleConfigDao, id);
  }

  @Override
  protected BookingScheduleConfig getModel(BookingScheduleConfigDto dto) {
    BookingScheduleConfig entity = new BookingScheduleConfig();
    entity.setId(dto.getId());
    entity.setTennisWorldUser(dto.getTennisWorldUser());
    entity.setSessionType(dto.getSessionType());
    entity.setSite(dto.getSite());
    entity.setDay(dto.getDay());
    entity.setTime(dto.getTime());
    entity.setBookingDate(dto.getBookingDate());
    entity.setBookAheadDays(dto.getBookAheadDays());
    entity.setEnabled(dto.getEnabled());
    entity.setDuration(dto.getDuration());
    return entity;
  }

  @Override
  protected void validate(BookingScheduleConfig entity) {
    // Validate duration length max 3, all numbers and other fields here
  }
}
