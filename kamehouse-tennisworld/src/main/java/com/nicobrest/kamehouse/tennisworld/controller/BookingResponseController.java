package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractCrudController;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;
import com.nicobrest.kamehouse.tennisworld.service.BookingResponseService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for the tennis world booking responses.
 *
 * @author nbrest
 */
@RestController
@RequestMapping(value = "/api/v1/tennis-world")
public class BookingResponseController extends
    AbstractCrudController<BookingResponse, BookingResponseDto> {

  private BookingResponseService bookingResponseService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public BookingResponseController(BookingResponseService bookingResponseService) {
    this.bookingResponseService = bookingResponseService;
  }

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public CrudService<BookingResponse, BookingResponseDto> getCrudService() {
    return bookingResponseService;
  }

  /**
   * Creates a new entity in the repository.
   */
  @PostMapping(path = "/booking-responses")
  @Override
  public ResponseEntity<Long> create(@RequestBody BookingResponseDto dto) {
    return super.create(dto);
  }

  /**
   * Returns a specific entity from the repository based on the id.
   */
  @GetMapping(path = "/booking-responses/{id}")
  @Override
  public ResponseEntity<BookingResponse> read(@PathVariable Long id) {
    return super.read(id);
  }

  /**
   * Returns all entities.
   */
  @GetMapping(path = "/booking-responses")
  @Override
  public ResponseEntity<List<BookingResponse>> readAll(
      @RequestParam(value = "maxRows", required = false, defaultValue = "0") Integer maxRows,
      @RequestParam(value = "sortColumn", required = false, defaultValue = "") String sortColumn,
      @RequestParam(value = "sortAscending", required = false, defaultValue = "true")
      Boolean sortAscending) {
    return super.readAll(maxRows, StringUtils.sanitize(sortColumn), sortAscending);
  }

  /**
   * Updates an entity in the repository.
   */
  @PutMapping(path = "/booking-responses/{id}")
  @Override
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody BookingResponseDto dto) {
    return super.update(id, dto);
  }

  /**
   * Deletes an entity from the repository.
   */
  @DeleteMapping(path = "/booking-responses/{id}")
  @Override
  public ResponseEntity<BookingResponse> delete(@PathVariable Long id) {
    return super.delete(id);
  }
}
