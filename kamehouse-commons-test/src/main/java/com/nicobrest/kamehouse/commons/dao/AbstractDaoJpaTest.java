package com.nicobrest.kamehouse.commons.dao;

import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Abstract class to group common functionality to execute DaoJpa tests. Extends AbstractDaoJpa so I
 * get the raw Jpa data access methods to setup the test data to test the DAOs.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public abstract class AbstractDaoJpaTest<E extends Identifiable, D extends Identifiable>
    extends AbstractDaoJpa<E> {

  protected TestUtils<E, D> testUtils;

  protected AbstractDaoJpaTest(EntityManagerFactory entityManagerFactory) {
    super(entityManagerFactory);
  }

  /**
   * Set testUtils.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public void setTestUtils(TestUtils<E, D> testUtils) {
    this.testUtils = testUtils;
  }

  /**
   * Using a normal insert made some tests hang when executed in parallel. Get an insert statement
   * for hsqldb for the specified table, columns and values. columns and values should be separated
   * by ", " and without any leading or trailing spaces.
   */
  protected static String getInsertQuery(String table, String columns, String values) {
    String columnsI = columns.replace(" ", " I.");
    return "MERGE INTO "
        + table
        + " USING (VALUES "
        + values
        + " ) "
        + " I ( "
        + columns
        + " ) ON ( "
        + table
        + ".ID = I.id ) "
        + " WHEN NOT MATCHED THEN "
        + " INSERT ( "
        + columns
        + " ) "
        + " VALUES ( I."
        + columnsI
        + " )";
  }

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
  protected void updateEntityValues(E persistedEntity, E entity) {
    // Method required by AbstractDaoJpa overriden here as it's not needed in child test classes.
  }
}
