package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractCrudController;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import com.nicobrest.kamehouse.tennisworld.service.BookingRequestService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller class for the tennis world booking requests.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/tennis-world")
public class BookingRequestController extends AbstractCrudController {

  @Autowired private BookingRequestService bookingRequestService;

  /** Creates a new entity in the repository. */
  @PostMapping(path = "/booking-requests")
  @ResponseBody
  public ResponseEntity<Long> create(@RequestBody BookingRequestDto dto) {
    return create(bookingRequestService, dto);
  }

  /** Returns a specific entity from the repository based on the id. */
  @GetMapping(path = "/booking-requests/{id}")
  @ResponseBody
  public ResponseEntity<BookingRequest> read(@PathVariable Long id) {
    return generatePasswordLessResponseEntity(read(bookingRequestService, id));
  }

  /** Returns all entities. */
  @GetMapping(path = "/booking-requests")
  @ResponseBody
  public ResponseEntity<List<BookingRequest>> readAll() {
    return generatePasswordLessResponseEntity(readAll(bookingRequestService));
  }

  /** Updates an entity in the repository. */
  @PutMapping(path = "/booking-requests/{id}")
  @ResponseBody
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody BookingRequestDto dto) {
    return update(bookingRequestService, id, dto);
  }

  /** Deletes an entity from the repository. */
  @DeleteMapping(path = "/booking-requests/{id}")
  @ResponseBody
  public ResponseEntity<BookingRequest> delete(@PathVariable Long id) {
    return generatePasswordLessResponseEntity(delete(bookingRequestService, id));
  }
}
