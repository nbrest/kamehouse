package com.nicobrest.kamehouse.main.testutils;

import com.nicobrest.kamehouse.main.dao.Identifiable;

import java.util.List;

/**
 * Abstract class to inherit by Test Utils classes.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractTestUtils<T, D> implements TestUtils<T, D> {

  private static final String NOT_IMPLEMENTED =
      "This method has not been implemented by this test utils class.";

  protected T singleTestData;
  protected List<T> testDataList;
  protected D testDataDto;
  
  @Override
  public void initTestData() {
    throw new UnsupportedOperationException(NOT_IMPLEMENTED);
  }
  
  @Override
  public T getSingleTestData() {
    return singleTestData;
  }

  @Override
  public List<T> getTestDataList() {
    return testDataList;
  }

  @Override
  public D getTestDataDto() {
    return testDataDto;
  }

  @Override
  public void setIds() {
    if (testDataDto != null) {
      Identifiable identifiableDto = (Identifiable) testDataDto;
      identifiableDto.setId(100L);
    }
    if (testDataList != null) {
      for (int i = 0 ; i < testDataList.size(); i++) {
        Identifiable identifiableTestDataEntry = (Identifiable) testDataList.get(i);
        Long id = 100L + i;
        identifiableTestDataEntry.setId(id);
      }
    }
  }

  @Override
  public void removeIds() {
    if (testDataDto != null) {
      Identifiable identifiableDto = (Identifiable) testDataDto;
      identifiableDto.setId(null);
    }
    if (testDataList != null) {
      for (int i = 0 ; i < testDataList.size(); i++) {
        Identifiable identifiableTestDataEntry = (Identifiable) testDataList.get(i); 
        identifiableTestDataEntry.setId(null);
      }
    }
  }

  @Override
  public void assertEqualsAllAttributes(T expectedEntity, T returnedEntity) {
    throw new UnsupportedOperationException(NOT_IMPLEMENTED);
  }
}
