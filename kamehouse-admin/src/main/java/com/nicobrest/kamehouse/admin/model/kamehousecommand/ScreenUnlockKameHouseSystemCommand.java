package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.ScreenLockSystemCommand;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.JvncSenderSystemCommand;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;

/**
 * KameHouseSystemCommand to unlock the screen.
 *
 * @author nbrest
 */
public class ScreenUnlockKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public ScreenUnlockKameHouseSystemCommand() {
    String unlockScreenPassword = getUnlockScreenPassword();
    systemCommands.add(new ScreenLockSystemCommand());
    systemCommands.add(new JvncSenderSystemCommand(KeyPress.ESC.get(), 3));
    systemCommands.add(new JvncSenderSystemCommand(KeyPress.ESC.get(), 3));
    systemCommands.add(new JvncSenderSystemCommand(unlockScreenPassword + KeyPress.ENTER.get()));
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
}
