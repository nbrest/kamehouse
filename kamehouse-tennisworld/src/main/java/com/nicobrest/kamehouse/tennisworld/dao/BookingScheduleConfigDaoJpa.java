package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.commons.utils.DateUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * JPA DAO for BookingScheduleConfig entity.
 *
 * @author nbrest
 */
@Repository
public class BookingScheduleConfigDaoJpa extends AbstractCrudDaoJpa<BookingScheduleConfig> {

  public static final Date DEFAULT_BOOKING_DATE = DateUtils.getDate(1984, Calendar.OCTOBER, 15);
  public static final String DEFAULT_BOOKING_DATE_STR = "1984-10-15";

  @Override
  public Class<BookingScheduleConfig> getEntityClass() {
    return BookingScheduleConfig.class;
  }

  @Override
  public Long create(BookingScheduleConfig entity) {
    setDefaultBookingDate(entity);
    return super.create(entity);
  }

  @Override
  public BookingScheduleConfig read(Long id) {
    BookingScheduleConfig entity = super.read(id);
    unsetDefaultBookingDate(entity);
    return entity;
  }

  @Override
  public List<BookingScheduleConfig> readAll() {
    return readAll(0, null, true);
  }

  @Override
  public List<BookingScheduleConfig> readAll(Integer maxRows, String sortColumn,
      Boolean sortAscending) {
    List<BookingScheduleConfig> entities = super.readAll(maxRows, sortColumn, sortAscending);
    unsetDefaultBookingDate(entities);
    return entities;
  }

  @Override
  public void update(BookingScheduleConfig entity) {
    setDefaultBookingDate(entity);
    super.update(entity);
  }

  @Override
  public BookingScheduleConfig delete(Long id) {
    BookingScheduleConfig entity = super.delete(id);
    unsetDefaultBookingDate(entity);
    return entity;
  }

  @Override
  protected void updateEntityValues(BookingScheduleConfig persistedEntity,
      BookingScheduleConfig entity) {
    persistedEntity.setTennisWorldUser(entity.getTennisWorldUser());
    persistedEntity.setSessionType(entity.getSessionType());
    persistedEntity.setSite(entity.getSite());
    persistedEntity.setDay(entity.getDay());
    persistedEntity.setTime(entity.getTime());
    persistedEntity.setBookingDate(entity.getBookingDate());
    persistedEntity.setBookAheadDays(entity.getBookAheadDays());
    persistedEntity.setEnabled(entity.isEnabled());
    persistedEntity.setDuration(entity.getDuration());
    persistedEntity.setCourtNumber(entity.getCourtNumber());
  }

  /**
   * BookingDate needs to be set for the @UniqueConstraint defined in BookingScheduleConfig.
   */
  private void setDefaultBookingDate(BookingScheduleConfig entity) {
    if (entity.getBookingDate() == null) {
      logger.trace("Setting default booking date for entity {}", entity);
      entity.setBookingDate(DEFAULT_BOOKING_DATE);
    }
  }

  /**
   * The default booking date is only set to handle @UniqueConstraint in the database layer. In all
   * other layers, if the value is the default, it should be treated as null.
   */
  private void unsetDefaultBookingDate(BookingScheduleConfig entity) {
    Date bookingDate = entity.getBookingDate();
    if (bookingDate == null) {
      return;
    }
    String formattedBookingDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD, bookingDate);
    if (DEFAULT_BOOKING_DATE_STR.equals(formattedBookingDate)) {
      logger.trace("Removing default booking date for entity {}", entity);
      entity.setBookingDate(null);
    }
  }

  /**
   * Unset the booking date for all entities.
   */
  private void unsetDefaultBookingDate(List<BookingScheduleConfig> entities) {
    if (entities == null || entities.isEmpty()) {
      return;
    }
    for (BookingScheduleConfig entity : entities) {
      unsetDefaultBookingDate(entity);
    }
  }
}
