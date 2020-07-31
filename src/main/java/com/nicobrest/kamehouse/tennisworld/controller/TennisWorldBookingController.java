package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.main.controller.AbstractController;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingResponse;
import com.nicobrest.kamehouse.tennisworld.service.TennisWorldBookingService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TennisWorldBookingController extends AbstractController {

  @Autowired
  private TennisWorldBookingService tennisWorldBookingService;

  /**
   * Process a tennis world booking request.
   */
  @PostMapping(path = "/bookings")
  @ResponseBody
  public ResponseEntity<TennisWorldBookingResponse> bookings(
      @RequestBody TennisWorldBookingRequest tennisWorldBookingRequest) {
    logger.info("Processing booking request: {}", tennisWorldBookingRequest);
    TennisWorldBookingResponse tennisWorldBookingResponse =
        tennisWorldBookingService.book(tennisWorldBookingRequest);
    switch (tennisWorldBookingResponse.getStatus()) {
      case ERROR:
        logger.error("Response {}", tennisWorldBookingResponse);
        return new ResponseEntity<>(tennisWorldBookingResponse, HttpStatus.BAD_REQUEST);
      case INTERNAL_ERROR:
        logger.error("Response {}", tennisWorldBookingResponse);
        return new ResponseEntity<>(tennisWorldBookingResponse, HttpStatus.INTERNAL_SERVER_ERROR);
      default:
        return generatePostResponseEntity(tennisWorldBookingResponse);
    }
  }
}
