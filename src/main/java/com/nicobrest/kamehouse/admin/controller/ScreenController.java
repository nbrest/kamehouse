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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controller to execute commands to control the screen.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/admin/screen")
public class ScreenController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private AdminCommandService adminCommandService;

  /**
   * Lock screen in the server running the application.
   */
  @PostMapping(path = "/lock")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> lockScreen() {
    logger.trace("In controller /api/v1/admin/screen/lock (POST)");
    AdminCommand lockScreenAdminCommand = new AdminCommand(AdminCommand.SCREEN_LOCK);
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(lockScreenAdminCommand); 
    return ControllerUtils.generateResponseEntity(commandOutputs);
  }

  /**
   * Unlock screen in the server running the application.
   */
  @PostMapping(path = "/unlock")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> unlockScreen() {
    logger.trace("In controller /api/v1/admin/screen/unlock (POST)");
    AdminCommand unlockScreenAdminCommand = new AdminCommand(AdminCommand.SCREEN_UNLOCK);
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(
        unlockScreenAdminCommand);
    return ControllerUtils.generateResponseEntity(commandOutputs);
  }
  
  /**
   * Wake up the screen. Run it when the screen goes dark after being idle for a while.
   */
  @PostMapping(path = "/wake-up")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> wakeUpScreen() {

    logger.trace("In controller /api/v1/admin/screen/wake-up (POST)");
    AdminCommand unlockScreenAdminCommand = new AdminCommand(AdminCommand.SCREEN_WAKE_UP);
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(
        unlockScreenAdminCommand);
    return ControllerUtils.generateResponseEntity(commandOutputs);
  }
}