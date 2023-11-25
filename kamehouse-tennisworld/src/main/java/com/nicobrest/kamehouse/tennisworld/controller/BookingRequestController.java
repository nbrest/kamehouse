package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractCrudController;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import com.nicobrest.kamehouse.tennisworld.service.BookingRequestService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
public class BookingRequestController extends
    AbstractCrudController<BookingRequest, BookingRequestDto> {

  private BookingRequestService bookingRequestService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public BookingRequestController(BookingRequestService bookingRequestService) {
    this.bookingRequestService = bookingRequestService;
  }

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public CrudService<BookingRequest, BookingRequestDto> getCrudService() {
    return bookingRequestService;
  }

  /**
   * Creates a new entity in the repository.
   */
  @PostMapping(path = "/booking-requests")
  @ResponseBody
  @Override
  public ResponseEntity<Long> create(@RequestBody BookingRequestDto dto) {
    return super.create(dto);
  }

  /**
   * Returns a specific entity from the repository based on the id.
   */
  @GetMapping(path = "/booking-requests/{id}")
  @ResponseBody
  @Override
  public ResponseEntity<BookingRequest> read(@PathVariable Long id) {
    return generatePasswordLessResponseEntity(super.read(id));
  }

  /**
   * Returns all entities.
   */
  @GetMapping(path = "/booking-requests")
  @ResponseBody
  @Override
  public ResponseEntity<List<BookingRequest>> readAll() {
    return generatePasswordLessResponseEntity(super.readAll());
  }

  /**
   * Updates an entity in the repository.
   */
  @PutMapping(path = "/booking-requests/{id}")
  @ResponseBody
  @Override
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody BookingRequestDto dto) {
    return super.update(id, dto);
  }

  /**
   * Deletes an entity from the repository.
   */
  @DeleteMapping(path = "/booking-requests/{id}")
  @ResponseBody
  @Override
  public ResponseEntity<BookingRequest> delete(@PathVariable Long id) {
    return generatePasswordLessResponseEntity(super.delete(id));
  }
}
