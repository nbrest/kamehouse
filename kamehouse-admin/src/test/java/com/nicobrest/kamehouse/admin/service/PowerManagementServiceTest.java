package com.nicobrest.kamehouse.admin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
import org.mockito.exceptions.base.MockitoException;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.text.ParseException;
import java.util.Date;

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
   * Shutdown server reschedule test.
   */
  @Test
  public void scheduleShutdownRescheduleTest() throws SchedulerException {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());
    when(scheduler.checkExists(Mockito.any(TriggerKey.class))).thenReturn(true);

    powerManagementService.scheduleShutdown(5400);
    // no exception thrown expected
  }

  /**
   * Shutdown server scheduler exception test.
   */
  @Test
  public void scheduleShutdownSchedulerExceptionTest() throws SchedulerException {
    thrown.expect(KameHouseServerErrorException.class);
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());
    when(scheduler.checkExists(Mockito.any(TriggerKey.class))).thenThrow(new SchedulerException());

    powerManagementService.scheduleShutdown(5400);
  }

  /**
   * Shutdown server trigger won't fire test.
   */
  @Test
  public void scheduleShutdownTriggerWontFireTest() throws SchedulerException {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());
    when(scheduler.checkExists(Mockito.any(TriggerKey.class)))
        .thenThrow(new SchedulerException(PowerManagementService.TRIGGER_WONT_FIRE));

    powerManagementService.scheduleShutdown(5400);
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
   * Get Shutdown server status successful scheduled test.
   */
  @Test
  public void getShutdownStatusSuccessScheduledTest() throws SchedulerException {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());
    Trigger trigger = Mockito.mock(Trigger.class);
    when(trigger.getNextFireTime()).thenReturn(new Date());
    when(scheduler.getTrigger(Mockito.any(TriggerKey.class))).thenReturn(trigger);

    String status = powerManagementService.getShutdownStatus();
    assertTrue(status.startsWith("Shutdown scheduled at"));
  }

  /**
   * Get Shutdown server status exception test.
   */
  @Test
  public void getShutdownStatusExceptionTest() throws SchedulerException {
    thrown.expect(KameHouseServerErrorException.class);
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());
    when(scheduler.getTrigger(Mockito.any(TriggerKey.class)))
        .thenThrow(new SchedulerException());

    powerManagementService.getShutdownStatus();
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
   * Cancel shutdown server successful cancelled test.
   */
  @Test
  public void cancelScheduledShutdownSuccessCancelledTest() throws SchedulerException {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());
    when(scheduler.unscheduleJob(Mockito.any(TriggerKey.class))).thenReturn(true);

    String status = powerManagementService.cancelScheduledShutdown();
    assertEquals("Shutdown cancelled", status);
  }

  /**
   * Cancel shutdown server exception test.
   */
  @Test
  public void cancelScheduledShutdownExceptionTest() throws SchedulerException {
    thrown.expect(KameHouseServerErrorException.class);
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());
    when(scheduler.unscheduleJob(Mockito.any(TriggerKey.class)))
        .thenThrow(new SchedulerException());

    powerManagementService.cancelScheduledShutdown();
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
   * Suspend server trigger won't fire test.
   */
  @Test
  public void scheduleSuspendTriggerWontFireTest() throws SchedulerException {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());
    when(scheduler.checkExists(Mockito.any(TriggerKey.class)))
        .thenThrow(new SchedulerException(PowerManagementService.TRIGGER_WONT_FIRE));

    powerManagementService.scheduleSuspend(5400);
  }

  /**
   * Get Suspend server status successful not scheduled test.
   */
  @Test
  public void getSuspendStatusSuccessNotScheduledTest() {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());

    String status = powerManagementService.getSuspendStatus();
    assertEquals("Suspend not scheduled", status);
  }

  /**
   * Get Suspend server status successful scheduled test.
   */
  @Test
  public void getSuspendStatusSuccessScheduledTest() throws SchedulerException {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());
    Trigger trigger = Mockito.mock(Trigger.class);
    when(trigger.getNextFireTime()).thenReturn(new Date());
    when(scheduler.getTrigger(Mockito.any(TriggerKey.class))).thenReturn(trigger);

    String status = powerManagementService.getSuspendStatus();
    assertTrue(status.startsWith("Suspend scheduled at"));
  }

  /**
   * Get Suspend server status exception test.
   */
  @Test
  public void getSuspendStatusExceptionTest() throws SchedulerException {
    thrown.expect(KameHouseServerErrorException.class);
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());
    when(scheduler.getTrigger(Mockito.any(TriggerKey.class)))
        .thenThrow(new SchedulerException());

    powerManagementService.getSuspendStatus();
  }

  /**
   * Cancel Suspend server successful not scheduled test.
   */
  @Test
  public void cancelScheduledSuspendSuccessNotScheduledTest() {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());

    String status = powerManagementService.cancelScheduledSuspend();
    assertEquals("Suspend was not scheduled, so no need to cancel", status);
  }

  /**
   * Cancel Suspend server successful cancelled test.
   */
  @Test
  public void cancelScheduledSuspendSuccessCancelledTest() throws SchedulerException {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());
    when(scheduler.unscheduleJob(Mockito.any(TriggerKey.class))).thenReturn(true);

    String status = powerManagementService.cancelScheduledSuspend();
    assertEquals("Suspend cancelled", status);
  }

  /**
   * Cancel Suspend server exception test.
   */
  @Test
  public void cancelScheduledSuspendExceptionTest() throws SchedulerException {
    thrown.expect(KameHouseServerErrorException.class);
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());
    when(scheduler.unscheduleJob(Mockito.any(TriggerKey.class)))
        .thenThrow(new SchedulerException());

    powerManagementService.cancelScheduledSuspend();
  }
}
