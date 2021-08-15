package com.nicobrest.kamehouse.commons.dao;

import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
  @SuppressFBWarnings // False positive: UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD
  public ExpectedException thrown = ExpectedException.none();

  @SuppressFBWarnings // False positive: UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD
  protected TestUtils<T, D> testUtils;
  
  /**
   * Clears all table data for the specified table.
   */
  protected void clearTable(String tableName) {
    EntityManager em = getEntityManager();
    em.getTransaction().begin();
    Query query = em.createNativeQuery("DELETE FROM " + tableName);
    query.executeUpdate();
    em.getTransaction().commit();
    em.close();
  }

  /**
   * Execute the specified insert query.
   */
  protected void insertData(String insertQuery) {
    EntityManager em = getEntityManager();
    em.getTransaction().begin();
    Query query = em.createNativeQuery(insertQuery);
    query.executeUpdate();
    em.getTransaction().commit();
    em.close();
  }

  @Override
  protected <E> void updateEntityValues(E persistedEntity, E entity) {
    // Method required by AbstractDaoJpa overriden here as it's not needed in child test classes.
  }
}
