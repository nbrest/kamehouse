package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import com.nicobrest.kamehouse.tennisworld.service.BookingService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to do bookings to tennis world.
 *
 * @author nbrest
 */
@RestController
@RequestMapping(value = "/api/v1/tennis-world")
public class BookingController extends AbstractController {

  private BookingService bookingService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public BookingController(@Qualifier("perfectGymBookingService") BookingService bookingService) {
    this.bookingService = bookingService;
  }

  /**
   * Process a tennis world booking request.
   */
  @PostMapping(path = "/bookings")
  public ResponseEntity<BookingResponse> bookings(
      @RequestBody BookingRequestDto bookingRequestDto) {
    bookingRequestDto.setScheduled(false);
    BookingRequest bookingRequest = bookingRequestDto.buildEntity();
    BookingResponse bookingResponse = bookingService.book(bookingRequest);
    switch (bookingResponse.getStatus()) {
      case ERROR:
        if (logger.isErrorEnabled()) {
          logger.error("Response {}", StringUtils.sanitize(bookingResponse));
        }
        return new ResponseEntity<>(bookingResponse, HttpStatus.BAD_REQUEST);
      case INTERNAL_ERROR:
        if (logger.isErrorEnabled()) {
          logger.error("Response {}", StringUtils.sanitize(bookingResponse));
        }
        return new ResponseEntity<>(bookingResponse, HttpStatus.INTERNAL_SERVER_ERROR);
      default:
        return generatePostResponseEntity(bookingResponse);
    }
  }

  /**
   * Trigger an execution to process all the scheduled bookings configured in the database.
   */
  @PostMapping(path = "/scheduled-bookings")
  public ResponseEntity<List<BookingResponse>> scheduledBookings() {
    List<BookingResponse> responses = bookingService.bookScheduledSessions();
    return generatePostResponseEntity(responses);
  }
}
