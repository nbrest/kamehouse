package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * JPA DAO for BookingResponse entity.
 *
 * @author nbrest
 */
@Repository
public class BookingResponseDaoJpa extends AbstractCrudDaoJpa implements CrudDao<BookingResponse> {

  @Override
  public Long create(BookingResponse entity) {
    return create(BookingResponse.class, entity);
  }

  @Override
  public BookingResponse read(Long id) {
    BookingResponse entity = read(BookingResponse.class, id);
    return entity;
  }

  @Override
  public List<BookingResponse> readAll() {
    List<BookingResponse> entities = readAll(BookingResponse.class);
    return entities;
  }

  @Override
  public void update(BookingResponse entity) {
    update(BookingResponse.class, entity);
  }

  @Override
  public BookingResponse delete(Long id) {
    BookingResponse entity = delete(BookingResponse.class, id);
    return entity;
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
