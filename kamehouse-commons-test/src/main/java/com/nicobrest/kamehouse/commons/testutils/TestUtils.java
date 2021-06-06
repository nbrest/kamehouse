package com.nicobrest.kamehouse.commons.testutils;

import java.util.List;

/**
 * Interface to be implemented by all test util classes.
 * 
 * @author nbrest
 *
 */
public interface TestUtils<T, D> {

  /**
   * Initializes test data.
   */
  public void initTestData();

  /**
   * Returns a single test data entity.
   */
  public T getSingleTestData();

  /**
   * Returns a list of test data entities.
   */
  public List<T> getTestDataList();

  /**
   * Returns a single test data DTO.
   */
  public D getTestDataDto();

  /**
   * Sets the ids of all test data.
   */
  public void setIds();

  /**
   * Removes the ids of all test data.
   */
  public void removeIds();

  /**
   * Verifies that all the attributes of the returned entity match the ones of the
   * expected entity.
   */
  public void assertEqualsAllAttributes(T expectedEntity, T returnedEntity);

  /**
   * Verifies that the lists are identical, checking it's size, and for each
   * element, checking that all their attributes match.
   */
  public void assertEqualsAllAttributesList(List<T> expectedList, List<T> returnedList);
}
