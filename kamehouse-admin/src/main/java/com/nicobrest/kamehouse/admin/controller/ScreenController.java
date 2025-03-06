package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.KeyPress;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenLockKameHouseCommand;
import com.nicobrest.kamehouse.commons.controller.AbstractKameHouseCommandController;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.MouseButton;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.MouseClickJvncSenderKameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.TextJvncSenderKameHouseCommand;
import com.nicobrest.kamehouse.commons.service.KameHouseCommandService;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import java.util.ArrayList;
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
public class ScreenController extends AbstractKameHouseCommandController {

  private static final int MIN_SCREEN_POS = 0;
  private static final int MAX_SCREEN_POS = 4096;
  private static final int MIN_CLICK_COUNT = 1;
  private static final int MAX_CLICK_COUNT = 5;

  public ScreenController(KameHouseCommandService kameHouseCommandService) {
    super(kameHouseCommandService);
  }

  /**
   * Locks screen in the server running the application.
   */
  @PostMapping(path = "/lock")
  public ResponseEntity<List<KameHouseCommandResult>> lockScreen() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new ScreenLockKameHouseCommand());
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Unlocks screen in the server running the application.
   */
  @PostMapping(path = "/unlock")
  public ResponseEntity<List<KameHouseCommandResult>> unlockScreen() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    String unlockScreenPassword = getUnlockScreenPassword();
    kameHouseCommands.add(new ScreenLockKameHouseCommand());
    kameHouseCommands.add(new TextJvncSenderKameHouseCommand(KeyPress.ESC.get(), 3));
    kameHouseCommands.add(new TextJvncSenderKameHouseCommand(KeyPress.ESC.get(), 3));
    kameHouseCommands.add(
        new TextJvncSenderKameHouseCommand(unlockScreenPassword + KeyPress.ENTER.get()));
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Wakes up the screen. Run it when the screen goes dark after being idle for a while.
   */
  @PostMapping(path = "/wake-up")
  public ResponseEntity<List<KameHouseCommandResult>> wakeUpScreen() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new MouseClickJvncSenderKameHouseCommand(400, 400, 1, 3));
    kameHouseCommands.add(new MouseClickJvncSenderKameHouseCommand(400, 500, 1, 3));
    kameHouseCommands.add(new MouseClickJvncSenderKameHouseCommand(500, 500, 1));
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Sends a key press on the server.
   */
  @PostMapping(path = "/key-press")
  public ResponseEntity<List<KameHouseCommandResult>> keyPress(
      @RequestParam(value = "key", required = true) KeyPress key,
      @RequestParam(value = "keyPresses", required = false) Integer keyPresses) {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    String text = key.get(keyPresses);
    kameHouseCommands.add(new TextJvncSenderKameHouseCommand(text));
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Sends a mouse click on the server.
   */
  @PostMapping(path = "/mouse-click")
  public ResponseEntity<List<KameHouseCommandResult>> mouseClick(
      @RequestParam(value = "mouseButton", required = true) MouseButton mouseButton,
      @RequestParam(value = "positionX", required = true) Integer positionX,
      @RequestParam(value = "positionY", required = true) Integer positionY,
      @RequestParam(value = "clickCount", required = false) Integer clickCount) {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    if (clickCount == null) {
      clickCount = 1;
    }
    validateParameters(positionX, positionY, clickCount);
    kameHouseCommands.add(
        new MouseClickJvncSenderKameHouseCommand(mouseButton, positionX, positionY, clickCount));
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Gets the unlock screen password.
   */
  private static String getUnlockScreenPassword() {
    String unlockScreenPwdFile =
        PropertiesUtils.getUserHome() + "/" + PropertiesUtils.getProperty("unlock.screen.pwd.file");
    try {
      String decryptedFile = EncryptionUtils.decryptKameHouseFileToString(unlockScreenPwdFile);
      if (StringUtils.isEmpty(decryptedFile)) {
        decryptedFile = FileUtils.EMPTY_FILE_CONTENT;
      }
      return decryptedFile;
    } catch (KameHouseInvalidDataException e) {
      return FileUtils.EMPTY_FILE_CONTENT;
    }
  }

  /**
   * Validate mouse click settings.
   */
  private static void validateParameters(Integer positionX, Integer positionY,
      Integer clickCount) {
    if (positionX < MIN_SCREEN_POS || positionX > MAX_SCREEN_POS) {
      throw new KameHouseInvalidCommandException("Invalid positionX " + positionX);
    }
    if (positionY < MIN_SCREEN_POS || positionY > MAX_SCREEN_POS) {
      throw new KameHouseInvalidCommandException("Invalid positionY " + positionX);
    }
    if (clickCount < MIN_CLICK_COUNT || clickCount > MAX_CLICK_COUNT) {
      throw new KameHouseInvalidCommandException("Invalid positionY " + positionX);
    }
  }
}
