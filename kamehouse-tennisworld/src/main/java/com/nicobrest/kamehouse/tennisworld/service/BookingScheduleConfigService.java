package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingScheduleConfigDto;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service layer to manage the TennisWorldBookingScheduleConfigs.
 *
 * @author nbrest
 */
@Service
public class BookingScheduleConfigService
    extends AbstractCrudService<BookingScheduleConfig, BookingScheduleConfigDto> {

  private static final Pattern TIME_PATTERN = Pattern.compile("\\d{2}:\\d{2}");
  private static final Pattern DURATION_PATTERN = Pattern.compile("\\d{1,3}");

  private CrudDao<BookingScheduleConfig> bookingScheduleConfigDao;
  private TennisWorldUserService tennisWorldUserService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public BookingScheduleConfigService(
      @Qualifier("bookingScheduleConfigDaoJpa") CrudDao<BookingScheduleConfig>
          bookingScheduleConfigDao, TennisWorldUserService tennisWorldUserService) {
    this.bookingScheduleConfigDao = bookingScheduleConfigDao;
    this.tennisWorldUserService = tennisWorldUserService;
  }

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public CrudDao<BookingScheduleConfig> getCrudDao() {
    return bookingScheduleConfigDao;
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

    if (entity.getDuration() != null && !DURATION_PATTERN.matcher(entity.getDuration()).matches()) {
      InputValidator.throwInputValidationError("Invalid duration");
    }
  }

  @Override
  public Long create(BookingScheduleConfigDto dto) {
    setTennisWorldUser(dto);
    return super.create(dto);
  }

  @Override
  public void update(BookingScheduleConfigDto dto) {
    setTennisWorldUser(dto);
    super.update(dto);
  }

  /**
   * Set the TennisWorldUser from the email on the booking schedule config request.
   */
  private void setTennisWorldUser(BookingScheduleConfigDto dto) {
    TennisWorldUser requestUser = dto.getTennisWorldUser();
    if (requestUser == null || StringUtils.isEmpty(requestUser.getEmail())) {
      throw new KameHouseInvalidDataException("tennisWorld.email is empty");
    }
    String email = requestUser.getEmail();
    try {
      TennisWorldUser tennisWorldUser = tennisWorldUserService.getByEmail(email);
      dto.setTennisWorldUser(tennisWorldUser);
    } catch (KameHouseNotFoundException e) {
      throw new KameHouseInvalidDataException("User not found for email: " + email);
    }
  }
}
