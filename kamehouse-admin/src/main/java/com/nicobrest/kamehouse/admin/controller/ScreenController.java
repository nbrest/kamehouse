package com.nicobrest.kamehouse.admin.controller;
 
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenLockKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenUnlockKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenWakeUpKameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.controller.AbstractSystemCommandController;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

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
public class ScreenController extends AbstractSystemCommandController {

  /**
   * Locks screen in the server running the application.
   */
  @PostMapping(path = "/lock")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> lockScreen() {
    logger.trace("/api/v1/admin/screen/lock (POST)");
    return execKameHouseSystemCommand(new ScreenLockKameHouseSystemCommand());
  }

  /**
   * Unlocks screen in the server running the application.
   */
  @PostMapping(path = "/unlock")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> unlockScreen() {
    logger.trace("/api/v1/admin/screen/unlock (POST)");
    return execKameHouseSystemCommand(new ScreenUnlockKameHouseSystemCommand());
  }

  /**
   * Wakes up the screen. Run it when the screen goes dark after being idle for a
   * while.
   */
  @PostMapping(path = "/wake-up")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> wakeUpScreen() {
    logger.trace("/api/v1/admin/screen/wake-up (POST)");
    return execKameHouseSystemCommand(new ScreenWakeUpKameHouseSystemCommand());
  }
}