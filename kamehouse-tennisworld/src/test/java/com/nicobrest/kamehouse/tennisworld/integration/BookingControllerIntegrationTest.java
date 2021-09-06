package com.nicobrest.kamehouse.tennisworld.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingRequestTestUtils;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the BookingController class.
 *
 * @author nbrest
 */
public class BookingControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/tennis-world";

  @Override
  public String getWebapp() {
    return "kame-house-tennisworld";
  }

  @Test
  public void bookingsTest() throws Exception {
    logger.info("Running bookingsTest");
    BookingRequestTestUtils bookingRequestTestUtils = new BookingRequestTestUtils();
    bookingRequestTestUtils.initTestData();
    BookingRequest bookingRequest = bookingRequestTestUtils.getSingleTestData();

    HttpResponse response = post(getWebappUrl() + API_URL + "/bookings", bookingRequest);

    assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    BookingResponse responseBody = getResponseBody(response, BookingResponse.class);
    assertNotNull(responseBody);
    logger.info("Response body {}", responseBody);
  }

  @Test
  public void scheduledBookingsTest() throws Exception {
    logger.info("Running scheduledBookingsTest");

    HttpResponse response = post(getWebappUrl() + API_URL + "/scheduled-bookings");

    verifySuccessfulCreatedResponse(response, List.class);
  }
}

