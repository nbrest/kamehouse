package com.nicobrest.kamehouse.tennisworld.model.perfectgym;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Tests the equality contracts of the PerfectGym request bodies.
 *
 * @author nbrest
 */
class PerfectGymRequestTest {

  @Test
  void bookConfirmationRequestEqualsTest() {
    BookConfirmationRequest request = bookConfirmationRequest(1L, 2L);
    assertEquality(request, bookConfirmationRequest(1L, 99L),
        bookConfirmationRequest(99L, 2L));
  }

  @Test
  void calendarFiltersRequestEqualsTest() {
    CalendarFiltersRequest request = calendarFiltersRequest(1L);
    assertEquality(request, calendarFiltersRequest(1L), calendarFiltersRequest(99L));
  }

  @Test
  void clubZoneTypesRequestEqualsTest() {
    ClubZoneTypesRequest request = clubZoneTypesRequest(1L);
    assertEquality(request, clubZoneTypesRequest(1L), clubZoneTypesRequest(99L));
  }

  @Test
  void courtWeeklyScheduleRequestEqualsTest() {
    CourtWeeklyScheduleRequest request = courtWeeklyScheduleRequest(1L, "2021-07-26", 2L,
        "08:00", 7, 3L);
    assertEquality(
        request,
        courtWeeklyScheduleRequest(1L, "2021-07-26", 2L, "08:00", 7, 3L),
        courtWeeklyScheduleRequest(99L, "2021-07-26", 2L, "08:00", 7, 3L),
        courtWeeklyScheduleRequest(1L, "2021-07-27", 2L, "08:00", 7, 3L),
        courtWeeklyScheduleRequest(1L, "2021-07-26", 99L, "08:00", 7, 3L),
        courtWeeklyScheduleRequest(1L, "2021-07-26", 2L, "09:00", 7, 3L),
        courtWeeklyScheduleRequest(1L, "2021-07-26", 2L, "08:00", 8, 3L),
        courtWeeklyScheduleRequest(1L, "2021-07-26", 2L, "08:00", 7, 99L));
  }

  @Test
  void dailyClassesRequestEqualsTest() {
    DailyClassesRequest request = dailyClassesRequest(1L, "2021-07-26", 2L, 3L, 4L, 5L);
    assertEquality(
        request,
        dailyClassesRequest(1L, "2021-07-26", 2L, 3L, 4L, 5L),
        dailyClassesRequest(99L, "2021-07-26", 2L, 3L, 4L, 5L),
        dailyClassesRequest(1L, "2021-07-27", 2L, 3L, 4L, 5L),
        dailyClassesRequest(1L, "2021-07-26", 99L, 3L, 4L, 5L),
        dailyClassesRequest(1L, "2021-07-26", 2L, 99L, 4L, 5L),
        dailyClassesRequest(1L, "2021-07-26", 2L, 3L, 99L, 5L),
        dailyClassesRequest(1L, "2021-07-26", 2L, 3L, 4L, 99L));
  }

  @Test
  void finalizeCourtBookingRequestEqualsTest() {
    FinalizeCourtBookingRequest request = finalizeCourtBookingRequest(1L, true, false);
    assertEquality(
        request,
        finalizeCourtBookingRequest(1L, true, false),
        finalizeCourtBookingRequest(99L, true, false),
        finalizeCourtBookingRequest(1L, false, false),
        finalizeCourtBookingRequest(1L, true, true));
  }

  @Test
  void loginRequestEqualsTest() {
    LoginRequest request = loginRequest(true, "goku", "goku-son");
    assertEquality(
        request,
        loginRequest(true, "goku", "goku-son"),
        loginRequest(false, "goku", "goku-son"),
        loginRequest(true, "gohan", "goku-son"),
        loginRequest(true, "goku", "gohan-son"));
  }

  @Test
  void setCourtBookingDetailsRequestEqualsTest() {
    SetCourtBookingDetailsRequest request = setCourtBookingDetailsRequest(
        1L, "08:00", 2L, "1", 60);
    assertEquality(
        request,
        setCourtBookingDetailsRequest(1L, "08:00", 2L, "1", 60),
        setCourtBookingDetailsRequest(99L, "08:00", 2L, "1", 60),
        setCourtBookingDetailsRequest(1L, "09:00", 2L, "1", 60),
        setCourtBookingDetailsRequest(1L, "08:00", 99L, "1", 60),
        setCourtBookingDetailsRequest(1L, "08:00", 2L, "2", 60),
        setCourtBookingDetailsRequest(1L, "08:00", 2L, "1", 90));
  }

  private static void assertEquality(Object request, Object equal, Object... different) {
    assertEquals(request, request);
    assertEquals(request, equal);
    assertEquals(request.hashCode(), equal.hashCode());
    assertNotEquals(null, request);
    assertNotEquals(request, new Object());
    for (Object other : different) {
      assertNotEquals(request, other);
    }
    assertNotNull(request.toString());
  }

  private static BookConfirmationRequest bookConfirmationRequest(long clubId, long classId) {
    BookConfirmationRequest request = new BookConfirmationRequest();
    request.setClubId(clubId);
    request.setClassId(classId);
    return request;
  }

  private static CalendarFiltersRequest calendarFiltersRequest(long clubId) {
    CalendarFiltersRequest request = new CalendarFiltersRequest();
    request.setClubId(clubId);
    return request;
  }

  private static ClubZoneTypesRequest clubZoneTypesRequest(long clubId) {
    ClubZoneTypesRequest request = new ClubZoneTypesRequest();
    request.setClubId(clubId);
    return request;
  }

  private static CourtWeeklyScheduleRequest courtWeeklyScheduleRequest(
      long clubId, String date, Long zoneTypeId, String slots, Integer daysInWeek, Long zoneId) {
    CourtWeeklyScheduleRequest request = new CourtWeeklyScheduleRequest();
    request.setClubId(clubId);
    request.setDate(date);
    request.setZoneTypeId(zoneTypeId);
    request.setSlots(slots);
    request.setDaysInWeek(daysInWeek);
    request.setZoneId(zoneId);
    return request;
  }

  private static DailyClassesRequest dailyClassesRequest(
      long clubId, String date, Long categoryId, Long timeTableId, Long trainerId, Long zoneId) {
    DailyClassesRequest request = new DailyClassesRequest();
    request.setClubId(clubId);
    request.setDate(date);
    request.setCategoryId(categoryId);
    request.setTimeTableId(timeTableId);
    request.setTrainerId(trainerId);
    request.setZoneId(zoneId);
    return request;
  }

  private static FinalizeCourtBookingRequest finalizeCourtBookingRequest(
      long ruleId, boolean otherCalendarEventBookedAtRequestedTime,
      boolean hasUserRequiredProducts) {
    FinalizeCourtBookingRequest request = new FinalizeCourtBookingRequest();
    request.setRuleId(ruleId);
    request.setOtherCalendarEventBookedAtRequestedTime(otherCalendarEventBookedAtRequestedTime);
    request.setHasUserRequiredProducts(hasUserRequiredProducts);
    return request;
  }

  private static LoginRequest loginRequest(boolean rememberMe, String username, String password) {
    LoginRequest request = new LoginRequest();
    request.setRememberMe(rememberMe);
    request.setUsername(username);
    request.setPassword(password);
    return request;
  }

  private static SetCourtBookingDetailsRequest setCourtBookingDetailsRequest(
      long userId, String startTime, Long zoneId, String requiredNumberOfSlots, int duration) {
    SetCourtBookingDetailsRequest request = new SetCourtBookingDetailsRequest();
    request.setUserId(userId);
    request.setStartTime(startTime);
    request.setZoneId(zoneId);
    request.setRequiredNumberOfSlots(requiredNumberOfSlots);
    request.setDuration(duration);
    return request;
  }
}
