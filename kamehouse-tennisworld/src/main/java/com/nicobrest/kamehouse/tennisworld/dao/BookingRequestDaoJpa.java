package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Repository;

/**
 * JPA DAO for BookingRequest entity.
 *
 * @author nbrest
 */
@Repository
public class BookingRequestDaoJpa extends AbstractCrudDaoJpa<BookingRequest> {

  public BookingRequestDaoJpa(EntityManagerFactory entityManagerFactory) {
    super(entityManagerFactory);
  }

  @Override
  public Class<BookingRequest> getEntityClass() {
    return BookingRequest.class;
  }

  @Override
  protected void updateEntityValues(BookingRequest persistedEntity, BookingRequest entity) {
    persistedEntity.setCardDetails(entity.getCardDetails());
    persistedEntity.setDate(entity.getDate());
    persistedEntity.setDryRun(entity.isDryRun());
    persistedEntity.setDuration(entity.getDuration());
    persistedEntity.setPassword(entity.getPassword());
    persistedEntity.setSessionType(entity.getSessionType());
    persistedEntity.setSite(entity.getSite());
    persistedEntity.setTime(entity.getTime());
    persistedEntity.setUsername(entity.getUsername());
    persistedEntity.setCourtNumber(entity.getCourtNumber());
  }
}
