package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.KeyPress;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.KeyPressKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.MouseClickKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenLockKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenUnlockKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenWakeUpKameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.controller.AbstractSystemCommandController;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.commons.service.SystemCommandService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to execute commands to control the screen.
 *
 * @author nbrest
 */
@RestController
@RequestMapping(value = "/api/v1/admin/screen")
public class ScreenController extends AbstractSystemCommandController {

  public ScreenController(SystemCommandService systemCommandService) {
    super(systemCommandService);
  }

  /**
   * Locks screen in the server running the application.
   */
  @PostMapping(path = "/lock")
  public ResponseEntity<List<SystemCommand.Output>> lockScreen() {
    return execKameHouseSystemCommand(new ScreenLockKameHouseSystemCommand());
  }

  /**
   * Unlocks screen in the server running the application.
   */
  @PostMapping(path = "/unlock")
  public ResponseEntity<List<SystemCommand.Output>> unlockScreen() {
    return execKameHouseSystemCommand(new ScreenUnlockKameHouseSystemCommand());
  }

  /**
   * Wakes up the screen. Run it when the screen goes dark after being idle for a while.
   */
  @PostMapping(path = "/wake-up")
  public ResponseEntity<List<SystemCommand.Output>> wakeUpScreen() {
    return execKameHouseSystemCommand(new ScreenWakeUpKameHouseSystemCommand());
  }

  /**
   * Sends a key press on the server.
   */
  @PostMapping(path = "/key-press")
  public ResponseEntity<List<SystemCommand.Output>> keyPress(
      @RequestParam(value = "key", required = true) KeyPress key,
      @RequestParam(value = "keyPresses", required = false) Integer keyPresses) {
    return execKameHouseSystemCommand(new KeyPressKameHouseSystemCommand(key, keyPresses));
  }

  /**
   * Sends a mouse click on the server.
   */
  @PostMapping(path = "/mouse-click")
  public ResponseEntity<List<SystemCommand.Output>> keyPress(
      @RequestParam(value = "positionX", required = true) Integer positionX,
      @RequestParam(value = "positionY", required = true) Integer positionY,
      @RequestParam(value = "clickCount", required = false) Integer clickCount) {
    return execKameHouseSystemCommand(
        new MouseClickKameHouseSystemCommand(positionX, positionY, clickCount));
  }
}
