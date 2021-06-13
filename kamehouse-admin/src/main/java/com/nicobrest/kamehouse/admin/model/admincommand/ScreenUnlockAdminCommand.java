package com.nicobrest.kamehouse.admin.model.admincommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.ScreenLockSystemCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.AdminCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.VncDoKeyPressSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.VncDoTypeSystemCommand;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;

/**
 * AdminCommand to unlock the screen.
 * 
 * @author nbrest
 *
 */
public class ScreenUnlockAdminCommand extends AdminCommand {

  /**
   * Sets the required SystemCommands to achieve this AdminCommand.
   */
  public ScreenUnlockAdminCommand() {
    String decodedPassword = getUnlockScreenPassword();
    systemCommands.add(new ScreenLockSystemCommand());
    systemCommands.add(new VncDoKeyPressSystemCommand("esc"));
    systemCommands.add(new VncDoTypeSystemCommand(decodedPassword));
    systemCommands.add(new VncDoKeyPressSystemCommand("enter"));
  }
  
  /**
   * Gets the unlock screen password.
   */
  private String getUnlockScreenPassword() {
    String unlockScreenPwdFile = PropertiesUtils.getUserHome() + "/" + PropertiesUtils
        .getProperty("unlock.screen.pwd.file");
    return FileUtils.getDecodedFileContent(unlockScreenPwdFile);
  }
}
