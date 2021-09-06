package com.nicobrest.kamehouse.tennisworld.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractCrudControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingResponseTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the BookingResponseController class.
 *
 * @author nbrest
 */
public class BookingResponseControllerIntegrationTest
    extends AbstractCrudControllerIntegrationTest<BookingResponse, BookingResponseDto> {

  private BookingRequestControllerIntegrationTest bookingRequestControllerIntegrationTest =
      new BookingRequestControllerIntegrationTest();

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
    BookingRequestDto bookingRequestDto = bookingRequestControllerIntegrationTest.getDto();
    bookingRequestDto.setId(bookingRequestControllerIntegrationTest.getCreatedId());
    dto.setRequest(bookingRequestDto.buildEntity());
    return dto;
  }

  @Override
  public void updateDto(BookingResponseDto dto) {
    String randomMessage = RandomStringUtils.randomAlphabetic(12);
    dto.setMessage(randomMessage + " - updated message");
  }

  /**
   * Creates an entity.
   */
  @Test
  @Order(1)
  @Override
  public void createTest() throws Exception {
    bookingRequestControllerIntegrationTest.beforeTest();
    bookingRequestControllerIntegrationTest.createTest();
    super.createTest();
  }

  /**
   * Deletes an entity.
   */
  @Test
  @Order(8)
  @Override
  public void deleteTest() throws Exception {
    super.deleteTest();
    bookingRequestControllerIntegrationTest.beforeTest();
    bookingRequestControllerIntegrationTest.deleteTest();
  }
}
