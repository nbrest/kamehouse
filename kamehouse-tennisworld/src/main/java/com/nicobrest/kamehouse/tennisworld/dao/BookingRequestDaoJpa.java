package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import org.springframework.stereotype.Repository;

/**
 * JPA DAO for BookingRequest entity.
 *
 * @author nbrest
 */
@Repository
public class BookingRequestDaoJpa extends AbstractCrudDaoJpa<BookingRequest> {

  @Override
  public Class<BookingRequest> getEntityClass() {
    return BookingRequest.class;
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
