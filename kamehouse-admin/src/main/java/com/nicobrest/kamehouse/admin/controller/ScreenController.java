package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.AltTabKeyKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.EnterKeyKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.EscKeyKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.RightKeyKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenLockKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenUnlockKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenWakeUpKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.WinTabKeyKameHouseSystemCommand;
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

  public ScreenController(
      SystemCommandService systemCommandService) {
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
   * Sends an ESC key press on the server.
   */
  @PostMapping(path = "/esc-key-press")
  public ResponseEntity<List<SystemCommand.Output>> escKeyPress() {
    return execKameHouseSystemCommand(new EscKeyKameHouseSystemCommand());
  }

  /**
   * Sends an ENTER key press on the server.
   */
  @PostMapping(path = "/enter-key-press")
  public ResponseEntity<List<SystemCommand.Output>> enterKeyPress() {
    return execKameHouseSystemCommand(new EnterKeyKameHouseSystemCommand());
  }

  /**
   * Sends an ALT+TAB key press on the server.
   */
  @PostMapping(path = "/alt-tab-key-press")
  public ResponseEntity<List<SystemCommand.Output>> altTabKeyPress(
      @RequestParam(value = "tabs", required = false) Integer tabs) {
    return execKameHouseSystemCommand(new AltTabKeyKameHouseSystemCommand(tabs));
  }

  /**
   * Sends an WIN+TAB key press on the server.
   */
  @PostMapping(path = "/win-tab-key-press")
  public ResponseEntity<List<SystemCommand.Output>> winTabKeyPress() {
    return execKameHouseSystemCommand(new WinTabKeyKameHouseSystemCommand());
  }

  /**
   * Sends a right key press on the server followed by the ENTER key.
   */
  @PostMapping(path = "/right-key-press")
  public ResponseEntity<List<SystemCommand.Output>> winTabKeyPress(
      @RequestParam(value = "keyPresses", required = false) Integer keyPresses) {
    return execKameHouseSystemCommand(new RightKeyKameHouseSystemCommand(keyPresses));
  }
}
