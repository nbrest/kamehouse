package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * JPA DAO for BookingRequest entity.
 *
 * @author nbrest
 */
@Repository
public class BookingRequestDaoJpa extends AbstractCrudDaoJpa implements CrudDao<BookingRequest> {

  @Override
  public Long create(BookingRequest entity) {
    return create(BookingRequest.class, entity);
  }

  @Override
  public BookingRequest read(Long id) {
    BookingRequest entity = read(BookingRequest.class, id);
    return entity;
  }

  @Override
  public List<BookingRequest> readAll() {
    List<BookingRequest> entities = readAll(BookingRequest.class);
    return entities;
  }

  @Override
  public void update(BookingRequest entity) {
    update(BookingRequest.class, entity);
  }

  @Override
  public BookingRequest delete(Long id) {
    BookingRequest entity = delete(BookingRequest.class, id);
    return entity;
  }

  @Override
  protected <T> void updateEntityValues(T persistedEntity, T entity) {
    BookingRequest persistedObject = (BookingRequest) persistedEntity;
    BookingRequest updatedObject = (BookingRequest) entity;
    persistedObject.setCardDetails(updatedObject.getCardDetails());
    persistedObject.setDate(updatedObject.getDate());
    persistedObject.setDryRun(updatedObject.isDryRun());
    persistedObject.setDuration(updatedObject.getDuration());
    persistedObject.setPassword(updatedObject.getPassword());
    persistedObject.setSessionType(updatedObject.getSessionType());
    persistedObject.setSite(updatedObject.getSite());
    persistedObject.setTime(updatedObject.getTime());
    persistedObject.setUsername(updatedObject.getUsername());
  }
}
