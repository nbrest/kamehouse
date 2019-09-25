package com.nicobrest.kamehouse.main.dao;

import com.nicobrest.kamehouse.main.testutils.TestUtils;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Abstract class to group common functionality to execute DaoJpa tests. Extends
 * AbstractDaoJpa so I get the raw Jpa data access methods to setup the
 * test data to test the DAOs.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractDaoJpaTest<T, D> extends AbstractDaoJpa {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  protected TestUtils<T, D> testUtils;
  
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

  @Override
  protected <E> void updateEntityValues(E persistedEntity, E entity) {
    // Method required by AbstractDaoJpa overriden here as it's not needed in child test classes.
  }
}
