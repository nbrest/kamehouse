package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDtoTranslator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service layer to manage the TennisWorld BookingRequests.
 *
 * @author nbrest
 */
@Service
public class BookingRequestService extends AbstractCrudService<BookingRequest, BookingRequestDto> {

  private static final BookingRequestDtoTranslator TRANSLATOR = new BookingRequestDtoTranslator();
  private static final Pattern TIME_PATTERN = Pattern.compile("\\d{2}:\\d{2}");
  private static final Pattern DURATION_PATTERN = Pattern.compile("\\d{1,3}");

  private CrudDao<BookingRequest> bookingRequestDao;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public BookingRequestService(
      @Qualifier("bookingRequestDaoJpa") CrudDao<BookingRequest> bookingRequestDao) {
    this.bookingRequestDao = bookingRequestDao;
  }

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public CrudDao<BookingRequest> getCrudDao() {
    return bookingRequestDao;
  }

  @Override
  public KameHouseDtoTranslator<BookingRequest, BookingRequestDto> getDtoTranslator() {
    return TRANSLATOR;
  }

  @Override
  protected void validate(BookingRequest entity) {
    // sessionType and site  get validated automatically as they are enums
    // date gets validated automatically to yyyy-mm-dd

    if (entity.getTime() == null || !TIME_PATTERN.matcher(entity.getTime()).matches()) {
      InputValidator.throwInputValidationError("Invalid time. Expected HH:MM");
    }

    if (entity.getDuration() != null && !DURATION_PATTERN.matcher(entity.getDuration()).matches()) {
      InputValidator.throwInputValidationError("Invalid duration. Expected 1 to 3 digits");
    }
  }
}
