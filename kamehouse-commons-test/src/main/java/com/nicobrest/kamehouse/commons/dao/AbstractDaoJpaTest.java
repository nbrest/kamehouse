package com.nicobrest.kamehouse.commons.dao;

import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Abstract class to group common functionality to execute DaoJpa tests. Extends AbstractDaoJpa so I
 * get the raw Jpa data access methods to setup the test data to test the DAOs.
 *
 * @author nbrest
 */
public abstract class AbstractDaoJpaTest<T, D> extends AbstractDaoJpa {

  @SuppressFBWarnings(value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD")
  protected TestUtils<T, D> testUtils;

  /**
   * Using a normal insert made some tests hang when executed in parallel. Get an insert statement
   * for hsqldb for the specified table, columns and values. columns and values should be separated
   * by ", " and without any leading or trailing spaces.
   */
  protected static String getInsertQuery(String table, String columns, String values) {
    String columnsI = columns.replace(" ", " I.");
    String insertQuery =
        "MERGE INTO "
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
    return insertQuery;
  }

  /** Clears all table data for the specified table. */
  protected void clearTable(String tableName) {
    EntityManager em = getEntityManager();
    em.getTransaction().begin();
    Query query = em.createNativeQuery("DELETE FROM " + tableName);
    query.executeUpdate();
    em.getTransaction().commit();
    em.close();
  }

  /** Execute the specified insert query. */
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
