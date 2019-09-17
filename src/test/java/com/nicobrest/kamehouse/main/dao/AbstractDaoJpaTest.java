package com.nicobrest.kamehouse.main.dao;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Abstract class to group common functionality to execute DaoJpa tests.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractDaoJpaTest extends AbstractDaoJpa {

  @Override
  protected <T> void updateEntityValues(T persistedEntity, T entity) {
    // Method not required in test classes.
  }
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  /**
   * Clear all table data for the specified table.
   */
  protected void clearTable(String tableName) {
    EntityManager em = getEntityManager();
    em.getTransaction().begin();
    Query query = em.createNativeQuery("DELETE FROM " + tableName);
    query.executeUpdate();
    em.getTransaction().commit();
    em.close();
  }
}
