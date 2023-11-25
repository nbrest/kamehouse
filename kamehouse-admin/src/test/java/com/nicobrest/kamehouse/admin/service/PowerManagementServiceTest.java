package com.nicobrest.kamehouse.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.config.AdminSchedulerConfig;
import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.utils.DateUtils;
import java.net.UnknownHostException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

/**
 * Unit tests for the PowerManagementService class.
 *
 * @author nbrest
 */
class PowerManagementServiceTest {

  @InjectMocks
  private PowerManagementService powerManagementService;

  @Mock(name = "scheduler")
  private Scheduler scheduler;

  @BeforeEach
  public void before() {
    powerManagementService = new PowerManagementService();
    MockitoAnnotations.openMocks(this);
  }

  /**
   * WOL server successful test.
   */
  @Test
  void wakeOnLanServerTest() {
    try {
      powerManagementService.wakeOnLan("media.server");
    } catch (KameHouseBadRequestException e) {
      // If an exception is thrown, I expect it to be for UnknownHostException
      assertTrue(e.getCause() instanceof UnknownHostException);
    }
  }

  /**
   * WOL invalid server test.
   */
  @Test
  void wakeOnLanInvalidServerTest() {
    assertThrows(
        KameHouseBadRequestException.class,
        () -> {
          powerManagementService.wakeOnLan("INVALID_SERVER");
        });
  }

  /**
   * WOL invalid mac address test.
   */
  @Test
  void wakeOnLanInvalidMacAddressLengthTest() {
    assertThrows(
        KameHouseBadRequestException.class,
        () -> {
          powerManagementService.wakeOnLan("AA:BB:CC:DD:EE", "10.10.9.9");
        });
  }

  /**
   * WOL invalid mac address test.
   */
  @Test
  void wakeOnLanInvalidMacAddressNumberFormatTest() {
    assertThrows(
        KameHouseBadRequestException.class,
        () -> {
          powerManagementService.wakeOnLan("AA:BB:CC:DD:EE:ZZ", "10.10.9.9");
        });
  }

  /**
   * WOL invalid broadcast address test.
   */
  @Test
  void wakeOnLanInvalidBroadcastTest() {
    assertThrows(
        KameHouseException.class,
        () -> {
          powerManagementService.wakeOnLan("AA:BB:CC:DD:EE:FF", "10.10.9.9.999");
        });
  }

  /**
   * WOL mac and broadcast successful test.
   */
  @Test
  void wakeOnLanMacAndBroadcastTest() throws KameHouseBadRequestException {
    Assertions.assertDoesNotThrow(() -> {
      powerManagementService.wakeOnLan("AA:BB:CC:DD:EE:FF", "10.10.9.9");
    });
  }

  /**
   * Shutdown server successful test.
   */
  @Test
  void scheduleShutdownSuccessTest() {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());

    Assertions.assertDoesNotThrow(() -> {
      powerManagementService.scheduleShutdown(5400);
    });
  }

  /**
   * Shutdown server exception test.
   */
  @Test
  void scheduleShutdownExceptionTest() {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());
    assertThrows(
        KameHouseBadRequestException.class,
        () -> {
          powerManagementService.scheduleShutdown(59);
        });
  }

  /**
   * Shutdown server reschedule test.
   */
  @Test
  void scheduleShutdownRescheduleTest() throws SchedulerException {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());
    when(scheduler.checkExists(Mockito.any(TriggerKey.class))).thenReturn(true);

    Assertions.assertDoesNotThrow(() -> {
      powerManagementService.scheduleShutdown(5400);
    });
  }

  /**
   * Shutdown server scheduler exception test.
   */
  @Test
  void scheduleShutdownSchedulerExceptionTest() throws SchedulerException {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());
    when(scheduler.checkExists(Mockito.any(TriggerKey.class)))
        .thenThrow(new SchedulerException());
    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          powerManagementService.scheduleShutdown(5400);
        });
  }

  /**
   * Shutdown server trigger won't fire test.
   */
  @Test
  void scheduleShutdownTriggerWontFireTest() throws SchedulerException {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());
    when(scheduler.checkExists(Mockito.any(TriggerKey.class)))
        .thenThrow(new SchedulerException(PowerManagementService.TRIGGER_WONT_FIRE));

    powerManagementService.scheduleShutdown(5400);
  }

  /**
   * Get shutdown server status successful test.
   */
  @Test
  void getShutdownStatusSuccessTest() {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());

    String status = powerManagementService.getShutdownStatus();
    assertEquals("Shutdown not scheduled", status);
  }

  /**
   * Get Shutdown server status successful scheduled test.
   */
  @Test
  void getShutdownStatusSuccessScheduledTest() throws SchedulerException {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());
    Trigger trigger = Mockito.mock(Trigger.class);
    when(trigger.getNextFireTime()).thenReturn(DateUtils.getCurrentDate());
    when(scheduler.getTrigger(Mockito.any(TriggerKey.class))).thenReturn(trigger);

    String status = powerManagementService.getShutdownStatus();
    assertTrue(status.startsWith("Shutdown scheduled at"));
  }

  /**
   * Get Shutdown server status exception test.
   */
  @Test
  void getShutdownStatusExceptionTest() throws SchedulerException {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());
    when(scheduler.getTrigger(Mockito.any(TriggerKey.class)))
        .thenThrow(new SchedulerException());
    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          powerManagementService.getShutdownStatus();
        });
  }

  /**
   * Cancel shutdown server successful test.
   */
  @Test
  void cancelScheduledShutdownSuccessTest() {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());

    String status = powerManagementService.cancelScheduledShutdown();
    assertEquals("Shutdown was not scheduled, so no need to cancel", status);
  }

  /**
   * Cancel shutdown server successful cancelled test.
   */
  @Test
  void cancelScheduledShutdownSuccessCancelledTest() throws SchedulerException {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());
    when(scheduler.unscheduleJob(Mockito.any(TriggerKey.class))).thenReturn(true);

    String status = powerManagementService.cancelScheduledShutdown();
    assertEquals("Shutdown cancelled", status);
  }

  /**
   * Cancel shutdown server exception test.
   */
  @Test
  void cancelScheduledShutdownExceptionTest() throws SchedulerException {
    powerManagementService.setShutdownJobDetail(new AdminSchedulerConfig().shutdownJobDetail());
    when(scheduler.unscheduleJob(Mockito.any(TriggerKey.class)))
        .thenThrow(new SchedulerException());
    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          powerManagementService.cancelScheduledShutdown();
        });
  }

  /**
   * Suspend server successful test.
   */
  @Test
  void suspendShutdownSuccessTest() {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());

    Assertions.assertDoesNotThrow(() -> {
      powerManagementService.scheduleSuspend(5400);
    });
  }

  /**
   * Suspend server exception test.
   */
  @Test
  void scheduleSuspendExceptionTest() {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());
    assertThrows(
        KameHouseBadRequestException.class,
        () -> {
          powerManagementService.scheduleSuspend(-1);
        });
  }

  /**
   * Suspend server reschedule test.
   */
  @Test
  void scheduleSuspendRescheduleTest() throws SchedulerException {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());
    when(scheduler.checkExists(Mockito.any(TriggerKey.class))).thenReturn(true);

    Assertions.assertDoesNotThrow(() -> {
      powerManagementService.scheduleSuspend(5400);
    });
  }

  /**
   * Suspend server scheduler exception test.
   */
  @Test
  void scheduleSuspendSchedulerExceptionTest() throws SchedulerException {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());
    when(scheduler.checkExists(Mockito.any(TriggerKey.class)))
        .thenThrow(new SchedulerException());
    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          powerManagementService.scheduleSuspend(5400);
        });
  }

  /**
   * Suspend server trigger won't fire test.
   */
  @Test
  void scheduleSuspendTriggerWontFireTest() throws SchedulerException {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());
    when(scheduler.checkExists(Mockito.any(TriggerKey.class)))
        .thenThrow(new SchedulerException(PowerManagementService.TRIGGER_WONT_FIRE));

    powerManagementService.scheduleSuspend(5400);
  }

  /**
   * Get Suspend server status successful not scheduled test.
   */
  @Test
  void getSuspendStatusSuccessNotScheduledTest() {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());

    String status = powerManagementService.getSuspendStatus();
    assertEquals("Suspend not scheduled", status);
  }

  /**
   * Get Suspend server status successful scheduled test.
   */
  @Test
  void getSuspendStatusSuccessScheduledTest() throws SchedulerException {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());
    Trigger trigger = Mockito.mock(Trigger.class);
    when(trigger.getNextFireTime()).thenReturn(DateUtils.getCurrentDate());
    when(scheduler.getTrigger(Mockito.any(TriggerKey.class))).thenReturn(trigger);

    String status = powerManagementService.getSuspendStatus();
    assertTrue(status.startsWith("Suspend scheduled at"));
  }

  /**
   * Get Suspend server status exception test.
   */
  @Test
  void getSuspendStatusExceptionTest() throws SchedulerException {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());
    when(scheduler.getTrigger(Mockito.any(TriggerKey.class)))
        .thenThrow(new SchedulerException());
    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          powerManagementService.getSuspendStatus();
        });
  }

  /**
   * Cancel Suspend server successful not scheduled test.
   */
  @Test
  void cancelScheduledSuspendSuccessNotScheduledTest() {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());

    String status = powerManagementService.cancelScheduledSuspend();
    assertEquals("Suspend was not scheduled, so no need to cancel", status);
  }

  /**
   * Cancel Suspend server successful cancelled test.
   */
  @Test
  void cancelScheduledSuspendSuccessCancelledTest() throws SchedulerException {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());
    when(scheduler.unscheduleJob(Mockito.any(TriggerKey.class))).thenReturn(true);

    String status = powerManagementService.cancelScheduledSuspend();
    assertEquals("Suspend cancelled", status);
  }

  /**
   * Cancel Suspend server exception test.
   */
  @Test
  void cancelScheduledSuspendExceptionTest() throws SchedulerException {
    powerManagementService.setSuspendJobDetail(new AdminSchedulerConfig().suspendJobDetail());
    when(scheduler.unscheduleJob(Mockito.any(TriggerKey.class)))
        .thenThrow(new SchedulerException());
    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          powerManagementService.cancelScheduledSuspend();
        });
  }
}
