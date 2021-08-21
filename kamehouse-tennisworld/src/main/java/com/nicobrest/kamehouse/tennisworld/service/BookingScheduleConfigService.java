package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingScheduleConfigDto;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Service layer to manage the TennisWorldBookingScheduleConfigs.
 *
 * @author nbrest
 */
@Service
public class BookingScheduleConfigService
    extends AbstractCrudService<BookingScheduleConfig, BookingScheduleConfigDto>
    implements CrudService<BookingScheduleConfig, BookingScheduleConfigDto> {

  private static final Pattern TIME_PATTERN = Pattern.compile("[0-9]{2}:[0-9]{2}");
  private static final Pattern DURATION_PATTERN = Pattern.compile("[0-9]{1,3}");

  @Autowired
  @Qualifier("bookingScheduleConfigDaoJpa")
  private CrudDao<BookingScheduleConfig> bookingScheduleConfigDao;

  @Autowired
  private TennisWorldUserService tennisWorldUserService;

  @Override
  public Long create(BookingScheduleConfigDto dto) {
    return create(bookingScheduleConfigDao, dto);
  }

  @Override
  public BookingScheduleConfig read(Long id) {
    return read(bookingScheduleConfigDao, id);
  }

  @Override
  public List<BookingScheduleConfig> readAll() {
    return readAll(bookingScheduleConfigDao);
  }

  @Override
  public void update(BookingScheduleConfigDto dto) {
    update(bookingScheduleConfigDao, dto);
  }

  @Override
  public BookingScheduleConfig delete(Long id) {
    return delete(bookingScheduleConfigDao, id);
  }

  @Override
  protected BookingScheduleConfig getModel(BookingScheduleConfigDto dto) {
    BookingScheduleConfig entity = new BookingScheduleConfig();
    entity.setId(dto.getId());
    entity.setTennisWorldUser(getTennisWorldUser(dto));
    entity.setSessionType(dto.getSessionType());
    entity.setSite(dto.getSite());
    entity.setDay(dto.getDay());
    entity.setTime(dto.getTime());
    entity.setBookingDate(dto.getBookingDate());
    entity.setBookAheadDays(dto.getBookAheadDays());
    entity.setEnabled(BooleanUtils.toBoolean(dto.getEnabled()));
    entity.setDuration(dto.getDuration());
    return entity;
  }

  @Override
  protected void validate(BookingScheduleConfig entity) {
    // sessionType, site and day get validated automatically as they are enums
    // tennisWorldUser gets looked up and fails the request if the lookup fails
    // bookingDate gets validated automatically to yyyy-mm-dd

    if (entity.getTime() == null || !TIME_PATTERN.matcher(entity.getTime()).matches()) {
      InputValidator.throwInputValidationError("Invalid time");
    }

    if (entity.getBookAheadDays() == null || entity.getBookAheadDays() < 0) {
      InputValidator.throwInputValidationError("Invalid bookAheadDays");
    }

    if (entity.getDuration() != null) {
      if (!DURATION_PATTERN.matcher(entity.getDuration()).matches()) {
        InputValidator.throwInputValidationError("Invalid duration");
      }
    }
  }

  /**
   * Get the TennisWorldUser from the email on the booking schedule config request.
   */
  private TennisWorldUser getTennisWorldUser(BookingScheduleConfigDto dto) {
    TennisWorldUser requestUser = dto.getTennisWorldUser();
    if (requestUser == null || StringUtils.isEmpty(requestUser.getEmail())) {
      throw new KameHouseInvalidDataException("tennisWorld.email is empty");
    }
    String email = requestUser.getEmail();
    try {
      return tennisWorldUserService.getByEmail(email);
    } catch (KameHouseNotFoundException e) {
      throw new KameHouseInvalidDataException("User not found for email: " + email);
    }
  }
}
