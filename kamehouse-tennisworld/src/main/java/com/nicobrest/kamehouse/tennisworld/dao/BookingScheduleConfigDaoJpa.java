package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.utils.DateUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * JPA DAO for BookingScheduleConfig entity.
 *
 * @author nbrest
 */
@Repository
public class BookingScheduleConfigDaoJpa extends AbstractCrudDaoJpa
    implements CrudDao<BookingScheduleConfig> {

  public static final Date DEFAULT_BOOKING_DATE = DateUtils.getDate(1984, 9, 15);
  public static final String DEFAULT_BOOKING_DATE_STR = "1984-10-15";

  @Override
  public Long create(BookingScheduleConfig entity) {
    setDefaultBookingDate(entity);
    return create(BookingScheduleConfig.class, entity);
  }

  @Override
  public BookingScheduleConfig read(Long id) {
    BookingScheduleConfig entity =  read(BookingScheduleConfig.class, id);
    unsetDefaultBookingDate(entity);
    return entity;
  }

  @Override
  public List<BookingScheduleConfig> readAll() {
    List<BookingScheduleConfig> entities = readAll(BookingScheduleConfig.class);
    unsetDefaultBookingDate(entities);
    return entities;
  }

  @Override
  public void update(BookingScheduleConfig entity) {
    setDefaultBookingDate(entity);
    update(BookingScheduleConfig.class, entity);
  }

  @Override
  public BookingScheduleConfig delete(Long id) {
    BookingScheduleConfig entity = delete(BookingScheduleConfig.class, id);
    unsetDefaultBookingDate(entity);
    return entity;
  }

  @Override
  protected <T> void updateEntityValues(T persistedEntity, T entity) {
    BookingScheduleConfig persistedObject =
        (BookingScheduleConfig) persistedEntity;
    BookingScheduleConfig updatedObject = (BookingScheduleConfig) entity;
    persistedObject.setTennisWorldUser(updatedObject.getTennisWorldUser());
    persistedObject.setSessionType(updatedObject.getSessionType());
    persistedObject.setSite(updatedObject.getSite());
    persistedObject.setDay(updatedObject.getDay());
    persistedObject.setTime(updatedObject.getTime());
    persistedObject.setBookingDate(updatedObject.getBookingDate());
    persistedObject.setBookAheadDays(updatedObject.getBookAheadDays());
    persistedObject.setEnabled(updatedObject.getEnabled());
    persistedObject.setDuration(updatedObject.getDuration());
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
   * The default booking date is only set to handle @UniqueConstraint in the database layer.
   * In all other layers, if the value is the default, it should be treated as null.
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
