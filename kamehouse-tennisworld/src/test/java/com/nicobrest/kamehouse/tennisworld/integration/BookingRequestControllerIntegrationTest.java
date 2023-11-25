package com.nicobrest.kamehouse.tennisworld.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractCrudControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingRequestTestUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Integration tests for the BookingRequestController class.
 *
 * @author nbrest
 */
class BookingRequestControllerIntegrationTest
    extends AbstractCrudControllerIntegrationTest<BookingRequest, BookingRequestDto> {

  @Override
  public boolean hasUniqueConstraints() {
    return false;
  }

  @Override
  public String getWebapp() {
    return "kame-house-tennisworld";
  }

  @Override
  public String getCrudUrlSuffix() {
    return BookingRequestTestUtils.API_V1_TENNISWORLD_BOOKING_REQUESTS;
  }

  @Override
  public Class<BookingRequest> getEntityClass() {
    return BookingRequest.class;
  }

  @Override
  public TestUtils<BookingRequest, BookingRequestDto> getTestUtils() {
    return new BookingRequestTestUtils();
  }

  @Override
  public BookingRequestDto buildDto(BookingRequestDto dto) {
    String randomUsername = RandomStringUtils.randomAlphabetic(12);
    dto.setUsername(randomUsername + "@dbz.com");
    return dto;
  }

  @Override
  public void updateDto(BookingRequestDto dto) {
    dto.setPassword(RandomStringUtils.randomAlphabetic(12));
  }
}
