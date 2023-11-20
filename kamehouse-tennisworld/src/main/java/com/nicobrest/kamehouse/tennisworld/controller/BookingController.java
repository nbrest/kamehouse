package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import com.nicobrest.kamehouse.tennisworld.service.BookingService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller to do bookings to tennis world.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/tennis-world")
public class BookingController extends AbstractController {

  @Autowired
  @Qualifier("perfectGymBookingService")
  private BookingService bookingService;

  /**
   * Process a tennis world booking request.
   */
  @PostMapping(path = "/bookings")
  @ResponseBody
  public ResponseEntity<BookingResponse> bookings(
      @RequestBody BookingRequestDto bookingRequestDto) {
    bookingRequestDto.setScheduled(false);
    BookingRequest bookingRequest = bookingRequestDto.buildEntity();
    StringUtils.sanitizeEntity(bookingRequest);
    BookingResponse bookingResponse = bookingService.book(bookingRequest);
    StringUtils.sanitizeEntity(bookingResponse);
    switch (bookingResponse.getStatus()) {
      case ERROR:
        logger.error("Response {}", bookingResponse);
        return new ResponseEntity<>(bookingResponse, HttpStatus.BAD_REQUEST);
      case INTERNAL_ERROR:
        logger.error("Response {}", bookingResponse);
        return new ResponseEntity<>(bookingResponse, HttpStatus.INTERNAL_SERVER_ERROR);
      default:
        return generatePostResponseEntity(bookingResponse);
    }
  }

  /**
   * Trigger an execution to process all the scheduled bookings configured in the database.
   */
  @PostMapping(path = "/scheduled-bookings")
  @ResponseBody
  public ResponseEntity<List<BookingResponse>> scheduledBookings() {
    List<BookingResponse> responses = bookingService.bookScheduledSessions();
    return generatePostResponseEntity(responses);
  }
}
