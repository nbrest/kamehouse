package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.RebootKameHouseCommand;
import com.nicobrest.kamehouse.admin.service.PowerManagementService;
import com.nicobrest.kamehouse.commons.controller.AbstractKameHouseCommandController;
import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.model.KameHouseGenericResponse;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.commons.service.KameHouseCommandService;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for the power management commands.
 *
 * @author nbrest
 */
@RestController
@RequestMapping(value = "/api/v1/admin/power-management")
public class PowerManagementController extends AbstractKameHouseCommandController {

  private PowerManagementService powerManagementService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public PowerManagementController(PowerManagementService powerManagementService,
      KameHouseCommandService kameHouseCommandService) {
    super(kameHouseCommandService);
    this.powerManagementService = powerManagementService;
  }

  /**
   * Shutdowns the local server with the specified delay in seconds.
   */
  @PostMapping(path = "/shutdown")
  public ResponseEntity<KameHouseGenericResponse> setShutdown(
      @RequestParam(value = "delay", required = true) Integer delay) {
    powerManagementService.scheduleShutdown(delay);
    KameHouseGenericResponse response = new KameHouseGenericResponse();
    response.setMessage("Scheduled shutdown at the specified delay of " + delay + " seconds");
    return generatePostResponseEntity(response);
  }

  /**
   * Gets the status of a shutdown command.
   */
  @GetMapping(path = "/shutdown")
  public ResponseEntity<KameHouseGenericResponse> statusShutdown() {
    String suspendStatus = powerManagementService.getShutdownStatus();
    KameHouseGenericResponse response = new KameHouseGenericResponse();
    response.setMessage(suspendStatus);
    return generateGetResponseEntity(response);
  }

  /**
   * Cancels a shutdown command.
   */
  @DeleteMapping(path = "/shutdown")
  public ResponseEntity<KameHouseGenericResponse> cancelShutdown() {
    String cancelSuspendStatus = powerManagementService.cancelScheduledShutdown();
    KameHouseGenericResponse response = new KameHouseGenericResponse();
    response.setMessage(cancelSuspendStatus);
    return generateGetResponseEntity(response);
  }

  /**
   * Schedule a job to suspend the server. Executed through a scheduled job because the suspend
   * command doesn't natively support scheduling/delay in windows.
   */
  @PostMapping(path = "/suspend")
  public ResponseEntity<KameHouseGenericResponse> setSuspend(
      @RequestParam(value = "delay", required = true) Integer delay) {
    powerManagementService.scheduleSuspend(delay);
    KameHouseGenericResponse response = new KameHouseGenericResponse();
    response.setMessage("Scheduled suspend at the specified delay of " + delay + " seconds");
    return generatePostResponseEntity(response);
  }

  /**
   * Gets the status of a scheduled suspend.
   */
  @GetMapping(path = "/suspend")
  public ResponseEntity<KameHouseGenericResponse> getSuspend() {
    String suspendStatus = powerManagementService.getSuspendStatus();
    KameHouseGenericResponse response = new KameHouseGenericResponse();
    response.setMessage(suspendStatus);
    return generateGetResponseEntity(response);
  }

  /**
   * Cancel a scheduled suspend.
   */
  @DeleteMapping(path = "/suspend")
  public ResponseEntity<KameHouseGenericResponse> cancelSuspend() {
    String cancelSuspendStatus = powerManagementService.cancelScheduledSuspend();
    KameHouseGenericResponse response = new KameHouseGenericResponse();
    response.setMessage(cancelSuspendStatus);
    return generateGetResponseEntity(response);
  }

  /**
   * Reboot the server.
   */
  @PostMapping(path = "/reboot")
  public ResponseEntity<List<KameHouseCommandResult>> reboot() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new RebootKameHouseCommand());
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Wake on lan the specified server or mac address.
   */
  @PostMapping(path = "/wol")
  public ResponseEntity<KameHouseGenericResponse> wakeOnLan(
      @RequestParam(value = "server", required = false) String server,
      @RequestParam(value = "mac", required = false) String mac,
      @RequestParam(value = "broadcast", required = false) String broadcast) {
    String serverSanitized = StringUtils.sanitize(server);
    String macSanitized = StringUtils.sanitize(mac);
    String broadcastSanitized = StringUtils.sanitize(broadcast);
    KameHouseGenericResponse response = new KameHouseGenericResponse();
    if (serverSanitized != null) {
      powerManagementService.wakeOnLan(serverSanitized);
      response.setMessage("WOL packet sent to " + serverSanitized);
    } else if (macSanitized != null && broadcastSanitized != null) {
      powerManagementService.wakeOnLan(macSanitized, broadcastSanitized);
      response.setMessage("WOL packet sent to " + macSanitized + " over " + broadcastSanitized);
    } else {
      throw new KameHouseBadRequestException("server OR mac and broadcast parameters are required");
    }
    return generatePostResponseEntity(response);
  }
}
