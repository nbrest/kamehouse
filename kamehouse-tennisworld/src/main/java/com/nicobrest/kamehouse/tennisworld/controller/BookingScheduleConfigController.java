package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractCrudController;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.utils.PasswordUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingScheduleConfigDto;
import com.nicobrest.kamehouse.tennisworld.service.BookingScheduleConfigService;
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
 * Controller class for the tennis world booking schedule config.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/tennis-world")
public class BookingScheduleConfigController extends
    AbstractCrudController<BookingScheduleConfig, BookingScheduleConfigDto> {

  private BookingScheduleConfigService bookingScheduleConfigService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public BookingScheduleConfigController(
      BookingScheduleConfigService bookingScheduleConfigService) {
    this.bookingScheduleConfigService = bookingScheduleConfigService;
  }

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public CrudService<BookingScheduleConfig, BookingScheduleConfigDto> getCrudService() {
    return bookingScheduleConfigService;
  }

  /**
   * Creates a new entity in the repository.
   */
  @PostMapping(path = "/booking-schedule-configs")
  @ResponseBody
  @Override
  public ResponseEntity<Long> create(@RequestBody BookingScheduleConfigDto dto) {
    return super.create(dto);
  }

  /**
   * Returns a specific entity from the repository based on the id.
   */
  @GetMapping(path = "/booking-schedule-configs/{id}")
  @ResponseBody
  @Override
  public ResponseEntity<BookingScheduleConfig> read(@PathVariable Long id) {
    return generatePasswordLessResponse(super.read(id));
  }

  /**
   * Returns all entities.
   */
  @GetMapping(path = "/booking-schedule-configs")
  @ResponseBody
  @Override
  public ResponseEntity<List<BookingScheduleConfig>> readAll() {
    return generatePasswordLessResponseList(super.readAll());
  }

  /**
   * Updates an entity in the repository.
   */
  @PutMapping(path = "/booking-schedule-configs/{id}")
  @ResponseBody
  @Override
  public ResponseEntity<Void> update(
      @PathVariable Long id, @RequestBody BookingScheduleConfigDto dto) {
    return super.update(id, dto);
  }

  /**
   * Deletes an entity from the repository.
   */
  @DeleteMapping(path = "/booking-schedule-configs/{id}")
  @ResponseBody
  @Override
  public ResponseEntity<BookingScheduleConfig> delete(@PathVariable Long id) {
    return generatePasswordLessResponse(super.delete(id));
  }

  /**
   * Remove the password from the tennisWorldUser in the response.
   */
  private ResponseEntity<BookingScheduleConfig> generatePasswordLessResponse(
      ResponseEntity<BookingScheduleConfig> responseEntity) {
    BookingScheduleConfig responseBody = responseEntity.getBody();
    if (responseBody != null) {
      PasswordUtils.unsetPassword(responseBody.getTennisWorldUser());
    }
    return responseEntity;
  }

  /**
   * Remove the password from the tennisWorldUser in the response.
   */
  private ResponseEntity<List<BookingScheduleConfig>> generatePasswordLessResponseList(
      ResponseEntity<List<BookingScheduleConfig>> responseEntity) {
    List<BookingScheduleConfig> responseBody = responseEntity.getBody();
    if (responseBody != null && !responseBody.isEmpty()) {
      for (BookingScheduleConfig bookingScheduleConfig : responseBody) {
        PasswordUtils.unsetPassword(bookingScheduleConfig.getTennisWorldUser());
      }
    }
    return responseEntity;
  }
}
