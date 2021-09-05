package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import org.springframework.stereotype.Repository;

/**
 * JPA DAO for BookingResponse entity.
 *
 * @author nbrest
 */
@Repository
public class BookingResponseDaoJpa extends AbstractCrudDaoJpa<BookingResponse> {

  @Override
  public Class<BookingResponse> getEntityClass() {
    return BookingResponse.class;
  }

  @Override
  protected <T> void updateEntityValues(T persistedEntity, T entity) {
    BookingResponse persistedObject = (BookingResponse) persistedEntity;
    BookingResponse updatedObject = (BookingResponse) entity;
    persistedObject.setStatus(updatedObject.getStatus());
    persistedObject.setMessage(updatedObject.getMessage());
    persistedObject.setRequest(updatedObject.getRequest());
  }
}
