package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.systemcommand.model.ScreenLockSystemCommand;
import com.nicobrest.kamehouse.systemcommand.model.VncDoKeyPressSystemCommand;
import com.nicobrest.kamehouse.systemcommand.model.VncDoTypeSystemCommand;
import com.nicobrest.kamehouse.utils.FileUtils;
import com.nicobrest.kamehouse.utils.PropertiesUtils;

/**
 * AdminCommand to unlock the screen.
 * 
 * @author nbrest
 *
 */
public class ScreenUnlockAdminCommand extends AdminCommand {

  /**
   * Default constructor.
   */
  public ScreenUnlockAdminCommand() {
    String decodedPassword = getUnlockScreenPassword();
    systemCommands.add(new ScreenLockSystemCommand());
    systemCommands.add(new VncDoKeyPressSystemCommand("esc"));
    systemCommands.add(new VncDoTypeSystemCommand(decodedPassword));
    systemCommands.add(new VncDoKeyPressSystemCommand("enter"));
  }
  
  /**
   * Get the unlock screen password.
   */
  private String getUnlockScreenPassword() {
    String unlockScreenPwdFile = PropertiesUtils.getUserHome() + "/" + PropertiesUtils
        .getAdminProperty("unlock.screen.pwd.file");
    return FileUtils.getDecodedFileContent(unlockScreenPwdFile);
  }
}
