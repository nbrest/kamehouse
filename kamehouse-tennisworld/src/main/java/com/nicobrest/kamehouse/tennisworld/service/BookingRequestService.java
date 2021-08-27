package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Service layer to manage the TennisWorld BookingRequests.
 *
 * @author nbrest
 */
@Service
public class BookingRequestService extends AbstractCrudService<BookingRequest, BookingRequestDto>
    implements CrudService<BookingRequest, BookingRequestDto> {

  private static final Pattern TIME_PATTERN = Pattern.compile("[0-9]{2}:[0-9]{2}");
  private static final Pattern DURATION_PATTERN = Pattern.compile("[0-9]{1,3}");

  @Autowired
  @Qualifier("bookingRequestDaoJpa")
  private CrudDao<BookingRequest> bookingRequestDao;

  @Override
  public Long create(BookingRequestDto dto) {
    return create(bookingRequestDao, dto);
  }

  @Override
  public BookingRequest read(Long id) {
    return read(bookingRequestDao, id);
  }

  @Override
  public List<BookingRequest> readAll() {
    return readAll(bookingRequestDao);
  }

  @Override
  public void update(BookingRequestDto dto) {
    update(bookingRequestDao, dto);
  }

  @Override
  public BookingRequest delete(Long id) {
    return delete(bookingRequestDao, id);
  }

  @Override
  //TODO Create an interface for toEntity for all dtos and remove this method from service layers
  protected BookingRequest getModel(BookingRequestDto dto) {
    return dto.toEntity();
  }

  @Override
  protected void validate(BookingRequest entity) {
    // sessionType and site  get validated automatically as they are enums
    // date gets validated automatically to yyyy-mm-dd

    if (entity.getTime() == null || !TIME_PATTERN.matcher(entity.getTime()).matches()) {
      InputValidator.throwInputValidationError("Invalid time");
    }

    if (entity.getDuration() != null) {
      if (!DURATION_PATTERN.matcher(entity.getDuration()).matches()) {
        InputValidator.throwInputValidationError("Invalid duration");
      }
    }
  }
}
