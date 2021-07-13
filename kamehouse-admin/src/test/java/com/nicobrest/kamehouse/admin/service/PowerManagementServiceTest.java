package com.nicobrest.kamehouse.admin.service;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;
import com.nicobrest.kamehouse.admin.config.AdminSchedulerConfig;
import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

/**
 * Unit tests for the PowerManagementService class.
 * 
 * @author nbrest
 *
 */
public class PowerManagementServiceTest {

  @InjectMocks
  private PowerManagementService powerManagementService;

  @Mock(name = "scheduler")
  private Scheduler scheduler;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * WOL server successful test.
   */
  @Test
  public void wakeOnLanServerTest() throws KameHouseBadRequestException {
    powerManagementService = new PowerManagementService();

    powerManagementService.wakeOnLan("media.server");
    // no exception thrown expected
  }

  /**
   * WOL invalid server test.
   */
  @Test
  public void wakeOnLanInvalidServerTest() {
    thrown.expect(KameHouseBadRequestException.class);
    thrown.expectMessage("INVALID_SERVER");
    powerManagementService = new PowerManagementService();

    powerManagementService.wakeOnLan("INVALID_SERVER");
  }

  /**
   * WOL invalid mac address test.
   */
  @Test
  public void wakeOnLanInvalidMacAddressLengthTest() {
    thrown.expect(KameHouseBadRequestException.class);
    thrown.expectMessage("Invalid MAC address");
    powerManagementService = new PowerManagementService();

    powerManagementService.wakeOnLan("AA:BB:CC:DD:EE", "10.10.9.9");
  }

  /**
   * WOL invalid mac address test.
   */
  @Test
  public void wakeOnLanInvalidMacAddressNumberFormatTest() {
    thrown.expect(KameHouseBadRequestException.class);
    thrown.expectMessage("Invalid MAC address");
    powerManagementService = new PowerManagementService();

    powerManagementService.wakeOnLan("AA:BB:CC:DD:EE:ZZ", "10.10.9.9");
  }

  /**
   * WOL invalid broadcast address test.
   */
  @Test
  public void wakeOnLanInvalidBroadcastTest() {
    thrown.expect(KameHouseException.class);
    thrown.expectMessage("java.net.UnknownHostException: 10.10.9.9.999");
    powerManagementService = new PowerManagementService();

    powerManagementService.wakeOnLan("AA:BB:CC:DD:EE:FF", "10.10.9.9.999");
  }

  /**
   * WOL mac and broadcast successful test.
   */
  @Test
  public void wakeOnLanMacAndBroadcastTest() throws KameHouseBadRequestException {
    powerManagementService = new PowerManagementService();

    powerManagementService.wakeOnLan("AA:BB:CC:DD:EE:FF", "10.10.9.9");
    // no exception thrown expected
  }

  /**
   * Shutdown server successful test.
   */
  @Test
  public void scheduleShutdownSuccessTest() {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());

    powerManagementService.scheduleShutdown(5400);
    // no exception thrown expected
  }

  /**
   * Shutdown server exception test.
   */
  @Test
  public void scheduleShutdownExceptionTest() {
    thrown.expect(KameHouseBadRequestException.class);
    thrown.expectMessage("Invalid delay specified");
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());

    powerManagementService.scheduleShutdown(59);
  }

  /**
   * Get shutdown server status successful test.
   */
  @Test
  public void getShutdownStatusSuccessTest() {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());

    String status = powerManagementService.getShutdownStatus();
    assertEquals("Shutdown not scheduled", status);
  }

  /**
   * Cancel shutdown server successful test.
   */
  @Test
  public void cancelScheduledShutdownSuccessTest() {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());

    String status = powerManagementService.cancelScheduledShutdown();
    assertEquals("Shutdown was not scheduled, so no need to cancel", status);
  }

  /**
   * Suspend server successful test.
   */
  @Test
  public void suspendShutdownSuccessTest() {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());

    powerManagementService.scheduleSuspend(5400);
    // no exception thrown expected
  }

  /**
   * Suspend server exception test.
   */
  @Test
  public void scheduleSuspendExceptionTest() {
    thrown.expect(KameHouseBadRequestException.class);
    thrown.expectMessage("Invalid delay specified");
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());

    powerManagementService.scheduleSuspend(-1);
  }

  /**
   * Suspend server reschedule test.
   */
  @Test
  public void scheduleSuspendRescheduleTest() throws SchedulerException {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());
    when(scheduler.checkExists(Mockito.any(TriggerKey.class))).thenReturn(true);

    powerManagementService.scheduleSuspend(5400);
    // no exception thrown expected
  }

  /**
   * Suspend server scheduler exception test.
   */
  @Test
  public void scheduleSuspendSchedulerExceptionTest() throws SchedulerException {
    thrown.expect(KameHouseServerErrorException.class);
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());
    when(scheduler.checkExists(Mockito.any(TriggerKey.class))).thenThrow(new SchedulerException());

    powerManagementService.scheduleSuspend(5400);
  }

  /**
   * Get Suspend server status successful test.
   */
  @Test
  public void getSuspendStatusSuccessTest() {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());

    String status = powerManagementService.getSuspendStatus();
    assertEquals("Suspend not scheduled", status);
  }

  /**
   * Cancel Suspend server successful test.
   */
  @Test
  public void cancelScheduledSuspendSuccessTest() {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());

    String status = powerManagementService.cancelScheduledSuspend();
    assertEquals("Suspend was not scheduled, so no need to cancel", status);
  }
}
