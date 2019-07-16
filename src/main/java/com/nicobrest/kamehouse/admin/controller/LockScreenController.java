package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.AdminCommand;
import com.nicobrest.kamehouse.admin.service.AdminCommandService;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;
import com.nicobrest.kamehouse.utils.ControllerUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controller to execute lock and unlock screen commands.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/admin")
public class LockScreenController {

  private static final Logger logger = LoggerFactory.getLogger(LockScreenController.class);

  @Autowired
  private AdminCommandService adminCommandService;

  /**
   * Lock screen in the server running the application.
   */
  @RequestMapping(value = "/lock-screen", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> lockScreen() {

    logger.trace("In controller /api/v1/admin/lock-screen (POST)");
    AdminCommand lockScreenAdminCommand = new AdminCommand(AdminCommand.LOCK_SCREEN);
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(lockScreenAdminCommand);
    ResponseEntity<List<SystemCommandOutput>> responseEntity = ControllerUtils
        .generateResponseEntity(commandOutputs);
    return responseEntity;
  }

  /**
   * Unlock screen in the server running the application.
   */
  @RequestMapping(value = "/unlock-screen", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> unlockScreen() {

    logger.trace("In controller /api/v1/admin/unlock-screen (POST)");
    AdminCommand unlockScreenAdminCommand = new AdminCommand(AdminCommand.UNLOCK_SCREEN);
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(
        unlockScreenAdminCommand);
    ResponseEntity<List<SystemCommandOutput>> responseEntity = ControllerUtils
        .generateResponseEntity(commandOutputs);
    return responseEntity;
  }
}