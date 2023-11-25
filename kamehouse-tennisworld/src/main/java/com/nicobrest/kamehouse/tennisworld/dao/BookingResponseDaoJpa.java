package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Repository;

/**
 * JPA DAO for BookingResponse entity.
 *
 * @author nbrest
 */
@Repository
public class BookingResponseDaoJpa extends AbstractCrudDaoJpa<BookingResponse> {

  public BookingResponseDaoJpa(EntityManagerFactory entityManagerFactory) {
    super(entityManagerFactory);
  }

  @Override
  public Class<BookingResponse> getEntityClass() {
    return BookingResponse.class;
  }

  @Override
  protected void updateEntityValues(BookingResponse persistedEntity, BookingResponse entity) {
    persistedEntity.setStatus(entity.getStatus());
    persistedEntity.setMessage(entity.getMessage());
    persistedEntity.setRequest(entity.getRequest());
  }
}
