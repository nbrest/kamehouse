package com.nicobrest.kamehouse.admin.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.service.PowerManagementService;
import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.model.KameHouseGenericResponse;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

/**
 * Unit tests for PowerManagementController class.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class PowerManagementControllerTest extends AbstractAdminCommandControllerTest {

  @InjectMocks
  private PowerManagementController adminPowerManagementController;

  @Mock
  protected PowerManagementService powerManagementService;

  @Before
  public void beforeTest() {
    adminCommandControllerTestSetup();
    mockMvc = MockMvcBuilders.standaloneSetup(adminPowerManagementController).build();
  }

  /**
   * Sets shutdown successful test.
   */
  @Test
  public void setShutdownTest() throws Exception {
    MockHttpServletResponse response = doPost("/api/v1/admin/power-management/shutdown?delay=5400");
    KameHouseGenericResponse responseBody = getResponseBody(response,
        KameHouseGenericResponse.class);

    verifyResponseStatus(response, HttpStatus.CREATED);
    assertEquals("Scheduled shutdown at the specified delay of 5400 seconds", responseBody.getMessage());
    verify(powerManagementService, times(1)).scheduleShutdown(Mockito.anyInt());
  }

  /**
   * Sets shutdown exception test.
   */

  @Test
  public void setShutdownExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(
        KameHouseBadRequestException.class));
    Mockito.doThrow(new KameHouseBadRequestException("Invalid delay specified")).when(powerManagementService)
        .scheduleShutdown(59);

    doPost("/api/v1/admin/power-management/shutdown?delay=59");
  }

  /**
   * Cancels shutdown successful test.
   */
  @Test
  public void cancelShutdownTest() throws Exception {
    when(powerManagementService.cancelScheduledShutdown()).thenReturn("Shutdown cancelled");

    MockHttpServletResponse response = doDelete("/api/v1/admin/power-management/shutdown");
    KameHouseGenericResponse responseBody = getResponseBody(response,
        KameHouseGenericResponse.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals("Shutdown cancelled", responseBody.getMessage());
    verify(powerManagementService, times(1)).cancelScheduledShutdown();
  }

  /**
   * Cancels shutdown server error test.
   */
  @Test
  public void cancelShutdownServerErrorTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(
        KameHouseServerErrorException.class));
    Mockito.doThrow(new KameHouseServerErrorException("")).when(powerManagementService)
        .cancelScheduledShutdown();

    doDelete("/api/v1/admin/power-management/shutdown");
  }

  /**
   * Shutdowns status successful test.
   */
  @Test
  public void statusShutdownTest() throws Exception {
    when(powerManagementService.getShutdownStatus()).thenReturn("Shutdown not scheduled");

    MockHttpServletResponse response = doGet("/api/v1/admin/power-management/shutdown");
    KameHouseGenericResponse responseBody = getResponseBody(response,
        KameHouseGenericResponse.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals("Shutdown not scheduled", responseBody.getMessage());
    verify(powerManagementService, times(1)).getShutdownStatus();
  }

  /**
   * Sets suspend successful test.
   */
  @Test
  public void setSuspendTest() throws Exception {
    MockHttpServletResponse response = doPost("/api/v1/admin/power-management/suspend?delay=5400");
    KameHouseGenericResponse responseBody = getResponseBody(response,
        KameHouseGenericResponse.class);

    verifyResponseStatus(response, HttpStatus.CREATED);
    assertEquals("Scheduled suspend at the specified delay of 5400 seconds", responseBody.getMessage());
    verify(powerManagementService, times(1)).scheduleSuspend(Mockito.anyInt());
  }

  /**
   * Sets suspend exception test.
   */

  @Test
  public void setSuspendExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(
        KameHouseBadRequestException.class));
    Mockito.doThrow(new KameHouseBadRequestException("Invalid delay specified")).when(powerManagementService)
        .scheduleSuspend(-1);

    doPost("/api/v1/admin/power-management/suspend?delay=-1");
  }

  /**
   * Cancels suspend successful test.
   */
  @Test
  public void cancelSuspendTest() throws Exception {
    when(powerManagementService.cancelScheduledSuspend()).thenReturn("Suspend cancelled");

    MockHttpServletResponse response = doDelete("/api/v1/admin/power-management/suspend");
    KameHouseGenericResponse responseBody = getResponseBody(response,
        KameHouseGenericResponse.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals("Suspend cancelled", responseBody.getMessage());
    verify(powerManagementService, times(1)).cancelScheduledSuspend();
  }

  /**
   * Cancels suspend server error test.
   */
  @Test
  public void cancelSuspendServerErrorTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(
        KameHouseServerErrorException.class));
    Mockito.doThrow(new KameHouseServerErrorException("")).when(powerManagementService)
        .cancelScheduledSuspend();

    doDelete("/api/v1/admin/power-management/suspend");
  }

  /**
   * Suspend status successful test.
   */
  @Test
  public void statusSuspendTest() throws Exception {
    when(powerManagementService.getSuspendStatus()).thenReturn("Suspend not scheduled");

    MockHttpServletResponse response = doGet("/api/v1/admin/power-management/suspend");
    KameHouseGenericResponse responseBody = getResponseBody(response,
        KameHouseGenericResponse.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals("Suspend not scheduled", responseBody.getMessage());
    verify(powerManagementService, times(1)).getSuspendStatus();
  }

  /**
   * WOL server successful test.
   */
  @Test
  public void wolServerTest() throws Exception {
    doNothing().when(powerManagementService).wakeOnLan(anyString(), anyString());

    MockHttpServletResponse response = doPost("/api/v1/admin/power-management/wol"
        + "?server=media.server");

    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
  }

  /**
   * WOL mac and broadcast successful test.
   */
  @Test
  public void wolMacAndBroadcastTest() throws Exception {
    doNothing().when(powerManagementService).wakeOnLan(anyString(), anyString());

    MockHttpServletResponse response = doPost("/api/v1/admin/power-management/wol"
       + "?mac=AA:BB:CC:DD:EE:FF&broadcast=192.168.0.255");

    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
  }

  /**
   * WOL invalid request test.
   */
  @Test
  public void wolInvalidRequestTest() throws Exception {
    doNothing().when(powerManagementService).wakeOnLan(anyString());
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(
        KameHouseBadRequestException.class));
    thrown.expectMessage("server OR mac and broadcast parameters are required");

    doPost("/api/v1/admin/power-management/wol");
  }
}
