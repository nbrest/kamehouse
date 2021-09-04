package com.nicobrest.kamehouse.tennisworld.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractCrudControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingResponseTestUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Integration tests for the BookingResponseController class.
 *
 * @author nbrest
 */
public class BookingResponseControllerIntegrationTest
    extends AbstractCrudControllerIntegrationTest<BookingResponse, BookingResponseDto> {

  @Override
  public boolean hasUniqueConstraints() {
    return false;
  }

  @Override
  public String getWebapp() {
    return "/kame-house-tennisworld";
  }

  @Override
  public String getCrudUrlSuffix() {
    return BookingResponseTestUtils.API_V1_TENNISWORLD_BOOKING_RESPONSES;
  }

  @Override
  public Class<BookingResponse> getEntityClass() {
    return BookingResponse.class;
  }

  @Override
  public TestUtils<BookingResponse, BookingResponseDto> getTestUtils() {
    return new BookingResponseTestUtils();
  }

  @Override
  public BookingResponseDto buildDto(BookingResponseDto dto) {
    String randomMessage = RandomStringUtils.randomAlphabetic(12);
    dto.setMessage(randomMessage + " - initial message");
    return dto;
  }

  @Override
  public void updateDto(BookingResponseDto dto) {
    String randomMessage = RandomStringUtils.randomAlphabetic(12);
    dto.setMessage(randomMessage + " - updated message");
  }
}
